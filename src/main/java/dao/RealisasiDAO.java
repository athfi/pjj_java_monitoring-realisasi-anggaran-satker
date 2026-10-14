package dao;

import model.Realisasi;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class RealisasiDAO implements CrudRepository<Realisasi> {
    private final Connection connection;

    public RealisasiDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Realisasi> findAll() {
        List<Realisasi> list = new ArrayList<>();
        // TODO: Implementasi SELECT all Realisasi menggunakan PreparedStatement
        return list;
    }

    @Override
    public Realisasi findById(long id) {
        // TODO: Implementasi SELECT Realisasi by id menggunakan PreparedStatement
        return null;
    }

    @Override
    public void insert(Realisasi data) {
        // TODO: Implementasi INSERT Realisasi menggunakan PreparedStatement
    }

    @Override
    public void update(Realisasi data) {
        // Sesuai aturan bisnis SDD 4.2 & 8.3: Transaksi realisasi yang sudah tersimpan tidak dapat diubah
        throw new UnsupportedOperationException("Transaksi realisasi tidak dapat diubah.");
    }

    @Override
    public void delete(long id) {
        // Sesuai aturan bisnis SDD 4.2 & 8.3: Transaksi realisasi yang sudah tersimpan tidak dapat dihapus
        throw new UnsupportedOperationException("Transaksi realisasi tidak dapat dihapus.");
    }

    public BigDecimal getTotalRealisasiByPaguId(long paguId) {
        // TODO: Hitung total realisasi untuk pagu tertentu
        return BigDecimal.ZERO;
    }
}
