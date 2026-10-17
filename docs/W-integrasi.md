# Integrasi tugas W ke clone tim

Disiapkan 16 Oktober 2026 berdasarkan ZIP `pjj_java_monitoring-realisasi-anggaran-satker.zip` yang dikirim pengguna. HEAD di dalam ZIP menunjuk branch `dev-aldy`; GitHub online belum diperiksa.

## Penyesuaian terhadap clone

- Nama field/parameter yang sudah ada dipertahankan: `satkerRepository`, `satker`, `paguAkunRepository`, `paguAkun`, `realisasiDAO`, `budgetService`, `monitoringDAO`, `paguAkunDAO`, `conn`, `paguId`, `nilaiPaguBaru` dan nama terkait dalam service.
- Nama package dan signature method publik lama dipertahankan. Controller/service mendapat overload dan method tambahan sesuai kebutuhan W.
- `BudgetService` mempertahankan field DAO, constructor dua DAO, pemformatan rupiah, serta alur lock dan transaksi yang sudah ditulis. Validasi dan error handling dilengkapi; operasi pagu tambahan memakai koneksi transaksi yang sama.
- Constructor RealisasiController satu argumen tetap tersedia, tetapi sekarang memasang service aman. Tidak ada jalur insert realisasi langsung dari controller.
- H2 tetap **2.2.224**, JUnit tetap **5.11.4**, compiler plugin tetap **3.13.0**, source/target Java tetap **25**. Tambahan pom.xml hanya Commons CSV 1.14.1 dan Surefire 3.5.4.
- DAO, model, exception, SQL, CSV asli, DatabaseConnection, SetupDatabase, InisiasiDB, MainConsole, MainGui, README dan SDD tidak diubah.

## Uji

Maven test dijalankan dengan JDK 25.0.4.1 dan H2 2.2.224: **33 tes lulus, 0 failure, 0 error, 0 skipped**. Lihat `docs/hasil-uji-W.txt`. Cakupan termasuk CRUD/validasi, batas uang, identitas pagu, dua transaksi bersamaan, pagu turun bersamaan realisasi, CSV dan rollback kegagalan insert kedua.

Di NetBeans: buka proyek clone -> Clean and Build -> Test. Run File pada `MainUjiW.java` (Shift+F6) untuk menu uji. Gunakan menu 9/tahun 2026 untuk ringkasan awal: pagu Rp4.050.000.000, realisasi Rp1.798.750.000, sisa Rp2.251.250.000, persen 44,41%.

MainUjiW memakai database latihan `./data/uji_w`, terpisah dari database aplikasi. Pada database uji yang belum memiliki tabel, demo dibuat sekali; Run berikutnya memakai data yang sudah ada. MainConsole asli tetap merupakan demo akses data, bukan menu akhir C. Integrasi menu akhir tetap perlu ditinjau bersama C.

Contoh file `imports/contoh_*_W.csv` merupakan data sintetis asisten untuk uji tambahan, bukan salinan lampiran resmi. Jalankan impor Satker sebelum pagu. Preview meminta konfirmasi YA di MainUjiW; semua penambahan satu file dibatalkan jika satu record gagal. Data asli src/resources tetap utuh.

## Kontrak untuk anggota C/Al

Controller dapat dirangkai memakai DAO clone yang sama dan satu BudgetService. Untuk database khusus, gunakan `new BudgetService(pembukaKoneksi)` dan berikan service tersebut ke constructor controller dua argumen, sehingga pembacaan dan transaksi memakai database yang sama. Controller/service melempar exception yang ditampilkan View; View bertanggung jawab atas input tanggal/angka dan dialog/Scanner.

Pagu boleh nol menurut panduan v1.5. Identitas pagu yang sudah memiliki realisasi tidak boleh diganti. Rekap total/bulanan meminta satu tahun tertentu; filter tahun 0 hanya didukung pada rekap Satker/akun. Rekap Satker/akun tahun 0 tetap dipisah per tahun oleh DAO.

Seluruh pengujian paket dilakukan melalui Java/Maven, bukan klik antarmuka NetBeans. Pengguna perlu menjalankan ulang tes pada clone sesudah pemasangan. Tidak ada klaim seluruh fitur anggota lain telah selesai atau selalu kompatibel dengan perubahan repository sesudah snapshot ini.
