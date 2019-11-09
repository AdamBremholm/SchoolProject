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
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Stateless
public class SchoolDataAccess implements SchoolAccessLocal, SchoolAccessRemote {


    @Inject
    StudentTransactionAccess studentTransactionAccess;

    @Inject
    SubjectTransactionAccess subjectTransactionAccess;


    //Student methods

    @Override
    public List<Student> listAllStudents(){
        List<Student> result = studentTransactionAccess.listStudents();
        result.forEach(System.out::println);
        return result;
    }

    @Override
    public StudentModel addStudent(Student studentToAdd) {
        List<String> nullOrEmptyFields = ReflectionUtil.listNullOrEmptyFields(studentToAdd, List.of("id", "subject"));
        if (nullOrEmptyFields.isEmpty()) {
            Student addedStudent = studentTransactionAccess.addStudent(studentToAdd);
            return StudentModel.toModel(addedStudent);
        } else {
            throw new MissingFieldException(nullOrEmptyFields.toString() + " are blank or missing");
        }
    }

    @Override
    public void removeStudent(Long id) {
       Student foundStudent = studentTransactionAccess.findStudentById(id).orElseThrow(NoSuchIdException::new);
       studentTransactionAccess.removeStudent(foundStudent);

    }

    @Override
    public StudentModel updateStudentPartial(Long id, Student updateInfo) {
        Student foundStudent = studentTransactionAccess.findStudentById(id).orElseThrow(() -> new NoSuchIdException("No student with id: " +id+  " found"));
        updateTargetFieldIfRequestFieldIsPresentAndNotBlank(foundStudent, updateInfo);
        studentTransactionAccess.updateStudent(foundStudent);
        return findStudentById(id);
    }


    @Override
    public StudentModel findStudentById(Long id) {
        Student result = studentTransactionAccess.findStudentById(id).orElseThrow(NoSuchIdException::new);
        return StudentModel.toModel(result);
    }

    @Override
    public List<Student> findStudentsByName(String name) {
        List<Student> result = studentTransactionAccess.findStudentByName(name);
       return result;
    }

    @Override
    public StudentModel updateStudentFull(Long id, Student student) {
        if(student.allFieldsExistsAndNotEmpty()) {
            studentTransactionAccess.findStudentById(id).orElseThrow(() -> new NoSuchIdException("No student with id: " +id+  " found"));
            student.setId(id);
            studentTransactionAccess.updateStudent(student);
            return findStudentById(id);
        }
        else
            throw new WrongHttpMethodException("use http PATCH for partial updates");
    }

    ///// Subject Methods

    @Override
    public List<Subject> listAllSubjects() {
       List<Subject> result = subjectTransactionAccess.listAllSubjects();
       return result;
    }

    @Override
    public SubjectModel addSubject(Subject subjectToAdd) {
        List<String> nullOrEmptyFields = ReflectionUtil.listNullOrEmptyFields(subjectToAdd, List.of("id"));
        if (nullOrEmptyFields.isEmpty()) {
            Subject addedSubject = subjectTransactionAccess.addSubject(subjectToAdd);
            return SubjectModel.toModel(addedSubject);
        } else {
            throw new MissingFieldException(nullOrEmptyFields.toString() + " are blank or missing");
        }
    }

    @Override
    public List<Subject> findSubjectsByName(List<String> subject) {
     return subjectTransactionAccess.getSubjectByName(subject);
    }


    //// Utility methods

    private void updateTargetFieldIfRequestFieldIsPresentAndNotBlank(Student foundStudent, Student updateInfo) {
        Optional<Student> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optUpdateInfo.isPresent()) {
            optUpdateInfo.map(Student::getEmail).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setEmail);
            optUpdateInfo.map(Student::getForename).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setForename);
            optUpdateInfo.map(Student::getLastname).filter(Predicate.not(String::isBlank)).ifPresent(foundStudent::setLastname);
        }
        else
            throw new IllegalArgumentException("updateInfo is null in update");
    }







}
