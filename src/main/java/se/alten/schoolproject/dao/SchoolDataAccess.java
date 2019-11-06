package se.alten.schoolproject.dao;


import net.bytebuddy.pool.TypePool;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.exceptions.NoSuchIdException;
import se.alten.schoolproject.model.StudentModel;

import se.alten.schoolproject.transaction.TransactionAccess;


import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public StudentModel add(Student studentToAdd) {
        StudentModel convertedStudent = studentModel.toModel(studentToAdd);
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
        if (studentTransactionAccess.findById(id).isPresent()) {
            studentTransactionAccess.remove(id);
        } else {
            throw new NoSuchElementException("No student with id: "+id+ " found");
        }
    }

    @Override
    public StudentModel update(Long id, Student updateInfo) {
        Student foundStudent = studentTransactionAccess.findById(id).orElseThrow(() -> new NoSuchIdException("No student with id: " +id+  " found"));
        Optional<Student> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optUpdateInfo.isPresent()){
            optUpdateInfo.map(Student::getEmail).ifPresent(foundStudent::setEmail);
            optUpdateInfo.map(Student::getForename).ifPresent(foundStudent::setForename);
            optUpdateInfo.map(Student::getLastname).ifPresent(foundStudent::setLastname);
        } else
            throw new IllegalArgumentException("updateInfo is null in update");

        studentTransactionAccess.update(id, foundStudent);
        return findById(id);
    }

    @Override
    public StudentModel findById(Long id) {
        Student result = studentTransactionAccess.findById(id).orElseThrow(NoSuchIdException::new);
        return studentModel.toModel(result);
    }

    @Override
    public List<StudentModel> findByName(String name) {
        List<Student> result = studentTransactionAccess.findByName(name);
        return studentModel.toModel(result);
    }




}
