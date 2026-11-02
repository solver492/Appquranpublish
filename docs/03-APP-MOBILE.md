# 03 — Spécification de l'application mobile

## Principe

Application **mobile-first**, installable (PWA), en français, avec une interface sombre et dorée. Elle doit être utilisable à une main, sur un écran de 360 px de large.

Le starter existant est dans `mobile/`. Il peut être repris tel quel ou régénéré par Google AI Studio.

## Navigation

Cinq onglets fixes en bas :

| Onglet | Rôle |
|---|---|
| **Accueil** | Prochaine prière, prochain Reel, statistiques, derniers Reels. |
| **Studio** | Créer et personnaliser un Reel. |
| **Planning** | Voir et modifier les 5 créneaux du jour. |
| **Reels** | Bibliothèque des Reels générés et publiés. |
| **Réglages** | Connexion serveur, page Facebook, préférences. |

Le header affiche le logo, le nom « Badr Quran », le sous-titre « Studio Reels » et un badge de connexion :

- vert **Connecté** si l'API répond ;
- doré **Démo** si l'API est absente.

## Écran 1 — Accueil

### Contenu

1. **Carte prochaine publication**
   - nom de la prière en arabe et en français ;
   - compte à rebours (« 2 h 14 », « 12 min ») ;
   - heure de l'adhan ;
   - mention « Publication 10 min avant » ;
   - bouton **Créer un Reel** ;
   - bouton **Voir le planning**.
2. **Trois statistiques**
   - Reels générés ;
   - Reels publiés ;
   - prochain verset (ex. `2:30`).
3. **Derniers Reels** : carrousel horizontal de 4 vignettes avec badge de statut, durée et référence.
4. **Dernière légende** : aperçu tronqué + bouton Partager.

### États

- Chargement : squelettes ou texte « Chargement… ».
- Aucun Reel : message « Aucun Reel pour le moment » + CTA Studio.
- Aucun créneau : « Aucun créneau actif aujourd'hui ».

## Écran 2 — Studio

C'est l'écran le plus important. Il est organisé en sections verticales.

### 2.1 Aperçu 9:16

En haut, un aperçu vertical compact (environ 250 px de large) :

- fond sélectionné ;
- dégradé sombre en bas ;
- watermark en haut (`بدر القرآنيات`) ;
- logo en haut à droite ;
- texte arabe centré, RTL, avec un mot en or ;
- traduction française en bas ;
- nom du récitateur ;
- badge durée (`30s`) et badge `9:16`.

L'aperçu se met à jour immédiatement quand l'utilisateur change un réglage.

### 2.2 Versets

- champ **Sourate** (numérique, 1 à 114) ;
- champ **Verset** (numérique) ;
- stepper **Nombre** (1 à 10) ;
- bouton **Prochain** qui remplit avec la progression courante ;
- segmented control **30 secondes / 60 secondes**.

### 2.3 Récitateur

Liste verticale de cartes :

- avatar 🎙️ ;
- nom latin ;
- nom arabe ;
- coche si sélectionné.

Sept récitateurs :

1. Yasser Ad-Dussary (défaut)
2. Abdurrahmaan As-Sudais
3. Muhsin Al Qasim
4. Abu Bakr Ash-Shaatree
5. Hani Ar-Rifai
6. Mohamed Al-Tablawi
7. Saud Al-Shuraim

### 2.4 Arrière-plan

Grille de 3 colonnes avec vignettes 9:12 :

- Vagues
- Mer et rochers
- Fleurs
- Cascade
- Forêt (défaut)
- Baie
- Nuages
- Prairie

La tuile sélectionnée a une bordure dorée et une coche.

### 2.5 Écriture des sous-titres

Liste de cartes avec un mini-aperçu arabe et une description :

| Style | Rendu |
|---|---|
| **Mot par mot (or)** | Le mot récité passe en or, les autres restent blancs. |
| **Ligne par ligne (or)** | Toute la ligne en cours passe en or. |
| **Verset entier en or** | Tout le verset est doré, sans surlignage mobile. |
| **Minimal** | Arabe blanc, sans traduction ni watermark. |
| **Bilingue renforcé** | Mot par mot + traduction plus grande. |

Un switch **Traduction française** permet de l'activer ou non.
Un champ **Watermark** permet de modifier le texte en haut.

### 2.6 Logo

Trois choix :

- **Logo transparent** (PNG) ;
- **Logo animé** (WebM, recommandé) ;
- **Sans logo**.

### 2.7 Génération

Bouton principal sticky en bas : **Générer le Reel**.

Pendant la génération :

- bouton désactivé ;
- carte de progression avec pourcentage ;
- dernière ligne de log ;
- en cas d'échec : message d'erreur et bouton **Nouveau rendu** ;
- en cas de succès : « Reel prêt », lien vers la bibliothèque.

## Écran 3 — Planning

### Contenu

1. **Navigation par date** : jour précédent / date du jour / jour suivant.
2. **Réglages du jour** :
   - ville ;
   - slider de marge avant l'adhan (0 à 45 minutes, pas de 5) ;
   - bouton **Enregistrer le planning**.
3. **Liste des 5 créneaux** :
   - heure de publication ;
   - heure de l'adhan ;
   - nom de la prière en français et en arabe ;
   - badge de statut : en attente, programmé, publié, manqué, échec ;
   - switch d'activation.
4. **Carte explicative** : « Le serveur récupère les horaires, génère le Reel suivant et le programme 10 minutes avant l'adhan. »

### Règles

- Désactiver un créneau ne supprime pas le Reel déjà généré.
- La modification de la marge recalcule les heures de publication.
- Le changement de ville recharge les horaires depuis Aladhan.
- Les heures sont affichées dans le fuseau local de la ville (`Africa/Casablanca`).

## Écran 4 — Reels (bibliothèque)

### Contenu

- Filtres : **Tous**, **Publiés**, **À publier**.
- Grille de 2 colonnes.
- Chaque carte :
  - vignette 9:13 ;
  - durée ;
  - bouton play ;
  - badge de statut ;
  - référence (sourate + versets) ;
  - date ;
  - bouton partager.
- Bouton rafraîchir.

### État vide

« Aucun Reel ici. Génère ton premier Reel depuis le Studio. »

## Écran 5 — Réglages

### Contenu

1. **Statut de connexion** : vert connecté, doré démo.
2. **Connexion serveur** :
   - champ URL de l'API ;
   - bouton **Tester** ;
   - bouton **Enregistrer l'URL**.
3. **Page Facebook** :
   - nom de la page ;
   - ID de la page ;
   - ville ;
   - marge avant l'adhan.
4. **À propos** : version, pipeline, TryPost.

## Feuille de partage

S'ouvre depuis n'importe quelle carte Reel.

Contenu :

- poignée de fond ;
- titre **Partager le Reel** ;
- référence du Reel ;
- carte avec vignette, statut et début de légende ;
- 4 actions :
  - **Partager** : `navigator.share` si disponible, sinon copie ;
  - **Facebook** : `https://www.facebook.com/sharer/sharer.php?u=...` ;
  - **WhatsApp** : `https://wa.me/?text=...` ;
  - **Copier** : légende + URL dans le presse-papiers ;
- zone de texte avec la légende complète ;
- lien **Ouvrir la vidéo publique** ;
- bouton **Publier maintenant sur Facebook** si le Reel n'est pas encore publié.

## Design system

### Couleurs

| Token | Valeur | Usage |
|---|---|---|
| `--bg` | `#06110d` | Fond général |
| `--surface` | `rgba(14, 33, 25, 0.82)` | Cartes |
| `--gold` | `#e8c36a` | Accent, mot actif, CTA |
| `--gold-2` | `#c79a3e` | Dégradé doré |
| `--green` | `#3f8b68` | Succès, connecté |
| `--red` | `#d97b6c` | Erreur |
| `--text` | `#f4f7f3` | Texte |
| `--muted` | `#9bb0a4` | Texte secondaire |

### Typographie

- Interface : **Manrope** (fallback système).
- Arabe : **Amiri** (fallback Noto Naskh Arabic, serif).
- Titres : 27 px, poids 800, letter-spacing -0.035em.
- Corps : 12 à 14 px.
- Labels : 10 px, majuscules, letter-spacing 0.08em.

### Espacements

- Rayon des cartes : 16 à 26 px.
- Padding des panneaux : 16 px.
- Gap vertical : 16 px.
- Hauteur minimale des boutons : 44 px.
- Barre d'onglets : 64 px + safe area.

### Composants

- `Shell` : header + contenu + tabbar.
- `PhonePreview` : aperçu 9:16.
- `ShareSheet` : feuille de partage.
- `Panel` : carte de section.
- `Badge` : statut.
- `Chip` : filtre.
- `Switch` : activation.
- `Stepper` : nombre de versets.
- `Segmented` : durée.
- `Toast` : confirmation.

## Accessibilité

- Contraste minimum AA sur les textes.
- Boutons avec `aria-label` quand ils sont iconiques.
- Navigation clavier possible sur desktop.
- `aria-current="page"` sur l'onglet actif.
- La feuille de partage se ferme avec `Échap`.
- Taille de police minimum 10 px pour les labels, 12 px pour le corps.

## Internationalisation

- Interface en français.
- Textes arabes toujours en `dir="rtl"`.
- Les noms de prières ont une version arabe et française.
- Les dates sont formatées avec `fr-FR`.
- Les heures sont formatées avec `fr-FR` et le fuseau local.

## Mode hors ligne

- L'app charge les données de démo si l'API échoue.
- Les Reels et le planning restent consultables.
- La génération est simulée.
- Le partage utilise les données locales.
- Un bandeau **Démo** indique clairement le mode.

## PWA

- `manifest.webmanifest` avec `display: standalone`.
- Icône SVG.
- Thème sombre.
- Orientation portrait.
- `viewport-fit=cover` pour les encoches.

## Critères d'acceptation UI

1. L'app s'affiche correctement de 360 px à 540 px de large.
2. Le Studio met à jour l'aperçu à chaque changement.
3. Le bouton Générer est désactivé pendant le rendu.
4. La feuille de partage s'ouvre et se ferme sans casser le scroll.
5. Le planning affiche 5 créneaux, même si l'API renvoie moins de données.
6. Le mode démo est visible en permanence.
7. Aucun texte arabe n'est coupé ou tronqué dans l'aperçu.
