package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.entity.Teacher;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.model.SubjectModel;
import se.alten.schoolproject.model.TeacherModel;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SchoolAccessLocal {

    //Student methods:

    List<StudentModel> listAllStudents();

    StudentModel addStudent(Student student);

    void removeStudent(String uuid);

    StudentModel updateStudentPartial(String uuid, Student student);

    StudentModel findStudentByUuid(String uuid);

    List<StudentModel> findStudentsByName(String name);

    StudentModel updateStudentFull(String uuid, Student student);

    //Subject methods:

    List<SubjectModel> listAllSubjects();

    SubjectModel addSubject(Subject subject);

    List<SubjectModel> findSubjectsByName(List<String> subject);

    void deleteSubjectByUuid(String uuid);

    //Teacher methods:

    List<TeacherModel> listAllTeachers();

    TeacherModel addTeacher(Teacher teacher);

    void removeTeacher(String uuid);

    TeacherModel updateTeacherPartial(String uuid, Teacher teacher);

    TeacherModel findTeacherByUuid(String uuid);

    TeacherModel updateTeacherFull(String uuid, Teacher teacher);




}
