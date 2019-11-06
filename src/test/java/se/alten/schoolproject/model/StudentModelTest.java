package se.alten.schoolproject.model;

import org.junit.Test;
import se.alten.schoolproject.entity.Student;

import static org.junit.Assert.*;

public class StudentModelTest {

    @Test
    public void ListNullOrEmptyFields() throws IllegalAccessException {
        StudentModel studentModel = new StudentModel(1L, null, "christofferson", "");
        assertTrue(studentModel.listNullOrEmptyFieldsExceptId().contains("email"));
        assertTrue(studentModel.listNullOrEmptyFieldsExceptId().contains("forename"));
        System.out.println(studentModel.listNullOrEmptyFieldsExceptId());
    }

    @Test
    public void toModel() {
        StudentModel studentModel = new StudentModel();
        Student student = new Student(1L, null, "christofferson", "");
        System.out.println(studentModel.toModel(student).toString());

    }
}