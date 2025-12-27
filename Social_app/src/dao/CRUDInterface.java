package dao;

public interface CRUDInterface<T> {

    boolean add(T entity);
    T getAccountDetails(int id);
    boolean update(T entity, int id);
}
