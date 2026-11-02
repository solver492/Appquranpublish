import { useState } from "react";
import type { Reel } from "../types";
import { IconPlay, IconRefresh, IconShare } from "../components/Icons";

type Props = {
  reels: Reel[];
  loading: boolean;
  onOpenReel: (reel: Reel) => void;
  onRefresh: () => void;
};

const FILTERS = [
  { id: "all", label: "Tous" },
  { id: "published", label: "Publiés" },
  { id: "rendered", label: "À publier" },
] as const;

export function LibraryScreen({ reels, loading, onOpenReel, onRefresh }: Props) {
  const [filter, setFilter] = useState<(typeof FILTERS)[number]["id"]>("all");

  const visible = reels.filter((reel) => {
    if (filter === "all") return true;
    if (filter === "published") return reel.status === "published";
    return reel.status === "rendered" || reel.status === "scheduled";
  });

  return (
    <div className="stack">
      <div className="filter-row">
        {FILTERS.map((item) => (
          <button
            key={item.id}
            className={`chip ${filter === item.id ? "chip-active" : ""}`}
            onClick={() => setFilter(item.id)}
          >
            {item.label}
          </button>
        ))}
        <button className="chip chip-icon" onClick={onRefresh} aria-label="Rafraîchir">
          <IconRefresh size={15} />
        </button>
      </div>

      <div className="library-grid">
        {visible.map((reel) => (
          <article className="library-card" key={reel.id}>
            <button className="library-thumb" onClick={() => onOpenReel(reel)}>
              <img src={reel.thumbnail} alt="" />
              <span className="reel-duration">{Math.round(reel.duration || 30)}s</span>
              <span className="reel-play">
                <IconPlay size={18} />
              </span>
              <span className={`badge badge-${reel.status} library-badge`}>{reel.status}</span>
            </button>
            <div className="library-meta">
              <div>
                <strong>
                  {reel.surah_name || `Sourate ${reel.surah}`} · {reel.ayah_start}–{reel.ayah_end}
                </strong>
                <span>{new Date(reel.created_at).toLocaleDateString("fr-FR")}</span>
              </div>
              <button className="icon-btn" onClick={() => onOpenReel(reel)} aria-label="Partager">
                <IconShare size={18} />
              </button>
            </div>
          </article>
        ))}
      </div>

      {!loading && visible.length === 0 && (
        <div className="empty">
          <strong>Aucun Reel ici.</strong>
          <span>Génère ton premier Reel depuis le Studio.</span>
        </div>
      )}
      {loading && <div className="empty">Chargement…</div>}
    </div>
  );
}
