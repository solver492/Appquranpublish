# 08 — Prompt prêt à coller dans Google AI Studio

> **Mode d'emploi** : ouvre Google AI Studio, crée un nouveau projet, importe ce dépôt GitHub, puis colle le bloc ci-dessous dans le champ de prompt. Le prompt est autonome : il rappelle le contexte, les écrans, les données et les critères d'acceptation. Les documents `03-APP-MOBILE.md`, `04-API.md` et `05-MODELE-DONNEES.md` servent de référence détaillée.

---

## PROMPT

Tu es développeur front-end senior spécialisé en applications mobiles React. Tu dois construire **Badr Quran Studio**, une application mobile-first installable (PWA) qui permet de créer, personnaliser, prévisualiser, partager et programmer des Reels Coran pour la page Facebook « Badr Quran — بدر القرآنيات ».

Le dépôt contient déjà :

- `mobile/` : un starter React 19 + Vite 6 + TypeScript fonctionnel ;
- `backend/` : une API FastAPI de référence ;
- `pipeline/` : le moteur Python qui génère réellement les vidéos ;
- `docs/` : la spécification complète.

**Lis d'abord `docs/00-START-HERE.md`, puis `docs/03-APP-MOBILE.md`, `docs/04-API.md` et `docs/05-MODELE-DONNEES.md`.** Le starter `mobile/` est la base : améliore-le, ne repars pas de zéro sans raison.

### 1. Contraintes produit

L'application est en **français**, en **thème sombre**, avec des accents **dorés**. Elle doit être utilisable à une main sur un écran de **360 px** de large minimum, et rester belle jusqu'à 540 px.

Elle doit fonctionner **sans serveur** en mode démo : si l'API ne répond pas, l'application affiche des données simulées et le badge « Démo ». Elle ne doit jamais afficher un écran blanc.

Elle ne doit contenir **aucun secret** : pas de clé API, pas de token, pas de mot de passe dans le code client.

### 2. Navigation

Cinq onglets fixes en bas :

1. **Accueil** — prochaine publication, statistiques, derniers Reels.
2. **Studio** — création et personnalisation d'un Reel.
3. **Planning** — les 5 créneaux de prière du jour.
4. **Reels** — bibliothèque.
5. **Réglages** — connexion serveur, page Facebook, préférences.

Header : logo « ب », nom « Badr Quran », sous-titre « Studio Reels », badge de connexion vert **Connecté** ou doré **Démo**.

### 3. Écran Studio — le cœur de l'application

Il doit permettre de définir :

- **les versets** : sourate (1 à 114), verset de départ, nombre de versets (1 à 10), durée (30 ou 60 s) ;
- **le récitateur** : 7 voix (Yasser Ad-Dussary par défaut, Abdurrahmaan As-Sudais, Muhsin Al Qasim, Abu Bakr Ash-Shaatree, Hani Ar-Rifai, Mohamed Al-Tablawi, Saud Al-Shuraim) ;
- **l'arrière-plan** : 8 vidéos nature (Vagues, Mer et rochers, Fleurs, Cascade, Forêt, Baie, Nuages, Prairie) ;
- **le style d'écriture des sous-titres** : mot par mot en or, ligne par ligne en or, verset entier en or, minimal, bilingue renforcé ;
- **la traduction française** : activable/désactivable ;
- **le watermark** : texte modifiable, par défaut `بدر القرآنيات` ;
- **le logo** : PNG transparent, logo animé WebM, ou aucun logo.

En haut de l'écran, un **aperçu 9:16** doit se mettre à jour immédiatement à chaque changement : fond, dégradé sombre, watermark, logo, texte arabe RTL avec un mot en or, traduction, nom du récitateur, badges durée et 9:16.

En bas, un bouton **Générer le Reel** désactivé pendant le rendu, avec une carte de progression (pourcentage, dernière ligne de log, message d'erreur, bouton « Nouveau rendu »).

### 4. Écran Planning

Afficher les **5 prières** du jour : Fajr, Dhuhr, Asr, Maghrib, Isha.

Pour chaque créneau :

- heure de publication ;
- heure de l'adhan ;
- nom français et arabe ;
- statut : en attente, programmé, publié, manqué, échec ;
- switch d'activation.

Réglages du jour :

- ville ;
- slider de marge avant l'adhan (0 à 45 min, pas de 5, défaut 10) ;
- bouton **Enregistrer le planning**.

Navigation par date : jour précédent / jour suivant.

Règle métier à afficher : « Un Reel est publié 10 minutes avant chaque adhan. Les versets avancent sans saut, de 1:1 à 114:6. »

### 5. Écran Reels (bibliothèque)

- Filtres : Tous, Publiés, À publier.
- Grille de 2 colonnes.
- Chaque carte : vignette 9:13, durée, bouton play, badge de statut, référence (sourate + versets), date, bouton partager.
- État vide : « Aucun Reel ici. Génère ton premier Reel depuis le Studio. »

### 6. Feuille de partage

S'ouvre depuis n'importe quelle carte Reel, en bottom sheet.

Contenu :

- référence du Reel ;
- carte avec vignette, statut et début de légende ;
- 4 actions : **Partager** (API native `navigator.share`, sinon copie), **Facebook** (`https://www.facebook.com/sharer/sharer.php?u=...`), **WhatsApp** (`https://wa.me/?text=...`), **Copier** (légende + URL) ;
- zone de texte avec la légende complète ;
- lien « Ouvrir la vidéo publique » ;
- bouton « Publier maintenant sur Facebook » si le Reel n'est pas publié.

La feuille se ferme au clic sur le fond et avec la touche Échap.

### 7. Écran Accueil

- Carte **prochaine publication** : prière en arabe et en français, compte à rebours, heure de l'adhan, mention « Publication 10 min avant », boutons « Créer un Reel » et « Voir le planning ».
- Trois statistiques : Reels générés, Reels publiés, prochain verset (ex. `2:30`).
- Carrousel des 4 derniers Reels.
- Aperçu de la dernière légende avec bouton Partager.

### 8. Écran Réglages

- Statut de connexion (vert connecté, doré démo).
- Champ URL de l'API + bouton Tester + bouton Enregistrer l'URL.
- Page Facebook : nom, ID, ville, marge avant l'adhan.
- À propos : version, pipeline, TryPost.

L'URL de l'API est stockée dans `localStorage`.

### 9. Données et API

Si l'API est joignable, utiliser ces endpoints (base `https://<serveur>/api`) :

| Méthode | Endpoint | Rôle |
|---|---|---|
| GET | `/config` | Catalogue : récitateurs, fonds, styles, logos, défauts, réglages. |
| GET | `/state` | Progression Coran. |
| GET | `/reels` | Liste des Reels. |
| POST | `/reels/render` | Crée un job de rendu, renvoie `{ job_id }`. |
| GET | `/jobs/{id}` | Progression du job. |
| POST | `/reels/{id}/publish` | Publie ou programme. |
| GET | `/reels/{id}/share` | Liens de partage. |
| GET | `/schedules/{date}` | Planning du jour. |
| PUT | `/schedules/{date}` | Recalcule le planning. |
| PATCH | `/schedules/{date}/slots/{prayer}` | Active/désactive un créneau. |
| GET/PUT | `/settings` | Réglages. |

Le contrat complet, avec exemples JSON, est dans `docs/04-API.md`. Les types TypeScript exacts sont dans `docs/05-MODELE-DONNEES.md`.

Le rendu est asynchrone : `POST /reels/render` renvoie un `job_id`, puis l'application interroge `GET /jobs/{id}` toutes les 1,5 s jusqu'à `completed` ou `failed`.

Si l'API échoue : basculer en mode démo avec `mobile/src/mock.ts`, sans casser l'interface.

### 10. Design system

- Fond : `#06110d`, surfaces `rgba(14, 33, 25, 0.82)`.
- Or : `#e8c36a`, or foncé `#c79a3e`.
- Vert succès : `#3f8b68`. Rouge erreur : `#d97b6c`.
- Texte : `#f4f7f3`. Texte secondaire : `#9bb0a4`.
- Police interface : **Manrope**. Police arabe : **Amiri**.
- Titres 27 px / 800. Corps 12 à 14 px. Labels 10 px majuscules.
- Rayons 16 à 26 px. Boutons 44 px minimum. Barre d'onglets 64 px + safe area.
- Pas de dégradés criards, pas de vert fluo, pas d'emojis dans les titres.
- L'or est réservé au mot récité, aux actions principales et aux accents.

### 11. Accessibilité et qualité

- Contraste AA.
- `aria-label` sur les boutons iconiques.
- `aria-current="page"` sur l'onglet actif.
- Navigation clavier sur desktop.
- `viewport-fit=cover` pour les encoches.
- Aucun texte arabe coupé ou tronqué dans l'aperçu.
- L'aperçu doit rester lisible même avec un verset long.

### 12. Technique

- React 19, Vite 6, TypeScript strict.
- Aucune bibliothèque UI lourde : CSS maison, icônes SVG inline.
- État local avec `useState` et `useEffect`. Pas de Redux.
- `localStorage` uniquement pour l'URL de l'API et les préférences légères.
- PWA : `manifest.webmanifest`, icône SVG, `display: standalone`, thème sombre, orientation portrait.
- Le build doit passer avec `npm run build`.

### 13. Ce que tu dois livrer

1. Le code complet de l'application dans `mobile/`.
2. Tous les écrans et composants décrits.
3. Le mode démo fonctionnel sans serveur.
4. Le branchement API complet.
5. Un `mobile/README.md` mis à jour avec les commandes.
6. Aucun secret, aucune donnée personnelle, aucun fichier lourd.

### 14. Critères d'acceptation

L'application est terminée quand :

1. `npm install && npm run build` réussit.
2. Elle se lance sans serveur et reste utilisable en mode démo.
3. Elle se connecte à l'API et affiche les vrais récitateurs, fonds, styles et logos.
4. Générer un Reel crée un job, affiche la progression et ajoute le Reel à la bibliothèque.
5. Le bouton Partager ouvre le partage natif et propose Facebook, WhatsApp et copie.
6. Le planning affiche les 5 prières, les heures d'adhan et de publication, et permet d'activer/désactiver un créneau.
7. L'interface est utilisable sur un écran de 360 px de large.
8. Aucun secret n'est présent dans le code client.
9. Le texte arabe est toujours RTL, complet et lisible.
10. Le badge Démo/Connecté reflète l'état réel.

### 15. Ce qu'il ne faut pas faire

- Ne pas réécrire le pipeline Python.
- Ne pas modifier `pipeline/quran_reels_v2.py` sans raison.
- Ne pas committer de vidéos, de fonds, de `node_modules`, de `dist` ou de `.env`.
- Ne pas inventer d'endpoints : utiliser le contrat de `docs/04-API.md`.
- Ne pas remplacer Amiri par une police arabe décorative.
- Ne pas utiliser de fond avec être humain, espace, galaxie ou lune.
- Ne pas publier automatiquement sans confirmation de l'utilisateur.

Commence par lire les documents, puis propose un plan court, puis implémente.

---

## Prompt court (variante)

Si tu veux un prompt plus compact :

> Construis **Badr Quran Studio**, une PWA React + Vite + TypeScript en français, thème sombre doré, mobile-first (360 px). Elle sert à créer des Reels Coran : choix des versets, de 7 récitateurs, de 8 fonds nature, de 5 styles de sous-titres arabes RTL (mot par mot en or, ligne, verset, minimal, bilingue), ajout ou retrait du logo, aperçu 9:16, génération asynchrone avec progression, bibliothèque, partage natif/Facebook/WhatsApp/copie, et planning des 5 prières avec marge avant l'adhan. Elle doit fonctionner en mode démo sans serveur et se brancher sur l'API FastAPI décrite dans `docs/04-API.md`. Lis `docs/03-APP-MOBILE.md` et `docs/05-MODELE-DONNEES.md` pour les écrans et les types exacts. Aucun secret dans le client. `npm run build` doit passer.

---

## Conseils d'utilisation dans AI Studio

1. **Importe le dépôt entier**, pas seulement `mobile/`. AI Studio a ainsi le pipeline, l'API et les docs.
2. Colle le prompt long. Il est conçu pour être autonome.
3. Si AI Studio demande un point de départ, indique `mobile/src/App.tsx`.
4. Demande-lui de lire `docs/03-APP-MOBILE.md` avant de coder.
5. Vérifie après génération :
   - `npm run build` ;
   - le mode démo (couper l'API) ;
   - l'affichage à 360 px ;
   - le RTL arabe ;
   - le partage.
6. Itère par écran, pas par fichier : « améliore l'écran Planning », « rends l'aperçu plus fidèle ».
7. Ne lui fais jamais écrire de clé API. Les secrets restent dans `pipeline/.env` côté serveur.
