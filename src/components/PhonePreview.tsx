import { useEffect, useState } from "react";
import { asset, sampleArabic, sampleTranslation } from "../mock";

type Props = {
  background: string;
  subtitleStyle: string;
  logo: string;
  translation: boolean;
  watermark: string;
  duration: number;
  reciter: string;
};

const words = sampleArabic.split(" ");

export function PhonePreview({
  background,
  subtitleStyle,
  logo,
  translation,
  watermark,
  duration,
  reciter,
}: Props) {
  const [isPlaying, setIsPlaying] = useState(false);
  const [activeWordIndex, setActiveWordIndex] = useState(3);

  useEffect(() => {
    if (!isPlaying) return;
    const timer = setInterval(() => {
      setActiveWordIndex((idx) => (idx + 1) % words.length);
    }, 650);
    return () => clearInterval(timer);
  }, [isPlaying]);

  const showTranslation = translation && subtitleStyle !== "minimal";
  const showWatermark = subtitleStyle !== "minimal" && watermark.trim().length > 0;
  const goldAll = subtitleStyle === "gold_verse";
  const goldLine = subtitleStyle === "gold_line";
  const activeLine = 3;

  return (
    <div className="preview-wrap">
      <div className="preview-phone">
        <img className="preview-bg" src={asset(`backgrounds/${background}.jpg`)} alt="" />
        <div className="preview-shade" />
        <div className="preview-top">
          <span className="preview-pill">{duration}s</span>
          <span className="preview-pill preview-pill-soft">9:16</span>
        </div>
        {showWatermark && <div className="preview-watermark">{watermark}</div>}
        {logo !== "none" && (
          <div className="preview-logo">
            {logo === "gif2" ? (
              <video src={asset("logo/logo_new_45s.webm")} autoPlay loop muted playsInline />
            ) : (
              <img src={asset("logo/logo_transparent.png")} alt="Logo" />
            )}
          </div>
        )}
        <div className="preview-caption">
          <div className="preview-arabic" dir="rtl">
            {words.map((word, index) => {
              const gold =
                goldAll ||
                (goldLine
                  ? index >= activeLine && index < activeLine + 3
                  : index === (isPlaying ? activeWordIndex : 3));
              return (
                <span key={`${word}-${index}`} className={gold ? "gold" : ""}>
                  {word}{" "}
                </span>
              );
            })}
          </div>
          {showTranslation && <p className="preview-translation">{sampleTranslation}</p>}
          <div className="preview-reciter">🎙️ {reciter}</div>
        </div>
        <button
          className="preview-play"
          onClick={() => setIsPlaying((p) => !p)}
          aria-label={isPlaying ? "Mettre en pause" : "Aperçu de la récitation"}
          style={{ cursor: "pointer", border: "none", background: "none" }}
        >
          <span style={isPlaying ? { borderRadius: "2px", width: "16px", height: "16px", background: "currentColor", transform: "none" } : undefined} />
        </button>
      </div>
    </div>
  );
}
