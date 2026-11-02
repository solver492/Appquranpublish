# 09 — Roadmap

## État actuel

| Élément | État |
|---|---|
| Pipeline de génération | ✅ Production, 5 Reels/jour |
| Planificateur de prières | ✅ Production, cron toutes les 10 min |
| Publication TryPost | ✅ Production |
| Application mobile | 🟡 Starter complet, mode démo |
| API FastAPI | 🟡 Référence, à durcir |
| Documentation | ✅ Complète |
| Tests automatisés | 🔴 À écrire |

## Phase 0 — Consolider le dépôt (fait)

- [x] Extraire le pipeline dans un dépôt propre.
- [x] Ajouter les options `--reciter`, `--subtitle-style`, `--logo none`.
- [x] Documenter le produit, l'architecture, l'API et le pipeline.
- [x] Fournir un starter mobile React + Vite.
- [x] Fournir une API de référence.
- [x] Rédiger le prompt Google AI Studio.

## Phase 1 — Application utilisable au quotidien

Objectif : l'administrateur peut créer et publier depuis son téléphone.

- [ ] Brancher l'application sur l'API réelle.
- [ ] Tester le rendu complet depuis le Studio.
- [ ] Afficher la vraie progression du job.
- [ ] Ajouter la lecture vidéo dans la bibliothèque.
- [ ] Ajouter le téléchargement du MP4.
- [ ] Ajouter la reprogrammation d'un Reel.
- [ ] Ajouter l'édition de la légende avant publication.
- [ ] Ajouter une confirmation avant publication immédiate.
- [ ] Tester sur iOS Safari et Android Chrome.
- [ ] Installer la PWA sur l'écran d'accueil.

**Critère de sortie** : un Reel est créé, prévisualisé, généré, publié et partagé depuis le téléphone, sans passer par le terminal.

## Phase 2 — Fiabilité

Objectif : zéro publication manquée.

- [ ] Tests unitaires du pipeline (`pytest`).
- [ ] Tests d'intégration de l'API (`httpx` + `TestClient`).
- [ ] Tests end-to-end Playwright sur le mode démo.
- [ ] Vérification automatique du RTL et de la complétude du verset.
- [ ] Surveillance : alerte si un créneau est `missed` ou `failed`.
- [ ] Alerte disque (seuil 80 %).
- [ ] Rotation automatique des MP4 de plus de 30 jours.
- [ ] Reprise automatique d'un rendu échoué (max 2 tentatives).
- [ ] Journal structuré en JSON.
- [ ] Sauvegarde quotidienne de `state.json` et `prayer_schedule.json`.

**Critère de sortie** : 30 jours consécutifs sans créneau manqué.

## Phase 3 — Confort éditorial

Objectif : moins de friction pour l'administrateur.

- [ ] Programmation d'un Reel à une heure personnalisée depuis l'app.
- [ ] File d'attente de Reels prêts à publier.
- [ ] Duplication d'un Reel avec un autre fond ou une autre voix.
- [ ] Favoris et recherche dans la bibliothèque.
- [ ] Statistiques simples : vues, réactions, partages (si l'API Facebook le permet).
- [ ] Notifications push quand un Reel est publié.
- [ ] Choix de la ville et de la méthode de calcul.
- [ ] Mode clair optionnel.
- [ ] Multi-pages Facebook.

**Critère de sortie** : programmer une semaine entière prend moins de 5 minutes.

## Phase 4 — Passage en application native

Objectif : sortir du navigateur, si nécessaire.

- [ ] Envelopper la PWA avec Capacitor (iOS + Android).
- [ ] Ajouter le partage natif avancé.
- [ ] Ajouter les notifications push natives.
- [ ] Publier sur les stores.
- [ ] Gérer les mises à jour OTA.

**À ne faire que si la PWA ne suffit plus.** Une PWA installée couvre 90 % des besoins.

## Phase 5 — Ouverture

Objectif : permettre à d'autres pages de l'utiliser.

- [ ] Multi-utilisateurs et rôles.
- [ ] Multi-tenant : chaque page a son catalogue, ses logos, ses fonds.
- [ ] Authentification (JWT ou OAuth).
- [ ] Base de données PostgreSQL à la place des fichiers JSON.
- [ ] Stockage objet (S3, R2) pour les MP4.
- [ ] File de jobs (Redis + worker) à la place des threads.
- [ ] Interface d'administration web.
- [ ] Facturation éventuelle.

## Idées hors périmètre immédiat

- Génération de carrousels et d'images statiques.
- Sous-titres en anglais, espagnol, ourdou.
- Voix off ajoutée par-dessus la récitation.
- Détection automatique du thème du verset (miséricorde, avertissement, récit).
- Suggestions de versets selon les événements du calendrier islamique.
- Génération de stories et de posts carrés.
- Analyse de performance des Reels.

## Décisions à prendre

| Question | Options | Recommandation |
|---|---|---|
| Où héberger l'API ? | VPS actuel / Vercel / Fly.io | VPS actuel, l'API doit lancer FFmpeg. |
| Base de données ? | JSON / SQLite / PostgreSQL | SQLite en phase 2, PostgreSQL en phase 5. |
| File de jobs ? | Threads / RQ / Celery | RQ avec Redis en phase 2. |
| Stockage vidéo ? | Disque local / S3 | Disque local + sauvegarde, S3 en phase 5. |
| Auth ? | Aucune / token / OAuth | Token en phase 1, OAuth en phase 5. |
| App native ? | PWA / Capacitor | PWA d'abord. |

## Risques

| Risque | Impact | Mitigation |
|---|---|---|
| API Quran.com indisponible | Rendu impossible | Cache local des textes et traductions. |
| Timings manquants pour un récitateur | Surlignage cassé | Valider les 7 récitateurs au démarrage. |
| Disque plein | Publication arrêtée | Rotation + alerte. |
| TryPost indisponible | Pas de publication | File d'attente et reprise. |
| Changement d'API Facebook | Publication cassée | Surveiller TryPost, garder un mode manuel. |
| Rendu trop lent | Créneau manqué | Rendu à l'avance (2 h avant). |
| Secret exposé | Risque de sécurité | `.env` ignoré, audit avant chaque push. |

## Priorité immédiate

1. Brancher l'app sur l'API.
2. Tester un rendu réel depuis le téléphone.
3. Ajouter les tests du pipeline.
4. Mettre en place la surveillance des créneaux.
