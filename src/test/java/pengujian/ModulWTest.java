package pengujian;
import controller.*;
import dao.*;
import exception.PaguExceededException;
import model.*;
import service.BudgetService;
import util.*;
import org.h2.tools.RunScript;
import org.h2.api.Trigger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class ModulWTest {
    Connection c;
    String url;
    BudgetService service;
    SatkerController satker;
    PaguAkunController pagu;
    RealisasiController realisasi;
    @TempDir Path folder;

    @BeforeEach void mulai() throws Exception {
        url = "jdbc:h2:mem:w_" + UUID.randomUUID() + ";LOCK_TIMEOUT=5000";
        c = buka();
        try (Reader r = new InputStreamReader(Objects.requireNonNull(
                getClass().getResourceAsStream("/setup_demo_h2.sql")), java.nio.charset.StandardCharsets.UTF_8)) {
            RunScript.execute(c, r);
        }
        service = new BudgetService(this::buka);
        satker = new SatkerController(new SatkerDAO(c));
        pagu = new PaguAkunController(new PaguAkunDAO(c), service);
        realisasi = new RealisasiController(new RealisasiDAO(c), service);
    }
    @AfterEach void selesai() throws Exception { c.close(); }
    Connection buka() throws SQLException { return DriverManager.getConnection(url, "sa", ""); }
    Realisasi r(String sp2d, String nominal) {
        return new Realisasi(1L, sp2d, LocalDate.of(2026,10,16), new BigDecimal(nominal), "");
    }
    long jumlah(String tabel) throws SQLException {
        String sql = switch(tabel) {
            case "satker" -> "SELECT COUNT(*) FROM satker";
            case "pagu_akun" -> "SELECT COUNT(*) FROM pagu_akun";
            case "realisasi" -> "SELECT COUNT(*) FROM realisasi";
            default -> throw new IllegalArgumentException();
        };
        try (var ps = c.prepareStatement(sql); var rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
    }
    void uang(String expected, BigDecimal actual) { assertEquals(0, new BigDecimal(expected).compareTo(actual)); }
    CsvImporter importer() { return new CsvImporter(this::buka); }
    Path csv(String nama, String teks) throws IOException { return Files.writeString(folder.resolve(nama), teks); }

    @Test void dataAwalDanRekapSesuaiPanduan() throws Exception {
        assertEquals(12, jumlah("satker"));
        assertEquals(26, jumlah("pagu_akun"));
        assertEquals(52, jumlah("realisasi"));
        var controller = new MonitoringController(new MonitoringDAO(c));
        var hasil = controller.getTotalRingkasan(2026);
        uang("4050000000", hasil.getTotalPagu());
        uang("1798750000", hasil.getTotalRealisasi());
        uang("2251250000", hasil.getSisaPagu());
        uang("44.41", hasil.getPersentase());
        uang("140000000", controller.getTotalRingkasan(2025).getTotalPagu());
    }
    @Test void crudSatkerValid() {
        Satker s = new Satker(null," W-TEST "," Unit Uji ");
        satker.tambahSatker(s);
        assertNotNull(s.getId());
        assertEquals("W-TEST", satker.getSatkerById(s.getId()).getKodeSatker());
        s.setNamaSatker("Unit diperbarui");
        satker.ubahSatker(s);
        assertEquals("Unit diperbarui", satker.getSatkerById(s.getId()).getNamaSatker());
        satker.hapusSatker(s.getId());
        assertThrows(IllegalArgumentException.class, () -> satker.getSatkerById(s.getId()));
    }
    @Test void tolakSatkerKosongDanDuplikat() {
        assertThrows(IllegalArgumentException.class, () -> satker.tambahSatker(new Satker(null,"W-X","   ")));
        assertThrows(IllegalArgumentException.class, () -> satker.tambahSatker(new Satker(null,"3.27.01.01","Nama")));
        assertThrows(IllegalArgumentException.class, () -> satker.tambahSatker(null));
    }
    @Test void hapusIndukYangDigunakanDitolak() {
        assertThrows(IllegalArgumentException.class, () -> satker.hapusSatker(1));
        assertThrows(IllegalArgumentException.class, () -> pagu.hapusPaguAkun(1));
    }
    @Test void paguNolBolehDanDapatDihapus() {
        var p = new PaguAkun(1L, "W-000", "Uji", 2026, "Uji nol", BigDecimal.ZERO);
        pagu.tambahPaguAkun(p);
        uang("0", pagu.getPaguAkunById(p.getId()).getNilaiPagu());
        pagu.hapusPaguAkun(p.getId());
        assertThrows(IllegalArgumentException.class, () -> pagu.getPaguAkunById(p.getId()));
        uang("0", new MonitoringRow("X","X",2026,BigDecimal.ZERO,BigDecimal.ZERO).getPersentase());
    }
    @Test void paguDuplikatDanSatkerTidakAdaDitolak() {
        var p = pagu.getPaguAkunById(1);
        assertThrows(IllegalArgumentException.class, () -> pagu.tambahPaguAkun(p));
        p.setSatkerId(999999L);
        assertThrows(IllegalArgumentException.class, () -> pagu.tambahPaguAkun(p));
    }
    @Test void paguTidakBolehDiBawahRealisasi() {
        var p = pagu.getPaguAkunById(1);
        p.setNilaiPagu(BigDecimal.ONE);
        assertThrows(IllegalArgumentException.class, () -> pagu.ubahPaguAkun(p));
        uang("120000000", pagu.getPaguAkunById(1).getNilaiPagu());
        assertThrows(IllegalArgumentException.class, () -> service.ubahNilaiPagu(1, BigDecimal.ONE));
    }
    @Test void perubahanPaguValidDiterima() {
        service.ubahNilaiPagu(1, new BigDecimal("130000000"));
        uang("130000000", pagu.getPaguAkunById(1).getNilaiPagu());
        var p = pagu.getPaguAkunById(1);
        p.setNamaAkun("Nama akun diperjelas");
        p.setNilaiPagu(new BigDecimal("125000000"));
        pagu.ubahPaguAkun(p);
        uang("125000000", pagu.getPaguAkunById(1).getNilaiPagu());
    }
    @ParameterizedTest @ValueSource(strings={"satker","akun","tahun","kegiatan"})
    void identitasPaguTerpakaiTidakBolehBerubah(String field) {
        var p = pagu.getPaguAkunById(1);
        switch(field) {
            case "satker" -> p.setSatkerId(2L);
            case "akun" -> p.setKodeAkun("W-BARU");
            case "tahun" -> p.setTahunAnggaran(2025);
            case "kegiatan" -> p.setDeskripsiKegiatan("Kegiatan diganti");
        }
        assertThrows(IllegalArgumentException.class, () -> pagu.ubahPaguAkun(p));
    }
    @Test void tepatSisaDiterimaLaluSatuRupiahDitolak() throws Exception {
        realisasi.catatRealisasi(r("W-TEPAT","7200000.00"));
        assertThrows(PaguExceededException.class, () -> realisasi.catatRealisasi(r("W-LEBIH","1")));
        assertEquals(53, jumlah("realisasi"));
        uang("120000000", new RealisasiDAO(c).getTotalRealisasiByPaguId(1));
    }
    @ParameterizedTest @ValueSource(strings={"0","-1","1.234","10000000000000000"})
    void realisasiNominalSalahDitolak(String v) throws Exception {
        assertThrows(IllegalArgumentException.class, () -> realisasi.catatRealisasi(r("W-SALAH",v)));
        assertEquals(52, jumlah("realisasi"));
    }
    @Test void tanggalSp2dDanPaguWajibValid() {
        Realisasi d = r("W-TANGGAL","1");
        d.setTanggalRealisasi(null);
        assertThrows(IllegalArgumentException.class, () -> realisasi.catatRealisasi(d));
        assertThrows(IllegalArgumentException.class, () -> realisasi.catatRealisasi(r("  ","1")));
        Realisasi asing = r("W-ASING","1"); asing.setPaguId(999999L);
        assertThrows(IllegalArgumentException.class, () -> realisasi.catatRealisasi(asing));
    }
    @Test void sp2dDuplikatRollback() throws Exception {
        realisasi.catatRealisasi(r("W-DUP","1"));
        assertThrows(IllegalArgumentException.class, () -> realisasi.catatRealisasi(r("W-DUP","1")));
        assertEquals(53, jumlah("realisasi"));
    }
    @Test void realisasiTidakBolehDiubahDihapus() {
        var dao = new RealisasiDAO(c);
        assertThrows(UnsupportedOperationException.class, () -> dao.update(dao.findById(1)));
        assertThrows(UnsupportedOperationException.class, () -> dao.delete(1));
        assertThrows(NullPointerException.class, () -> new RealisasiController(dao, null));
    }
    @Test @Timeout(15) void duaTransaksiBersamaanHanyaSatuCommit() throws Exception {
        CountDownLatch siap = new CountDownLatch(2);
        CountDownLatch mulai = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<Boolean>> hasil = new ArrayList<>();
            for (int i=0;i<2;i++) {
                String sp2d = "W-PARALEL-" + i;
                hasil.add(pool.submit(() -> {
                    siap.countDown(); mulai.await();
                    try { service.catatRealisasi(r(sp2d,"5000000")); return true; }
                    catch (PaguExceededException e) { return false; }
                }));
            }
            assertTrue(siap.await(3, TimeUnit.SECONDS)); mulai.countDown();
            int sukses = 0;
            for (var h : hasil) if (h.get(8,TimeUnit.SECONDS)) sukses++;
            assertEquals(1,sukses);
            assertEquals(53,jumlah("realisasi"));
            uang("117800000",new RealisasiDAO(c).getTotalRealisasiByPaguId(1));
        } finally { mulai.countDown(); pool.shutdownNow(); }
    }
    @Test void filterMonitoringSalahDitolak() {
        var m = new MonitoringController(new MonitoringDAO(c));
        assertThrows(IllegalArgumentException.class, () -> m.getTotalRingkasan(0));
        assertThrows(IllegalArgumentException.class, () -> m.getRekapPerSatker(-1));
        assertThrows(IllegalArgumentException.class, () -> m.getRekapitulasiBulanan(1,0));
    }
    @Test @Timeout(15) void penurunanPaguBersamaanDenganRealisasiTetapAman() throws Exception {
        CountDownLatch siap = new CountDownLatch(2);
        CountDownLatch mulai = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> turun = pool.submit(() -> {
                siap.countDown(); mulai.await();
                try { service.ubahNilaiPagu(1,new BigDecimal("115000000")); return true; }
                catch (IllegalArgumentException e) { return false; }
            });
            Future<Boolean> catat = pool.submit(() -> {
                siap.countDown(); mulai.await();
                try { service.catatRealisasi(r("W-BERSAMA","5000000")); return true; }
                catch (PaguExceededException e) { return false; }
            });
            assertTrue(siap.await(3,TimeUnit.SECONDS)); mulai.countDown();
            assertNotEquals(turun.get(8,TimeUnit.SECONDS),catat.get(8,TimeUnit.SECONDS));
            BigDecimal batas = pagu.getPaguAkunById(1).getNilaiPagu();
            assertTrue(new RealisasiDAO(c).getTotalRealisasiByPaguId(1).compareTo(batas) <= 0);
        } finally { mulai.countDown(); pool.shutdownNow(); }
    }
    @ParameterizedTest @ValueSource(strings={"-1","1.234","10000000000000000"})
    void paguNominalSalahDitolak(String nilai) throws Exception {
        var p = new PaguAkun(1L,"W-X","Uji",2026,"Uji nominal",new BigDecimal(nilai));
        assertThrows(IllegalArgumentException.class, () -> pagu.tambahPaguAkun(p));
        assertEquals(26,jumlah("pagu_akun"));
    }
    @Test void panjangKolomDanIdTidakValidDitolak() {
        assertThrows(IllegalArgumentException.class, () -> satker.tambahSatker(new Satker(null,"X".repeat(21),"Unit")));
        assertThrows(IllegalArgumentException.class, () -> satker.getSatkerById(0));
        assertThrows(IllegalArgumentException.class, () -> pagu.getPaguAkunById(999999));
        assertThrows(IllegalArgumentException.class, () -> realisasi.getRealisasiById(-1));
    }
    @Test void csvBomKutipKomaBatalDanImporUlang() throws Exception {
        Path file = csv("satker.csv","\uFEFFkode_satker,nama_satker\r\nW-CSV,\"Unit, Uji\"\r\n");
        var imp = importer();
        var preview = imp.pratinjau(file,CsvImporter.Tipe.SATKER);
        assertEquals(1,preview.jumlah());
        assertEquals(0,imp.simpan(preview,false));
        assertEquals(12,jumlah("satker"));
        assertEquals(1,imp.simpan(preview,true));
        assertEquals("Unit, Uji",new SatkerDAO(c).findByKode("W-CSV").getNamaSatker());
        assertThrows(IllegalArgumentException.class, () -> imp.simpan(preview,true));
        assertEquals(13,jumlah("satker"));
    }
    @Test void csvSatuBarisSalahTidakMenyimpanApapun() throws Exception {
        Path file = csv("salah.csv","kode_satker,nama_satker\nW-OK,Unit\nW-NO,   \n");
        var e = assertThrows(IllegalArgumentException.class, () -> importer().pratinjau(file,CsvImporter.Tipe.SATKER));
        assertTrue(e.getMessage().contains("3"));
        assertEquals(12,jumlah("satker"));
    }
    @Test void csvFileBerubahSesudahPreviewDitolak() throws Exception {
        Path file = csv("ubah.csv","kode_satker,nama_satker\nW-A,Unit\n");
        var imp = importer(); var p = imp.pratinjau(file,CsvImporter.Tipe.SATKER);
        Files.writeString(file,"kode_satker,nama_satker\nW-B,Unit\n");
        assertThrows(IllegalArgumentException.class, () -> imp.simpan(p,true));
        assertEquals(12,jumlah("satker"));
    }
    @Test void csvHeaderDanDuplikatDalamFileDitolak() throws Exception {
        Path header = csv("header.csv","kode,nama\nW-A,Unit\n");
        assertThrows(IllegalArgumentException.class, () -> importer().pratinjau(header,CsvImporter.Tipe.SATKER));
        Path dup = csv("dup.csv","kode_satker,nama_satker\nW-A,A\nW-A,B\n");
        assertThrows(IllegalArgumentException.class, () -> importer().pratinjau(dup,CsvImporter.Tipe.SATKER));
        assertEquals(12,jumlah("satker"));
    }
    @Test void csvPaguValidDanRelasiAsingDitolak() throws Exception {
        String header = "kode_satker,kode_akun,nama_akun,tahun_anggaran,deskripsi_kegiatan,nilai_pagu\n";
        Path file = csv("pagu.csv",header + "3.27.01.01,W-A,Uji,2026,Impor uji,1000.00\n");
        var imp = importer();
        assertEquals(1,imp.simpan(imp.pratinjau(file,CsvImporter.Tipe.PAGU),true));
        assertEquals(27,jumlah("pagu_akun"));
        Path asing = csv("asing.csv",header + "TIDAK-ADA,W-B,Uji,2026,Impor asing,1000\n");
        assertThrows(IllegalArgumentException.class, () -> imp.pratinjau(asing,CsvImporter.Tipe.PAGU));
    }
    @Test void csvKegagalanInsertKeduaRollbackInsertPertama() throws Exception {
        try (var ps=c.prepareStatement("CREATE TRIGGER audit_tolak BEFORE INSERT ON satker FOR EACH ROW CALL 'pengujian.ModulWTest$TolakSatker'")) {
            ps.execute();
        }
        Path file = csv("dbfail.csv","kode_satker,nama_satker\nW-OK,Unit\nFAIL-DB,Unit\n");
        var imp = importer(); var p = imp.pratinjau(file,CsvImporter.Tipe.SATKER);
        assertThrows(IllegalArgumentException.class, () -> imp.simpan(p,true));
        assertEquals(12,jumlah("satker"));
        assertNull(new SatkerDAO(c).findByKode("W-OK"));
    }
    public static class TolakSatker implements Trigger {
        @Override public void fire(Connection c, Object[] lama, Object[] baru) throws SQLException {
            if ("FAIL-DB".equals(baru[1])) throw new SQLException("Kegagalan insert untuk uji rollback","45000");
        }
    }
}
