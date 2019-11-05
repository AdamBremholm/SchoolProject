package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.transaction.TransactionAccess;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Stateless
public class SchoolDataAccess implements SchoolAccessLocal<Student, StudentModel>, SchoolAccessRemote<Student, StudentModel> {

    private Student student = new Student();
    private StudentModel studentModel = new StudentModel();

    @Inject
    TransactionAccess<Student> studentTransactionAccess;

    @Override
    public List<StudentModel> listAll(){
        List<Student> result = studentTransactionAccess.list();
        return studentModel.toModel(result);
    }

    @Override
    public StudentModel add(String jsonString) {
        Student studentToAdd = student.toEntity(jsonString);
        boolean checkForEmptyVariables = Stream.of(studentToAdd.getForename(), studentToAdd.getLastname(), studentToAdd.getEmail()).anyMatch(String::isBlank);

        if (checkForEmptyVariables) {
            studentToAdd.setForename("empty");
            return studentModel.toModel(studentToAdd);
        } else {
            studentTransactionAccess.add(studentToAdd);
            return studentModel.toModel(studentToAdd);
        }
    }

    @Override
    public void remove(Long id) {
        studentTransactionAccess.remove(id);
    }

    @Override
    public void update(Long id, Student updateInfo) {
        studentTransactionAccess.update(id, updateInfo);
    }

    @Override
    public StudentModel findById(Long id) {
        Student result = studentTransactionAccess.findById(id).orElseThrow(NoSuchElementException::new);
        return studentModel.toModel(result);
    }

    @Override
    public List<StudentModel> findByName(String name) {
        List<Student> result = studentTransactionAccess.findByName(name);
        return studentModel.toModel(result);
    }


}
