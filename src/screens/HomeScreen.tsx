import { useEffect, useMemo, useState } from "react";
import type { Reel, Schedule } from "../types";
import { IconArrowRight, IconClock, IconPlay, IconSparkles } from "../components/Icons";
import type { Tab } from "../components/Shell";

type Props = {
  schedule: Schedule | null;
  reels: Reel[];
  state: { surah: number; ayah: number; total_generated: number } | null;
  onTab: (tab: Tab) => void;
  onOpenReel: (reel: Reel) => void;
};

function countdown(target: string, now: number) {
  const diff = new Date(target).getTime() - now;
  if (diff <= 0) return "maintenant";
  const hours = Math.floor(diff / 3600000);
  const minutes = Math.floor((diff % 3600000) / 60000);
  if (hours > 0) return `${hours} h ${minutes.toString().padStart(2, "0")}`;
  return `${minutes} min`;
}

export function HomeScreen({ schedule, reels, state, onTab, onOpenReel }: Props) {
  const [now, setNow] = useState(Date.now());

  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 30000);
    return () => clearInterval(timer);
  }, []);

  const nextSlot = useMemo(() => {
    if (!schedule) return null;
    return (
      schedule.slots.find(
        (slot) => slot.enabled && new Date(slot.publish_at).getTime() > now - 5 * 60000,
      ) || schedule.slots.find((slot) => slot.enabled)
    );
  }, [schedule, now]);

  const published = reels.filter((reel) => reel.status === "published").length;
  const last = reels[0];

  return (
    <div className="stack">
      <section className="hero">
        <div className="hero-top">
          <span className="eyebrow">
            <IconClock size={15} /> Prochaine publication
          </span>
          <span className="hero-live">5 Reels / jour</span>
        </div>
        {nextSlot ? (
          <>
            <div className="hero-prayer">
              {nextSlot.prayer_ar} <span>{nextSlot.prayer}</span>
            </div>
            <div className="hero-count">{countdown(nextSlot.publish_at, now)}</div>
            <div className="hero-meta">
              <span>Adhan {new Date(nextSlot.adhan).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" })}</span>
              <span>Publication 10 min avant</span>
            </div>
          </>
        ) : (
          <div className="hero-empty">Aucun créneau actif aujourd’hui.</div>
        )}
        <div className="hero-actions">
          <button className="btn btn-gold" onClick={() => onTab("studio")}>
            <IconSparkles size={18} /> Créer un Reel
          </button>
          <button className="btn btn-ghost" onClick={() => onTab("schedule")}>
            Voir le planning
          </button>
        </div>
      </section>

      <section className="stats">
        <div className="stat">
          <strong>{state?.total_generated ?? 0}</strong>
          <span>Reels générés</span>
        </div>
        <div className="stat">
          <strong>{published}</strong>
          <span>Publiés</span>
        </div>
        <div className="stat">
          <strong>
            {state?.surah ?? 1}:{state?.ayah ?? 1}
          </strong>
          <span>Prochain verset</span>
        </div>
      </section>

      <section className="section">
        <div className="section-head">
          <h2>Derniers Reels</h2>
          <button className="text-btn" onClick={() => onTab("library")}>
            Tout voir <IconArrowRight size={15} />
          </button>
        </div>
        <div className="reel-row">
          {reels.slice(0, 4).map((reel) => (
            <button className="reel-card" key={reel.id} onClick={() => onOpenReel(reel)}>
              <div className="reel-thumb">
                <img src={reel.thumbnail} alt="" />
                <span className="reel-duration">{Math.round(reel.duration || 30)}s</span>
                <span className="reel-play">
                  <IconPlay size={18} />
                </span>
              </div>
              <div className="reel-meta">
                <span className={`badge badge-${reel.status}`}>{reel.status}</span>
                <span className="reel-ref">
                  {reel.surah_name || `Sourate ${reel.surah}`} · {reel.ayah_start}–{reel.ayah_end}
                </span>
              </div>
            </button>
          ))}
          {reels.length === 0 && <div className="empty">Aucun Reel pour le moment.</div>}
        </div>
      </section>

      {last && (
        <section className="section">
          <div className="section-head">
            <h2>Dernière légende</h2>
            <button className="text-btn" onClick={() => onOpenReel(last)}>
              Partager <IconArrowRight size={15} />
            </button>
          </div>
          <div className="caption-preview">{last.caption?.slice(0, 220)}…</div>
        </section>
      )}
    </div>
  );
}
