package controller;

import dao.RealisasiDAO;
import exception.PaguExceededException;
import model.Realisasi;

import java.util.List;

public class RealisasiController {
    private final RealisasiDAO realisasiDAO;

    public RealisasiController(RealisasiDAO realisasiDAO) {
        this.realisasiDAO = realisasiDAO;
    }

    public List<Realisasi> getAllRealisasi() {
        return realisasiDAO.findAll();
    }

    public Realisasi getRealisasiById(long id) {
        return realisasiDAO.findById(id);
    }

    public void catatRealisasi(Realisasi realisasi) throws PaguExceededException {
        // TODO: Validasi nilai realisasi > 0, cek sisa pagu, lempar PaguExceededException jika melampaui
        realisasiDAO.insert(realisasi);
    }
}
