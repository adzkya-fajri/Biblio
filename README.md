# 📚 Perpustakaan Digital — Android App (Kotlin + Jetpack Compose)

Perpustakaan Digital adalah aplikasi Android modern yang memungkinkan pengguna untuk mencari, membaca, serta meminjam buku secara digital. Dibangun menggunakan Jetpack Compose untuk UI deklaratif yang lebih cepat, bersih, dan efisien.

## TODO
- [ ] Add bookmark functionality
- [x] Add reading progress tracker
- ~~[ ] Add notes/highlights~~
- [x] Add swipe gesture for next/prev chapter
- [x] Add reading statistics
- ~~[ ] Add text-to-speech~~
- [x] Add night mode schedule
- [x] Add font family selection

## Struktur Navigasi AppNavHost
```tree
AppNavHost
├── WelcomeScreen
├── LoginScreen
├── RegisterScreen
├── MainScreen (Auth Guard)
│   ├── BerandaScreen
│   │   └── ProfileScreen
│   │       └── SettingsScreen
│   │           └── AboutScreen
│   ├── CariScreen
│   │   └── BukuScreen
│   ├── KoleksiScreen
│   │   └── FavoriteScreen
│   └── BukuScreen ← shared (dari Beranda/Cari/Favorite/KoleksiScreen)
└── ReaderScreen (Auth Guard)
```
## 📱 Instalasi Aplikasi (APK)

Untuk mencoba aplikasi secara langsung tanpa membangun proyek di Android Studio, kamu dapat mengunduh dan menginstal file APK berikut:

📥 Download APK:
[git](app/release/app-release.apk) , 
[anyone link](https://drive.google.com/file/d/17TPV4_CoAkwqWTKSvTZ7sUpm7Yaajx-L/view?usp=drivesdk)
## Inspirasi
Terinspirasi dari banyak aplikasi seperti Spotify, Google Play Book, dll.
