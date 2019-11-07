package se.alten.schoolproject.util;

import org.junit.Test;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.model.StudentModel;

import static org.junit.Assert.*;

public class ReflectionUtilTest {

    @Test
    public void ListNullOrEmptyFields() throws IllegalAccessException {
        Student student = new Student(1L, null, "christofferson", "");
        assertTrue(ReflectionUtil.listNullOrEmptyFieldsExceptId(student, "id").contains("email"));
        assertTrue(ReflectionUtil.listNullOrEmptyFieldsExceptId(student, "id").contains("forename"));
    }
}