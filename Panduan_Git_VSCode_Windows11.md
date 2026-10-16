# Panduan Singkat Git di VS Code — Windows 11

**Tujuan:** mengambil repo tim, menemukan file, mengerjakan tugas dan mengirim perubahan. Contoh memakai GitHub dan branch utama `main`; gunakan nama branch utama tim jika berbeda.

**Alur kerja:** Clone sekali → buka file → Pull → buat branch → edit → Stage → Commit → Push → Pull Request.

## 1. Persiapan — Sekali Saja

1. Pasang VS Code dan [Git for Windows](https://git-scm.com/install/windows), lalu buka ulang VS Code.
2. Terima undangan akses repo GitHub tim. Minta URL repo kepada C/W.
3. Pilih **Terminal → New Terminal**, lalu jalankan per baris. Ganti nama/email:

```powershell
git --version
git config --global user.name "Nama Anda"
git config --global user.email "email-anda@example.com"
```

Perintah pertama menampilkan versi Git. Nama/email adalah [identitas commit](https://git-scm.com/book/en/v2/Getting-Started-First-Time-Git-Setup), bukan password login.

## 2. Clone Repo ke Komputer — Sekali Saja

**Clone** berarti membuat salinan repo beserta riwayatnya di komputer.

1. Pada halaman repo GitHub, pilih **Code → HTTPS** dan salin URL yang berakhiran `.git`.
2. Di VS Code tekan **Ctrl+Shift+P**, pilih [Git: Clone](https://code.visualstudio.com/docs/sourcecontrol/repos-remotes), lalu tempel URL.
3. Pilih folder induk lokal, misalnya `D:\Proyek`. Git membuat folder repo di dalamnya.
4. Pilih **Open**. Jika diminta login, ikuti autentikasi browser dengan akun yang punya akses.

**Selesai jika:** file repo dan nama branch terlihat. Berikutnya buka lewat **File → Open Recent**; cukup clone sekali.

## 3. Temukan File yang Dibutuhkan

| Yang ingin dilakukan | Tombol/menu |
| --- | --- |
| Melihat folder dan file | **Explorer**, Ctrl+Shift+E |
| Membuka file berdasarkan nama | **Ctrl+P**, ketik misalnya `Satker.java` |
| Mencari teks di seluruh proyek | **Ctrl+Shift+F** |
| Melihat perubahan Git | **Source Control**, Ctrl+Shift+G |
| Mencari perintah VS Code/Git | **Ctrl+Shift+P** |
| Membaca Markdown dengan tampilan rapi | Buka `.md`, lalu **Ctrl+Shift+V** |

Baca `README.md`, lalu cari `pom.xml`, `MainConsole.java`, panduan dan `setup_demo_h2.sql`. Folder berisi `pom.xml` adalah proyek Maven. [Navigasi VS Code](https://code.visualstudio.com/docs/editing/getting-started/userinterface) menyediakan Explorer dan pencarian file.

**Melihat branch anggota lain:** pastikan Changes dan Staged Changes kosong → **Ctrl+Shift+P → Git: Fetch** → klik nama branch kiri bawah → pilih branch rekan, misalnya `origin/at/model`. Fetch memperbarui informasi branch online; berpindah branch mengganti isi folder. File yang belum terlihat di `main` mungkin masih berada di branch anggota.

Untuk menjalankan Java di NetBeans, buka **folder hasil clone yang sama**. Edit dari NetBeans juga terlihat di Source Control VS Code.

## 4. Ambil Kode Terbaru dan Buat Branch Tugas

Pastikan **Changes dan Staged Changes kosong**: commit pekerjaan sebelumnya sebelum berganti branch atau Pull.

1. Klik nama branch di kiri bawah, pilih `main`.
2. Tekan **Ctrl+Shift+P → Git: Pull** untuk mengambil perubahan online ke branch aktif.
3. Tekan **Ctrl+Shift+P → Git: Create Branch…**.
4. Beri nama sesuai anggota/tugas, misalnya `w/validasi-realisasi`. Pastikan nama branch baru tampil.

Kerjakan perubahan pada [branch tugas sendiri](https://code.visualstudio.com/docs/sourcecontrol/branches-worktrees), lalu usulkan penggabungannya ke `main`.

## 5. Edit, Periksa, Stage dan Commit

1. Ubah file sesuai tugas, **Ctrl+S**, lalu jalankan/uji aplikasinya.
2. Buka **Source Control**. Klik file di **Changes** untuk membandingkan isi sebelum/sesudah perubahan. `M` berarti diubah; `U` berarti file baru.
3. Klik **+** di samping file yang ingin dikirim. **Stage** memilih perubahan untuk commit; file masuk ke **Staged Changes**.
4. Isi pesan singkat, misalnya `Validasi realisasi agar tidak melebihi sisa pagu`, lalu klik **Commit**.

**Commit** mencatat perubahan lokal; **Push** mengunggahnya. Menyimpan file belum membuat commit. Lihat [alur stage dan commit](https://code.visualstudio.com/docs/sourcecontrol/quickstart).

Untuk membatalkan pilihan Stage tanpa menghapus edit, gunakan **− / Unstage Changes**. **Discard Changes** menghapus edit yang belum disimpan dalam commit.

**Khusus capstone:** commit kode, SQL, CSV dan dokumen. `.gitignore` harus mengecualikan `config/db.properties`, `config/ai.properties`, `data/`, `target/` dan `*.gguf`. Jika file tersebut muncul di Changes, jangan Stage; minta C/W memeriksanya.

## 6. Push dan Ajukan Penggabungan

1. Untuk branch baru, klik **Publish Branch**. Untuk kiriman berikutnya, gunakan **Ctrl+Shift+P → Git: Push**. **Push** mengunggah commit ke branch online.
2. Buka GitHub, pilih branch tugasmu dan periksa commit/file.
3. Pilih **Compare & pull request**, atau **Pull requests → New pull request**.
4. Pastikan **base: main** dan **compare: branch tugasmu**. Tulis ringkasan perubahan dan hasil uji, lalu **Create pull request**.
5. Bagikan tautannya kepada C/W untuk diperiksa dan digabungkan.

**Pull Request (PR)** mengusulkan penggabungan branch; Push belum memasukkan perubahan ke `main`. Lihat [langkah PR GitHub](https://docs.github.com/en/pull-requests/how-tos/create-pull-requests/creating-a-pull-request).

## 7. Setelah PR Digabungkan

1. Pastikan pekerjaan sudah di-commit dan Source Control bersih.
2. Pindah ke `main`, lalu jalankan **Git: Pull**.
3. Untuk tugas baru, buat branch baru dari `main` yang sudah diperbarui, kemudian ulangi langkah 5–6.

Untuk PR yang belum digabungkan, lanjutkan pada branch tugasnya. Pull di sana mengambil pembaruan branch itu. Untuk memasukkan perubahan `main`, minta C/W membantu.

## 8. Jika Tersendat

| Masalah | Langkah pertama |
| --- | --- |
| Git tidak ditemukan | Pasang Git for Windows, lalu buka ulang VS Code. |
| Repo tidak ditemukan/akses ditolak | Periksa URL, akun login dan undangan akses repo. |
| Push ditolak karena branch online lebih baru | Commit pekerjaanmu, lalu Pull pada branch tugas. Jika konflik, minta bantuan C/W; jangan Force Push. |
| Muncul konflik/penanda `<<<<<<<` | Dua perubahan perlu dipadukan. Simpan keadaan saat ini dan minta C/W membantu meninjau bagian tersebut. |
| Bingung status pekerjaan | Di terminal jalankan `git status`; periksa nama branch dan file yang berubah. |

Untuk rutinitas ini, gunakan **Pull** dan **Push** terpisah. **Sync Changes** menjalankan keduanya; lihat [perbedaannya pada dokumentasi VS Code](https://code.visualstudio.com/docs/sourcecontrol/repos-remotes).

**Latihan pertama:** clone → temukan `Satker.java`/panduan → buat branch sendiri → lakukan satu perubahan dokumentasi yang relevan → Commit → Push → ajukan PR. Berhasil jika perubahan terlihat di GitHub pada branch milikmu.
