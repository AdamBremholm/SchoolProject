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
    void setForename(String s);
    void setLastname(String s);
    void setUuid(String s);
    void setId(Long id);
    void setSubjects(List<String> subjects);
    void setSubject(Set<Subject> subject);


}
