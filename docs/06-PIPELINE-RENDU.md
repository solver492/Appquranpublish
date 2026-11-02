# 06 — Pipeline de rendu

Le pipeline est le moteur qui transforme un verset en vidéo MP4 verticale. Il vit dans `pipeline/` et fonctionne sans l'application mobile.

## Fichiers

| Fichier | Rôle |
|---|---|
| `quran_reels_v2.py` | Génération d'un Reel complet. |
| `prayer_reels.py` | Planificateur 5 Reels/jour avant chaque adhan. |
| `verify_reel.py` | Vérifie qu'un MP4 est lisible et conforme. |
| `run_prayer.sh` | Entrée du cron toutes les 10 minutes. |
| `daily_reel.sh` | Ancien cron quotidien, conservé pour compatibilité. |
| `serve.sh` | Sert `output/` en HTTP pour que TryPost télécharge les MP4. |
| `compare3.sh` | Compare les rendus des différentes versions du logo. |
| `state.json` | Progression Coran. |
| `prayer_schedule.json` | Planning des 5 prières. |
| `.env` | Secrets (ignoré par Git). |

## Dépendances système

- Python 3.10 ou plus.
- FFmpeg avec `libass`, `libx264`, `aac`.
- Polices :
  - `/usr/share/fonts/opentype/fonts-hosny-amiri/Amiri-Regular.ttf`
  - `/usr/share/fonts/opentype/fonts-hosny-amiri/Amiri-Bold.ttf`
  - `/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf`
  - `/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf`
- `flock` (util-linux).
- Accès réseau à Quran.com, jsDelivr et EveryAyah.

## Sources de données

| Donnée | Source |
|---|---|
| Texte arabe (Uthmani) | `https://api.quran.com/api/v4` |
| Traduction française | API Quran.com, traduction Hamidullah |
| Timings mot par mot | `https://cdn.jsdelivr.net/gh/spa5k/quran-timings-api@main/data/api/reciters/<slug>/surahs/<surah>/timings.json` |
| Audio verset par verset | `https://everyayah.com/data/<dossier>/<surah><ayah>.mp3` |
| Fonds nature | Mixkit, téléchargés une fois dans `backgrounds/` |
| Horaires de prière | `https://api.aladhan.com/v1/timingsByCity` |

## Commande principale

```bash
python3 quran_reels_v2.py \
  --duration 30 \
  --surah 2 --ayah 8 --verses 2 \
  --reciter yasser_ad_dussary \
  --background nature_forest \
  --subtitle-style gold_word \
  --logo gif2
```

### Options

| Option | Valeurs | Défaut |
|---|---|---|
| `--surah` | 1 à 114 | progression courante |
| `--ayah` | entier | progression courante |
| `--verses` | 1 à 10 | calculé selon la durée |
| `--duration` | `30`, `60` | `30` |
| `--reciter` | voir ci-dessous | `yasser_ad_dussary` |
| `--background` | nom d'un fond | aléatoire |
| `--subtitle-style` | `gold_word`, `gold_line`, `gold_verse`, `minimal`, `bilingual` | `gold_word` |
| `--logo` | `png`, `gif`, `gif2`, `video`, `none` | `png` |
| `--publish` | drapeau | faux |
| `--schedule-at` | ISO 8601 | immédiat |
| `--list-state` | drapeau | — |

### Récitateurs

| ID | Nom | Timings | EveryAyah |
|---|---|---|---|
| `yasser_ad_dussary` | Yasser Ad-Dussary | `eya_yasser_ad_dussary_128kbps` | `Yasser_Ad-Dussary_128kbps` |
| `abdurrahmaan_as_sudais` | Abdurrahmaan As-Sudais | `eya_abdurrahmaan_as_sudais_192kbps` | `Abdurrahmaan_As-Sudais_192kbps` |
| `muhsin_al_qasim` | Muhsin Al Qasim | `eya_muhsin_al_qasim_192kbps` | `Muhsin_Al_Qasim_192kbps` |
| `abu_bakr_ash_shaatree` | Abu Bakr Ash-Shaatree | `qcom_abu_bakr_al_shatri` | `Abu_Bakr_Ash-Shaatree_128kbps` |
| `hani_ar_rifai` | Hani Ar-Rifai | `qcom_hani_ar_rifai` | `Hani_Rifai_192kbps` |
| `mohamed_al_tablawi` | Mohamed Al-Tablawi | `qcom_mohamed_al_tablawi` | `Mohammad_al_Tablaway_128kbps` |
| `saud_al_shuraim` | Saud Al-Shuraim | `quranicaudio_sa3ood_al_shuraym` | `Saood_ash-Shuraym_128kbps` |

Chaque récitateur doit avoir à la fois des timings mot par mot et des MP3 EveryAyah. Sans timings, le surlignage mot par mot est impossible.

### Fonds nature

| ID | Nom |
|---|---|
| `nature_waves` | Vagues |
| `nature_sea_rocks` | Mer et rochers |
| `nature_flowers` | Fleurs |
| `nature_waterfall` | Cascade |
| `nature_forest` | Forêt |
| `nature_bay` | Baie |
| `nature_clouds` | Nuages |
| `nature_meadow` | Prairie |

Règle éditoriale : pas d'être humain, pas d'espace, pas de galaxie, pas de lune. Uniquement la nature.

### Styles de sous-titres

| Style | Comportement |
|---|---|
| `gold_word` | Un seul mot en or à la fois, synchronisé sur le timing. Traduction FR en bas. Watermark. |
| `gold_line` | Toute la ligne en cours passe en or, mot après mot. Traduction FR. Watermark. |
| `gold_verse` | Tout le verset est doré pendant toute sa durée. Traduction FR. Watermark. |
| `minimal` | Arabe blanc, sans traduction et sans watermark. |
| `bilingual` | Mot par mot + traduction française plus grande (48 px au lieu de 38 px). |

### Logos

| ID | Fichier | Rendu |
|---|---|---|
| `png` | `logo_transparent.png` | Image fixe, 200 px de large. |
| `gif2` | `logo_new_45s.webm` | Vidéo VP9 avec alpha, 240 px, contraste renforcé. |
| `gif` | `logo_anim_45s.webm` | Vidéo VP9 avec alpha, 200 px. |
| `video` | `logo_video.mp4` | MP4 en boucle avec `colorkey`. |
| `none` | — | Aucun logo. |

## Déroulé d'un rendu

```
1. Lire l'état (surah, ayah)
2. Télécharger le texte arabe + la traduction (Quran.com)
3. Télécharger les timings mot par mot (jsDelivr)
4. Télécharger l'audio du récitateur (EveryAyah)
5. Choisir un fond nature
6. Calculer les timings absolus de chaque mot
7. Construire le fichier ASS (sous-titres + watermark)
8. Construire le filtre FFmpeg (fond, overlay logo, sous-titres)
9. Encoder le MP4 (1080x1920, H.264, AAC)
10. Écrire le sidecar JSON
11. Mettre à jour state.json
12. Publier si demandé
```

## Format de sortie

| Propriété | Valeur |
|---|---|
| Résolution | 1080 × 1920 (9:16) |
| Codec vidéo | H.264, `yuv420p` |
| Codec audio | AAC |
| Durée | 30 à 34 s, jusqu'à 60 s |
| Débit | environ 8 à 10 Mb/s |
| Taille | 30 à 80 Mo |

## Rendu ASS

Le fichier ASS définit trois styles :

```
Style: Verse,Amiri,150,...,Alignment=2
Style: Translation,DejaVu Sans,38,...,Alignment=2
Style: Watermark,Amiri,80,...,Alignment=8
```

- `Verse` : texte arabe centré, marges de 430 px en bas.
- `Translation` : traduction française, 150 px du bas.
- `Watermark` : `بدر القرآنيات` en haut.

### RTL

Le texte arabe est rendu avec `libass`. Pour un rendu correct :

- les mots sont placés un par un avec `\pos(x,y)` ;
- l'ordre des mots dans chaque ligne est inversé avant écriture ;
- le mot actif est coloré en or (`&H0000D7FF` en BGR) ;
- les autres restent blancs.

Cette approche évite les erreurs de bidi de libass.

### Taille de police adaptative

La police diminue si le verset est long, jusqu'à un plancher lisible. Si le texte reste trop long, le pipeline découpe le verset en extraits contigus plutôt que de le tronquer.

## Gestion des versets longs

- Un verset est découpé en extraits de 30 ou 60 s.
- Chaque extrait commence exactement au mot suivant.
- `partial: true` et `word_start` / `word_end` sont enregistrés.
- Le sidecar et l'historique conservent ces informations.
- Aucun mot n'est sauté, aucun mot n'est coupé.

## Reprise et idempotence

- `state.json` n'avance qu'après un rendu réussi.
- Un rendu échoué ne consomme pas de verset.
- Le verrou `flock` empêche deux rendus simultanés.
- `QURAN_KEEP_TEMP=1` conserve les fichiers temporaires pour le débogage.
- `QURAN_DRY_RUN=1` simule le rendu sans encoder.

## Vérification

```bash
python3 verify_reel.py output/reel_s002a008_1793648823.mp4
```

Contrôle :

- le fichier existe et est lisible ;
- la durée est cohérente ;
- la résolution est 1080 × 1920 ;
- l'audio est présent ;
- le codec est H.264/AAC.

## Publication

Voir [07-PUBLICATION-PLANNING.md](07-PUBLICATION-PLANNING.md).

## Débogage

| Symptôme | Cause probable | Solution |
|---|---|---|
| `Aucun timing trouvé` | Slug de récitateur invalide | Vérifier `timings_slug` dans `catalog.json`. |
| `Audio 404` | Dossier EveryAyah incorrect | Vérifier `everyayah_folder`. |
| Texte arabe à l'envers | Ordre des mots non inversé | Vérifier `_verse_state_text`. |
| Sous-titres absents | `libass` manquant | Réinstaller FFmpeg avec `--enable-libass`. |
| Logo noir | WebM sans alpha | Ajouter `-c:v libvpx-vp9` avant `-i`. |
| Rendu très long | Fond 4K ou CPU faible | Utiliser `nature_clouds` ou réduire la durée. |
| `Permission denied` sur output | Droits du dossier | `chmod 755 output`. |

## Variables d'environnement

Voir `.env.example` à la racine du dépôt.

| Variable | Rôle |
|---|---|
| `TRYPOST_BASE_URL` | URL de l'instance TryPost. |
| `TRYPOST_API_KEY` | Clé API TryPost. |
| `TRYPOST_FACEBOOK_PAGE_ID` | UUID de la page Facebook. |
| `TRYPOST_MEDIA_BASE_URL` | URL publique des vidéos. |
| `PUBLISH_DELAY_MINUTES` | Délai minimum avant publication (2 min). |
| `PEXELS_API_KEY` | Optionnel, fonds supplémentaires. |
| `PRAYER_CITY` | Ville des horaires. |
| `PRAYER_COUNTRY` | Pays. |
| `PRAYER_METHOD` | Méthode Aladhan (21 = Habous). |
| `PRAYER_OFFSET_MIN` | Marge avant l'adhan. |
| `PRAYER_TZ` | Fuseau (`Africa/Casablanca`). |
| `PRAYER_LOOKAHEAD_MIN` | Fenêtre de traitement (120 min). |
| `PRAYER_MIN_LEAD_MIN` | Délai minimum avant génération (12 min). |
| `PRAYER_GRACE_MIN` | Tolérance après l'heure (5 min). |
| `PRAYER_REEL_DURATION` | Durée cible (30 ou 60). |
| `PRAYER_LOGO` | Logo utilisé par le cron. |
