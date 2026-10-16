# Catatan bug modul W

Sumber sebelum: snapshot pjj_java.zip; reproduksi awal tersimpan pada folder analisis-pjj-java-2026-10-16/verifikasi di workspace. Perbaikan paket disiapkan asisten untuk tugas W; pengguna meninjau, memahami dan mengintegrasikan kontribusi sebelum mengumpulkan. Tidak mengklaim pengguna telah menulis atau melakukan pengujian ini sendiri.

| ID | Gejala sebelum | Penyebab | Perbaikan | Bukti tes ulang |
| --- | --- | --- | --- | --- |
| W01 | Realisasi Rp8 juta diterima saat sisa Rp7,2 juta; sisa minus Rp800 ribu | Constructor satu argumen memberi service null, kemudian insert langsung | Semua constructor memasang service non-null; catat selalu melalui service | tepatSisaDiterimaLaluSatuRupiahDitolak; duaTransaksiBersamaanHanyaSatuCommit |
| W02 | Pagu menjadi Rp1 saat realisasi Rp112,8 juta | Controller memanggil repository.update tanpa transaksi service | Penulisan pagu dialihkan ke service, lock dan cek total | paguTidakBolehDiBawahRealisasi; penurunanPaguBersamaanDenganRealisasiTetapAman |
| W03 | Kegiatan pagu terpakai dapat diganti | Tidak ada pemeriksaan identitas pagu | Bandingkan Satker/akun/tahun/kegiatan setelah lock | identitasPaguTerpakaiTidakBolehBerubah (4 kasus) |
| W04 | Nama Satker hanya spasi tersimpan | TODO validasi belum diisi; NOT NULL tidak menolak spasi | Validasi isBlank dan panjang | tolakSatkerKosongDanDuplikat |
| W05 | Input 1.234 tersimpan sebagai 1.23 | Tidak ada validasi skala sebelum kolom DECIMAL membulatkan | Tolak scale >2, cek batas DECIMAL(18,2) | realisasiNominalSalahDitolak; paguNominalSalahDitolak |

Tes tambahan memastikan CSV salah tidak menyimpan sebagian data, termasuk kegagalan insert kedua yang dipaksakan melalui trigger khusus database tes. R dapat menggabungkan catatan ini ke buglog tim setelah meninjau bukti.
