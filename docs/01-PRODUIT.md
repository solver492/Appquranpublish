# 01 — Produit

## Vision

Permettre à une petite équipe de publier chaque jour du contenu coranique de qualité, sans monter une usine à gaz : un verset, une voix, un fond nature, un sous-titre lisible, une publication au bon moment.

Badr Quran Studio est l'outil interne de la page **Badr Quran — بدر القرآنيات**. Il doit être assez simple pour être utilisé depuis un téléphone, entre deux prières.

## Utilisateur principal

**L'administrateur de la page**

- gère la page Facebook Badr Quran ;
- connaît le Coran et veut un rendu respectueux ;
- n'est pas développeur ;
- publie depuis son téléphone ;
- veut programmer la semaine sans y penser ;
- veut partager un Reel sur WhatsApp ou en story.

## Jobs to be done

1. « Je veux créer un Reel propre en moins de 2 minutes. »
2. « Je veux choisir la voix et l'ambiance sans toucher au code. »
3. « Je veux être sûr que le texte arabe est complet et bien aligné. »
4. « Je veux que les Reels partent tout seuls avant chaque prière. »
5. « Je veux partager un Reel à un ami ou sur une story. »
6. « Je veux voir ce qui a été publié et ce qui arrive. »

## Fonctionnalités

### Must have

- Sélection des versets : sourate, verset de départ, nombre de versets.
- Durée : 30 ou 60 secondes.
- Récitateur : 7 voix.
- Arrière-plan : 8 vidéos nature.
- Style de sous-titres : mot par mot, ligne par ligne, verset entier, minimal, bilingue.
- Logo : PNG transparent, logo animé ou aucun.
- Aperçu 9:16 avant génération.
- Génération avec progression et gestion d'erreur.
- Bibliothèque des Reels générés.
- Partage natif + Facebook + WhatsApp + copie de légende.
- Planning des 5 prières avec activation/désactivation.
- Marge de publication avant l'adhan.
- Mode démo sans serveur.

### Should have

- Programmation d'un Reel à une heure personnalisée.
- Republier un Reel déjà généré sans le regénérer.
- Statistiques simples : nombre de Reels générés, publiés, prochaine publication.
- Choix de la ville pour les horaires de prière.
- Notification quand un Reel est publié.

### Could have

- Édition manuelle de la légende.
- Téléchargement de la vidéo.
- Favoris.
- Mode sombre/clair (le mode sombre est le défaut).
- Multi-pages Facebook.

### Won't have (v1)

- Édition vidéo avancée (timeline, effets).
- Upload de fonds personnalisés depuis le téléphone.
- Gestion de plusieurs langues de traduction.
- Comptes utilisateurs et rôles.
- Paiement.

## Parcours principal

1. J'ouvre l'app. Je vois la prochaine prière et le prochain Reel.
2. Je vais dans **Studio**.
3. L'app me propose le prochain verset.
4. Je choisis la voix, le fond, le style et le logo.
5. Je regarde l'aperçu.
6. Je génère.
7. Le Reel apparaît dans **Reels**.
8. Je le partage ou je le laisse partir au prochain créneau.

## Parcours planning

1. J'ouvre **Planning**.
2. Je vois les 5 prières du jour avec l'heure d'adhan et l'heure de publication.
3. Je désactive un créneau si je ne veux pas publier.
4. Je règle la marge à 10 minutes.
5. J'enregistre.
6. Le serveur publie automatiquement.

## Ton et esthétique

- Sombre, nature, or.
- Sobre, pas clinquant.
- Typographie arabe traditionnelle (Amiri) et sans-serif moderne pour l'interface.
- Le doré est réservé au mot récité, aux actions principales et aux accents.
- Pas de vert fluo, pas de dégradés criards, pas d'emojis dans les titres.

## Métriques de succès

- Temps pour créer un Reel : moins de 2 minutes.
- Taux de génération réussie : supérieur à 95 %.
- Taux de publication réussie : supérieur à 98 %.
- Aucun verset tronqué.
- Aucun Reel publié avec un texte arabe cassé.
- L'administrateur peut programmer une semaine en moins de 5 minutes.

## Contraintes

- Le pipeline existant est en Python et utilise FFmpeg.
- Les timings mot par mot viennent de `spa5k/quran-timings-api`.
- Les textes et traductions viennent de l'API Quran.com.
- Les audios viennent d'EveryAyah.
- La publication passe par TryPost, une instance auto-hébergée.
- Les vidéos sont lourdes (30 à 80 Mo) : ne jamais les mettre dans Git.
