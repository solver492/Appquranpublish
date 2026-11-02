# 02 — Architecture

## Vue d'ensemble

```
┌──────────────────────────────┐
│  Application mobile (PWA)    │
│  React + Vite + TypeScript   │
│  - Studio                    │
│  - Planning                  │
│  - Bibliothèque              │
│  - Partage                   │
└──────────────┬───────────────┘
               │ HTTPS / JSON
               ▼
┌──────────────────────────────┐
│  API Badr Quran Studio       │
│  FastAPI (backend/app.py)    │
│  - /api/config               │
│  - /api/reels/render         │
│  - /api/jobs/{id}            │
│  - /api/schedules/{date}     │
│  - /api/reels/{id}/share     │
└──────────────┬───────────────┘
               │ subprocess
               ▼
┌──────────────────────────────┐
│  Pipeline Python             │
│  quran_reels_v2.py           │
│  - Quran.com API             │
│  - quran-timings-api         │
│  - EveryAyah                 │
│  - FFmpeg                    │
└──────────────┬───────────────┘
               │ MP4 + JSON
               ▼
┌──────────────────────────────┐
│  Stockage local              │
│  pipeline/output/            │
│  - reel_*.mp4                │
│  - reel_*.json (sidecar)     │
└──────────────┬───────────────┘
               │ URL publique
               ▼
┌──────────────────────────────┐
│  TryPost                     │
│  - crée le post              │
│  - status=scheduled          │
│  - publie sur Facebook       │
└──────────────────────────────┘
```

## Composants

### 1. Application mobile

- React 19, Vite 6, TypeScript.
- Aucune dépendance UI lourde : CSS maison, icônes SVG inline.
- État local : `useState` + `localStorage` pour l'URL de l'API.
- Mode démo : `mobile/src/mock.ts`.
- Partage : `navigator.share`, liens Facebook/WhatsApp, presse-papiers.
- Cible : écran de 360 px minimum, PWA installable.

### 2. API FastAPI

- Un seul fichier `backend/app.py`.
- Stockage JSON dans `backend/data/` (jobs, reels, schedules, settings).
- Exécution du pipeline via `subprocess` pour les rendus.
- Publication via la fonction `publish_to_trypost` du pipeline.
- CORS ouvert par défaut pour le développement.
- Authentification optionnelle par `API_TOKEN`.

### 3. Pipeline Python

- `quran_reels_v2.py` : génération d'un Reel.
- `prayer_reels.py` : planification 5 fois par jour.
- `verify_reel.py` : contrôle du rendu.
- `run_prayer.sh` : cron toutes les 10 minutes.
- `daily_reel.sh` : ancien cron quotidien, conservé pour compatibilité.

### 4. Stockage

- `pipeline/output/` : MP4 et sidecars JSON.
- `pipeline/backgrounds/` : fonds nature (non versionnés).
- `pipeline/temp/` : fichiers temporaires de rendu.
- `pipeline/state.json` : progression Coran.
- `pipeline/prayer_schedule.json` : planning du jour.

### 5. TryPost

- API REST auto-hébergée.
- Crée un post en `draft`, puis un `PUT` le passe en `scheduled`.
- Le scheduler TryPost publie à l'heure prévue.
- Retourne l'URL publique du Reel Facebook.

## Flux : générer un Reel

1. L'utilisateur choisit les options dans le Studio.
2. L'app envoie `POST /api/reels/render`.
3. L'API crée un job et lance un thread.
4. Le thread exécute :

```bash
python3 quran_reels_v2.py \
  --duration 30 \
  --surah 2 --ayah 8 --verses 2 \
  --reciter yasser_ad_dussary \
  --background nature_forest \
  --subtitle-style gold_word \
  --logo gif2
```

5. Le pipeline télécharge le texte, la traduction, les timings et l'audio.
6. FFmpeg assemble la vidéo.
7. Le pipeline écrit `output/reel_*.mp4` et `output/reel_*.json`.
8. L'API met à jour le job et enregistre le Reel.
9. L'app affiche la progression puis le Reel.

## Flux : publier un Reel

1. L'utilisateur appuie sur **Publier maintenant** ou attend le planning.
2. L'API appelle `publish_to_trypost`.
3. TryPost crée le post, le passe en `scheduled`.
4. TryPost publie sur la page Facebook.
5. L'URL du Reel est enregistrée dans le sidecar.

## Flux : planning quotidien

1. `prayer_reels.py --tick` tourne toutes les 10 minutes.
2. Il récupère les horaires Aladhan pour Casablanca (méthode 21).
3. Il crée 5 slots : Fajr, Dhuhr, Asr, Maghrib, Isha.
4. Chaque slot publie à `adhan - 10 min`.
5. Au plus un Reel est généré par tick.
6. Un verrou `flock` empêche deux générations simultanées.
7. Les versets avancent séquentiellement dans `state.json`.

## Sécurité

- Les clés ne sont jamais dans le code client.
- `pipeline/.env` est ignoré par Git.
- L'API doit être protégée par `API_TOKEN` si elle est exposée sur Internet.
- Restreindre `allow_origins` en production.
- Les vidéos publiques peuvent être servies par un serveur statique (pm2 `reels-viewer` sur le port 3200).
- Ne pas exposer `pipeline/.env`, `backend/data/` ni les logs.

## Contraintes de déploiement

- FFmpeg doit être installé sur le serveur.
- Les polices Amiri et DejaVu doivent être présentes.
- Le serveur doit pouvoir télécharger Quran.com, jsDelivr et EveryAyah.
- TryPost doit pouvoir télécharger l'URL publique de la vidéo.
- Le fuseau du serveur doit être cohérent avec `Africa/Casablanca`.

## Choix techniques

| Choix | Raison |
|---|---|
| React + Vite | Rapide, compatible AI Studio, PWA. |
| FastAPI | Python déjà présent, API simple, documentation auto. |
| JSON store | Pas de base de données à installer pour démarrer. |
| subprocess | Réutilise le pipeline existant sans le réécrire. |
| TryPost | Déjà en production, gère Facebook et le scheduler. |
| Sidecar JSON | Permet de republier sans regénérer. |
