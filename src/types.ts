export type Reciter = {
  id: string;
  name: string;
  arabic_name?: string;
  default?: boolean;
};

export type Background = {
  id: string;
  name: string;
  kind: "video" | "image";
  thumb: string;
  source?: string;
};

export type SubtitleStyle = {
  id: string;
  name: string;
  description: string;
  translation: boolean;
  watermark: boolean;
};

export type LogoOption = {
  id: string;
  name: string;
  kind: "image" | "video" | "none";
  file: string | null;
};

export type Catalog = {
  reciters: Reciter[];
  backgrounds: Background[];
  subtitle_styles: SubtitleStyle[];
  logos: LogoOption[];
  durations: number[];
  prayers: { id: string; name: string; arabic_name: string }[];
  defaults: Record<string, string | number | boolean>;
  demo_mode?: boolean;
  settings?: Settings;
};

export type Reel = {
  id: string;
  filename: string;
  public_url: string;
  thumbnail: string;
  created_at: string;
  duration?: number;
  surah?: number;
  ayah_start?: number;
  ayah_end?: number;
  surah_name?: string;
  surah_name_arabic?: string;
  arabic_text?: string;
  translation?: string;
  caption?: string;
  status: "rendered" | "scheduled" | "published" | "failed";
  publish?: {
    post_id?: string;
    url?: string;
    scheduled_at?: string;
  } | null;
  spec?: Record<string, unknown>;
};

export type Job = {
  id: string;
  type: string;
  status: "queued" | "running" | "completed" | "failed";
  progress: number;
  created_at: string;
  started_at?: string;
  finished_at?: string;
  duration_seconds?: number;
  reel_id?: string;
  error?: string;
  log: string[];
};

export type Slot = {
  prayer: string;
  prayer_ar: string;
  adhan: string;
  publish_at: string;
  publish_at_local: string;
  enabled: boolean;
  status: "pending" | "scheduled" | "published" | "missed" | "failed";
  reel_id?: string | null;
  post_id?: string | null;
  platform_url?: string | null;
  error?: string | null;
};

export type Schedule = {
  date: string;
  city: string;
  country: string;
  method: number;
  offset_min: number;
  enabled: boolean;
  slots: Slot[];
};

export type Settings = {
  page_name: string;
  facebook_page_id?: string;
  defaults: Record<string, string | number | boolean>;
  city: string;
  country: string;
  method: number;
  offset_min: number;
};

export type RenderSpec = {
  surah?: number;
  ayah?: number;
  verse_count?: number;
  duration: number;
  reciter: string;
  background: string;
  subtitle_style: string;
  logo: string;
  translation: boolean;
  watermark: string;
  publish?: boolean;
  schedule_at?: string | null;
};
