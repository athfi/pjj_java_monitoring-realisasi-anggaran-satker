package controller;

import dao.RealisasiDAO;
import exception.PaguExceededException;
import model.Realisasi;
import service.BudgetService;

import java.util.List;
import java.util.Objects;
import util.Validasi;

public class RealisasiController {
    private final RealisasiDAO realisasiDAO;
    private final BudgetService budgetService;

    public RealisasiController(RealisasiDAO realisasiDAO) {
        this(realisasiDAO, new BudgetService());
    }

    public RealisasiController(RealisasiDAO realisasiDAO, BudgetService budgetService) {
        this.realisasiDAO = Objects.requireNonNull(realisasiDAO);
        this.budgetService = Objects.requireNonNull(budgetService, "BudgetService wajib tersedia");
    }

    public List<Realisasi> getAllRealisasi() {
        return realisasiDAO.findAll();
    }

    public Realisasi getRealisasiById(long id) {
        Validasi.id(id, "Realisasi ID");
        Realisasi realisasi = realisasiDAO.findById(id);
        if (realisasi == null) throw new IllegalArgumentException("Realisasi tidak ditemukan.");
        return realisasi;
    }

    public void catatRealisasi(Realisasi realisasi) throws PaguExceededException {
        budgetService.catatRealisasi(realisasi);
    }
}
