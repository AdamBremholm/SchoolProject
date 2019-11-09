package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.model.SubjectModel;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SchoolAccessLocal {

    List<Student> listAllStudents();

    StudentModel addStudent(Student student);

    void removeStudent(Long id);

    StudentModel updateStudentPartial(Long id, Student student);

    StudentModel findStudentById(Long id);

    List<Student> findStudentsByName(String name);

    StudentModel updateStudentFull(Long id, Student student);

    List<Subject> listAllSubjects();

    SubjectModel addSubject(Subject subject);

    List<Subject> findSubjectsByName(List<String> subject);
}
