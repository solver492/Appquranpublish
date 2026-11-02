# 10 — Assets, sources et licences

## Résumé

| Élément | Source | Licence |
|---|---|---|
| Texte coranique | Quran.com API v4 | Domaine public (texte) |
| Traduction française | Quran.com (Hamidullah) | Voir Quran.com |
| Timings mot par mot | `spa5k/quran-timings-api` | MIT (code), données issues de Quran.com |
| Audio des récitations | EveryAyah | Voir EveryAyah |
| Fonds vidéo nature | Mixkit | Licence Mixkit gratuite |
| Police Amiri | Fonts Hosny Amiri | SIL Open Font License 1.1 |
| Police DejaVu Sans | DejaVu Fonts | Licence DejaVu (libre) |
| Police Manrope | Google Fonts | SIL Open Font License 1.1 |
| Logos Badr Quran | Création propre | © Badr Quran |
| Code du dépôt | Ce dépôt | MIT |

## Texte coranique

- API : `https://api.quran.com/api/v4`
- Script : Uthmani.
- Le texte coranique lui-même n'est pas soumis au droit d'auteur.
- Les traductions ont leurs propres conditions : vérifier avant redistribution commerciale.

## Traduction française

- Traduction Muhammad Hamidullah, via Quran.com.
- Utilisée pour l'affichage dans la vidéo et la légende.
- Ne pas modifier le sens du texte.
- En cas de doute, citer la source dans la description.

## Timings mot par mot

- Dépôt : `https://github.com/spa5k/quran-timings-api`
- Accès : CDN jsDelivr.
- Chemin : `data/api/reciters/<slug>/surahs/<surah>/timings.json`
- Licence du code : MIT.
- Les données de timing proviennent de sources ouvertes ; vérifier les conditions avant un usage commercial à grande échelle.

## Audio

- Site : `https://everyayah.com`
- Un MP3 par verset.
- Dossiers utilisés :

| Récitateur | Dossier |
|---|---|
| Yasser Ad-Dussary | `Yasser_Ad-Dussary_128kbps` |
| Abdurrahmaan As-Sudais | `Abdurrahmaan_As-Sudais_192kbps` |
| Muhsin Al Qasim | `Muhsin_Al_Qasim_192kbps` |
| Abu Bakr Ash-Shaatree | `Abu_Bakr_Ash-Shaatree_128kbps` |
| Hani Ar-Rifai | `Hani_Rifai_192kbps` |
| Mohamed Al-Tablawi | `Mohammad_al_Tablaway_128kbps` |
| Saud Al-Shuraim | `Saood_ash-Shuraym_128kbps` |

- EveryAyah diffuse des récitations existantes. Les droits restent ceux des récitateurs et des producteurs.
- Pour un usage commercial, vérifier les autorisations.
- Le dépôt ne versionne aucun MP3.

## Fonds vidéo

- Site : `https://mixkit.co`
- Licence Mixkit gratuite : usage personnel et commercial autorisé, pas de revente du stock lui-même.
- Les fonds ne sont **pas** versionnés (trop volumineux).
- Utiliser `scripts/download-backgrounds.sh` pour les récupérer.

| ID | Nom | URL |
|---|---|---|
| `nature_waves` | Vagues | `https://assets.mixkit.co/videos/1164/1164-1080.mp4` |
| `nature_sea_rocks` | Mer et rochers | `https://assets.mixkit.co/videos/51506/51506-1080.mp4` |
| `nature_flowers` | Fleurs | `https://assets.mixkit.co/videos/1187/1187-1080.mp4` |
| `nature_waterfall` | Cascade | `https://assets.mixkit.co/videos/2213/2213-1080.mp4` |
| `nature_forest` | Forêt | `https://assets.mixkit.co/videos/50847/50847-1080.mp4` |
| `nature_bay` | Baie | `https://assets.mixkit.co/videos/1954/1954-1080.mp4` |
| `nature_clouds` | Nuages | `https://assets.mixkit.co/videos/26108/26108-1080.mp4` |
| `nature_meadow` | Prairie | `https://assets.mixkit.co/videos/4075/4075-1080.mp4` |

### Règles éditoriales

- Nature uniquement.
- Pas d'être humain.
- Pas d'espace, de galaxie ou de lune.
- Pas de texte, de logo ou de watermark dans le fond.
- Boucle propre, sans coupure visible.
- Résolution minimale 1080 × 1920.

## Vignettes

- Générées à partir des fonds avec FFmpeg.
- Format JPEG, largeur 270 px.
- Stockées dans `assets/backgrounds/` et `mobile/public/backgrounds/`.
- Versionnées, car légères (5 à 40 Ko).

## Logos

| Fichier | Format | Usage |
|---|---|---|
| `logo_transparent.png` | PNG alpha | Logo fixe |
| `logo_new_45s.webm` | WebM VP9 alpha | Logo animé recommandé |
| `logo_anim_45s.webm` | WebM VP9 alpha | Logo animé alternatif |
| `logo_video.mp4` | MP4 | Logo vidéo avec colorkey |
| `logo_anim.gif` | GIF | Ancienne version |
| `logo_new.jpg` | JPEG | Source |

- Les logos sont la propriété de la page Badr Quran.
- Ne pas les réutiliser pour une autre marque.
- Ils sont versionnés dans `assets/` car légers et nécessaires au rendu.

## Polices

### Amiri

- Auteur : Khaled Hosny.
- Licence : SIL Open Font License 1.1.
- Usage : texte arabe dans la vidéo et l'interface.
- Chemin serveur : `/usr/share/fonts/opentype/fonts-hosny-amiri/`.

### DejaVu Sans

- Licence DejaVu (libre, compatible GPL).
- Usage : traduction française dans la vidéo.
- Chemin serveur : `/usr/share/fonts/truetype/dejavu/`.

### Manrope

- Google Fonts.
- Licence : SIL Open Font License 1.1.
- Usage : interface de l'application mobile.
- Chargée via Google Fonts dans `index.html`.

## Services externes

| Service | Usage | Conditions |
|---|---|---|
| Quran.com API | Texte et traduction | Gratuit, pas de clé. |
| jsDelivr | CDN des timings | Gratuit, pas de clé. |
| EveryAyah | Audio | Gratuit, pas de clé. |
| Mixkit | Fonds | Gratuit, attribution appréciée. |
| Aladhan | Horaires de prière | Gratuit, pas de clé. |
| TryPost | Publication | Auto-hébergé, clé privée. |
| Facebook Graph | Publication via TryPost | Via TryPost. |

## Ce qui n'est pas versionné

| Dossier | Raison |
|---|---|
| `pipeline/backgrounds/` | 8 vidéos, plusieurs centaines de Mo. |
| `pipeline/output/` | Vidéos générées, 30 à 80 Mo chacune. |
| `pipeline/temp/` | Fichiers temporaires. |
| `old_renders/` | Archives. |
| `pipeline/.env` | Secrets. |
| `mobile/node_modules/` | Dépendances. |
| `mobile/dist/` | Build. |
| `backend/data/` | Données locales. |

## Attribution

Si tu publies ce projet ou une œuvre dérivée, crédite :

- **Badr Quran — بدر القرآنيات** pour les logos et la marque ;
- **Quran.com** pour le texte et la traduction ;
- **EveryAyah** pour les récitations ;
- **Mixkit** pour les fonds ;
- **Khaled Hosny** pour Amiri ;
- **Google** pour Manrope.

## Vérifications avant publication commerciale

1. Vérifier la licence de chaque fond utilisé.
2. Vérifier les droits des récitations.
3. Vérifier les conditions de la traduction.
4. Conserver les preuves de licence.
5. Ne pas revendre les fonds ou les audios tels quels.
6. Ne pas utiliser les logos Badr Quran pour une autre page.
