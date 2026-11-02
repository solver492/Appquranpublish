# START HERE — Badr Quran Studio

## En une phrase

Badr Quran Studio est une application mobile qui permet de créer, personnaliser, prévisualiser, partager et programmer des Reels Coran pour la page Facebook **Badr Quran — بدر القرآنيات**.

## Ce que Google AI Studio doit construire

À partir de ce dépôt, construire une **application mobile-first installable (PWA)** avec React, Vite et TypeScript, qui consomme l'API décrite dans [04-API.md](04-API.md).

L'application doit permettre à l'utilisateur de :

- choisir la sourate et le verset de départ ;
- choisir le nombre de versets (1 à 10) et la durée (30 ou 60 s) ;
- choisir le récitateur ;
- choisir l'arrière-plan nature ;
- choisir le style d'écriture des sous-titres ;
- ajouter ou retirer le logo ;
- prévisualiser le rendu vertical 9:16 ;
- générer le Reel ;
- partager le Reel (partage natif, Facebook, WhatsApp, copie de légende) ;
- programmer les publications sur les 5 créneaux de prière ;
- activer ou désactiver chaque créneau ;
- régler la marge de publication avant l'adhan.

## Ordre de lecture recommandé

1. **[01-PRODUIT.md](01-PRODUIT.md)** — comprendre le problème et les utilisateurs.
2. **[03-APP-MOBILE.md](03-APP-MOBILE.md)** — écrans, parcours et design system.
3. **[04-API.md](04-API.md)** — contrat entre l'application et le serveur.
4. **[05-MODELE-DONNEES.md](05-MODELE-DONNEES.md)** — formes JSON exactes.
5. **[06-PIPELINE-RENDU.md](06-PIPELINE-RENDU.md)** — ce que le serveur fait réellement.
6. **[07-PUBLICATION-PLANNING.md](07-PUBLICATION-PLANNING.md)** — logique des adhans et de TryPost.
7. **[08-PROMPT-AI-STUDIO.md](08-PROMPT-AI-STUDIO.md)** — prompt prêt à coller.

## Règles à ne pas casser

- **Arabe RTL** : le texte arabe doit toujours être rendu de droite à gauche, avec le mot actif en or.
- **Verset complet** : ne jamais tronquer un verset. Si le texte est trop long, réduire la police ou découper en blocs contigus.
- **Progression séquentielle** : les versets avancent de 1:1 à 114:6, sans saut.
- **5 Reels par jour** : un avant chaque adhan (Fajr, Dhuhr, Asr, Maghrib, Isha), 10 minutes avant par défaut.
- **Aucun secret dans le dépôt** : les clés TryPost et Facebook vivent dans `pipeline/.env`.
- **Pas de fond avec être humain, espace, galaxie ou lune** : uniquement la nature.
- **Le mode démo doit fonctionner sans serveur** : l'application ne doit jamais afficher un écran blanc si l'API est absente.

## Structure du dépôt

```
.
├── README.md
├── docs/                  Documentation produit et technique
├── pipeline/              Moteur Python de production
│   ├── quran_reels_v2.py  Génération d'un Reel
│   ├── prayer_reels.py    Planificateur 5x/jour
│   ├── verify_reel.py     Vérification du rendu
│   ├── daily_reel.sh      Ancien cron quotidien
│   ├── run_prayer.sh      Cron toutes les 10 minutes
│   └── state.json         Progression Coran
├── backend/               API FastAPI de référence
│   ├── app.py
│   ├── catalog.json       Récitateurs, fonds, styles, logos
│   └── requirements.txt
├── mobile/                Application React + Vite + TypeScript
│   ├── src/
│   └── public/
├── assets/                Logos et vignettes
└── scripts/               Téléchargement des fonds, setup
```

## Mode démo

L'application démarre en mode démo si l'API n'est pas joignable :

- les récitateurs, fonds, styles et logos viennent de `mobile/src/mock.ts` ;
- les Reels et le planning sont simulés ;
- la génération affiche une progression fictive ;
- le partage fonctionne avec les données locales.

Dès que l'utilisateur renseigne une URL d'API dans **Réglages > Connexion serveur**, l'application bascule sur les données réelles.

## Ce qui existe déjà dans `mobile/`

Le dossier `mobile/` contient déjà un starter fonctionnel :

- navigation à 5 onglets : Accueil, Studio, Planning, Reels, Réglages ;
- aperçu 9:16 du Reel ;
- sélection des versets, du récitateur, du fond, du style, du logo ;
- génération avec barre de progression ;
- planning des 5 prières ;
- bibliothèque de Reels ;
- feuille de partage ;
- mode démo.

Google AI Studio peut repartir de ce starter, l'améliorer et le brancher sur l'API.

## Critères de réussite

L'application est considérée comme terminée quand :

1. elle se lance sans serveur et reste utilisable en mode démo ;
2. elle se connecte à l'API et affiche les vrais récitateurs, fonds, styles et logos ;
3. générer un Reel crée un job, affiche la progression et ajoute le Reel à la bibliothèque ;
4. le bouton Partager ouvre le partage natif et propose Facebook, WhatsApp et copie ;
5. le planning affiche les 5 prières, les heures d'adhan et de publication, et permet d'activer/désactiver un créneau ;
6. l'interface est utilisable sur un écran de 360 px de large ;
7. aucun secret n'est présent dans le code client.
