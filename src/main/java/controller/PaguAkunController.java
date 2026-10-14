package controller;

import dao.CrudRepository;
import model.PaguAkun;

import java.util.List;

public class PaguAkunController {
    private final CrudRepository<PaguAkun> paguAkunRepository;

    public PaguAkunController(CrudRepository<PaguAkun> paguAkunRepository) {
        this.paguAkunRepository = paguAkunRepository;
    }

    public List<PaguAkun> getAllPaguAkun() {
        return paguAkunRepository.findAll();
    }

    public PaguAkun getPaguAkunById(long id) {
        return paguAkunRepository.findById(id);
    }

    public void tambahPaguAkun(PaguAkun paguAkun) {
        // TODO: Validasi nilai pagu > 0, kombinasi unik (satker, kode akun, tahun, kegiatan)
        paguAkunRepository.insert(paguAkun);
    }

    public void ubahPaguAkun(PaguAkun paguAkun) {
        // TODO: Validasi perubahan pagu
        paguAkunRepository.update(paguAkun);
    }

    public void hapusPaguAkun(long id) {
        // TODO: Validasi apakah pagu sudah memiliki transaksi realisasi
        paguAkunRepository.delete(id);
    }
}
