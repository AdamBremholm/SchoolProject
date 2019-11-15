package se.alten.schoolproject.transaction;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.entity.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherTransactionAccess {

    List<Teacher> listTeachers();
    Teacher addTeacher(Teacher teacher);
    void removeTeacher(Teacher teacher);
    Teacher updateTeacher(Teacher updateInfo);
    Optional<Teacher> findTeacherById(Long id);
    Optional<Teacher> findTeacherByUuid(String uuid);
    void removeSubjectFromTeacher(Teacher t, Subject target);
}
