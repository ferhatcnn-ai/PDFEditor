# PDF Editor - Android

Sade ve hızlı bir Android PDF düzenleyici uygulaması.

## Özellikler

- 📄 **PDF Görüntüleme** — Akıcı kaydırma ve zum desteğiyle PDF okuma
- ✏️ **Elle Çizim** — Serbest kalem ve işaretleyici aracı
- 🔤 **Metin Ekleme** — İstediğiniz konuma yazı ekleyin, boyut ve renk seçin
- ✍️ **İmza** — Parmağınızla çizin, PDF'e ekleyin
- 🔷 **Şekiller** — Dikdörtgen, daire, çizgi ve ok ekleyin
- 🗑️ **Silgi** — Çizgileri silin
- ↩️ **Geri/Yinele** — Sınırsız geri alma
- 💾 **Kaydetme** — Değişiklikleri PDF'e uygulayın
- 📤 **Paylaşma** — PDF'i diğer uygulamalarla paylaşın
- 🌍 **Çoklu Dil** — Türkçe ve İngilizce

## Minimum Gereksinimler

- Android 7.0 (API 24) ve üzeri
- ~50 MB depolama alanı

## Kurulum

```bash
git clone https://github.com/KULLANICI_ADINIZ/PDFEditor.git
cd PDFEditor
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Kullanılan Kütüphaneler

| Kütüphane | Amaç |
|-----------|------|
| android-pdf-viewer | PDF görüntüleme |
| iText7 | PDF düzenleme / kaydetme |
| signature-pad | İmza çizimi |
| Material Components | UI |

## Proje Yapısı

```
app/src/main/
├── java/com/pdfeditor/app/
│   ├── ui/
│   │   ├── activities/   ← MainActivity, EditorActivity
│   │   ├── adapters/     ← RecentFilesAdapter
│   │   ├── dialogs/      ← SignatureDialog, TextInputDialog
│   │   └── views/        ← DrawingOverlayView (özel çizim katmanı)
│   └── utils/            ← PdfEditUtils
└── res/
    ├── layout/
    ├── values/           ← Renkler, temalar, İngilizce metinler
    └── values-tr/        ← Türkçe metinler
```
