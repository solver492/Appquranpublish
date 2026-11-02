import { useCallback, useEffect, useState } from "react";
import { api, getApiBase, pollJob, setApiBase } from "./api";
import { Shell, type Tab } from "./components/Shell";
import { ShareSheet } from "./components/ShareSheet";
import { mockCatalog, mockReels, mockSchedule, mockSettings } from "./mock";
import { HomeScreen } from "./screens/HomeScreen";
import { LibraryScreen } from "./screens/LibraryScreen";
import { ScheduleScreen } from "./screens/ScheduleScreen";
import { SettingsScreen } from "./screens/SettingsScreen";
import { StudioScreen } from "./screens/StudioScreen";
import type { Catalog, Job, Reel, RenderSpec, Schedule, Settings } from "./types";

const today = () => {
  const date = new Date();
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 10);
};

const TITLES: Record<Tab, { title: string; subtitle: string }> = {
  home: { title: "Bonjour 👋", subtitle: "Ton studio de Reels Coran, prêt à publier." },
  studio: { title: "Studio", subtitle: "Compose ton Reel en quelques gestes." },
  schedule: { title: "Planning", subtitle: "5 publications par jour, avant chaque adhan." },
  library: { title: "Bibliothèque", subtitle: "Tous tes Reels, prêts à partager." },
  settings: { title: "Réglages", subtitle: "Connexion, page et préférences." },
};

const getStoredReels = (): Reel[] => {
  try {
    const raw = localStorage.getItem("badr.reels");
    if (raw) {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed) && parsed.length > 0) return parsed;
    }
  } catch {
    /* ignore */
  }
  return mockReels;
};

const getStoredState = () => {
  try {
    const raw = localStorage.getItem("badr.state");
    if (raw) return JSON.parse(raw);
  } catch {
    /* ignore */
  }
  return { surah: 2, ayah: 30, total_generated: 22 };
};

const getStoredSchedule = (d: string): Schedule => {
  try {
    const raw = localStorage.getItem(`badr.schedule.${d}`);
    if (raw) return JSON.parse(raw);
  } catch {
    /* ignore */
  }
  return mockSchedule(d);
};

const getStoredSettings = (): Settings => {
  try {
    const raw = localStorage.getItem("badr.settings");
    if (raw) return JSON.parse(raw);
  } catch {
    /* ignore */
  }
  return mockSettings;
};

export default function App() {
  const [tab, setTab] = useState<Tab>("home");
  const [catalog, setCatalog] = useState<Catalog>(mockCatalog);
  const [settings, setSettings] = useState<Settings>(getStoredSettings);
  const [reels, setReels] = useState<Reel[]>(getStoredReels);
  const [state, setState] = useState<{ surah: number; ayah: number; total_generated: number } | null>(getStoredState);
  const [schedule, setSchedule] = useState<Schedule | null>(() => getStoredSchedule(today()));
  const [date, setDate] = useState(today());
  const [demo, setDemo] = useState(true);
  const [loading, setLoading] = useState(true);
  const [apiBase, setApiBaseState] = useState(getApiBase());
  const [generating, setGenerating] = useState(false);
  const [job, setJob] = useState<Job | null>(null);
  const [activeReel, setActiveReel] = useState<Reel | null>(null);
  const [saving, setSaving] = useState(false);
  const [toast, setToast] = useState<string | null>(null);

  const notify = (message: string) => {
    setToast(message);
    setTimeout(() => setToast(null), 3200);
  };

  useEffect(() => {
    if (demo) {
      localStorage.setItem("badr.reels", JSON.stringify(reels));
    }
  }, [reels, demo]);

  useEffect(() => {
    if (demo && state) {
      localStorage.setItem("badr.state", JSON.stringify(state));
    }
  }, [state, demo]);

  useEffect(() => {
    if (demo && schedule) {
      localStorage.setItem(`badr.schedule.${date}`, JSON.stringify(schedule));
    }
  }, [schedule, date, demo]);

  useEffect(() => {
    if (demo && settings) {
      localStorage.setItem("badr.settings", JSON.stringify(settings));
    }
  }, [settings, demo]);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const config = await api.config();
      const [reelsResponse, stateResponse, scheduleResponse] = await Promise.all([
        api.reels(),
        api.state(),
        api.schedule(date),
      ]);
      setCatalog({ ...config, demo_mode: false });
      setSettings(config.settings || mockSettings);
      setReels(reelsResponse.items);
      setState(stateResponse);
      setSchedule(scheduleResponse);
      setDemo(false);
    } catch {
      setCatalog(mockCatalog);
      setSettings(getStoredSettings());
      setReels(getStoredReels());
      setState(getStoredState());
      setSchedule(getStoredSchedule(date));
      setDemo(true);
    } finally {
      setLoading(false);
    }
  }, [date]);

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!demo) {
      api.schedule(date).then(setSchedule).catch(() => undefined);
    }
  }, [date, demo]);

  const generate = async (spec: RenderSpec) => {
    setGenerating(true);
    setJob(null);
    try {
      if (demo) {
        const jobId = `job_${Date.now()}`;
        for (const progress of [8, 22, 41, 63, 81, 94, 100]) {
          await new Promise((resolve) => setTimeout(resolve, 420));
          setJob({
            id: jobId,
            type: "render",
            status: progress === 100 ? "completed" : "running",
            progress,
            created_at: new Date().toISOString(),
            log: [progress === 100 ? "Rendu simulé terminé." : "Assemblage des versets…"],
          });
        }
        const newReel: Reel = {
          ...mockReels[0],
          id: `reel_demo_${Date.now()}`,
          filename: `reel_demo_${Date.now()}.mp4`,
          created_at: new Date().toISOString(),
          surah: spec.surah,
          ayah_start: spec.ayah,
          ayah_end: (spec.ayah || 1) + (spec.verse_count || 1) - 1,
          duration: spec.duration,
          status: "rendered",
          publish: null,
          spec,
        };
        setReels((items) => [newReel, ...items]);
        setState((value) => (value ? { ...value, total_generated: value.total_generated + 1 } : value));
        notify("Reel généré (démo). Ouvre la bibliothèque pour le partager.");
        return;
      }

      const { job_id } = await api.render(spec);
      const finished = await pollJob(job_id, setJob);
      if (finished.status === "failed") {
        notify(finished.error || "Le rendu a échoué.");
        return;
      }
      const response = await api.reels();
      setReels(response.items);
      const fresh = await api.state();
      setState(fresh);
      notify("Reel généré. Il est prêt à être publié.");
    } catch (error) {
      notify(error instanceof Error ? error.message : "Génération impossible.");
    } finally {
      setGenerating(false);
    }
  };

  const publishNow = async (reel: Reel) => {
    if (demo) {
      setReels((items) =>
        items.map((item) =>
          item.id === reel.id
            ? { ...item, status: "published", publish: { url: item.public_url } }
            : item,
        ),
      );
      notify("Publication simulée (mode démo).");
      return;
    }
    await api.publish(reel.id, null);
    const response = await api.reels();
    setReels(response.items);
    notify("Reel publié sur la page Badr Quran.");
  };

  const saveSchedule = async (update: Partial<Schedule>) => {
    setSaving(true);
    try {
      if (demo && schedule) {
        setSchedule({
          ...schedule,
          ...update,
          slots: schedule.slots.map((slot) => ({
            ...slot,
            publish_at: slot.publish_at,
            publish_at_local: slot.publish_at_local,
          })),
        });
      } else {
        const response = await api.saveSchedule(date, update);
        setSchedule(response);
      }
      notify("Planning mis à jour.");
    } catch (error) {
      notify(error instanceof Error ? error.message : "Impossible d’enregistrer le planning.");
    } finally {
      setSaving(false);
    }
  };

  const toggleSlot = async (prayer: string, enabled: boolean) => {
    if (!schedule) return;
    if (demo) {
      setSchedule({
        ...schedule,
        slots: schedule.slots.map((slot) =>
          slot.prayer === prayer ? { ...slot, enabled } : slot,
        ),
      });
      return;
    }
    try {
      await api.toggleSlot(date, prayer, enabled);
      const response = await api.schedule(date);
      setSchedule(response);
    } catch (error) {
      notify(error instanceof Error ? error.message : "Impossible de modifier le créneau.");
    }
  };

  const saveSettings = async (update: Partial<Settings>) => {
    setSettings((value) => ({ ...value, ...update }));
    if (!demo) {
      const response = await api.saveSettings(update);
      setSettings(response);
    }
    notify("Réglages enregistrés.");
  };

  const changeApiBase = (value: string) => {
    setApiBase(value);
    setApiBaseState(value);
  };

  const meta = TITLES[tab];

  return (
    <>
      <Shell
        tab={tab}
        onTab={setTab}
        title={meta.title}
        subtitle={meta.subtitle}
        demo={demo}
      >
        {tab === "home" && (
          <HomeScreen
            schedule={schedule}
            reels={reels}
            state={state}
            onTab={setTab}
            onOpenReel={setActiveReel}
          />
        )}
        {tab === "studio" && (
          <StudioScreen
            catalog={catalog}
            nextVerse={{ surah: state?.surah ?? 1, ayah: state?.ayah ?? 1 }}
            generating={generating}
            job={job}
            onGenerate={generate}
            onResetJob={() => setJob(null)}
          />
        )}
        {tab === "schedule" && (
          <ScheduleScreen
            schedule={schedule}
            date={date}
            onDate={setDate}
            onSave={saveSchedule}
            onToggleSlot={toggleSlot}
            saving={saving}
          />
        )}
        {tab === "library" && (
          <LibraryScreen
            reels={reels}
            loading={loading}
            onOpenReel={setActiveReel}
            onRefresh={() => void load()}
          />
        )}
        {tab === "settings" && (
          <SettingsScreen
            settings={settings}
            apiBase={apiBase}
            demo={demo}
            onApiBase={changeApiBase}
            onSave={saveSettings}
            onReload={() => void load()}
          />
        )}
      </Shell>

      {activeReel && (
        <ShareSheet
          reel={activeReel}
          onClose={() => setActiveReel(null)}
          onPublishNow={activeReel.status === "rendered" ? publishNow : undefined}
        />
      )}

      {toast && <div className="toast">{toast}</div>}
    </>
  );
}
