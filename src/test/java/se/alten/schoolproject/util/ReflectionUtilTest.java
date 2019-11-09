package se.alten.schoolproject.util;

import org.junit.Test;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ReflectionUtilTest {

    @Test
    public void ListNullOrEmptyFields() {
        Set<Subject> subject = new HashSet<>();
        List<String> subjects = List.of("hej", "din", "dåre");
        Student student = new Student(1L, null, "christofferson", "", null, subjects);
        System.out.println(ReflectionUtil.listNullOrEmptyFields(student, List.of("id", "subject")));
        assertTrue(ReflectionUtil.listNullOrEmptyFields(student, List.of("id", "subject")).contains("email"));
        assertTrue(ReflectionUtil.listNullOrEmptyFields(student, List.of("id", "subject")).contains("forename"));
        assertFalse(ReflectionUtil.listNullOrEmptyFields(student, List.of("id", "subject")).contains("subject"));
    }
}