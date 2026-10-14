package dao;

import model.Satker;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class SatkerDAO implements CrudRepository<Satker> {
    private final Connection connection;

    public SatkerDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Satker> findAll() {
        List<Satker> list = new ArrayList<>();
        // TODO: Implementasi SELECT all Satker menggunakan PreparedStatement
        return list;
    }

    @Override
    public Satker findById(long id) {
        // TODO: Implementasi SELECT Satker by id menggunakan PreparedStatement
        return null;
    }

    @Override
    public void insert(Satker data) {
        // TODO: Implementasi INSERT Satker menggunakan PreparedStatement
    }

    @Override
    public void update(Satker data) {
        // TODO: Implementasi UPDATE Satker menggunakan PreparedStatement
    }

    @Override
    public void delete(long id) {
        // TODO: Implementasi DELETE Satker menggunakan PreparedStatement
    }
}
