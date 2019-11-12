package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.model.SubjectModel;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SchoolAccessLocal {

    List<StudentModel> listAllStudents();

    StudentModel addStudent(Student student);

    void removeStudent(String uuid);

    StudentModel updateStudentPartial(String uuid, Student student);

    StudentModel findStudentByUuid(String uuid);

    List<StudentModel> findStudentsByName(String name);

    StudentModel updateStudentFull(String uuid, Student student);

    List<SubjectModel> listAllSubjects();

    SubjectModel addSubject(Subject subject);

    List<SubjectModel> findSubjectsByName(List<String> subject);
}
