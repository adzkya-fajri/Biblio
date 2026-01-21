# Integrasi API Biblio (Android ↔ Laravel/Filament)

## Arsitektur

```
Android App
    → Firebase Auth (login)
    → POST /api/auth/firebase (Sanctum token)
    → GET /api/genres/with-books (katalog)
    → GET /api/books/{id}/download (buka buku)

Laravel Backend + Filament Admin
    → Database (Oracle Cloud / PostgreSQL / dll.)
    → MinIO/S3 (file buku)
```

Filament dipakai admin untuk upload buku ke database; aplikasi Android hanya memanggil REST API.

## Konfigurasi Android

Edit `Biblio/local.properties`:

```properties
API_BASE_URL=http://10.0.2.2:8000/api
WEB_CLIENT_ID=your-firebase-web-client-id
```

- **Emulator**: `10.0.2.2` = localhost PC
- **HP fisik**: ganti dengan IP LAN server atau URL production Oracle Cloud

## Tombol di halaman buku

| Tombol | API | Perilaku |
|--------|-----|----------|
| **Baca Saja** | `GET /books/{id}/download?preview=true` | Buku **gratis** (`price=0`): file penuh. Buku **berbayar**: sinopsis saja (403 dari API). |
| **Beli & Baca** | `GET /books/{id}/download` | Akses file penuh (belum ada pembayaran — semua buku bisa diunduh). |

## Backend (sudah disesuaikan)

`BookController::download` menerima query `preview=true` untuk membatasi buku berbayar pada mode Baca Saja.

## Menjalankan backend lokal

```bash
cd biblio-backend
docker compose up -d
php artisan migrate --seed
```

Pastikan Firebase credentials ada di `storage/app/firebase-credentials.json`.

## Oracle Cloud

Set di `.env` backend (bukan di Android):

```env
DB_CONNECTION=oracle
# ... host, service name, user, password Oracle Cloud
```

Android tidak perlu kredensial database — hanya `API_BASE_URL` ke server Laravel.
