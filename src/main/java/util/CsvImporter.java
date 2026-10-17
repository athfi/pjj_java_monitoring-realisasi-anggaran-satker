package util;
import dao.*;
import model.*;
import service.BudgetService.PembukaKoneksi;
import org.apache.commons.csv.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.sql.*;
import java.util.*;

/** Dua tahap: pratinjau tanpa menulis, lalu simpan setelah pengguna mengonfirmasi. */
public class CsvImporter {
    public enum Tipe { SATKER, PAGU }
    public record Pratinjau(Tipe tipe, Path file, int jumlah, String sidikFile) {}
    private record Baris(long nomor, List<String> isi) {}
    private record KunciPagu(Long satker, String akun, int tahun, String kegiatan) {}
    private final PembukaKoneksi pembuka;
    private static final List<String> HEADER_SATKER = List.of("kode_satker", "nama_satker");
    private static final List<String> HEADER_PAGU = List.of("kode_satker", "kode_akun", "nama_akun",
            "tahun_anggaran", "deskripsi_kegiatan", "nilai_pagu");

    public CsvImporter() { this(DatabaseConnection::getConnection); }
    public CsvImporter(PembukaKoneksi pembuka) { this.pembuka = Objects.requireNonNull(pembuka); }

    public Pratinjau pratinjau(Path file, Tipe tipe) {
        Objects.requireNonNull(tipe, "Jenis impor wajib diisi");
        byte[] isi = bacaFile(file);
        List<Baris> baris = bacaCsv(isi, tipe);
        try (Connection c = pembuka.buka()) {
            validasi(c, baris, tipe);
            return new Pratinjau(tipe, file.toAbsolutePath(), baris.size(), hash(isi));
        } catch (SQLException e) { throw PesanDatabase.terjemahkan(e); }
    }

    public int simpan(Pratinjau rencana, boolean disetujui) {
        Objects.requireNonNull(rencana, "Pratinjau wajib dibuat dahulu");
        if (!disetujui) return 0;
        byte[] isi = bacaFile(rencana.file());
        if (!hash(isi).equals(rencana.sidikFile()))
            throw new IllegalArgumentException("File berubah setelah pratinjau. Buat pratinjau ulang.");
        List<Baris> baris = bacaCsv(isi, rencana.tipe());
        try (Connection c = pembuka.buka()) {
            c.setAutoCommit(false);
            try {
                List<Object> data = validasi(c, baris, rencana.tipe());
                for (int i = 0; i < data.size(); i++) {
                    try {
                        if (rencana.tipe() == Tipe.SATKER) new SatkerDAO(c).insert((Satker) data.get(i));
                        else new PaguAkunDAO(c).insert((PaguAkun) data.get(i));
                    } catch (RuntimeException e) {
                        throw new IllegalArgumentException("Record CSV " + baris.get(i).nomor() + ": "
                                + PesanDatabase.terjemahkan(e).getMessage(), e);
                    }
                }
                c.commit();
                return data.size();
            } catch (Exception e) {
                try { c.rollback(); } catch (SQLException r) { e.addSuppressed(r); }
                // Pertahankan nomor record di pesan; penyebab SQL ada pada cause.
                if (e instanceof RuntimeException r) throw r;
                throw PesanDatabase.terjemahkan(e);
            }
        } catch (SQLException e) { throw PesanDatabase.terjemahkan(e); }
    }

    private List<Object> validasi(Connection c, List<Baris> baris, Tipe tipe) {
        List<Object> hasil = new ArrayList<>();
        List<String> masalah = new ArrayList<>();
        Set<String> kode = new HashSet<>();
        Set<KunciPagu> kunci = new HashSet<>();
        SatkerDAO satkerDAO = new SatkerDAO(c);
        if (tipe == Tipe.SATKER) {
            for (Satker s : satkerDAO.findAll()) kode.add(s.getKodeSatker());
        } else {
            for (PaguAkun p : new PaguAkunDAO(c).findAll())
                kunci.add(new KunciPagu(p.getSatkerId(), p.getKodeAkun(), p.getTahunAnggaran(), p.getDeskripsiKegiatan()));
        }
        for (Baris b : baris) {
            try {
                List<String> v = b.isi();
                int n = tipe == Tipe.SATKER ? 2 : 6;
                if (v.size() != n) throw new IllegalArgumentException("Jumlah kolom harus " + n + ".");
                if (tipe == Tipe.SATKER) {
                    Satker s = new Satker(null, v.get(0), v.get(1));
                    Validasi.satker(s);
                    if (!kode.add(s.getKodeSatker())) throw new IllegalArgumentException("Kode Satker duplikat.");
                    hasil.add(s);
                } else {
                    String kodeSatker = Validasi.teks(v.get(0), "Kode Satker", 20);
                    Satker s = satkerDAO.findByKode(kodeSatker);
                    if (s == null) throw new IllegalArgumentException("Satker tidak ditemukan: " + kodeSatker);
                    String nominal = v.get(5).strip();
                    if (!nominal.matches("[+-]?[0-9]+(\\.[0-9]+)?"))
                        throw new IllegalArgumentException("Nominal memakai titik desimal tanpa pemisah ribuan.");
                    PaguAkun p = new PaguAkun(s.getId(), v.get(1), v.get(2),
                            Integer.parseInt(v.get(3).strip()), v.get(4), new java.math.BigDecimal(nominal));
                    Validasi.pagu(p);
                    if (!kunci.add(new KunciPagu(p.getSatkerId(), p.getKodeAkun(), p.getTahunAnggaran(), p.getDeskripsiKegiatan())))
                        throw new IllegalArgumentException("Kombinasi pagu duplikat.");
                    hasil.add(p);
                }
            } catch (NumberFormatException e) {
                masalah.add("Record CSV " + b.nomor() + ": format angka/tahun tidak valid.");
            } catch (IllegalArgumentException e) {
                masalah.add("Record CSV " + b.nomor() + ": " + e.getMessage());
            }
        }
        if (!masalah.isEmpty()) throw new IllegalArgumentException(String.join(System.lineSeparator(), masalah));
        return hasil;
    }

    private static List<Baris> bacaCsv(byte[] bytes, Tipe tipe) {
        List<String> header = tipe == Tipe.SATKER ? HEADER_SATKER : HEADER_PAGU;
        try (PushbackReader reader = new PushbackReader(new InputStreamReader(
                new ByteArrayInputStream(bytes), StandardCharsets.UTF_8), 1)) {
            int awal = reader.read();
            if (awal != -1 && awal != 0xFEFF) reader.unread(awal);
            CSVFormat format = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(false).get();
            try (CSVParser parser = format.parse(reader)) {
                if (!parser.getHeaderNames().equals(header))
                    throw new IllegalArgumentException("Header CSV harus tepat: " + String.join(",", header));
                List<Baris> hasil = new ArrayList<>();
                for (CSVRecord r : parser) {
                    List<String> nilai = new ArrayList<>();
                    for (String s : r) nilai.add(s);
                    hasil.add(new Baris(r.getRecordNumber() + 1, List.copyOf(nilai)));
                }
                if (hasil.isEmpty()) throw new IllegalArgumentException("CSV tidak berisi data.");
                return hasil;
            }
        } catch (IOException | UncheckedIOException e) {
            throw new IllegalArgumentException("Format CSV tidak valid: " + e.getMessage(), e);
        }
    }

    private static byte[] bacaFile(Path file) {
        try { return Files.readAllBytes(file); }
        catch (IOException e) { throw new IllegalArgumentException("File CSV tidak dapat dibaca: " + file, e); }
    }
    private static String hash(byte[] isi) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(isi)); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
}
