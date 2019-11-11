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
import java.util.Set;
import java.util.function.Predicate;

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
            Student addedStudent = studentTransactionAccess.addStudent(studentToAdd);
            if (!emptyFields.contains("subjects")) {
                List<Subject> subjects = subjectTransactionAccess.getSubjectByName(studentToAdd.getSubjects());
                                subjects.forEach(sub -> {
                    addedStudent.getSubject().add(sub);
                });


            }

            return StudentModel.toModel(addedStudent);

        } else {
            throw new MissingFieldException(emptyFieldsAfterExclusions.toString() + " are blank or missing");
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
    public List<StudentModel> findStudentsByName(String name) {
        List<Student> result = studentTransactionAccess.findStudentByName(name);
       return StudentModel.toModel(result);
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
