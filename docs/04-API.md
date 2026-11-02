# 04 — Contrat d'API

Base URL : `https://<serveur>/api`

En développement : `http://localhost:8000/api`

Authentification optionnelle : si `API_TOKEN` est défini côté serveur, ajouter l'en-tête :

```
Authorization: Bearer <API_TOKEN>
```

Toutes les réponses sont en JSON. Les dates sont en ISO 8601 UTC (`2026-11-03T05:12:00Z`) sauf mention contraire.

## Erreurs

| Code | Signification |
|---|---|
| `400` | Requête invalide. |
| `401` | Token manquant ou invalide. |
| `404` | Ressource introuvable. |
| `409` | Conflit (TryPost non configuré, Reel déjà publié...). |
| `422` | Validation Pydantic échouée. |
| `502` | Échec d'un service externe (TryPost, Aladhan...). |

Format :

```json
{ "detail": "Message lisible par l'utilisateur." }
```

---

## GET /api/health

Vérifie que le serveur répond.

```json
{
  "status": "ok",
  "version": "1.0.0",
  "demo_mode": false,
  "pipeline": true,
  "time": "2026-11-02T20:00:00Z"
}
```

## GET /api/config

Renvoie tout le catalogue nécessaire à l'application.

```json
{
  "reciters": [
    {
      "id": "yasser_ad_dussary",
      "name": "Yasser Ad-Dussary",
      "arabic_name": "ياسر الدوسري",
      "timings_slug": "eya_yasser_ad_dussary_128kbps",
      "everyayah_folder": "Yasser_Ad-Dussary_128kbps",
      "default": true
    }
  ],
  "backgrounds": [
    {
      "id": "nature_forest",
      "name": "Forêt",
      "kind": "video",
      "thumb": "/assets/backgrounds/nature_forest.jpg",
      "source": "https://assets.mixkit.co/videos/50847/50847-1080.mp4"
    }
  ],
  "subtitle_styles": [
    {
      "id": "gold_word",
      "name": "Surlignage mot par mot (or)",
      "description": "Le mot récité passe en or.",
      "translation": true,
      "watermark": true
    }
  ],
  "logos": [
    { "id": "gif2", "name": "Logo animé", "kind": "video", "file": "logo_new_45s.webm" }
  ],
  "durations": [30, 60],
  "prayers": [
    { "id": "Fajr", "name": "Fajr", "arabic_name": "الفجر" }
  ],
  "defaults": {
    "reciter": "yasser_ad_dussary",
    "background": "nature_forest",
    "subtitle_style": "gold_word",
    "logo": "gif2",
    "duration": 30,
    "verse_count": 2,
    "translation": true,
    "watermark": "بدر القرآنيات",
    "city": "Casablanca",
    "country": "Morocco",
    "method": 21,
    "offset_min": 10
  },
  "settings": {
    "page_name": "Badr Quran — بدر القرآنيات",
    "facebook_page_id": "00000000-0000-0000-0000-000000000000",
    "city": "Casablanca",
    "country": "Morocco",
    "method": 21,
    "offset_min": 10
  },
  "demo_mode": false,
  "media_base_url": "https://media.example.com"
}
```

## GET /api/state

Progression Coran.

```json
{
  "surah": 2,
  "ayah": 30,
  "total_generated": 22,
  "mode": "prayer_5x",
  "history": []
}
```

## GET /api/verses?count=5

Aperçu indicatif des prochains versets.

```json
{
  "items": [
    { "surah": 2, "ayah": 30, "label": "2:30" },
    { "surah": 2, "ayah": 31, "label": "2:31" }
  ],
  "note": "Aperçu indicatif ; le pipeline gère les changements de sourate."
}
```

## GET /api/reels

Liste des Reels.

```json
{
  "items": [
    {
      "id": "reel_s002a008_1793648823",
      "filename": "reel_s002a008_1793648823.mp4",
      "public_url": "https://media.example.com/reel_s002a008_1793648823.mp4",
      "thumbnail": "/assets/backgrounds/nature_forest.jpg",
      "created_at": "2026-11-02T19:51:28Z",
      "duration": 30.6,
      "surah": 2,
      "ayah_start": 8,
      "ayah_end": 9,
      "surah_name": "Al-Baqarah",
      "surah_name_arabic": "البقرة",
      "arabic_text": "وَمِنَ ٱلنَّاسِ...",
      "translation": "Il est des gens qui disent...",
      "caption": "📖 Sourate Al-Baqarah...",
      "status": "published",
      "publish": {
        "post_id": "post_demo_0001",
        "url": "https://www.facebook.com/reel/0000000000000000"
      }
    }
  ]
}
```

## GET /api/reels/{id}

Renvoie un Reel.

## DELETE /api/reels/{id}

Supprime le MP4 et son sidecar.

```json
{ "deleted": "reel_s002a008_1793648823" }
```

## POST /api/reels/render

Crée un job de rendu.

### Requête

```json
{
  "surah": 2,
  "ayah": 8,
  "verse_count": 2,
  "duration": 30,
  "reciter": "yasser_ad_dussary",
  "background": "nature_forest",
  "subtitle_style": "gold_word",
  "logo": "gif2",
  "translation": true,
  "watermark": "بدر القرآنيات",
  "publish": false,
  "schedule_at": null
}
```

### Réponse `202`

```json
{ "job_id": "job_ab12cd34ef56", "status": "queued" }
```

## GET /api/jobs/{id}

État du job.

```json
{
  "id": "job_ab12cd34ef56",
  "type": "render",
  "status": "running",
  "progress": 45,
  "created_at": "2026-11-02T20:00:00Z",
  "started_at": "2026-11-02T20:00:01Z",
  "log": ["Téléchargement des timings...", "Assemblage FFmpeg..."],
  "request": {}
}
```

`status` : `queued`, `running`, `completed`, `failed`.

En cas de succès :

```json
{
  "status": "completed",
  "progress": 100,
  "reel_id": "reel_s002a008_1793648823",
  "duration_seconds": 84.2,
  "finished_at": "2026-11-02T20:01:25Z"
}
```

En cas d'échec :

```json
{
  "status": "failed",
  "error": "Le pipeline a échoué (code 1)",
  "log": ["..."]
}
```

## GET /api/jobs

50 derniers jobs, du plus récent au plus ancien.

## POST /api/reels/{id}/publish

Publie ou programme un Reel.

### Requête

```json
{
  "when": "2026-11-03T05:12:00Z",
  "platforms": ["facebook"],
  "caption_override": null
}
```

`when` à `null` publie immédiatement (avec le délai TryPost de 2 minutes).

### Réponse

```json
{
  "reel_id": "reel_s002a008_1793648823",
  "status": "scheduled",
  "post_id": "post_demo_0001",
  "scheduled_at": "2026-11-03T05:12:00Z",
  "url": null
}
```

Erreurs :

- `409` si TryPost n'est pas configuré ;
- `502` si TryPost refuse la publication.

## GET /api/reels/{id}/share

Renvoie les liens de partage.

```json
{
  "reel_id": "reel_s002a008_1793648823",
  "public_url": "https://media.example.com/reel_s002a008_1793648823.mp4",
  "caption": "📖 Sourate Al-Baqarah...",
  "facebook_share_url": "https://www.facebook.com/sharer/sharer.php?u=...",
  "whatsapp_url": "https://wa.me/?text=..."
}
```

## GET /api/schedules/{date}

`date` au format `YYYY-MM-DD`.

```json
{
  "date": "2026-11-03",
  "city": "Casablanca",
  "country": "Morocco",
  "method": 21,
  "offset_min": 10,
  "enabled": true,
  "slots": [
    {
      "prayer": "Fajr",
      "prayer_ar": "الفجر",
      "adhan": "2026-11-03T05:22:00+01:00",
      "publish_at": "2026-11-03T05:12:00Z",
      "publish_at_local": "2026-11-03T05:12:00+01:00",
      "enabled": true,
      "status": "scheduled",
      "reel_id": "reel_s001a007_1793638698",
      "post_id": "post_demo_0002",
      "platform_url": null,
      "error": null
    }
  ],
  "updated_at": "2026-11-02T20:00:00Z"
}
```

## PUT /api/schedules/{date}

Recalcule le planning du jour.

### Requête

```json
{
  "enabled": true,
  "offset_min": 10,
  "city": "Casablanca",
  "country": "Morocco",
  "method": 21,
  "custom_times": {
    "Fajr": "05:21",
    "Dhuhr": "12:19"
  }
}
```

`custom_times` est optionnel et permet de forcer une heure par prière.

## PATCH /api/schedules/{date}/slots/{prayer}

Active ou désactive un créneau.

```json
{ "enabled": false }
```

## GET /api/settings

```json
{
  "page_name": "Badr Quran — بدر القرآنيات",
  "facebook_page_id": "00000000-0000-0000-0000-000000000000",
  "defaults": {},
  "city": "Casablanca",
  "country": "Morocco",
  "method": 21,
  "offset_min": 10
}
```

## PUT /api/settings

Met à jour partiellement les réglages.

## GET /api/analytics/summary

```json
{
  "reels_total": 22,
  "published_total": 19,
  "rendered_total": 3,
  "last_published_at": "2026-11-02T19:51:28Z",
  "recent": []
}
```

## Fichiers statiques

- `/media/<fichier>.mp4` : vidéos générées.
- `/assets/backgrounds/<id>.jpg` : vignettes des fonds.
- `/assets/logo/...` : logos.

En production, ces fichiers peuvent être servis par un serveur séparé (pm2 `reels-viewer` sur le port 3200) et `PUBLIC_MEDIA_BASE_URL` doit pointer vers ce serveur.

## Notes d'implémentation

- Le backend de référence stocke les jobs et les Reels dans `backend/data/*.json`.
- Il exécute le pipeline via `subprocess` et lit le fichier de sortie.
- Le mode `DEMO_MODE=1` simule les rendus.
- La publication réutilise `publish_to_trypost` du pipeline pour garantir le même comportement que le cron.
