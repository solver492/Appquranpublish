import type { Catalog, Job, Reel, RenderSpec, Schedule, Settings } from "./types";

const API_KEY = "badr.apiBase";
const DEFAULT_API = "http://localhost:8000/api";

export function getApiBase(): string {
  return localStorage.getItem(API_KEY) || DEFAULT_API;
}

export function setApiBase(value: string) {
  localStorage.setItem(API_KEY, value.replace(/\/$/, ""));
}

async function request<T>(path: string, options: RequestInit = {}, timeout = 15000): Promise<T> {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeout);
  try {
    const response = await fetch(`${getApiBase()}${path}`, {
      ...options,
      signal: controller.signal,
      headers: {
        "Content-Type": "application/json",
        ...(options.headers || {}),
      },
    });
    if (!response.ok) {
      let detail = `HTTP ${response.status}`;
      try {
        const body = await response.json();
        detail = body.detail || detail;
      } catch {
        /* ignore */
      }
      throw new Error(detail);
    }
    return (await response.json()) as T;
  } finally {
    clearTimeout(timer);
  }
}

export const api = {
  config: () => request<Catalog>("/config"),
  state: () => request<{ surah: number; ayah: number; total_generated: number; mode: string }>("/state"),
  reels: () => request<{ items: Reel[] }>("/reels"),
  reel: (id: string) => request<Reel>(`/reels/${id}`),
  schedule: (date: string) => request<Schedule>(`/schedules/${date}`),
  saveSchedule: (date: string, body: Partial<Schedule>) =>
    request<Schedule>(`/schedules/${date}`, { method: "PUT", body: JSON.stringify(body) }),
  toggleSlot: (date: string, prayer: string, enabled: boolean) =>
    request(`/schedules/${date}/slots/${prayer}`, {
      method: "PATCH",
      body: JSON.stringify({ enabled }),
    }),
  render: (spec: RenderSpec) =>
    request<{ job_id: string; status: string }>("/reels/render", {
      method: "POST",
      body: JSON.stringify(spec),
    }),
  job: (id: string) => request<Job>(`/jobs/${id}`),
  publish: (id: string, when: string | null) =>
    request(`/reels/${id}/publish`, {
      method: "POST",
      body: JSON.stringify({ when, platforms: ["facebook"] }),
    }),
  share: (id: string) =>
    request<{ public_url: string; caption: string; facebook_share_url: string; whatsapp_url: string }>(
      `/reels/${id}/share`,
    ),
  settings: () => request<Settings>("/settings"),
  saveSettings: (body: Partial<Settings>) =>
    request<Settings>("/settings", { method: "PUT", body: JSON.stringify(body) }),
};

export async function pollJob(id: string, onUpdate: (job: Job) => void): Promise<Job> {
  for (let i = 0; i < 180; i += 1) {
    const job = await api.job(id);
    onUpdate(job);
    if (job.status === "completed" || job.status === "failed") return job;
    await new Promise((resolve) => setTimeout(resolve, 1500));
  }
  throw new Error("Le rendu prend trop de temps.");
}
