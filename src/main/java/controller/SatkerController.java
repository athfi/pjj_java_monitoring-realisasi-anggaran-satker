package controller;

import dao.CrudRepository;
import model.Satker;

import java.util.List;
import java.util.Objects;
import util.Validasi;
import util.PesanDatabase;

public class SatkerController {
    private final CrudRepository<Satker> satkerRepository;

    public SatkerController(CrudRepository<Satker> satkerRepository) {
        this.satkerRepository = Objects.requireNonNull(satkerRepository);
    }

    public List<Satker> getAllSatker() {
        return satkerRepository.findAll();
    }

    public Satker getSatkerById(long id) {
        Validasi.id(id, "Satker ID");
        Satker satker = satkerRepository.findById(id);
        if (satker == null) throw new IllegalArgumentException("Satker tidak ditemukan.");
        return satker;
    }

    public void tambahSatker(Satker satker) {
        Validasi.satker(satker);
        periksaKodeUnik(satker, null);
        try { satkerRepository.insert(satker); }
        catch (RuntimeException e) { throw PesanDatabase.terjemahkan(e); }
    }

    public void ubahSatker(Satker satker) {
        Validasi.satker(satker);
        Validasi.id(satker.getId(), "Satker ID");
        getSatkerById(satker.getId());
        periksaKodeUnik(satker, satker.getId());
        try { satkerRepository.update(satker); }
        catch (RuntimeException e) { throw PesanDatabase.terjemahkan(e); }
    }

    public void hapusSatker(long id) {
        getSatkerById(id);
        // Foreign key tetap menjaga relasi jika ada penambahan pagu bersamaan.
        try { satkerRepository.delete(id); }
        catch (RuntimeException e) { throw PesanDatabase.terjemahkan(e); }
    }

    private void periksaKodeUnik(Satker satker, Long kecualiId) {
        boolean duplikat = satkerRepository.findAll().stream().anyMatch(data ->
                satker.getKodeSatker().equals(data.getKodeSatker())
                && !Objects.equals(kecualiId, data.getId()));
        if (duplikat) throw new IllegalArgumentException("Kode Satker sudah digunakan.");
    }
}
