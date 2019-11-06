package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.exceptions.NoSuchIdException;
import se.alten.schoolproject.exceptions.WrongHttpMethodException;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.transaction.TransactionAccess;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

@Stateless
public class SchoolDataAccess implements SchoolAccessLocal<Student, StudentModel>, SchoolAccessRemote<Student, StudentModel> {


    @Inject
    TransactionAccess<Student> studentTransactionAccess;

    @Override
    public List<StudentModel> listAll(){
        List<Student> result = studentTransactionAccess.list();
        return StudentModel.toModel(result);
    }

    @Override
    public StudentModel add(Student studentToAdd) {
        StudentModel convertedStudent = StudentModel.toModel(studentToAdd);
        List<String> nullOrEmptyFields = convertedStudent.listNullOrEmptyFieldsExceptId();
        if (nullOrEmptyFields.isEmpty()) {
            studentTransactionAccess.add(studentToAdd);
            return convertedStudent;
        } else {
            throw new IllegalArgumentException(nullOrEmptyFields.toString() + " are blank or missing");
        }
    }

    @Override
    public void remove(Long id) {
       Student foundStudent = studentTransactionAccess.findById(id).orElseThrow(NoSuchIdException::new);
       studentTransactionAccess.remove(foundStudent);

    }

    @Override
    public StudentModel update(Long id, Student updateInfo) {
        Student foundStudent = studentTransactionAccess.findById(id).orElseThrow(() -> new NoSuchIdException("No student with id: " +id+  " found"));
        Optional<Student> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optUpdateInfo.isPresent()){
            optUpdateInfo.map(Student::getEmail).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setEmail);
            optUpdateInfo.map(Student::getForename).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setForename);
            optUpdateInfo.map(Student::getLastname).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setLastname);
        } else
            throw new IllegalArgumentException("updateInfo is null in update");


        studentTransactionAccess.update(id, foundStudent);
        return findById(id);
    }

    @Override
    public StudentModel findById(Long id) {
        Student result = studentTransactionAccess.findById(id).orElseThrow(NoSuchIdException::new);
        return StudentModel.toModel(result);
    }

    @Override
    public List<StudentModel> findByName(String name) {
        List<Student> result = studentTransactionAccess.findByName(name);
        return StudentModel.toModel(result);
    }

    @Override
    public StudentModel updateFull(Long id, Student student) {
        if(allFieldsExistsAndNotEmpty(student)) {
            studentTransactionAccess.findById(id).orElseThrow(() -> new NoSuchIdException("No student with id: " +id+  " found"));
            student.setId(id);
            studentTransactionAccess.update(id, student);
            return findById(id);
        }
        else
            throw new WrongHttpMethodException("use http PATCH for partial updates");
    }

    private boolean allFieldsExistsAndNotEmpty(Student student) {
        boolean emailExists =  Optional.ofNullable(student).map(Student::getEmail).filter(Predicate.not(String::isBlank)).isPresent();
        boolean forenameExists =  Optional.ofNullable(student).map(Student::getForename).filter(Predicate.not(String::isBlank)).isPresent();
        boolean lastNameExists =  Optional.ofNullable(student).map(Student::getLastname).filter(Predicate.not(String::isBlank)).isPresent();
        return emailExists && forenameExists && lastNameExists;
    }


}
