# 12 — Déploiement

## Vue d'ensemble

Trois choses à déployer :

1. **Le pipeline** sur un serveur avec FFmpeg (VPS).
2. **L'API** sur le même serveur ou un serveur dédié.
3. **L'application mobile** sur un hébergement statique (Vercel, Netlify, Cloudflare Pages, ou le VPS).

## Prérequis serveur

- Ubuntu 22.04 ou plus.
- Python 3.10+.
- FFmpeg avec `libass`.
- 4 Go de RAM minimum (8 Go recommandé).
- 40 Go de disque (les vidéos s'accumulent).
- Accès réseau sortant vers Quran.com, jsDelivr, EveryAyah, Mixkit, Aladhan.
- Accès réseau entrant pour l'API et les vidéos.

### Installation des dépendances

```bash
sudo apt update
sudo apt install -y python3 python3-venv python3-pip ffmpeg fonts-hosny-amiri fonts-dejavu-core flock
```

Vérifier FFmpeg :

```bash
ffmpeg -version | grep -o 'enable-libass'
```

Si `libass` manque, installer un build complet de FFmpeg.

## Déploiement du pipeline

```bash
git clone https://github.com/<compte>/badr-quran-reels-studio.git
cd badr-quran-reels-studio
cp .env.example pipeline/.env
nano pipeline/.env   # renseigner les secrets
```

Renseigner :

```
TRYPOST_BASE_URL=https://trypost.example.com
TRYPOST_API_KEY=...
TRYPOST_FACEBOOK_PAGE_ID=...
TRYPOST_MEDIA_BASE_URL=https://media.example.com
PRAYER_CITY=Casablanca
PRAYER_COUNTRY=Morocco
PRAYER_METHOD=21
PRAYER_OFFSET_MIN=10
PRAYER_TZ=Africa/Casablanca
PRAYER_LOGO=gif2
```

Télécharger les fonds :

```bash
bash scripts/download-backgrounds.sh
```

Tester un rendu :

```bash
cd pipeline
python3 quran_reels_v2.py --surah 1 --ayah 1 --verses 1 --duration 30 --logo gif2
```

Vérifier le fichier dans `pipeline/output/`.

## Cron du planificateur

```bash
crontab -e
```

Ajouter :

```cron
*/10 * * * * cd /root/badr-quran-reels-studio/pipeline && ./run_prayer.sh >> prayer.log 2>&1
```

`run_prayer.sh` doit être exécutable :

```bash
chmod +x pipeline/run_prayer.sh pipeline/daily_reel.sh pipeline/serve.sh
```

Vérifier :

```bash
tail -f pipeline/prayer.log
python3 prayer_reels.py --status
```

## Serveur média

Les vidéos doivent être accessibles par TryPost. Le plus simple : un serveur statique.

### Option A — nginx

```nginx
server {
    listen 80;
    server_name media.example.com;

    location / {
        root /root/badr-quran-reels-studio/pipeline/output;
        autoindex off;
        add_header Cache-Control "public, max-age=3600";
    }
}
```

### Option B — Python + pm2

```bash
cd pipeline
pm2 start "python3 -m http.server 3200 --directory output" --name reels-viewer
pm2 save
```

Puis :

```
TRYPOST_MEDIA_BASE_URL=http://<ip>:3200
```

### Option C — reverse proxy HTTPS

Mettre nginx devant, avec Certbot :

```bash
sudo certbot --nginx -d media.example.com
```

## Déploiement de l'API

### Avec systemd

```ini
# /etc/systemd/system/badr-api.service
[Unit]
Description=Badr Quran Studio API
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/root/badr-quran-reels-studio/backend
Environment="DATA_DIR=/root/badr-quran-reels-studio/backend/data"
Environment="PUBLIC_MEDIA_BASE_URL=https://media.example.com"
Environment="API_TOKEN=change-me"
ExecStart=/root/badr-quran-reels-studio/backend/.venv/bin/uvicorn app:app --host 0.0.0.0 --port 8000
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

```bash
cd backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
deactivate

sudo systemctl daemon-reload
sudo systemctl enable --now badr-api
sudo systemctl status badr-api
```

### Avec pm2

```bash
cd backend
pm2 start ".venv/bin/uvicorn app:app --host 0.0.0.0 --port 8000" --name badr-api
pm2 save
```

### Reverse proxy

```nginx
server {
    listen 443 ssl;
    server_name api.example.com;

    ssl_certificate     /etc/letsencrypt/live/api.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.example.com/privkey.pem;

    client_max_body_size 200M;

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_read_timeout 300s;
    }
}
```

### Vérification

```bash
curl https://api.example.com/api/health
curl -H "Authorization: Bearer <token>" https://api.example.com/api/config
```

## Déploiement de l'application mobile

### Build

```bash
cd mobile
npm ci
npm run build
```

Le résultat est dans `mobile/dist/`.

### Hébergement statique

**Vercel**

```bash
npx vercel --prod
```

**Netlify**

```bash
npx netlify deploy --prod --dir=dist
```

**Cloudflare Pages**

- Build command : `npm run build`
- Output directory : `dist`
- Root directory : `mobile`

**Sur le VPS avec nginx**

```nginx
server {
    listen 443 ssl;
    server_name app.example.com;

    root /root/badr-quran-reels-studio/mobile/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

### Configuration de l'API dans l'app

L'utilisateur renseigne l'URL de l'API dans **Réglages > Connexion serveur**. Elle est stockée dans `localStorage`.

Pour définir une valeur par défaut, modifier `DEFAULT_API` dans `mobile/src/api.ts`.

### PWA

- `manifest.webmanifest` est servi à la racine.
- L'icône est `icon.svg`.
- Sur iOS, ajouter une icône 180×180 PNG pour l'écran d'accueil.
- Sur Android, Chrome propose l'installation automatiquement.

## Sécurité

### Secrets

- `pipeline/.env` n'est jamais versionné.
- `API_TOKEN` doit être long et aléatoire.
- Ne jamais mettre de clé dans le code mobile.
- Renouveler la clé TryPost si elle a fuité.

### API

- Définir `API_TOKEN` dès que l'API est publique.
- Restreindre `allow_origins` à l'URL de l'application.
- Limiter les tailles de requête.
- Ne pas exposer `/docs` en production si l'API est publique (ou protéger par token).
- Journaliser sans secret.

### Serveur

- Pare-feu : n'ouvrir que 22, 80, 443.
- Pas de port 8000 exposé directement.
- Mises à jour de sécurité automatiques.
- Sauvegardes chiffrées.

## Sauvegardes

À sauvegarder :

| Fichier | Fréquence |
|---|---|
| `pipeline/state.json` | Quotidienne |
| `pipeline/prayer_schedule.json` | Quotidienne |
| `backend/data/*.json` | Quotidienne |
| `pipeline/.env` | Hors ligne, chiffré |
| `pipeline/output/*.json` | Hebdomadaire |
| MP4 publiés | Non, mais conserver les URLs |

Exemple :

```bash
0 3 * * * tar czf /backup/badr-$(date +\%F).tgz \
  /root/badr-quran-reels-studio/pipeline/state.json \
  /root/badr-quran-reels-studio/pipeline/prayer_schedule.json \
  /root/badr-quran-reels-studio/backend/data
```

## Surveillance

### Vérifications quotidiennes

```bash
python3 prayer_reels.py --status
tail -50 prayer.log
df -h
```

### Alertes recommandées

- Créneau `missed` ou `failed`.
- Disque > 80 %.
- API `/health` ne répond pas.
- Aucun Reel généré depuis 24 h.

### Rotation

```bash
# supprimer les MP4 de plus de 30 jours
find pipeline/output -name 'reel_*.mp4' -mtime +30 -delete
```

Conserver les sidecars JSON pour l'historique.

## Mise à jour

```bash
cd /root/badr-quran-reels-studio
git pull
python3 -m py_compile pipeline/*.py backend/app.py
sudo systemctl restart badr-api
```

Pour le mobile :

```bash
cd mobile
npm ci
npm run build
```

## Rollback

```bash
git log --oneline -5
git checkout <commit>
sudo systemctl restart badr-api
```

Le pipeline n'a pas de migration de données : `state.json` reste compatible.

## Dépannage

| Problème | Cause | Solution |
|---|---|---|
| `ffmpeg: command not found` | FFmpeg absent | `apt install ffmpeg` |
| `libass` manquant | Build sans libass | Installer un FFmpeg complet. |
| Police Amiri absente | Paquet non installé | `apt install fonts-hosny-amiri` |
| API 502 | Uvicorn arrêté | `systemctl status badr-api` |
| TryPost refuse la vidéo | URL non accessible | Tester `curl -I <url>` depuis le serveur. |
| Créneau manqué | Rendu trop lent | Générer plus tôt (`PRAYER_LOOKAHEAD_MIN`). |
| Disque plein | Vidéos accumulées | Rotation. |
| App en mode démo | Mauvaise URL d'API | Vérifier l'URL et le CORS. |
| CORS bloqué | Origine non autorisée | Ajuster `allow_origins`. |

## Checklist de mise en production

- [ ] FFmpeg avec libass installé.
- [ ] Polices Amiri et DejaVu présentes.
- [ ] `pipeline/.env` renseigné et non versionné.
- [ ] Fonds téléchargés.
- [ ] Rendu de test réussi.
- [ ] Serveur média accessible publiquement.
- [ ] TryPost configuré et testé.
- [ ] Cron installé.
- [ ] API protégée par token.
- [ ] HTTPS activé.
- [ ] Sauvegardes planifiées.
- [ ] Alertes configurées.
- [ ] Application déployée.
- [ ] URL de l'API renseignée dans l'app.
- [ ] Test de bout en bout : créer, générer, publier, partager.
