# 11 — Tests et critères d'acceptation

## Philosophie

Le pipeline produit du contenu religieux publié publiquement. Une erreur de texte, de timing ou de publication est visible immédiatement. Les tests doivent donc couvrir en priorité :

1. l'intégrité du texte arabe (aucun mot perdu, RTL correct) ;
2. la synchronisation audio / surlignage ;
3. la publication au bon moment ;
4. la robustesse de l'application mobile.

## Tests du pipeline

### Tests unitaires

| Test | Vérifie |
|---|---|
| `test_state_advance` | Le pointeur avance au bon mot après un rendu. |
| `test_state_no_advance_on_failure` | Un échec ne consomme pas de verset. |
| `test_verse_split` | Un verset long est découpé sans perte de mot. |
| `test_reciter_catalog` | Les 7 récitateurs ont timings + EveryAyah. |
| `test_subtitle_styles` | Les 5 styles produisent un ASS valide. |
| `test_logo_none` | `--logo none` ne produit aucune entrée logo. |
| `test_caption_build` | La légende contient référence, arabe, traduction, récitateur. |
| `test_ass_escape` | Les caractères spéciaux ASS sont échappés. |
| `test_wrap_latin` | La traduction est découpée proprement. |
| `test_surah_rollover` | Après 114:6 on revient à 1:1. |

### Tests d'intégration

| Test | Vérifie |
|---|---|
| `test_render_short_verse` | Rendu d'un verset court en moins de 3 min. |
| `test_render_long_verse` | Rendu d'un verset long avec découpage. |
| `test_render_all_reciters` | Un rendu par récitateur. |
| `test_render_all_styles` | Un rendu par style. |
| `test_render_all_logos` | Un rendu par logo, dont `none`. |
| `test_verify_reel` | Le MP4 passe `verify_reel.py`. |

### Tests du planificateur

| Test | Vérifie |
|---|---|
| `test_plan_creation` | 5 créneaux créés pour une date. |
| `test_offset` | Publication = adhan − offset. |
| `test_single_reel_per_tick` | Un seul créneau traité par tick. |
| `test_missed_detection` | Un créneau passé devient `missed`. |
| `test_lookahead` | Un créneau trop lointain n'est pas traité. |
| `test_flock` | Deux ticks simultanés ne produisent qu'un rendu. |
| `test_timezone` | Les changements d'heure sont gérés. |

## Tests de l'API

Avec `pytest` et `fastapi.testclient.TestClient`.

| Test | Vérifie |
|---|---|
| `test_health` | `GET /api/health` renvoie `ok`. |
| `test_config` | Le catalogue contient 7 récitateurs, 8 fonds, 5 styles, 5 logos. |
| `test_render_creates_job` | `POST /api/reels/render` renvoie `202` + `job_id`. |
| `test_job_progress` | `GET /api/jobs/{id}` progresse jusqu'à `completed`. |
| `test_job_failure` | Un rendu invalide renvoie `failed` avec `error`. |
| `test_schedule_default` | `GET /api/schedules/{date}` crée 5 créneaux. |
| `test_schedule_offset` | `PUT` recalcule les heures. |
| `test_slot_toggle` | `PATCH` active/désactive un créneau. |
| `test_publish_without_trypost` | `POST /publish` renvoie `409` si TryPost absent. |
| `test_share_links` | `GET /share` renvoie les 2 liens. |
| `test_auth_required` | `401` si `API_TOKEN` est défini et absent. |
| `test_demo_mode` | `DEMO_MODE=1` simule un rendu. |

## Tests de l'application mobile

### Tests manuels (checklist)

- [ ] L'app s'ouvre sans serveur et affiche le badge **Démo**.
- [ ] L'app s'ouvre avec l'API et affiche le badge **Connecté**.
- [ ] Le Studio met à jour l'aperçu à chaque changement.
- [ ] Changer le fond change l'image de l'aperçu.
- [ ] Changer le style change le surlignage de l'aperçu.
- [ ] Choisir « Sans logo » retire le logo de l'aperçu.
- [ ] Choisir « Minimal » retire la traduction et le watermark.
- [ ] Le bouton Générer est désactivé pendant le rendu.
- [ ] La progression avance et le Reel apparaît dans la bibliothèque.
- [ ] Le partage natif s'ouvre sur mobile.
- [ ] Le lien Facebook s'ouvre dans un nouvel onglet.
- [ ] Le lien WhatsApp est correctement encodé.
- [ ] La copie met légende + URL dans le presse-papiers.
- [ ] Le planning affiche 5 créneaux.
- [ ] Désactiver un créneau change son apparence.
- [ ] Enregistrer le planning affiche une confirmation.
- [ ] Changer de date recharge le planning.
- [ ] L'écran tient à 360 px sans scroll horizontal.
- [ ] Le texte arabe est lisible et RTL.
- [ ] La feuille de partage se ferme avec Échap.
- [ ] Aucun écran blanc en cas d'erreur API.

### Tests automatisés (Playwright)

| Test | Vérifie |
|---|---|
| `home_loads` | L'accueil affiche la prochaine publication. |
| `studio_preview_updates` | Changer un fond met à jour l'aperçu. |
| `demo_generation` | La génération simulée ajoute un Reel. |
| `share_sheet` | La feuille s'ouvre et se ferme. |
| `schedule_toggle` | Le switch change l'état. |
| `mobile_viewport` | Aucun scroll horizontal à 360 px. |

## Critères d'acceptation produit

### Application

1. `npm install && npm run build` réussit.
2. L'app fonctionne en mode démo sans serveur.
3. L'app se connecte à l'API et affiche les vraies données.
4. Générer un Reel crée un job, affiche la progression et ajoute le Reel.
5. Le partage propose natif, Facebook, WhatsApp et copie.
6. Le planning affiche les 5 prières avec heures d'adhan et de publication.
7. Chaque créneau peut être activé/désactivé.
8. L'interface est utilisable à 360 px.
9. Aucun secret dans le code client.
10. Le texte arabe est toujours RTL et complet.

### Pipeline

1. Un Reel dure entre 30 et 60 secondes.
2. La résolution est 1080 × 1920.
3. L'audio est présent et synchronisé.
4. Le mot actif est en or au bon moment.
5. La traduction correspond au verset.
6. Le logo est incrusté ou absent selon l'option.
7. Le fichier est lisible par FFmpeg et par Facebook.
8. Le sidecar contient les paramètres exacts.

### Publication

1. 5 Reels par jour, un avant chaque adhan.
2. Publication à `adhan − 10 min`, tolérance ±2 min.
3. Aucune double publication.
4. Aucun verset sauté.
5. Aucun créneau `failed` non traité.
6. L'URL Facebook est enregistrée.

## Commandes

```bash
# pipeline
python3 -m py_compile pipeline/*.py
python3 pipeline/verify_reel.py pipeline/output/<fichier>.mp4

# api
cd backend
python3 -m py_compile app.py
pytest -q

# mobile
cd mobile
npm run build
npm run dev

# e2e
npx playwright test
```

## Jeu de données de test

Utiliser des versets courts et longs :

| Cas | Référence | Pourquoi |
|---|---|---|
| Très court | 1:1 | Vérifie le minimum. |
| Court | 2:8–9 | Cas de production. |
| Moyen | 2:30 | Verset classique. |
| Long | 2:282 | Verset le plus long, découpage. |
| Changement de sourate | 114:6 | Fin du Coran, retour à 1:1. |
| Caractères spéciaux | versets avec sajdah | Vérifie l'échappement ASS. |

## Rapport de bug

Un rapport utile contient :

- la référence du verset ;
- le récitateur ;
- le style ;
- le logo ;
- la durée ;
- le fichier MP4 ou une capture ;
- la ligne de log exacte ;
- l'heure ;
- le fuseau.
