package dao;

import model.PaguAkun;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PaguAkunDAO implements CrudRepository<PaguAkun> {
    private final Connection connection;

    public PaguAkunDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<PaguAkun> findAll() {
        List<PaguAkun> list = new ArrayList<>();
        // TODO: Implementasi SELECT all PaguAkun menggunakan PreparedStatement
        return list;
    }

    @Override
    public PaguAkun findById(long id) {
        // TODO: Implementasi SELECT PaguAkun by id menggunakan PreparedStatement
        return null;
    }

    @Override
    public void insert(PaguAkun data) {
        // TODO: Implementasi INSERT PaguAkun menggunakan PreparedStatement
    }

    @Override
    public void update(PaguAkun data) {
        // TODO: Implementasi UPDATE PaguAkun menggunakan PreparedStatement
    }

    @Override
    public void delete(long id) {
        // TODO: Implementasi DELETE PaguAkun menggunakan PreparedStatement
    }
}
