package se.alten.schoolproject.transaction;

import se.alten.schoolproject.entity.Student;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

@Local
public interface StudentTransactionAccess {
    List<Student> listStudents();
    Student addStudent(Student student);
    void removeStudent(Student student);
    void updateStudent(Student updateInfo);
    Optional<Student> findStudentById(Long id);
    List<Student> findStudentByName(String name);
    default Optional<Student> findStudentByEmail(String email) {
        return Optional.empty();
    }
}
