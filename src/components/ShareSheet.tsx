import { useEffect, useState } from "react";
import type { Reel } from "../types";
import { IconCheck, IconDownload, IconFacebook, IconShare, IconX } from "./Icons";

type Props = {
  reel: Reel;
  onClose: () => void;
  onPublishNow?: (reel: Reel) => Promise<void>;
};

export function ShareSheet({ reel, onClose, onPublishNow }: Props) {
  const [copied, setCopied] = useState(false);
  const [publishing, setPublishing] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    const onKey = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onClose]);

  const caption = reel.caption || `Sourate ${reel.surah_name || ""} — ${reel.ayah_start}-${reel.ayah_end}`;
  const url = reel.public_url || "";

  const nativeShare = async () => {
    if (navigator.share) {
      await navigator.share({ title: "Badr Quran", text: caption, url: url || undefined });
    } else {
      await copy();
    }
  };

  const copy = async () => {
    await navigator.clipboard.writeText(`${caption}\n${url}`);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const publishNow = async () => {
    if (!onPublishNow) return;
    setPublishing(true);
    setMessage(null);
    try {
      await onPublishNow(reel);
      setMessage("Publication programmée sur la page Badr Quran.");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Publication impossible.");
    } finally {
      setPublishing(false);
    }
  };

  return (
    <div className="sheet-backdrop" onClick={onClose} role="presentation">
      <div className="sheet" onClick={(event) => event.stopPropagation()} role="dialog" aria-modal="true">
        <div className="sheet-handle" />
        <div className="sheet-head">
          <div>
            <h3>Partager le Reel</h3>
            <p>
              Sourate {reel.surah_name || reel.surah} · versets {reel.ayah_start}–{reel.ayah_end}
            </p>
          </div>
          <button className="icon-btn" onClick={onClose} aria-label="Fermer">
            <IconX size={20} />
          </button>
        </div>

        <div className="share-card">
          <img src={reel.thumbnail} alt="" />
          <div>
            <span className={`badge badge-${reel.status}`}>{reel.status}</span>
            <p>{caption.slice(0, 120)}…</p>
          </div>
        </div>

        <div className="share-actions">
          <button className="share-btn" onClick={nativeShare}>
            <IconShare size={20} />
            Partager
          </button>
          <a
            className="share-btn"
            href={`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url || "https://facebook.com")}`}
            target="_blank"
            rel="noreferrer"
          >
            <IconFacebook size={20} />
            Facebook
          </a>
          <a
            className="share-btn"
            href={`https://wa.me/?text=${encodeURIComponent(`${caption}\n${url}`)}`}
            target="_blank"
            rel="noreferrer"
          >
            WhatsApp
          </a>
          <button className="share-btn" onClick={copy}>
            {copied ? <IconCheck size={20} /> : <IconDownload size={20} />}
            {copied ? "Copié" : "Copier"}
          </button>
        </div>

        <textarea className="caption-box" value={caption} readOnly rows={4} />
        {reel.public_url && (
          <a className="link-row" href={reel.public_url} target="_blank" rel="noreferrer">
            Ouvrir la vidéo publique
          </a>
        )}

        {onPublishNow && (
          <button className="btn btn-primary btn-block" onClick={publishNow} disabled={publishing}>
            {publishing ? "Publication…" : "Publier maintenant sur Facebook"}
          </button>
        )}
        {message && <p className="sheet-message">{message}</p>}
      </div>
    </div>
  );
}
