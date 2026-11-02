import { useState } from "react";
import type { Settings } from "../types";
import { IconCheck, IconInfo, IconRefresh } from "../components/Icons";

type Props = {
  settings: Settings;
  apiBase: string;
  demo: boolean;
  onApiBase: (value: string) => void;
  onSave: (settings: Partial<Settings>) => Promise<void>;
  onReload: () => void;
};

export function SettingsScreen({ settings, apiBase, demo, onApiBase, onSave, onReload }: Props) {
  const [pageName, setPageName] = useState(settings.page_name);
  const [pageId, setPageId] = useState(settings.facebook_page_id || "");
  const [city, setCity] = useState(settings.city);
  const [offset, setOffset] = useState(settings.offset_min);
  const [saved, setSaved] = useState(false);
  const [api, setApi] = useState(apiBase);

  const save = async () => {
    await onSave({
      page_name: pageName,
      facebook_page_id: pageId,
      city,
      offset_min: offset,
    });
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  return (
    <div className="stack">
      <section className={`status-card ${demo ? "status-demo" : "status-live"}`}>
        <div className="status-icon">{demo ? <IconInfo size={22} /> : <IconCheck size={22} />}</div>
        <div>
          <strong>{demo ? "Mode démonstration" : "Serveur connecté"}</strong>
          <p>
            {demo
              ? "Les données sont simulées. Renseigne l’URL de l’API pour piloter le vrai pipeline."
              : "L’application parle à ton serveur Badr Quran Studio."}
          </p>
        </div>
      </section>

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Connexion serveur</h2>
            <p>Adresse de l’API FastAPI.</p>
          </div>
          <button className="chip chip-soft" onClick={onReload}>
            <IconRefresh size={14} /> Tester
          </button>
        </div>
        <label className="field">
          <span>URL de l’API</span>
          <input
            value={api}
            onChange={(event) => setApi(event.target.value)}
            placeholder="https://mon-serveur.example.com/api"
          />
        </label>
        <button
          className="btn btn-primary btn-block"
          onClick={() => {
            onApiBase(api);
            onReload();
          }}
        >
          Enregistrer l’URL
        </button>
      </section>

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Page Facebook</h2>
            <p>Destination des publications.</p>
          </div>
        </div>
        <label className="field">
          <span>Nom de la page</span>
          <input value={pageName} onChange={(event) => setPageName(event.target.value)} />
        </label>
        <label className="field">
          <span>ID de la page</span>
          <input value={pageId} onChange={(event) => setPageId(event.target.value)} />
        </label>
        <label className="field">
          <span>Ville (horaires de prière)</span>
          <input value={city} onChange={(event) => setCity(event.target.value)} />
        </label>
        <label className="field">
          <span>Marge avant l’adhan (minutes)</span>
          <input
            type="number"
            min={0}
            max={45}
            value={offset}
            onChange={(event) => setOffset(Number(event.target.value))}
          />
        </label>
        <button className="btn btn-primary btn-block" onClick={save}>
          {saved ? <IconCheck size={18} /> : null}
          {saved ? "Réglages enregistrés" : "Enregistrer"}
        </button>
      </section>

      <section className="panel soft">
        <h3>À propos</h3>
        <p>
          Badr Quran Studio génère des Reels Coran de 30 à 60 s : texte arabe RTL, surlignage mot par mot,
          traduction française, récitation, fond nature et logo. Le serveur publie 5 Reels par jour, 10
          minutes avant chaque adhan.
        </p>
        <div className="about-grid">
          <div>
            <span>Version</span>
            <strong>1.0.0</strong>
          </div>
          <div>
            <span>Pipeline</span>
            <strong>quran_reels_v2</strong>
          </div>
          <div>
            <span>Publication</span>
            <strong>TryPost</strong>
          </div>
        </div>
      </section>
    </div>
  );
}
