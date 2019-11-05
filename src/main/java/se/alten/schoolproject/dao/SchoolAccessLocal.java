package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.model.StudentModel;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

@Local
public interface SchoolAccessLocal<T, V> {

    List<V> listAll() throws Exception;

    V add(String jsonString);

    void remove(Long id);

    void update(Long id, T updateInfo);

    V findById(Long id);

    List<V> findByName(String name);


}
