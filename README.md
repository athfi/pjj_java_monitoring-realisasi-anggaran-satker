# Monitoring Realisasi Anggaran Satker

**Capstone Project – Action Learning PJJ Java Fundamental 2026**  
**Topik B | Java + JDBC + PostgreSQL**

## Deskripsi

Aplikasi Java untuk mengelola data satuan kerja (Satker), pagu akun, dan transaksi realisasi anggaran. Aplikasi memantau sisa pagu, menolak realisasi yang melampaui anggaran tersedia, serta menyajikan rekapitulasi per akun dan Satker.

Proyek ini menerapkan Java Fundamental, Object-Oriented Programming (OOP), JDBC, basis data relasional, dan penanganan exception.

## Anggota Kelompok

| No. | Nama | NIP |
|---:|---|---|
| 1 | Saverio Ratu | 198511232007101001 |
| 2 | Wawan Istiyono | 198701232010121004 |
| 3 | A’thfi Rijal Mufied | 198806162009121001 |
| 4 | Chandra Anshori | 199508132015021001 |
| 5 | Rizaldy Dhaniar | 199812162018011003 |

## Status Repository

Saat ini repository berisi dokumentasi proyek dan script database H2. Source code Java dan `pom.xml` belum tersedia di repository ini, sehingga clone repository ini saja belum cukup untuk menjalankan aplikasi.

## Fitur

- CRUD data Satker.
- Pengelolaan pagu akun dan kegiatan anggaran.
- Pencatatan dan pembacaan realisasi berdasarkan nomor SP2D yang unik.
- Validasi agar realisasi baru tidak melebihi sisa pagu.
- Rekapitulasi pagu, realisasi, sisa, dan persentase per akun atau Satker.
- Pesan kesalahan yang mudah dipahami untuk input tidak valid dan masalah database.

Console adalah antarmuka awal. GUI Swing merupakan tambahan opsional setelah fitur utama berjalan.

## Teknologi dan Database

- Java 25
- Apache NetBeans 31
- Maven
- JDBC
- H2 2.2.224, embedded dan berbasis file
- Antarmuka Console; Swing opsional

Catatan pilihan database: soal capstone awal menyebut MySQL atau PostgreSQL, sedangkan desain proyek saat ini menggunakan PostgreSQL. Catat perubahan ini dalam laporan dan pastikan kesesuaiannya dengan arahan pengajar.

## Menjalankan Aplikasi

Langkah berikut berlaku setelah source code Java dan `pom.xml` tersedia di repository:

1. Clone repository dan buka folder proyek yang berisi `pom.xml` di NetBeans atau IDE yang mendukung Maven.
2. Pastikan JDK 25 tersedia dan dependency H2 2.2.224 tercantum di `pom.xml`.
3. Siapkan konfigurasi H2 lokal. Contoh URL: `jdbc:h2:file:./data/monitoring_anggaran` dengan pengguna `sa` dan password kosong.
4. Jalankan `setup_demo_h2.sql` secara manual untuk membuat ulang tabel dan data dummy. **Script ini menghapus tabel yang ada; jangan jalankan saat aplikasi dibuka atau pada database yang datanya ingin dipertahankan.**
5. Jalankan `id.capstone.anggaran.MainConsole`.

Setup awal berisi 12 Satker, 26 pagu, dan 52 realisasi. Database berbasis file menyimpan data pada komputer masing-masing anggota.

## Aturan Bisnis

- Kode dan nama Satker wajib diisi; kode Satker harus unik.
- Setiap kombinasi Satker, akun, tahun anggaran, dan kegiatan hanya memiliki satu pagu.
- Nilai pagu harus nol atau positif; nilai realisasi harus lebih besar dari nol.
- Nomor SP2D wajib dan unik.
- Realisasi hanya dapat dicatat untuk pagu yang tersedia dan tidak boleh melebihi sisa pagu.
- Satker atau pagu yang masih memiliki data terkait tidak dapat dihapus.
- Realisasi yang sudah tersimpan hanya dapat dibaca melalui aplikasi.

**Rumus:**

- Sisa pagu = nilai pagu − total realisasi.
- Persentase realisasi = total realisasi ÷ nilai pagu × 100%.
- Jika pagu bernilai nol, persentase ditampilkan sebagai 0%.

## Luaran Capstone

- Source code Java.
- Script SQL untuk struktur database dan data dummy.
- Laporan proyek dalam format PDF.
- Video demonstrasi aplikasi.

Seluruh data dalam proyek ini adalah data simulasi, bukan data transaksi anggaran riil.
