package dao;

import java.util.List;

public interface CrudRepository<T> {
    List<T> findAll();
    T findById(long id);
    void insert(T data);
    void update(T data);
    void delete(long id);
}
