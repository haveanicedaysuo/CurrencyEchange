package org.pshhs.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GenericDAO<T,ID> {

    List<T> findAll() throws SQLException;

    Optional<T> create(T entity) throws SQLException;

    Optional<T> update(T entity)throws SQLException;

    boolean delete(ID id);
}
