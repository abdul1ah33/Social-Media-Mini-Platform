package dao;

import model.User;

public interface DAOInterface<T> {

    boolean add(T entity);
    T getAccountDetails(int id);
//    String update();
}
