package se.alten.schoolproject.transaction;

import se.alten.schoolproject.entity.Student;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

@Local
public interface TransactionAccess<T> {
    List<T> list();
    T add(T t);
    void remove(Long id);
    void update(Long id, T updateInfo);
    Optional<T> findById(Long id);
    List<T> findByName(String name);
}
