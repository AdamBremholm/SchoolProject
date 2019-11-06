package se.alten.schoolproject.dao;


import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.model.StudentModel;

import se.alten.schoolproject.transaction.TransactionAccess;


import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;

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
        studentTransactionAccess.remove(id);
    }

    @Override
    public StudentModel update(Long id, String jsonString) {
        Student updateInfo = student.toEntity(jsonString);
        studentTransactionAccess.update(id, updateInfo);
        return findById(id);
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
