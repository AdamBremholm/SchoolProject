package se.alten.schoolproject.model;

import org.junit.Test;
import se.alten.schoolproject.entity.Student;

public class StudentModelTest {


    @Test
    public void toModel() {
        StudentModel studentModel = new StudentModel();
        Student student = new Student(1L, null, "christofferson", "");
        System.out.println(StudentModel.toModel(student).toString());

    }
}