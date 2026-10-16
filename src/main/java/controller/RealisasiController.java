package controller;

import dao.RealisasiDAO;
import exception.PaguExceededException;
import model.Realisasi;
import service.BudgetService;

import java.util.List;

public class RealisasiController {
    private final RealisasiDAO realisasiDAO;
    private final BudgetService budgetService;

    public RealisasiController(RealisasiDAO realisasiDAO) {
        this.realisasiDAO = realisasiDAO;
        this.budgetService = null;
    }

    public RealisasiController(RealisasiDAO realisasiDAO, BudgetService budgetService) {
        this.realisasiDAO = realisasiDAO;
        this.budgetService = budgetService;
    }

    public List<Realisasi> getAllRealisasi() {
        return realisasiDAO.findAll();
    }

    public Realisasi getRealisasiById(long id) {
        return realisasiDAO.findById(id);
    }

    public void catatRealisasi(Realisasi realisasi) throws PaguExceededException {
        if (budgetService != null) {
            budgetService.catatRealisasi(realisasi);
        } else {
            realisasiDAO.insert(realisasi);
        }
    }
}
