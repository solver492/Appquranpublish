import { useEffect, useState } from "react";
import type { Schedule } from "../types";
import { IconCalendar, IconCheck, IconClock } from "../components/Icons";

type Props = {
  schedule: Schedule | null;
  date: string;
  onDate: (date: string) => void;
  onSave: (update: Partial<Schedule>) => Promise<void>;
  onToggleSlot: (prayer: string, enabled: boolean) => Promise<void>;
  saving: boolean;
};

const statusLabel: Record<string, string> = {
  pending: "En attente",
  scheduled: "Programmé",
  published: "Publié",
  missed: "Manqué",
  failed: "Échec",
};

const shiftDate = (date: string, days: number) => {
  const value = new Date(`${date}T12:00:00`);
  value.setDate(value.getDate() + days);
  return value.toISOString().slice(0, 10);
};

export function ScheduleScreen({ schedule, date, onDate, onSave, onToggleSlot, saving }: Props) {
  const [offset, setOffset] = useState(schedule?.offset_min ?? 10);
  const [city, setCity] = useState(schedule?.city ?? "Casablanca");
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    if (schedule) {
      setOffset(schedule.offset_min);
      setCity(schedule.city);
    }
  }, [schedule]);

  const save = async () => {
    await onSave({ offset_min: offset, city, country: schedule?.country || "Morocco", enabled: true });
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  const label = new Date(`${date}T12:00:00`).toLocaleDateString("fr-FR", {
    weekday: "long",
    day: "numeric",
    month: "long",
  });

  return (
    <div className="stack">
      <section className="date-nav">
        <button className="icon-btn" onClick={() => onDate(shiftDate(date, -1))} aria-label="Jour précédent">
          ‹
        </button>
        <div>
          <span className="eyebrow">
            <IconCalendar size={14} /> Planning
          </span>
          <strong>{label}</strong>
        </div>
        <button className="icon-btn" onClick={() => onDate(shiftDate(date, 1))} aria-label="Jour suivant">
          ›
        </button>
      </section>

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Réglages du jour</h2>
            <p>Un Reel est publié avant chaque adhan.</p>
          </div>
          <span className="chip chip-soft">{schedule?.slots.filter((slot) => slot.enabled).length ?? 0}/5</span>
        </div>
        <label className="field">
          <span>Ville</span>
          <input value={city} onChange={(event) => setCity(event.target.value)} />
        </label>
        <div className="slider-row">
          <div>
            <strong>Publication {offset} min avant l’adhan</strong>
            <small>Marge de sécurité recommandée : 10 min.</small>
          </div>
          <input
            type="range"
            min={0}
            max={45}
            step={5}
            value={offset}
            onChange={(event) => setOffset(Number(event.target.value))}
          />
        </div>
        <button className="btn btn-primary btn-block" onClick={save} disabled={saving}>
          {saved ? <IconCheck size={18} /> : <IconClock size={18} />}
          {saving ? "Enregistrement…" : saved ? "Planning enregistré" : "Enregistrer le planning"}
        </button>
      </section>

      <section className="section">
        <div className="section-head">
          <h2>Créneaux</h2>
          <span className="muted">Heure locale · {schedule?.city || "Casablanca"}</span>
        </div>
        <div className="slot-list">
          {(schedule?.slots || []).map((slot) => (
            <div className={`slot ${slot.enabled ? "" : "slot-off"}`} key={slot.prayer}>
              <div className="slot-time">
                <strong>
                  {new Date(slot.publish_at_local).toLocaleTimeString("fr-FR", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </strong>
                <span>Adhan {new Date(slot.adhan).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" })}</span>
              </div>
              <div className="slot-main">
                <div className="slot-name">
                  <strong>{slot.prayer}</strong>
                  <span>{slot.prayer_ar}</span>
                </div>
                <span className={`badge badge-${slot.status}`}>{statusLabel[slot.status] || slot.status}</span>
              </div>
              <button
                className={`switch ${slot.enabled ? "switch-on" : ""}`}
                onClick={() => onToggleSlot(slot.prayer, !slot.enabled)}
                aria-label={`Activer ${slot.prayer}`}
              />
            </div>
          ))}
        </div>
      </section>

      <section className="panel soft">
        <h3>Comment ça marche</h3>
        <p>
          Le serveur récupère les horaires de prière (Aladhan, méthode 21), génère le Reel suivant et le
          programme sur TryPost 10 minutes avant l’adhan. Les versets avancent sans saut, de 1:1 à 114:6.
        </p>
      </section>
    </div>
  );
}
