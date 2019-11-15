package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Person;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.entity.Teacher;
import se.alten.schoolproject.exceptions.MissingFieldException;
import se.alten.schoolproject.exceptions.NoSuchIdException;
import se.alten.schoolproject.exceptions.WrongHttpMethodException;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.model.SubjectModel;
import se.alten.schoolproject.model.TeacherModel;
import se.alten.schoolproject.transaction.StudentTransactionAccess;
import se.alten.schoolproject.transaction.SubjectTransactionAccess;
import se.alten.schoolproject.transaction.TeacherTransactionAccess;
import se.alten.schoolproject.util.ReflectionUtil;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Stateless
public class SchoolDataAccess implements SchoolAccessLocal, SchoolAccessRemote {


    @Inject
    StudentTransactionAccess studentTransactionAccess;

    @Inject
    SubjectTransactionAccess subjectTransactionAccess;

    @Inject
    TeacherTransactionAccess teacherTransactionAccess;


    //Student methods

    @Override
    public List<StudentModel> listAllStudents(){
        List<Student> result = studentTransactionAccess.listStudents();
        return StudentModel.toModel(result);
    }

    @Override
    public StudentModel addStudent(Student studentToAdd) {
        Set<String> emptyFields = ReflectionUtil.listNullOrEmptyFields(studentToAdd);
        Set<String> emptyFieldsAfterExclusions = ReflectionUtil.removeExceptionsFromSet(emptyFields, Set.of("id", "uuid", "subject, subjects"));
        if (emptyFieldsAfterExclusions.isEmpty()) {

            if (!studentToAdd.getSubjects().isEmpty()) {
                List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(studentToAdd.getSubjects());
                List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(studentToAdd.getSubjects(), dbSubjects);
                subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));
                List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(studentToAdd.getSubjects());
                allSubjects.forEach(sub -> {
                    studentToAdd.getSubject().add(sub);
                });

            } else {
                studentToAdd.setSubject(null);
            }
            Student addedStudent = studentTransactionAccess.addStudent(studentToAdd);
            return StudentModel.toModel(studentToAdd);

        } else {
            throw new MissingFieldException(emptyFieldsAfterExclusions.toString() + " are blank or missing");
        }
    }

    @Override
    public void removeStudent(String uuid) {
       Student foundStudent = studentTransactionAccess.findStudentByUuid(uuid).orElseThrow(NoSuchIdException::new);
       studentTransactionAccess.removeStudent(foundStudent);

    }

    @Override
    public StudentModel updateStudentPartial(String uuid, Student updateInfo) {
        Student foundStudent = studentTransactionAccess.findStudentByUuid(uuid).orElseThrow(() -> new NoSuchIdException("No student with uuid: " +uuid+  " found"));
        System.out.println("updateStudentpartial subjects: " + updateInfo.getSubjects().toString());
        studentUpdateTargetFieldIfRequestFieldIsPresentAndNotBlank(foundStudent, updateInfo);
        foundStudent.getSubject().forEach(subject -> System.out.println("subject in found student: "+ subject.getTitle()));
        return StudentModel.toModel(studentTransactionAccess.updateStudent(foundStudent));
    }


    @Override
    public StudentModel findStudentByUuid(String uuid) {
        Student result = studentTransactionAccess.findStudentByUuid(uuid).orElseThrow(NoSuchIdException::new);
        return StudentModel.toModel(result);
    }

    @Override
    public List<StudentModel> findStudentsByName(String name) {
        List<Student> result = studentTransactionAccess.findStudentByName(name);
       return StudentModel.toModel(result);
    }

    @Override
    public StudentModel updateStudentFull(String uuid, Student updateInfo) {
        if(updateInfo.allMutableFieldsExistsAndNotEmpty()) {
            Student foundStudent = studentTransactionAccess.findStudentByUuid(uuid).orElseThrow(() -> new NoSuchIdException("No student with uuid: " +uuid+  " found"));
            updateInfo.setId(foundStudent.getId());
            updateInfo.setUuid(uuid);
            updateSubjectsInDb(updateInfo, foundStudent);
            return StudentModel.toModel(studentTransactionAccess.updateStudent(foundStudent));
        }
        else
            throw new WrongHttpMethodException("use http PATCH for partial updates");
    }



    ///// Subject Methods

    @Override
    public List<SubjectModel> listAllSubjects() {
       List<Subject> result = subjectTransactionAccess.listAllSubjects();
      return SubjectModel.toModel(result);
    }

    @Override
    public SubjectModel addSubject(Subject subjectToAdd) {
        Set<String> emptyFields = ReflectionUtil.listNullOrEmptyFields(subjectToAdd);
        Set<String> emptyFieldsAfterExclusions = ReflectionUtil.removeExceptionsFromSet(emptyFields, Set.of("id", "uuid"));
        if (emptyFieldsAfterExclusions.isEmpty()) {
            Subject addedSubject = subjectTransactionAccess.addSubject(subjectToAdd);
            return SubjectModel.toModel(addedSubject);
        } else {
            throw new MissingFieldException(emptyFieldsAfterExclusions.toString() + " are blank or missing");
        }
    }

    @Override
    public List<SubjectModel> findSubjectsByName(List<String> subject) {
     return SubjectModel.toModel(subjectTransactionAccess.getSubjectByName(subject));
    }

    @Override
    public void deleteSubjectByUuid(String uuid){
        Subject target = subjectTransactionAccess.findSubjectByUuid(uuid).orElseThrow(NoSuchIdException::new);
        target.getStudents().forEach(s -> studentTransactionAccess.removeSubjectFromStudent(s, target));
        target.getTeachers().forEach(t -> teacherTransactionAccess.removeSubjectFromTeacher(t, target));
        subjectTransactionAccess.removeSubject(target);
    }


    //// Teacher methods


    @Override
    public List<TeacherModel> listAllTeachers() {
       return TeacherModel.toModel(teacherTransactionAccess.listTeachers());
    }

    @Override
    public TeacherModel addTeacher(Teacher teacher) {
        Set<String> emptyFields = ReflectionUtil.listNullOrEmptyFields(teacher);
        Set<String> emptyFieldsAfterExclusions = ReflectionUtil.removeExceptionsFromSet(emptyFields, Set.of("id", "uuid", "subject", "subjects"));
        if (emptyFieldsAfterExclusions.isEmpty()) {

            if (!teacher.getSubjects().isEmpty()) {
                List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(teacher.getSubjects());
                List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(teacher.getSubjects(), dbSubjects);
                subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));
                List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(teacher.getSubjects());
                allSubjects.forEach(sub -> {
                    teacher.getSubject().add(sub);
                });
            }
            else {
                teacher.setSubject(null);
            }

            Teacher addedTeacher = teacherTransactionAccess.addTeacher(teacher);
            return TeacherModel.toModel(addedTeacher);

        } else {
            throw new MissingFieldException(emptyFieldsAfterExclusions.toString() + " are blank or missing");
        }
    }

    @Override
    public void removeTeacher(String uuid) {
        Teacher foundTeacher = teacherTransactionAccess.findTeacherByUuid(uuid).orElseThrow(NoSuchIdException::new);
        teacherTransactionAccess.removeTeacher(foundTeacher);
    }

    @Override
    public TeacherModel updateTeacherPartial(String uuid, Teacher updateInfo) {
        Teacher foundStudent = teacherTransactionAccess.findTeacherByUuid(uuid).orElseThrow(() -> new NoSuchIdException("No teacher with uuid: " +uuid+  " found"));
        studentUpdateTargetFieldIfRequestFieldIsPresentAndNotBlank(foundStudent, updateInfo);
        foundStudent.getSubject().forEach(subject -> System.out.println("subject in found student: "+ subject.getTitle()));
        return StudentModel.toModel(studentTransactionAccess.updateStudent(foundStudent));
    }

    @Override
    public TeacherModel findTeacherByUuid(String uuid) {
        Teacher result = teacherTransactionAccess.findTeacherByUuid(uuid).orElseThrow(NoSuchIdException::new);
        return TeacherModel.toModel(result);
    }

    @Override
    public TeacherModel updateTeacherFull(String uuid, Teacher teacher) {
        return null;
    }



    //// Utility methods

    private Student studentUpdateTargetFieldIfRequestFieldIsPresentAndNotBlank(Student foundStudent, Student updateInfo) {
        Optional<Student> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optUpdateInfo.isPresent()) {
            optUpdateInfo.map(Student::getEmail).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setEmail);
            optUpdateInfo.map(Student::getForename).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setForename);
            optUpdateInfo.map(Student::getLastname).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setLastname);
            if(optUpdateInfo.map(Student::getSubjects).filter(Predicate.not(List::isEmpty)).isPresent()){
                updateSubjectsInDb(updateInfo, foundStudent);
                return foundStudent;

          }
            return foundStudent;
        }
        else
            throw new IllegalArgumentException("updateInfo is null in update");
    }

    private Student teacherUpdateTargetFieldIfRequestFieldIsPresentAndNotBlank(Teacher foundTeacher, Teacher updateInfo) {
        Optional<Teacher> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optUpdateInfo.isPresent()) {
            optUpdateInfo.map(Teacher::getForename).filter(Predicate.not(String::isBlank)).ifPresent(foundTeacher::setForename);
            optUpdateInfo.map(Teacher::getLastname).filter(Predicate.not(String::isBlank)).ifPresent(foundTeacher::setLastname);
            if(optUpdateInfo.map(Teacher::getSubjects).filter(Predicate.not(List::isEmpty)).isPresent()){
                updateSubjectsInDb(updateInfo, foundTeacher);
                return foundTeacher;

            }
            return foundTeacher;
        }
        else
            throw new IllegalArgumentException("updateInfo is null in update");
    }


    public List<Subject> getSubjectsToAddToDb(List<String> stringSubjects, List<Subject> dbSubjects) {
        checkIfNullAndThenThrowException(dbSubjects);
        List<String> dbSubjectsTitleList = new ArrayList<>();
        dbSubjects.forEach(s-> Optional.ofNullable(s).map(Subject::getTitle).ifPresent(dbSubjectsTitleList::add));
       return toSubject(stringSubjects.stream().filter(s -> !dbSubjectsTitleList.contains(s)).collect(Collectors.toList()));
    }

    public List<Subject> getSubjectsToRemoveFromPerson(List<String> stringSubjects, Set<Subject> dbSubjects) {
        checkIfNullAndThenThrowException(dbSubjects);
        return dbSubjects.stream().filter(s -> !stringSubjects.contains(s.getTitle())).collect(Collectors.toList());
    }

    private List<Subject> toSubject(List<String> stringSubject){
        List<Subject> subjects = new ArrayList<>();
        stringSubject.forEach(s -> {
            Subject subject = new Subject();
            subject.setTitle(s);
            subjects.add(subject);
        });
        return subjects;
    }


    private void checkIfNullAndThenThrowException(Object object){
        if(object==null)
            throw new IllegalArgumentException();
    }

    private void updateSubjectsInDb(Person updateInfoPerson, Person foundPerson) {

        if(foundPerson instanceof Student && updateInfoPerson instanceof Student){

            Student foundStudent = (Student)foundPerson;
            Student updateInfo = (Student)updateInfoPerson;

            List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(updateInfo.getSubjects());
            List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(updateInfo.getSubjects(), dbSubjects);
            List<Subject> subjectsToRemoveFromStudent = getSubjectsToRemoveFromPerson(updateInfo.getSubjects(), foundStudent.getSubject());
            subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));
            subjectsToRemoveFromStudent.forEach(s -> studentTransactionAccess.removeSubjectFromStudent(foundStudent, s));
            List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(updateInfo.getSubjects());
            allSubjects.forEach(sub -> foundStudent.getSubject().add(sub));

        } else if (foundPerson instanceof Teacher && updateInfoPerson instanceof Teacher) {


            List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(updateInfoPerson.getSubjects());
            List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(updateInfoPerson.getSubjects(), dbSubjects);
            List<Subject> subjectsToRemoveFromStudent = getSubjectsToRemoveFromPerson(updateInfoPerson.getSubjects(), foundPerson.getSubject());
            subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));
            subjectsToRemoveFromStudent.forEach(s -> studentTransactionAccess.removeSubjectFromStudent(foundStudent, s));
            List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(updateInfoPerson.getSubjects());
            allSubjects.forEach(sub -> foundPerson.getSubject().add(sub));

        }



    }














}
