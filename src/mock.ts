import type { Catalog, Reel, Schedule, Settings } from "./types";

const base = import.meta.env.BASE_URL;

export const asset = (path: string) => `${base}${path.replace(/^\//, "")}`;

export const bgThumb = (id: string) => asset(`backgrounds/${id}.jpg`);

const today = new Date();
const iso = (hours: number, minutes: number, dayOffset = 0) => {
  const d = new Date(today);
  d.setDate(d.getDate() + dayOffset);
  d.setHours(hours, minutes, 0, 0);
  return d.toISOString();
};

export const mockSettings: Settings = {
  page_name: "Badr Quran — بدر القرآنيات",
  facebook_page_id: "00000000-0000-0000-0000-000000000000",
  defaults: {
    reciter: "yasser_ad_dussary",
    background: "nature_forest",
    subtitle_style: "gold_word",
    logo: "gif2",
    duration: 30,
    verse_count: 2,
  },
  city: "Casablanca",
  country: "Morocco",
  method: 21,
  offset_min: 10,
};

export const mockCatalog: Catalog = {
  reciters: [
    { id: "yasser_ad_dussary", name: "Yasser Ad-Dussary", arabic_name: "ياسر الدوسري", default: true },
    { id: "abdurrahmaan_as_sudais", name: "Abdurrahmaan As-Sudais", arabic_name: "عبد الرحمن السديس" },
    { id: "muhsin_al_qasim", name: "Muhsin Al Qasim", arabic_name: "محسن القاسم" },
    { id: "abu_bakr_ash_shaatree", name: "Abu Bakr Ash-Shaatree", arabic_name: "أبو بكر الشاطري" },
    { id: "hani_ar_rifai", name: "Hani Ar-Rifai", arabic_name: "هاني الرفاعي" },
    { id: "mohamed_al_tablawi", name: "Mohamed Al-Tablawi", arabic_name: "محمد الطبلاوي" },
    { id: "saud_al_shuraim", name: "Saud Al-Shuraim", arabic_name: "سعود الشريم" },
  ],
  backgrounds: [
    { id: "nature_waves", name: "Vagues", kind: "video", thumb: bgThumb("nature_waves") },
    { id: "nature_sea_rocks", name: "Mer et rochers", kind: "video", thumb: bgThumb("nature_sea_rocks") },
    { id: "nature_flowers", name: "Fleurs", kind: "video", thumb: bgThumb("nature_flowers") },
    { id: "nature_waterfall", name: "Cascade", kind: "video", thumb: bgThumb("nature_waterfall") },
    { id: "nature_forest", name: "Forêt", kind: "video", thumb: bgThumb("nature_forest") },
    { id: "nature_bay", name: "Baie", kind: "video", thumb: bgThumb("nature_bay") },
    { id: "nature_clouds", name: "Nuages", kind: "video", thumb: bgThumb("nature_clouds") },
    { id: "nature_meadow", name: "Prairie", kind: "video", thumb: bgThumb("nature_meadow") },
  ],
  subtitle_styles: [
    {
      id: "gold_word",
      name: "Mot par mot (or)",
      description: "Le mot récité passe en or. Traduction française en bas.",
      translation: true,
      watermark: true,
    },
    {
      id: "gold_line",
      name: "Ligne par ligne (or)",
      description: "Toute la ligne en cours passe en or. Plus calme.",
      translation: true,
      watermark: true,
    },
    {
      id: "gold_verse",
      name: "Verset entier en or",
      description: "Texte arabe doré fixe, sans surlignage mobile.",
      translation: true,
      watermark: true,
    },
    {
      id: "minimal",
      name: "Minimal",
      description: "Arabe blanc, sans traduction ni watermark.",
      translation: false,
      watermark: false,
    },
    {
      id: "bilingual",
      name: "Bilingue renforcé",
      description: "Surlignage mot par mot et traduction plus grande.",
      translation: true,
      watermark: true,
    },
  ],
  logos: [
    { id: "png", name: "Logo transparent", kind: "image", file: "logo_transparent.png" },
    { id: "gif2", name: "Logo animé (recommandé)", kind: "video", file: "logo_new_45s.webm" },
    { id: "none", name: "Sans logo", kind: "none", file: null },
  ],
  durations: [30, 60],
  prayers: [
    { id: "Fajr", name: "Fajr", arabic_name: "الفجر" },
    { id: "Dhuhr", name: "Dhuhr", arabic_name: "الظهر" },
    { id: "Asr", name: "Asr", arabic_name: "العصر" },
    { id: "Maghrib", name: "Maghrib", arabic_name: "المغرب" },
    { id: "Isha", name: "Isha", arabic_name: "العشاء" },
  ],
  defaults: mockSettings.defaults,
  settings: mockSettings,
  demo_mode: true,
};

export const mockReels: Reel[] = [
  {
    id: "reel_s002a008_demo",
    filename: "reel_s002a008_demo.mp4",
    public_url: "https://www.facebook.com/reel/0000000000000000",
    thumbnail: bgThumb("nature_forest"),
    created_at: iso(19, 51, -1),
    duration: 30.6,
    surah: 2,
    ayah_start: 8,
    ayah_end: 9,
    surah_name: "Al-Baqarah",
    surah_name_arabic: "البقرة",
    arabic_text:
      "وَمِنَ ٱلنَّاسِ مَن يَقُولُ ءَامَنَّا بِٱللَّهِ وَبِٱلْيَوْمِ ٱلْآخِرِ وَمَا هُم بِمُؤْمِنِينَ",
    translation:
      "Il est des gens qui disent : « Nous croyons en Allah et au Jour Dernier » et qui ne sont point croyants.",
    caption: "📖 Sourate Al-Baqarah (البقرة) — versets 8 à 9\n\n#Coran #Quran #بدر_القرآنيات",
    status: "published",
    publish: { post_id: "post_demo_0001", url: "https://www.facebook.com/reel/0000000000000000" },
  },
  {
    id: "reel_s002a007_demo",
    filename: "reel_s002a007_demo.mp4",
    public_url: "https://www.facebook.com/reel/0000000000000000",
    thumbnail: bgThumb("nature_waterfall"),
    created_at: iso(18, 47, -1),
    duration: 28.2,
    surah: 2,
    ayah_start: 7,
    ayah_end: 7,
    surah_name: "Al-Baqarah",
    surah_name_arabic: "البقرة",
    arabic_text: "خَتَمَ ٱللَّهُ عَلَىٰ قُلُوبِهِمْ وَعَلَىٰ سَمْعِهِمْ",
    translation: "Allah a scellé leurs cœurs et leurs ouïes.",
    caption: "📖 Sourate Al-Baqarah (البقرة) — verset 7\n\n#Coran #Quran",
    status: "published",
    publish: { post_id: "post_demo_0002", url: "https://www.facebook.com/reel/0000000000000000" },
  },
  {
    id: "reel_s002a006_demo",
    filename: "reel_s002a006_demo.mp4",
    public_url: "",
    thumbnail: bgThumb("nature_meadow"),
    created_at: iso(15, 20, -1),
    duration: 32.1,
    surah: 2,
    ayah_start: 6,
    ayah_end: 6,
    surah_name: "Al-Baqarah",
    surah_name_arabic: "البقرة",
    arabic_text: "إِنَّ ٱلَّذِينَ كَفَرُوا۟ سَوَآءٌ عَلَيْهِمْ ءَأَنذَرْتَهُمْ أَمْ لَمْ تُنذِرْهُمْ لَا يُؤْمِنُونَ",
    translation: "Quant à ceux qui ne croient pas, que tu les avertis ou non, ils ne croiront pas.",
    caption: "📖 Sourate Al-Baqarah (البقرة) — verset 6\n\n#Coran #Quran",
    status: "rendered",
  },
  {
    id: "reel_s002a005_demo",
    filename: "reel_s002a005_demo.mp4",
    public_url: "https://www.facebook.com/reel/0000000000000000",
    thumbnail: bgThumb("nature_clouds"),
    created_at: iso(12, 9, -2),
    duration: 30.0,
    surah: 2,
    ayah_start: 5,
    ayah_end: 7,
    surah_name: "Al-Baqarah",
    surah_name_arabic: "البقرة",
    arabic_text: "أُو۟لَـٰٓئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ وَأُو۟لَـٰٓئِكَ هُمُ ٱلْمُفْلِحُونَ",
    translation: "Ceux-là sont sur le bon chemin de leur Seigneur, et ce sont eux qui réussissent.",
    caption: "📖 Sourate Al-Baqarah (البقرة) — versets 5 à 7\n\n#Coran #Quran",
    status: "published",
    publish: { post_id: "post_demo_0003", url: "https://www.facebook.com/reel/0000000000000000" },
  },
];

export const mockSchedule = (date: string): Schedule => ({
  date,
  city: "Casablanca",
  country: "Morocco",
  method: 21,
  offset_min: 10,
  enabled: true,
  slots: [
    { prayer: "Fajr", prayer_ar: "الفجر", adhan: `${date}T05:21:00+01:00`, publish_at: `${date}T05:11:00+01:00`, publish_at_local: `${date}T05:11:00+01:00`, enabled: true, status: "published" },
    { prayer: "Dhuhr", prayer_ar: "الظهر", adhan: `${date}T12:19:00+01:00`, publish_at: `${date}T12:09:00+01:00`, publish_at_local: `${date}T12:09:00+01:00`, enabled: true, status: "published" },
    { prayer: "Asr", prayer_ar: "العصر", adhan: `${date}T15:15:00+01:00`, publish_at: `${date}T15:05:00+01:00`, publish_at_local: `${date}T15:05:00+01:00`, enabled: true, status: "scheduled" },
    { prayer: "Maghrib", prayer_ar: "المغرب", adhan: `${date}T17:42:00+01:00`, publish_at: `${date}T17:32:00+01:00`, publish_at_local: `${date}T17:32:00+01:00`, enabled: true, status: "pending" },
    { prayer: "Isha", prayer_ar: "العشاء", adhan: `${date}T18:57:00+01:00`, publish_at: `${date}T18:47:00+01:00`, publish_at_local: `${date}T18:47:00+01:00`, enabled: false, status: "pending" },
  ],
});

export const sampleArabic = "وَمِنَ ٱلنَّاسِ مَن يَقُولُ ءَامَنَّا بِٱللَّهِ وَبِٱلْيَوْمِ ٱلْآخِرِ وَمَا هُم بِمُؤْمِنِينَ";
export const sampleTranslation = "Il est des gens qui disent : « Nous croyons en Allah et au Jour Dernier » et qui ne sont point croyants.";
