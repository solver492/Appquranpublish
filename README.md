# Badr Quran Studio — application Android

Application Android (Jetpack Compose) qui pilote réellement le pipeline de Reels Coran de la page
**Badr Quran — بدر القرآنيات** : génération de la vidéo, choix du récitateur, de l'arrière-plan,
du style de sous-titres et du logo, partage, publication Facebook et programmation quotidienne.

Contrairement à la maquette initiale, **tout passe par l'API réelle** : l'application envoie les
paramètres au serveur, suit la progression du rendu FFmpeg, puis récupère la vidéo produite.

## Ce que fait l'application

| Écran | Fonctions réelles |
|---|---|
| **Accueil** | Prochain créneau calculé depuis les horaires de prière du serveur, compte à rebours, statistiques (Reels générés, publiés, prochain verset), derniers Reels |
| **Studio** | Sourate, verset de départ, nombre de versets, durée 30/60 s, 7 récitateurs, 8 arrière-plans, 5 styles de sous-titres, 5 logos (dont « aucun »), traduction FR, filigrane, aperçu 9:16, génération avec progression en direct |
| **Planning** | 5 créneaux (Fajr, Dhuhr, Asr, Maghrib, Isha), heure de l'adhan et de publication, marge de sécurité réglable, activation/désactivation par créneau, navigation jour par jour |
| **Reels** | Bibliothèque réelle, filtres (tous / publiés / à publier), partage, publication immédiate |
| **Réglages** | URL de l'API, jeton d'accès, ville/pays, marge, état de la connexion |

## Architecture

```
Téléphone (APK)  ──HTTPS──►  API FastAPI (VPS)  ──►  pipeline FFmpeg  ──►  MP4
                                    │
                                    └──►  TryPost  ──►  page Facebook
```

- `app/` : application Android (Kotlin, Compose, OkHttp, `org.json` — aucune dépendance de sérialisation)
- `src/`, `index.html`, `public/` : export web d'AI Studio, conservé comme maquette de référence ;
  ce n'est pas l'application livrée
- `.github/workflows/build-apk.yml` : construit l'APK à chaque push sur `main`

Le code du serveur (pipeline + API FastAPI + documentation) se trouve dans le dépôt privé
`solver492/badr-quran-reels-studio` (accès sur demande).

## Installation

1. Télécharger l'APK de la dernière version :
   [**badr-quran-studio-debug.apk**](https://github.com/solver492/Appquranpublish/releases/latest/download/badr-quran-studio-debug.apk)
   (autoriser « Installer des applications inconnues » pour le navigateur ou le gestionnaire de fichiers).
2. Lancer l'application, aller dans **Réglages**.
3. Vérifier l'URL de l'API — préremplie avec `https://badr.72-62-210-165.sslip.io/api` — puis coller le **jeton d'accès** fourni par l'administrateur.
4. Appuyer sur **Tester** : le voyant doit passer au vert « Connecté au serveur ».

Si une version antérieure signée avec une autre clé est installée, la désinstaller une fois avant
d'installer celle-ci. Les mises à jour suivantes s'installent normalement par-dessus.

Le jeton n'est **jamais** dans le code : il est saisi par l'utilisateur et stocké uniquement dans les
`SharedPreferences` du téléphone.

## Compiler soi-même

```bash
# Prérequis : JDK 21 et Android SDK (compileSdk 36)
./gradlew :app:assembleDebug
# APK : app/build/outputs/apk/debug/app-debug.apk
```

La compilation est aussi automatisée par GitHub Actions. La clé de signature est conservée dans les
secrets du dépôt (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`) afin que
chaque version puisse s'installer par-dessus la précédente. Sans ces secrets, le workflow retombe sur
un keystore de debug généré à la volée.

## Sécurité

- Transport chiffré de bout en bout (TLS Let's Encrypt, renouvellement automatique par Certbot).
- Aucun secret versionné : ni jeton API, ni clé TryPost, ni identifiant de page Facebook.
- L'API refuse toute requête sans `Authorization: Bearer <jeton>` (401).
- Les fichiers `.env`, `local.properties`, `.gradle/` et les keystores sont ignorés par Git.

## Licence

Code sous licence MIT.
