# Monitoring Realisasi Anggaran Satker

**Capstone Project – Action Learning PJJ Java Fundamental 2026**  
**Topik B | Java + JDBC + MySQL/PostgreSQL**

## Deskripsi

Aplikasi sederhana berbasis Java untuk mengelola pagu dan memantau realisasi anggaran Satuan Kerja (Satker). Aplikasi mencatat transaksi realisasi berdasarkan nomor SP2D, memvalidasi penggunaan anggaran agar tidak melebihi sisa pagu, serta menyajikan rekapitulasi realisasi per akun dan Satker.

Proyek ini dikembangkan sebagai implementasi kompetensi Java Fundamental, Object-Oriented Programming (OOP), JDBC, database relasional, dan penanganan exception.

## Anggota Kelompok

| No. | Nama | NIP |
|---:|---|---|
| 1 | Saverio Ratu | 198511232007101001 |
| 2 | Wawan Istiyono | 198701232010121004 |
| 3 | A’thfi Rijal Mufied | 198806162009121001 |
| 4 | Chandra Anshori | 199508132015021001 |
| 5 | Rizaldy Dhaniar | 199812162018011003 |

## Fitur Utama

- **Manajemen Satker:** CRUD data satuan kerja.
- **Manajemen Pagu:** Pencatatan dan pengelolaan pagu anggaran per akun.
- **Pencatatan Realisasi:** Penyimpanan transaksi berdasarkan nomor SP2D, tanggal, dan nilai realisasi.
- **Validasi Anggaran:** Menolak realisasi yang melebihi sisa pagu.
- **Rekapitulasi:** Menghitung total pagu, total realisasi, sisa pagu, dan persentase realisasi per akun maupun Satker.
- **Error Handling:** Validasi input dan penanganan kesalahan database dengan pesan yang informatif.

## Teknologi

- Java (JDK 25 LTS)
- Apache NetBeans 31
- Maven
- JDBC
- H2 Database Engine
- Java Swing atau antarmuka berbasis konsol

## Menjalankan Aplikasi

1. Clone repository ini.
2. Buka proyek menggunakan Apache NetBeans atau IDE yang mendukung Maven.
3. Pastikan dependency H2 JDBC tersedia dalam pom.xml.
4. Periksa konfigurasi koneksi database.
5. Jalankan kelas utama aplikasi.

## Aturan Bisnis Utama

- Kode Satker harus unik.
- Setiap kombinasi Satker dan akun hanya memiliki satu data pagu.
- Nilai pagu dan realisasi harus lebih besar dari nol.
- Realisasi hanya dapat dicatat jika Satker dan pagu akun tersedia.
- Total realisasi tidak boleh melebihi pagu akun.

**Rumus perhitungan:**

- Sisa Pagu = Pagu − Total Realisasi
- Persentase Realisasi = (Total Realisasi / Pagu) × 100%

## Luaran Capstone

- Source code Java.
- Script SQL untuk struktur database dan data dummy.
- Laporan proyek dalam format PDF.
- Video demonstrasi aplikasi.

## Catatan

Proyek ini ditujukan untuk pembelajaran dan demonstrasi pemrograman. Seluruh data yang digunakan merupakan data dummy dan bukan data transaksi anggaran riil.
