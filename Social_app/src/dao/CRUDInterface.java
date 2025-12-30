package dao;

import com.mysql.cj.exceptions.ConnectionIsClosedException;

import java.sql.Connection;

public interface CRUDInterface<T> {

    boolean add(Connection conn, T entity);
    T getDetails(Connection conn, int id);
    boolean update(Connection conn, T entity, int id);
    boolean delete(Connection conn, int id);
}
