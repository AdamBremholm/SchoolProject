package se.alten.schoolproject.dao;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SchoolAccessLocal<T, V> {

    List<V> listAll();

    V add(T t);

    void remove(Long id);

    V update(Long id, T t);

    V findById(Long id);

    List<V> findByName(String name);

    V updateFull(Long id, T student);
}
