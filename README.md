# Biblio 
Sebuah aplikasi perpustakaan sederhana.

## TODO
- [ ] Add bookmark functionality
- [ ] Add reading progress tracker
- [ ] Add notes/highlights
- [ ] Add swipe gesture for next/prev chapter
- [ ] Add reading statistics
- [ ] Add text-to-speech
- [ ] Add night mode schedule
- [ ] Add font family selection

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

## Inspirasi
Terinspirasi dari banyak aplikasi seperti Spotify, Google Play Book, dll.