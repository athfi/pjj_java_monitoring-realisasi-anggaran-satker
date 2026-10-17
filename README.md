# Monitoring Realisasi Anggaran Satker

**Panduan penguji · Windows 11 · JDK 25 · NetBeans · PostgreSQL 18 · AI lokal KoboldCpp
Alamat repo: [github.com/athfi/pjj_java_monitoring-realisasi-anggaran-satker](https://github.com/athfi/pjj_java_monitoring-realisasi-anggaran-satker)**

Aplikasi membantu petugas mengelola Satker, pagu dan realisasi serta melihat sisa anggaran. Dataset simulasi memakai skenario **Suku Dinas Pertamanan dan Hutan Kota DKI Jakarta**. Angka resmi dihitung Java; AI memberi jawaban atas teks yang dikirim pengguna.

**Baca dua dokumen saja:** README ini untuk menjalankan dan memeriksa aplikasi; [SDD.md](SDD.md) untuk arsitektur, diagram kelas/DB, aturan bisnis dan bukti pengujian. [Capstone_Action_Learning_Java.pdf](Capstone_Action_Learning_Java.pdf) adalah dokumen tugas asli yang dipertahankan utuh.

**Status verifikasi:** PostgreSQL **18** adalah database penyerahan yang ditetapkan tim. Salinan kode yang diperiksa masih memerlukan penyelarasan driver, koneksi dan startup PostgreSQL; rincian ada di [status implementasi](SDD.md#10-status-implementasi-dan-bukti-uji). Langkah PostgreSQL di bawah adalah persiapan untuk versi final, belum merupakan klaim bahwa koneksi Java ke PostgreSQL sudah lulus. Uji chat AI dapat dilakukan secara terpisah sekarang.

## 1. Buka dan build proyek

1. Unduh/clone repository tim atau ekstrak paket penyerahan. Pastikan `pom.xml`, `src/main/java/MainConsole.java` dan `src/main/java/MainUjiAi.java` tersedia. Jika file AI belum ada, minta paket terbaru kepada tim.
2. NetBeans → **File → Open Project** → pilih **root folder yang berisi `pom.xml` dan `src`**. Gunakan proyek Java 25 ini; folder nested `MonitoringRealisasi/` adalah proyek lain.
3. Pastikan JDK 25 tersedia. Klik kanan proyek → **Clean and Build**. Tunggu **BUILD SUCCESS** dan semua tes lulus. Internet diperlukan untuk dependency awal; setelah model dan dependency tersedia, inferensi AI dapat berjalan lokal.

Tes otomatis yang sudah tercatat memakai database uji terpisah. Hasil tersebut tidak menggantikan uji koneksi PostgreSQL 18.

## 2. Siapkan database PostgreSQL 18

1. Pasang/jalankan [PostgreSQL 18 dan pgAdmin](https://www.postgresql.org/download/windows/). Catat user, password dan port lokal; port standar adalah `5432`.
2. Di pgAdmin, buat database latihan baru bernama `monitoring_anggaran`. Pilih database itu → **Query Tool** → buka `setup_demo.sql` dari root proyek → jalankan. Script **menghapus dan membuat ulang tiga tabel**; gunakan database latihan, bukan database dengan data yang hendak dipertahankan.
3. Hasil awal harus **12 Satker, 26 pagu, 52 realisasi**. Dataset awal tahun 2026: pagu **Rp4.050.000.000**, realisasi **Rp1.798.750.000**, sisa **Rp2.251.250.000**, serapan **44,41%**.
4. Buat **`db.properties` di root proyek, sejajar `pom.xml`**:

   ```properties
   db.url=jdbc:postgresql://localhost:5432/monitoring_anggaran
   db.user=USER_POSTGRESQL_ANDA
   db.password=PASSWORD_POSTGRESQL_ANDA
   ```

   Ganti user/password dan port sesuai perangkat. Pada versi PostgreSQL final, driver Maven `org.postgresql:postgresql` harus tersedia dan startup memakai tabel yang sudah dibuat. [Format koneksi JDBC](https://jdbc.postgresql.org/documentation/use/)
5. Setelah penyelarasan kode tersebut selesai, jalankan **Source Packages → `<default package>` → `MainConsole.java` → Run File**. File yang diperiksa saat ini adalah demo akses data dan rekap; menu pengelolaan akhir perlu dibedakan dari demo ini. Cocokkan angka dengan langkah 3. Jika belum berhasil, catat uji PostgreSQL sebagai **belum lulus**, lalu laporkan pesan error kepada tim.

## 3. Jalankan AI lokal pada perangkat penguji

1. Unduh executable Windows dari [rilis KoboldCpp](https://github.com/LostRuins/koboldcpp/releases). Unduh model teks [gemma-4-E2B-it-qat-UD-Q2_K_XL.gguf](https://huggingface.co/unsloth/gemma-4-E2B-it-qat-GGUF/blob/main/gemma-4-E2B-it-qat-UD-Q2_K_XL.gguf), sekitar 2,19 GB, yang dipakai pada demo tim.
2. Buka KoboldCpp → pilih `.gguf` → **Launch**. Tunggu `Load Text Model OK: True` dan server muncul di port `5001`. Biarkan KoboldCpp terbuka. [Petunjuk resmi](https://github.com/LostRuins/koboldcpp/wiki#quick-start)
3. Buka PowerShell, ambil ID model:

   ```powershell
   $modelInfo = Invoke-RestMethod -Uri 'http://localhost:5001/v1/models'
   $modelInfo.data[0].id
   ```
4. Buat folder **`config` sejajar `pom.xml`**, lalu buat **`config/ai.properties`**:

   ```properties
   ai.enabled=true
   ai.endpoint=http://localhost:5001/v1/chat/completions
   ai.model=GANTI_DENGAN_ID_MODEL_DARI_POWERSHELL
   ```

   Salin ID aktual tanpa tanda kutip. Jika port berbeda, sesuaikan kedua alamat. Pastikan nama file bukan `ai.properties.txt`. Hentikan/jalankan ulang Java setelah mengubah konfigurasi.
5. Klik kanan **`MainUjiAi.java` → Run File**. Klik area input pada panel **Output**, ketik setelah `Anda:`, lalu tekan **Enter**:

   ```text
   Halo, jelaskan fungsi DAO dalam satu kalimat.
   ```
6. Uji data anggaran dengan satu baris berikut:

   ```text
   Data dummy rekap 2026: pagu Rp4.050.000.000; realisasi Rp1.798.750.000; sisa Rp2.251.250.000; serapan 44,41%. Berikan maksimal 3 poin pengamatan tanpa menambah angka atau menebak penyebab.
   ```

   **AI menerima teks tersebut, belum membaca PostgreSQL atau repository secara otomatis. Setiap pertanyaan dikirim tanpa riwayat chat.** Sertakan data pada pesan yang sama. Ketik **`keluar`** untuk berhenti. Kualitas jawaban dan waktu respons bergantung pada model/perangkat.

## 4. Checklist dan bantuan singkat

- [ ] Build dan seluruh tes otomatis berhasil; catat jumlah aktualnya.
- [ ] Koneksi PostgreSQL 18, data awal dan rekap telah diperiksa langsung.
- [ ] Fitur pengelolaan diuji: CRUD Satker, pagu, pencatatan realisasi dan impor jika disertakan; lihat matriks di SDD.
- [ ] Chat AI menjawab; permintaan terlihat di terminal KoboldCpp.
- [ ] Tutup KoboldCpp, lalu kirim pertanyaan: tampil pesan AI tidak tersedia tanpa program berhenti mendadak.
- [ ] Ubah `ai.enabled=false`, restart Java: tampil pesan AI dinonaktifkan. Aplikasi core tetap dapat digunakan tanpa AI.

| Gejala                                  | Periksa                                                                                             |
| --------------------------------------- | --------------------------------------------------------------------------------------------------- |
| `com.google.gson` tidak ditemukan     | Dependency Gson `2.13.2` pada `pom.xml`, lalu build ulang.                                      |
| Koneksi PostgreSQL gagal                | Server/port, database/user/password, driver JDBC dan status implementasi pada SDD.                  |
| AI dinonaktifkan/konfigurasi belum siap | Lokasi `config/ai.properties`, nilai `ai.enabled`, working directory root proyek; restart Java. |
| KoboldCpp tidak dapat dihubungi         | Model sudah dimuat; perintah PowerShell pada langkah 3 berhasil.                                    |
| Waktu tunggu AI habis                   | Periksa progres KoboldCpp; service saat diperiksa menunggu maksimal 120 detik.                      |

**Tim:** Saverio Ratu, Wawan Istiyono, A’thfi Rijal Mufied, Chandra Anshori, Rizaldy Dhaniar. Konfigurasi lokal, password dan model GGUF tidak dikumpulkan sebagai source code.
