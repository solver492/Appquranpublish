# 05 — Modèle de données

Tous les objets sont en JSON, en `camelCase` côté TypeScript et `snake_case` côté API. Les dates sont en ISO 8601.

## Catalog

Le catalogue décrit tout ce que l'utilisateur peut choisir. Il est renvoyé par `GET /api/config` et stocké dans `backend/catalog.json`.

```ts
type Catalog = {
  reciters: Reciter[];
  backgrounds: Background[];
  subtitle_styles: SubtitleStyle[];
  logos: LogoOption[];
  durations: number[];
  prayers: Prayer[];
  defaults: Defaults;
  settings?: Settings;
  demo_mode?: boolean;
};
```

### Reciter

```ts
type Reciter = {
  id: string;              // "yasser_ad_dussary"
  name: string;            // "Yasser Ad-Dussary"
  arabic_name?: string;    // "ياسر الدوسري"
  timings_slug: string;    // "eya_yasser_ad_dussary_128kbps"
  everyayah_folder: string;// "Yasser_Ad-Dussary_128kbps"
  default?: boolean;
};
```

### Background

```ts
type Background = {
  id: string;       // "nature_forest"
  name: string;     // "Forêt"
  kind: "video" | "image";
  thumb: string;    // "/assets/backgrounds/nature_forest.jpg"
  source?: string;  // URL Mixkit du MP4
};
```

### SubtitleStyle

```ts
type SubtitleStyle = {
  id: "gold_word" | "gold_line" | "gold_verse" | "minimal" | "bilingual";
  name: string;
  description: string;
  translation: boolean;
  watermark: boolean;
};
```

### LogoOption

```ts
type LogoOption = {
  id: "png" | "gif2" | "gif" | "video" | "none";
  name: string;
  kind: "image" | "video" | "none";
  file: string | null;
};
```

### Defaults

```ts
type Defaults = {
  reciter: string;         // "yasser_ad_dussary"
  background: string;      // "nature_forest"
  subtitle_style: string;  // "gold_word"
  logo: string;            // "gif2"
  duration: 30 | 60;
  verse_count: number;     // 2
  translation: boolean;    // true
  watermark: string;       // "بدر القرآنيات"
  city: string;            // "Casablanca"
  country: string;         // "Morocco"
  method: number;          // 21
  offset_min: number;      // 10
};
```

## RenderSpec

Ce que l'application envoie pour générer un Reel.

```ts
type RenderSpec = {
  surah?: number;          // 1..114
  ayah?: number;           // >= 1
  verse_count?: number;    // 1..10
  duration: 30 | 60;
  reciter: string;
  background: string;
  subtitle_style: string;
  logo: string;
  translation: boolean;
  watermark: string;
  publish?: boolean;
  schedule_at?: string | null;
};
```

## Reel

```ts
type Reel = {
  id: string;                 // "reel_s002a008_1793648823"
  filename: string;           // "reel_s002a008_1793648823.mp4"
  public_url: string;         // URL HTTP du MP4
  thumbnail: string;          // "/assets/backgrounds/nature_forest.jpg"
  created_at: string;
  duration?: number;          // secondes, ex. 30.63
  surah?: number;
  ayah_start?: number;
  ayah_end?: number;
  surah_name?: string;        // "Al-Baqarah"
  surah_name_arabic?: string; // "البقرة"
  arabic_text?: string;
  translation?: string;
  caption?: string;
  status: "rendered" | "scheduled" | "published" | "failed";
  publish?: {
    post_id?: string;
    url?: string;
    scheduled_at?: string;
  } | null;
  spec?: RenderSpec;
};
```

### Cycle de vie

```
rendu (rendered)
   │ POST /publish avec when
   ├──► programmé (scheduled)
   │        │ TryPost publie à l'heure
   │        ▼
   └──► publié (published)
```

Un Reel `published` conserve son `post_id` et son `url`. Un Reel `failed` conserve l'erreur dans `publish`.

## Job

```ts
type Job = {
  id: string;              // "job_ab12cd34ef56"
  type: "render";
  status: "queued" | "running" | "completed" | "failed";
  progress: number;        // 0..100
  created_at: string;
  started_at?: string;
  finished_at?: string;
  duration_seconds?: number;
  reel_id?: string;
  error?: string;
  log: string[];
  request?: RenderSpec;
  command?: string[];
};
```

### Règles

- Un job est créé immédiatement, avant le rendu.
- `progress` est monotone.
- Le job ne dépasse jamais 30 minutes (timeout).
- En cas de succès, `reel_id` pointe vers le Reel créé.
- En cas d'échec, `error` contient un message lisible et `log` les 120 dernières lignes.

## Schedule

```ts
type Schedule = {
  date: string;        // "2026-11-03"
  city: string;
  country: string;
  method: number;
  offset_min: number;
  enabled: boolean;
  slots: Slot[];
  updated_at?: string;
};
```

### Slot

```ts
type Slot = {
  prayer: "Fajr" | "Dhuhr" | "Asr" | "Maghrib" | "Isha";
  prayer_ar: string;         // "الفجر"
  adhan: string;             // ISO avec fuseau local
  publish_at: string;        // ISO UTC
  publish_at_local: string;  // ISO avec fuseau local
  enabled: boolean;
  status: "pending" | "scheduled" | "published" | "missed" | "failed";
  reel_id?: string | null;
  post_id?: string | null;
  platform_url?: string | null;
  error?: string | null;
  attempts?: number;
};
```

### États d'un slot

| Statut | Signification |
|---|---|
| `pending` | Créneau prévu, rien n'a encore été généré. |
| `scheduled` | Reel généré et programmé sur TryPost. |
| `published` | TryPost a publié, l'URL est disponible. |
| `missed` | L'heure est passée sans publication. |
| `failed` | Erreur de génération ou de publication. |

## Settings

```ts
type Settings = {
  page_name: string;         // "Badr Quran — بدر القرآنيات"
  facebook_page_id?: string; // UUID de la page dans TryPost
  defaults: Defaults;
  city: string;
  country: string;
  method: number;
  offset_min: number;
};
```

## State (progression Coran)

`pipeline/state.json` est la source de vérité du pipeline.

```json
{
  "surah": 2,
  "ayah": 10,
  "total_generated": 25,
  "logo_default": "gif2",
  "mode": "prayer_5x",
  "prayer_city": "Casablanca",
  "prayer_country": "Morocco",
  "prayer_offset_min": 10,
  "reset_at": "2026-11-02T16:21:45.998466+00:00",
  "history": [
    {
      "surah": 2,
      "ayah_start": 8,
      "ayah_end": 9,
      "surahs": [
        { "surah": 2, "name": "Al-Baqarah", "name_arabic": "البقرة", "ayah_start": 8, "ayah_end": 9 }
      ],
      "verse_count": 2,
      "duration": 30.631501,
      "file": "reel_s002a008_1793648823.mp4",
      "partial": false,
      "word_start": 0,
      "word_end": 9,
      "generated_at": "2026-11-02 19:48:10"
    }
  ]
}
```

### Champs

| Champ | Rôle |
|---|---|
| `surah` | Prochaine sourate à traiter. |
| `ayah` | Prochain verset de départ. |
| `total_generated` | Nombre total de Reels produits. |
| `mode` | `manual` ou `prayer_5x`. |
| `logo_default` | Logo par défaut du pipeline. |
| `history` | Historique des 42 derniers rendus. |

### Règle de progression

- Le pointeur avance au mot suivant de la récitation, pas seulement au verset.
- Un verset trop long est découpé en extraits contigus : `partial: true`, `word_start`, `word_end`.
- Aucun mot n'est sauté, aucun mot n'est coupé.
- Après 114:6, le pointeur revient à 1:1.

## PrayerSchedule (fichier pipeline)

`pipeline/prayer_schedule.json` conserve les plannings par jour.

```json
{
  "city": "Casablanca",
  "country": "Morocco",
  "method": "21",
  "offset_min": 10,
  "days": {
    "2026-11-02": {
      "date": "2026-11-02",
      "timezone": "Africa/Casablanca",
      "city": "Casablanca",
      "country": "Morocco",
      "slots": [
        {
          "prayer": "Fajr",
          "prayer_ar": "الفجر",
          "adhan": "2026-11-02T05:21:00+00:00",
          "publish_at": "2026-11-02T05:11:00Z",
          "publish_at_local": "2026-11-02T05:11:00+00:00",
          "status": "published",
          "file": "reel_s002a002_1793638698.mp4",
          "post_id": "post_demo_0002",
          "error": null,
          "attempts": 1
        }
      ]
    }
  }
}
```

Le pipeline conserve plusieurs jours pour permettre le rattrapage et l'audit.

## Sidecar d'un Reel

Chaque MP4 a un JSON du même nom dans `pipeline/output/`.

```json
{
  "result": {
    "success": true,
    "output_path": "/root/badr-quran-reels-studio/pipeline/output/reel_s002a008_1793648823.mp4",
    "output_name": "reel_s002a008_1793648823.mp4",
    "verse_count": 2,
    "duration": 30.631501,
    "file_size": 38397490,
    "surah": 2,
    "surah_name": "Al-Baqarah",
    "surah_name_arabic": "البقرة",
    "ayah_start": 8,
    "ayah_end": 9,
    "arabic_text": "...",
    "translation": "...",
    "spec": {
      "reciter": "yasser_ad_dussary",
      "background": "nature_forest",
      "subtitle_style": "gold_word",
      "logo": "gif2",
      "duration": 30
    }
  },
  "caption": "📖 Sourate Al-Baqarah...",
  "publish": {
    "success": true,
    "post_id": "post_demo_0001",
    "url": "https://www.facebook.com/reel/0000000000000000"
  }
}
```

Le sidecar permet de :

- reconstruire la bibliothèque sans base de données ;
- republier un Reel sans le regénérer ;
- afficher la légende exacte utilisée ;
- auditer les paramètres de rendu.

## Stockage côté API

| Fichier | Contenu |
|---|---|
| `backend/data/jobs.json` | Jobs de rendu. |
| `backend/data/reels.json` | Reels indexés. |
| `backend/data/schedules.json` | Plannings par date. |
| `backend/data/settings.json` | Réglages. |

Ces fichiers sont créés automatiquement et **ignorés par Git**.

## Validation

- `surah` entre 1 et 114.
- `ayah` supérieur ou égal à 1.
- `verse_count` entre 1 et 10.
- `duration` dans `{30, 60}`.
- `reciter`, `background`, `subtitle_style` et `logo` doivent exister dans le catalogue.
- `date` au format `YYYY-MM-DD`.
- `when` au format ISO 8601 avec fuseau ou `Z`.

L'API renvoie `422` si un champ est invalide.
