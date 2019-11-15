package se.alten.schoolproject.entity;

import java.util.List;
import java.util.Set;

public interface Person {

    List<String> getSubjects();
    Set<Subject> getSubject();
    String getForename();
    String getLastname();
    String getUuid();
    Long getId();
}
