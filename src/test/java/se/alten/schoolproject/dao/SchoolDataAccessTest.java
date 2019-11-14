package se.alten.schoolproject.dao;

import org.junit.Test;
import se.alten.schoolproject.entity.Subject;

import java.util.List;
import java.util.Set;

public class SchoolDataAccessTest {

    @Test
    public void subjectsToRemoveFroStudent() {

        SchoolDataAccess schoolDataAccess = new SchoolDataAccess();
        List<String> strings = List.of("historia", "matte");
        Set<Subject> subjects = Set.of(new Subject(null, null, "vg", null));
        System.out.println(schoolDataAccess.getSubjectsToRemoveFromStudent(strings, subjects));
    }


    @Test
    public void subjectsToAddGlobally() {

        SchoolDataAccess schoolDataAccess = new SchoolDataAccess();
        List<String> strings = List.of("historia", "matte");
        List<Subject> subjects = List.of(new Subject(null, null, "historia", null));
        System.out.println(schoolDataAccess.getSubjectsToAddToDb(strings, subjects));
    }

}