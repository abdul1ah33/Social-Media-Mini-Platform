package dao;

import model.User;

public interface DAOInterface<T> {

    boolean add(T entity);
    T getAccountDetails(int id);
    boolean update(T entity, int id);
}
