package se.alten.schoolproject.transaction;

import se.alten.schoolproject.entity.Subject;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SubjectTransactionAccess {
    List<Subject> listAllSubjects();
    Subject addSubject(Subject subject);
    List<Subject> getSubjectByName(List<String> subject);
}
