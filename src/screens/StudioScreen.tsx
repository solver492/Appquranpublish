import { useMemo, useState } from "react";
import type { Catalog, Job, RenderSpec } from "../types";
import { PhonePreview } from "../components/PhonePreview";
import { IconCheck, IconRefresh, IconSparkles } from "../components/Icons";
import { asset } from "../mock";

type Props = {
  catalog: Catalog;
  nextVerse: { surah: number; ayah: number };
  generating: boolean;
  job: Job | null;
  onGenerate: (spec: RenderSpec) => void;
  onResetJob: () => void;
};

const numberValue = (value: string, fallback: number) => {
  const parsed = Number.parseInt(value, 10);
  return Number.isFinite(parsed) ? parsed : fallback;
};

export function StudioScreen({ catalog, nextVerse, generating, job, onGenerate, onResetJob }: Props) {
  const defaults = catalog.defaults;
  const [surah, setSurah] = useState(String(nextVerse.surah));
  const [ayah, setAyah] = useState(String(nextVerse.ayah));
  const [verseCount, setVerseCount] = useState(Number(defaults.verse_count) || 2);
  const [duration, setDuration] = useState<number>(Number(defaults.duration) || 30);
  const [reciter, setReciter] = useState(String(defaults.reciter));
  const [background, setBackground] = useState(String(defaults.background));
  const [subtitleStyle, setSubtitleStyle] = useState(String(defaults.subtitle_style));
  const [logo, setLogo] = useState(String(defaults.logo));
  const [translation, setTranslation] = useState(true);
  const [watermark, setWatermark] = useState(String(defaults.watermark || "بدر القرآنيات"));

  const selectedReciter = useMemo(
    () => catalog.reciters.find((item) => item.id === reciter) || catalog.reciters[0],
    [catalog.reciters, reciter],
  );
  const selectedStyle = useMemo(
    () => catalog.subtitle_styles.find((item) => item.id === subtitleStyle),
    [catalog.subtitle_styles, subtitleStyle],
  );

  const spec: RenderSpec = {
    surah: numberValue(surah, nextVerse.surah),
    ayah: numberValue(ayah, nextVerse.ayah),
    verse_count: verseCount,
    duration,
    reciter,
    background,
    subtitle_style: subtitleStyle,
    logo,
    translation,
    watermark,
  };

  return (
    <div className="stack studio">
      <PhonePreview
        background={background}
        subtitleStyle={subtitleStyle}
        logo={logo}
        translation={translation && (selectedStyle?.translation ?? true)}
        watermark={watermark}
        duration={duration}
        reciter={selectedReciter?.name || "Récitateur"}
      />

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Versets</h2>
            <p>La progression suit l’ordre du Coran.</p>
          </div>
          <button
            className="chip chip-soft"
            onClick={() => {
              setSurah(String(nextVerse.surah));
              setAyah(String(nextVerse.ayah));
            }}
          >
            <IconRefresh size={14} /> Prochain
          </button>
        </div>
        <div className="grid-3">
          <label className="field">
            <span>Sourate</span>
            <input
              inputMode="numeric"
              value={surah}
              onChange={(event) => setSurah(event.target.value.replace(/\D/g, ""))}
            />
          </label>
          <label className="field">
            <span>Verset</span>
            <input
              inputMode="numeric"
              value={ayah}
              onChange={(event) => setAyah(event.target.value.replace(/\D/g, ""))}
            />
          </label>
          <label className="field">
            <span>Nombre</span>
            <div className="stepper">
              <button onClick={() => setVerseCount((n) => Math.max(1, n - 1))} aria-label="Moins">
                −
              </button>
              <strong>{verseCount}</strong>
              <button onClick={() => setVerseCount((n) => Math.min(10, n + 1))} aria-label="Plus">
                +
              </button>
            </div>
          </label>
        </div>
        <div className="segmented">
          {catalog.durations.map((value) => (
            <button
              key={value}
              className={duration === value ? "seg-active" : ""}
              onClick={() => setDuration(value)}
            >
              {value} secondes
            </button>
          ))}
        </div>
      </section>

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Récitateur</h2>
            <p>Voix et timings mot par mot.</p>
          </div>
        </div>
        <div className="option-list">
          {catalog.reciters.map((item) => (
            <button
              key={item.id}
              className={`option ${reciter === item.id ? "option-active" : ""}`}
              onClick={() => setReciter(item.id)}
            >
              <div className="option-avatar">🎙️</div>
              <div className="option-text">
                <strong>{item.name}</strong>
                {item.arabic_name && <span>{item.arabic_name}</span>}
              </div>
              {reciter === item.id && <IconCheck size={18} />}
            </button>
          ))}
        </div>
      </section>

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Arrière-plan</h2>
            <p>Vidéos nature, sans être humain.</p>
          </div>
          <span className="chip chip-soft">{catalog.backgrounds.length} fonds</span>
        </div>
        <div className="bg-grid">
          {catalog.backgrounds.map((item) => (
            <button
              key={item.id}
              className={`bg-tile ${background === item.id ? "bg-active" : ""}`}
              onClick={() => setBackground(item.id)}
              title={item.name}
            >
              <img src={item.thumb.startsWith("http") ? item.thumb : asset(`backgrounds/${item.id}.jpg`)} alt={item.name} />
              <span>{item.name}</span>
              {background === item.id && (
                <em>
                  <IconCheck size={14} />
                </em>
              )}
            </button>
          ))}
        </div>
      </section>

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Écriture des sous-titres</h2>
            <p>Style, couleur et traduction.</p>
          </div>
        </div>
        <div className="style-list">
          {catalog.subtitle_styles.map((item) => (
            <button
              key={item.id}
              className={`style-card ${subtitleStyle === item.id ? "style-active" : ""}`}
              onClick={() => {
                setSubtitleStyle(item.id);
                setTranslation(item.translation);
              }}
            >
              <div className={`style-preview style-${item.id}`} dir="rtl">
                <span>ٱلْحَمْدُ</span> <span className="gold">لِلَّهِ</span> <span>رَبِّ</span>
              </div>
              <div className="style-text">
                <strong>{item.name}</strong>
                <span>{item.description}</span>
              </div>
              {subtitleStyle === item.id && <IconCheck size={18} />}
            </button>
          ))}
        </div>
        <label className="switch-row">
          <span>
            <strong>Traduction française</strong>
            <small>Afficher la traduction sous l’arabe</small>
          </span>
          <button
            className={`switch ${translation ? "switch-on" : ""}`}
            onClick={() => setTranslation((value) => !value)}
            aria-label="Activer la traduction"
          />
        </label>
        <label className="field">
          <span>Watermark</span>
          <input value={watermark} onChange={(event) => setWatermark(event.target.value)} />
        </label>
      </section>

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Logo</h2>
            <p>Incrusté en haut à droite.</p>
          </div>
        </div>
        <div className="logo-row">
          {catalog.logos.map((item) => (
            <button
              key={item.id}
              className={`logo-tile ${logo === item.id ? "logo-active" : ""}`}
              onClick={() => setLogo(item.id)}
            >
              {item.kind === "none" ? (
                <span className="logo-none">Ø</span>
              ) : (
                <img src={asset("logo/logo_transparent.png")} alt="" />
              )}
              <span>{item.name}</span>
            </button>
          ))}
        </div>
      </section>

      {job && (
        <section className={`job ${job.status === "failed" ? "job-failed" : ""}`}>
          <div className="job-head">
            <strong>
              {job.status === "completed"
                ? "Reel prêt"
                : job.status === "failed"
                  ? "Rendu échoué"
                  : "Génération en cours"}
            </strong>
            <span>{job.progress}%</span>
          </div>
          <div className="progress">
            <span style={{ width: `${job.progress}%` }} />
          </div>
          <p>{job.error || job.log[job.log.length - 1] || "Préparation…"}</p>
          {(job.status === "completed" || job.status === "failed") && (
            <button className="text-btn" onClick={onResetJob}>
              Nouveau rendu
            </button>
          )}
        </section>
      )}

      <button
        className="btn btn-gold btn-block generate"
        onClick={() => onGenerate(spec)}
        disabled={generating}
      >
        <IconSparkles size={18} />
        {generating ? "Génération du Reel…" : "Générer le Reel"}
      </button>
    </div>
  );
}
