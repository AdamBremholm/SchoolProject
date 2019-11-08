package se.alten.schoolproject.transaction;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

@Local
public interface TransactionAccess<T> {
    List<T> list();
    void add(T t);
    void remove(T t);
    void update(T updateInfo);
    Optional<T> findById(Long id);
    List<T> findByName(String name);
    Optional<T> findByEmail(String email);
}
