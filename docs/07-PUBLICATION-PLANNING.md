# 07 — Publication et planning

## Objectif

Publier **5 Reels par jour** sur la page Facebook **Badr Quran — بدر القرآنيات**, chacun **10 minutes avant l'un des cinq adhans** de Casablanca.

## Vue d'ensemble

```
Aladhan (horaires)
      │
      ▼
prayer_reels.py --tick   (cron toutes les 10 min)
      │
      ├── crée le planning du jour
      ├── choisit le prochain créneau
      ├── lance quran_reels_v2.py
      ├── publie via TryPost
      └── met à jour prayer_schedule.json + state.json
```

## Horaires de prière

- API : `https://api.aladhan.com/v1/timingsByCity`
- Ville : `Casablanca`
- Pays : `Morocco`
- Méthode : `21` (Ministère des Habous et des Affaires Islamiques)
- Fuseau : `Africa/Casablanca`
- École Asr : standard (Shafi'i)

Les horaires changent chaque jour. Le planning est recalculé au premier tick de la journée.

## Créneaux

| Prière | Arabe | Heure de publication |
|---|---|---|
| Fajr | الفجر | adhan − 10 min |
| Dhuhr | الظهر | adhan − 10 min |
| Asr | العصر | adhan − 10 min |
| Maghrib | المغرب | adhan − 10 min |
| Isha | العشاء | adhan − 10 min |

La marge est configurable (`PRAYER_OFFSET_MIN`, `offset_min` côté API). Valeur recommandée : 10 minutes.

## Tick du planificateur

Le cron appelle `run_prayer.sh` toutes les 10 minutes :

```cron
*/10 * * * * cd /root/badr-quran-reels-studio/pipeline && ./run_prayer.sh >> prayer.log 2>&1
```

À chaque tick :

1. Charge `.env` et `prayer_schedule.json`.
2. Crée le planning du jour s'il n'existe pas.
3. Marque `missed` les créneaux dont l'heure est passée sans publication.
4. Sélectionne **au plus un** créneau :
   - statut `pending` ;
   - publication dans moins de `PRAYER_LOOKAHEAD_MIN` (120 min) ;
   - assez de temps pour générer (`PRAYER_MIN_LEAD_MIN`, 12 min).
5. Prend un verrou `flock` pour éviter deux rendus simultanés.
6. Lance `quran_reels_v2.py` avec les paramètres du jour.
7. Publie via TryPost avec `scheduled_at`.
8. Enregistre `file`, `post_id`, `status`, `attempts`.
9. Libère le verrou.

### Pourquoi un seul Reel par tick ?

Un rendu dure 1 à 3 minutes. En traitant un seul créneau par tick, on évite :

- deux encodages FFmpeg simultanés ;
- deux avancées du pointeur de versets ;
- deux publications en double.

Le tick suivant (10 min plus tard) traite le créneau suivant si nécessaire.

## Verrou

```bash
flock -n /tmp/quran_reels.lock python3 quran_reels_v2.py ...
```

Si le verrou est occupé, le tick s'arrête proprement et réessaiera au prochain passage.

## Progression des versets

- Pointeur stocké dans `state.json` : `surah`, `ayah`.
- Avance après un rendu réussi uniquement.
- Progression stricte : 1:1 → 114:6 → 1:1.
- Aucune sourate ni aucun verset n'est sauté.
- Un verset long est découpé en extraits contigus, sans perte de mot.

## TryPost

### Rôle

TryPost est une instance auto-hébergée qui :

- stocke le post et sa légende ;
- télécharge la vidéo depuis une URL publique ;
- programme la publication ;
- publie sur la page Facebook ;
- renvoie l'URL du Reel.

### Flux de publication

```
1. POST /api/v1/posts
   {
     "account_id": "<facebook_page_id>",
     "content": "<légende>",
     "media_urls": ["<url_public_video>"],
     "status": "draft"
   }
   → { "id": "<post_id>" }

2. PUT /api/v1/posts/<post_id>
   { "status": "scheduled", "scheduled_at": "<ISO>" }

3. TryPost publie à l'heure prévue.

4. Le pipeline enregistre post_id et url dans le sidecar.
```

### Contraintes

- La vidéo doit être accessible publiquement par TryPost.
- L'URL média est construite avec `TRYPOST_MEDIA_BASE_URL`.
- Le serveur média (`reels-viewer`, pm2, port 3200) expose `output/`.
- TryPost refuse une publication moins de `PUBLISH_DELAY_MINUTES` (2 min) dans le futur.
- En cas d'échec, le Reel reste `rendered` et peut être republié.

### Légende

Générée par `build_caption(result)` :

```
📖 Sourate Al-Baqarah (البقرة) — versets 8 à 9

<texte arabe>

<traduction française>

🎙️ Récitation : Yasser Ad-Dussary

#Coran #Quran #بدر_القرآنيات
```

Règles :

- l'arabe est tronqué à 380 caractères sur une frontière de mot ;
- la traduction est tronquée à 620 caractères sur une frontière de phrase ;
- les hashtags sont fixes ;
- la légende peut être remplacée par `caption_override` dans l'API.

## Statuts et reprise

| Statut | Sens | Action suivante |
|---|---|---|
| `pending` | Rien n'a été fait. | Le tick peut le prendre. |
| `scheduled` | Programmé sur TryPost. | Attendre la publication. |
| `published` | Publié. | Rien. |
| `missed` | Heure passée sans publication. | Aucune, sauf republier manuellement. |
| `failed` | Erreur. | Corriger puis relancer. |

### Règles de reprise

- Un créneau `failed` peut être retenté manuellement.
- Un créneau `missed` n'est pas rattrapé automatiquement : on ne publie pas un Reel après l'adhan.
- Un Reel `rendered` peut être publié à tout moment via l'application.
- Un Reel `scheduled` peut être reprogrammé.

## Fuseaux horaires

- Les horaires Aladhan sont locaux à la ville.
- `publish_at` est stocké en UTC.
- `publish_at_local` conserve le fuseau local pour l'affichage.
- L'application affiche toujours l'heure locale de la ville.
- Le serveur doit avoir `TZ=Africa/Casablanca` ou utiliser explicitement `zoneinfo`.

## Cas particuliers

### Changement d'heure

Le Maroc applique des transitions saisonnières. Utiliser `zoneinfo` et jamais des offsets fixes. Le pipeline stocke les deux représentations pour éviter toute ambiguïté.

### Journée incomplète

Si le service démarre en milieu de journée, les créneaux passés sont marqués `missed` avec la raison « déploiement en cours de journée ».

### Panne réseau

- Si Aladhan est indisponible, le planning du jour précédent n'est pas réutilisé à l'aveugle : le tick échoue et réessaie.
- Si TryPost est indisponible, le Reel est généré mais reste `rendered`.
- Si EveryAyah est indisponible, le rendu échoue et le pointeur n'avance pas.

### Double publication

- Le verrou `flock` empêche deux rendus.
- Le statut du slot passe à `scheduled` avant l'appel TryPost.
- Si un `post_id` existe déjà, le pipeline ne republie pas.

## API de planning

Voir [04-API.md](04-API.md) :

- `GET /api/schedules/{date}` : planning du jour ;
- `PUT /api/schedules/{date}` : recalcul ;
- `PATCH /api/schedules/{date}/slots/{prayer}` : activation/désactivation.

## Commandes utiles

```bash
# état du planning
python3 prayer_reels.py --status

# (re)créer le planning du jour
python3 prayer_reels.py --plan

# simuler un tick
python3 prayer_reels.py --tick --dry-run

# repartir à 1:1
python3 prayer_reels.py --reset

# publier un Reel précis maintenant
python3 quran_reels_v2.py --surah 2 --ayah 8 --verses 2 --publish

# programmer un Reel
python3 quran_reels_v2.py --surah 2 --ayah 8 --verses 2 \
  --publish --schedule-at 2026-11-03T05:12:00Z
```

## Journalisation

- `prayer.log` : sortie du cron.
- `cron.log` : ancien journal quotidien.
- Chaque sidecar contient le résultat exact de la publication.
- Les logs ne doivent jamais contenir la clé TryPost.

## Surveillance

À vérifier chaque jour :

1. Les 5 créneaux sont `scheduled` ou `published`.
2. `state.json` a avancé.
3. Aucun créneau `failed`.
4. Le disque ne se remplit pas (`output/` grossit de 150 à 400 Mo par jour).
5. TryPost répond.

### Rotation du disque

Prévoir une purge des MP4 de plus de 30 jours, en conservant les sidecars pour l'historique. Ne jamais supprimer un MP4 publié sans avoir sauvegardé son URL.
