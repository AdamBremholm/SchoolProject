package se.alten.schoolproject.transaction;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

@Local
public interface SubjectTransactionAccess {
    List<Subject> listAllSubjects();
    Subject addSubject(Subject subject);
    List<Subject> getSubjectByName(List<String> subject);
    Optional<Subject> findSubjectByUuid(String uuid);
}
