package dao;

public interface CRUDInterface<T> {

    boolean add(T entity);
    T getDetails(int id);
    boolean update(T entity, int id);
    boolean delete(int id);
}
