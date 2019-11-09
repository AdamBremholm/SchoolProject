package se.alten.schoolproject.model;

import org.junit.Test;
import static org.junit.Assert.*;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentModelTest {


    @Test
    public void toModel() {
        Set<Subject> subject = new HashSet<>();
        List<String> subjects = new ArrayList<>();
        Student student = new Student(1L, null, "christofferson", "", subject, subjects);
        StudentModel studentModel = StudentModel.toModel(student);
        assertEquals("christofferson", studentModel.getLastname());

    }
}