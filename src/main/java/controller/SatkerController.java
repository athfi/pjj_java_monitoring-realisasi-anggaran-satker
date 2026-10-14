package controller;

import dao.CrudRepository;
import model.Satker;

import java.util.List;

public class SatkerController {
    private final CrudRepository<Satker> satkerRepository;

    public SatkerController(CrudRepository<Satker> satkerRepository) {
        this.satkerRepository = satkerRepository;
    }

    public List<Satker> getAllSatker() {
        return satkerRepository.findAll();
    }

    public Satker getSatkerById(long id) {
        return satkerRepository.findById(id);
    }

    public void tambahSatker(Satker satker) {
        // TODO: Validasi kode unik, nama tidak kosong, dll.
        satkerRepository.insert(satker);
    }

    public void ubahSatker(Satker satker) {
        // TODO: Validasi data sebelum update
        satkerRepository.update(satker);
    }

    public void hapusSatker(long id) {
        // TODO: Validasi apakah masih ada pagu terkait
        satkerRepository.delete(id);
    }
}
