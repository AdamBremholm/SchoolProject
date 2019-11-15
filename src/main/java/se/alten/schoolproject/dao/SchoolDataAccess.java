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
                addSubjectsInDbAndAddToPerson(studentToAdd.getSubjects(), studentToAdd.getSubject());

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
        updateTargetFieldIfRequestFieldIsPresentAndNotBlank(foundStudent, updateInfo);
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
                addSubjectsInDbAndAddToPerson(teacher.getSubjects(), teacher.getSubject());
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
        Teacher foundTeacher = teacherTransactionAccess.findTeacherByUuid(uuid).orElseThrow(() -> new NoSuchIdException("No teacher with uuid: " +uuid+  " found"));
        updateTargetFieldIfRequestFieldIsPresentAndNotBlank(foundTeacher, updateInfo);
        return TeacherModel.toModel(teacherTransactionAccess.updateTeacher(foundTeacher));
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

    private void addSubjectsInDbAndAddToPerson(List<String> subjects, Set<Subject> subject) {
        List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(subjects);
        List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(subjects, dbSubjects);
        subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));
        List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(subjects);
        subject.addAll(allSubjects);
    }

    private Person updateTargetFieldIfRequestFieldIsPresentAndNotBlank(Person foundPerson, Person updateInfo) {
        Optional<Person> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optUpdateInfo.isPresent()) {
            if(updateInfo instanceof Student && foundPerson instanceof Student)
                Optional.of((Student)updateInfo).map(Student::getEmail).filter(Predicate.not(String::isBlank)).ifPresent(((Student)foundPerson)::setEmail);

            optUpdateInfo.map(Person::getForename).filter(Predicate.not(String::isBlank)).ifPresent(foundPerson::setForename);
            optUpdateInfo.map(Person::getLastname).filter(Predicate.not(String::isBlank)).ifPresent(foundPerson::setLastname);
            if(optUpdateInfo.map(Person::getSubjects).filter(Predicate.not(List::isEmpty)).isPresent()){
                updateSubjectsInDb(updateInfo, foundPerson);
                return foundPerson;

          }
            return foundPerson;
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

        List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(updateInfoPerson.getSubjects());
        List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(updateInfoPerson.getSubjects(), dbSubjects);
        List<Subject> subjectsToRemoveFromStudent = getSubjectsToRemoveFromPerson(updateInfoPerson.getSubjects(), foundPerson.getSubject());
        subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));

        if(foundPerson instanceof Student)
            subjectsToRemoveFromStudent.forEach(s -> studentTransactionAccess.removeSubjectFromStudent((Student)foundPerson, s));
        else if(foundPerson instanceof Teacher)
            subjectsToRemoveFromStudent.forEach(s -> teacherTransactionAccess.removeSubjectFromTeacher((Teacher)foundPerson, s));

        List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(updateInfoPerson.getSubjects());
        allSubjects.forEach(sub -> foundPerson.getSubject().add(sub));

    }














}
