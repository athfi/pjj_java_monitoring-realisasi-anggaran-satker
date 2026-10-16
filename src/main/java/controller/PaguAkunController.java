package controller;

import dao.CrudRepository;
import model.PaguAkun;

import java.util.List;
import java.util.Objects;
import service.BudgetService;
import util.Validasi;

public class PaguAkunController {
    private final CrudRepository<PaguAkun> paguAkunRepository;
    private final BudgetService budgetService;

    public PaguAkunController(CrudRepository<PaguAkun> paguAkunRepository) {
        this(paguAkunRepository, new BudgetService());
    }

    public PaguAkunController(CrudRepository<PaguAkun> paguAkunRepository, BudgetService budgetService) {
        this.paguAkunRepository = Objects.requireNonNull(paguAkunRepository);
        this.budgetService = Objects.requireNonNull(budgetService, "BudgetService wajib tersedia");
    }

    public List<PaguAkun> getAllPaguAkun() {
        return paguAkunRepository.findAll();
    }

    public PaguAkun getPaguAkunById(long id) {
        Validasi.id(id, "Pagu ID");
        PaguAkun paguAkun = paguAkunRepository.findById(id);
        if (paguAkun == null) throw new IllegalArgumentException("Pagu tidak ditemukan.");
        return paguAkun;
    }

    public void tambahPaguAkun(PaguAkun paguAkun) {
        budgetService.tambahPaguAkun(paguAkun);
    }

    public void ubahPaguAkun(PaguAkun paguAkun) {
        budgetService.ubahPaguAkun(paguAkun);
    }

    public void hapusPaguAkun(long id) {
        budgetService.hapusPaguAkun(id);
    }
}
