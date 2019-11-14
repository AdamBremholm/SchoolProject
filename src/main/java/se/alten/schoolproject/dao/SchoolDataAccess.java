package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.exceptions.MissingFieldException;
import se.alten.schoolproject.exceptions.NoSuchIdException;
import se.alten.schoolproject.exceptions.WrongHttpMethodException;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.model.SubjectModel;
import se.alten.schoolproject.transaction.StudentTransactionAccess;
import se.alten.schoolproject.transaction.SubjectTransactionAccess;
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


    //Student methods

    @Override
    public List<StudentModel> listAllStudents(){
        List<Student> result = studentTransactionAccess.listStudents();
        return StudentModel.toModel(result);
    }

    @Override
    public StudentModel addStudent(Student studentToAdd) {
        Set<String> emptyFields = ReflectionUtil.listNullOrEmptyFields(studentToAdd);
        Set<String> emptyFieldsAfterExclusions = ReflectionUtil.removeExceptionsFromSet(emptyFields, Set.of("id", "uuid", "subject", "subjects"));
        if (emptyFieldsAfterExclusions.isEmpty()) {

            if (!emptyFields.contains("subjects")) {
                List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(studentToAdd.getSubjects());
                List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(studentToAdd.getSubjects(), dbSubjects);
                subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));
                List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(studentToAdd.getSubjects());
                allSubjects.forEach(sub -> {
                    studentToAdd.getSubject().add(sub);
                });
            }
            Student addedStudent = studentTransactionAccess.addStudent(studentToAdd);

            return StudentModel.toModel(addedStudent);

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
            List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(updateInfo.getSubjects());
            List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(updateInfo.getSubjects(), dbSubjects);
            List<Subject> subjectsToRemoveFromStudent = getSubjectsToRemoveFromStudent(updateInfo.getSubjects(), foundStudent.getSubject());
            subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));
            subjectsToRemoveFromStudent.forEach(s -> studentTransactionAccess.removeSubjectFromStudent(foundStudent, s));
            List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(updateInfo.getSubjects());
            allSubjects.forEach(sub -> foundStudent.getSubject().add(sub));
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

    //// Utility methods

    private Student updateTargetFieldIfRequestFieldIsPresentAndNotBlank(Student foundStudent, Student updateInfo) {
        Optional<Student> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optUpdateInfo.isPresent()) {
            optUpdateInfo.map(Student::getEmail).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setEmail);
            optUpdateInfo.map(Student::getForename).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setForename);
            optUpdateInfo.map(Student::getLastname).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setLastname);
            if(optUpdateInfo.map(Student::getSubjects).filter(Predicate.not(List::isEmpty)).isPresent()){

                List<Subject> dbSubjects = subjectTransactionAccess.getSubjectByName(updateInfo.getSubjects());
                List<Subject> subjectsToAddGlobally = getSubjectsToAddToDb(updateInfo.getSubjects(), dbSubjects);
                List<Subject> subjectsToRemoveFromStudent = getSubjectsToRemoveFromStudent(updateInfo.getSubjects(), foundStudent.getSubject());
                subjectsToAddGlobally.forEach(s -> subjectTransactionAccess.addSubject(s));
                subjectsToRemoveFromStudent.forEach(s -> studentTransactionAccess.removeSubjectFromStudent(foundStudent, s));
                List<Subject> allSubjects = subjectTransactionAccess.getSubjectByName(updateInfo.getSubjects());
                allSubjects.forEach(sub -> foundStudent.getSubject().add(sub));
                return foundStudent;

          }
            return foundStudent;
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

    public List<Subject> getSubjectsToRemoveFromStudent(List<String> stringSubjects, Set<Subject> dbSubjects) {
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














}
