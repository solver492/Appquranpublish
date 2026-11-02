# Badr Quran Studio

Application mobile pour la création, la personnalisation et la programmation de Reels Coran pour la page **Badr Quran — بدر القرآنيات**.

L'application transforme un verset du Coran en Reel vertical (format 9:16) :
- Texte arabe RTL avec **surlignage mot par mot doré** ou ligne par ligne
- Traduction française synchronisée
- Choix de 7 récitations de référence (Yasser Ad-Dussary, Sudais, Al Qasim, etc.)
- Choix parmi 8 arrière-plans nature immersifs (vagues, mer & rochers, forêt, cascade, nuages, prairie)
- Incrustation de logo transparent ou animé WebM
- Prévisualisation interactive dynamique mot par mot sur maquette de smartphone 9:16
- Partage natif (Web Share API, Facebook, WhatsApp, copier le texte)
- Programmation quotidienne alignée sur les 5 prières canoniques (Fajr, Dhuhr, Asr, Maghrib, Isha)
- Mode démonstration autonome avec persistance locale (localStorage) et bascule vers API distante FastAPI

## Fonctionnalités

1. **Accueil (Dashboard)** :
   - Prochain créneau de publication calculé en temps réel avec compte à rebours
   - Statistiques globales (reels générés, reels publiés, progression du Coran)
   - Accès rapide aux derniers Reels générés et aperçu de la légende
2. **Studio de création** :
   - Choix de la Sourate, du verset de départ et du nombre de versets
   - Bouton « Prochain » pour reprendre automatiquement la lecture séquentielle
   - Sélection de la durée (30s / 60s)
   - Sélection du récitateur avec nom arabe et français
   - Sélection du fond vidéo / photo nature
   - 5 styles de sous-titrage (mot par mot doré, ligne par ligne, verset entier, bilingue renforcé, minimal)
   - Incrustation et personnalisation du filigrane / watermark
   - Aperçu interactif avec animation de lecture au clic
   - Génération avec barre de progression temps réel
3. **Planning & Prières** :
   - Visualisation des 5 créneaux de prière du jour (horaire de l'adhan et de publication)
   - Configuration de la marge de sécurité (ex. 10 min avant l'adhan)
   - Activation / désactivation par créneau
   - Navigation jour par jour
4. **Bibliothèque de Reels** :
   - Filtrage (Tous, Publiés, À publier)
   - Fiches détaillées avec durées et badges d'état
   - Fiche de partage complète (partage direct, Facebook, WhatsApp, copie)
   - Publication instantanée
5. **Réglages** :
   - Configuration de l'URL du serveur API backend
   - Identifiants de page Facebook
   - Ville et pays de calcul des horaires de prière
   - Mode démo interactif sécurisé

## Démarrage

```bash
# Installation des dépendances
npm install

# Lancement du serveur de développement (port 3000)
npm run dev

# Vérification du build de production
npm run build
```

## Structure du projet

- `src/` : Composants React, écrans (`HomeScreen`, `StudioScreen`, `ScheduleScreen`, `LibraryScreen`, `SettingsScreen`), types et API client
- `public/` : Ressources statiques (fonds nature, logos, icônes SVG, manifest)
- `metadata.json` : Métadonnées AI Studio
- `docs/` : Documentation technique et produit

## Licence

Code sous licence MIT.
