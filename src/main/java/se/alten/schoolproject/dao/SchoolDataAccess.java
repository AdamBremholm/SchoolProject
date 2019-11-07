package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.exceptions.MissingFieldException;
import se.alten.schoolproject.exceptions.NoSuchEmailException;
import se.alten.schoolproject.exceptions.NoSuchIdException;
import se.alten.schoolproject.exceptions.WrongHttpMethodException;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.transaction.TransactionAccess;
import se.alten.schoolproject.util.ReflectionUtil;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
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
        List<String> nullOrEmptyFields = ReflectionUtil.listNullOrEmptyFieldsExceptId(studentToAdd, "id");
        if (nullOrEmptyFields.isEmpty()) {
            studentTransactionAccess.add(studentToAdd);
            Student foundStudent = studentTransactionAccess.findByEmail(studentToAdd.getEmail()).orElseThrow(NoSuchEmailException::new);
            return StudentModel.toModel(foundStudent);
        } else {
            throw new MissingFieldException(nullOrEmptyFields.toString() + " are blank or missing");
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
        updateTargetFieldIfRequestFieldIsPresentAndNotBlank(foundStudent, updateInfo);
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
        if(student.allFieldsExistsAndNotEmpty()) {
            studentTransactionAccess.findById(id).orElseThrow(() -> new NoSuchIdException("No student with id: " +id+  " found"));
            student.setId(id);
            studentTransactionAccess.update(id, student);
            return findById(id);
        }
        else
            throw new WrongHttpMethodException("use http PATCH for partial updates");
    }

    private void updateTargetFieldIfRequestFieldIsPresentAndNotBlank(Student foundStudent, Student updateInfo) {
        Optional<Student> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optUpdateInfo.isPresent()) {
            optUpdateInfo.map(Student::getEmail).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setEmail);
            optUpdateInfo.map(Student::getForename).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setForename);
            optUpdateInfo.map(Student::getLastname).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setLastname);
        }
        else
            throw new IllegalArgumentException("updateInfo is null in update");
    }


}
