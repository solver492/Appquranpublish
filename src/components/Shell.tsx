import { useCallback, useEffect, useRef, type ReactNode } from "react";
import {
  IconCalendar,
  IconHome,
  IconLibrary,
  IconSettings,
  IconSparkles,
} from "./Icons";

export type Tab = "home" | "studio" | "schedule" | "library" | "settings";

type TabConfig = {
  id: Tab;
  label: string;
  bg: string;
  accent: string;
  icon: (p: { size?: number }) => ReactNode;
};

const TABS: TabConfig[] = [
  {
    id: "home",
    label: "Accueil",
    bg: "rgba(245, 158, 11, 0.18)",
    accent: "#f59e0b",
    icon: (p) => <IconHome size={p.size ?? 22} />,
  },
  {
    id: "studio",
    label: "Studio",
    bg: "rgba(16, 185, 129, 0.18)",
    accent: "#10b981",
    icon: (p) => <IconSparkles size={p.size ?? 22} />,
  },
  {
    id: "schedule",
    label: "Planning",
    bg: "rgba(59, 130, 246, 0.18)",
    accent: "#3b82f6",
    icon: (p) => <IconCalendar size={p.size ?? 22} />,
  },
  {
    id: "library",
    label: "Reels",
    bg: "rgba(249, 115, 22, 0.18)",
    accent: "#f97316",
    icon: (p) => <IconLibrary size={p.size ?? 22} />,
  },
  {
    id: "settings",
    label: "Réglages",
    bg: "rgba(139, 92, 246, 0.18)",
    accent: "#8b5cf6",
    icon: (p) => <IconSettings size={p.size ?? 22} />,
  },
];

type Props = {
  tab: Tab;
  onTab: (tab: Tab) => void;
  title: string;
  subtitle?: string;
  demo: boolean;
  children: ReactNode;
};

export function Shell({ tab, onTab, title, subtitle, demo, children }: Props) {
  const menuRef = useRef<HTMLElement>(null);
  const borderRef = useRef<HTMLDivElement>(null);
  const itemRefs = useRef<(HTMLButtonElement | null)[]>([]);

  const activeIndex = TABS.findIndex((item) => item.id === tab);
  const activeTab = TABS[activeIndex] || TABS[0];

  const moveBorder = useCallback(() => {
    const item = itemRefs.current[activeIndex];
    const border = borderRef.current;
    const menu = menuRef.current;
    if (item && border && menu) {
      const x = item.offsetLeft + item.offsetWidth / 2 - border.offsetWidth / 2;
      border.style.setProperty("--x", `${x}px`);
      menu.style.setProperty("--accent", activeTab.accent);
    }
  }, [activeIndex, activeTab.accent]);

  useEffect(() => {
    moveBorder();
    const timer = setTimeout(moveBorder, 60);
    window.addEventListener("resize", moveBorder);
    return () => {
      clearTimeout(timer);
      window.removeEventListener("resize", moveBorder);
    };
  }, [moveBorder]);

  return (
    <div
      className="app"
      style={{ "--stage": activeTab.bg, "--accent": activeTab.accent } as React.CSSProperties}
    >
      <div className="app-glow" aria-hidden="true" />
      <header className="topbar">
        <div className="brand">
          <div className="brand-mark">ب</div>
          <div>
            <div className="brand-name">Badr Quran</div>
            <div className="brand-sub">Studio Reels</div>
          </div>
        </div>
        <div className={`conn ${demo ? "conn-demo" : "conn-live"}`}>
          <span className="dot" />
          {demo ? "Démo" : "Connecté"}
        </div>
      </header>

      <main className="main">
        <div className="page-head">
          <h1>{title}</h1>
          {subtitle && <p>{subtitle}</p>}
        </div>
        {children}
      </main>

      <nav className="menu" id="menu" ref={menuRef} aria-label="Navigation principale">
        <div className="menu__border" id="border" ref={borderRef}>
          <svg viewBox="0 0 110 30" preserveAspectRatio="none">
            <path d="M0 30 C 22 30, 28 0, 55 0 C 82 0, 88 30, 110 30 Z" />
          </svg>
        </div>

        {TABS.map(({ id, label, bg, accent, icon: Icon }, index) => {
          const isActive = tab === id;
          return (
            <button
              key={id}
              ref={(el) => {
                itemRefs.current[index] = el;
              }}
              className={`menu__item ${isActive ? "active" : ""}`}
              data-bg={bg}
              data-accent={accent}
              onClick={() => onTab(id)}
              aria-label={label}
              title={label}
            >
              <Icon size={24} />
            </button>
          );
        })}
      </nav>
    </div>
  );
}
