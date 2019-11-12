package se.alten.schoolproject.dao;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.exceptions.MissingFieldException;
import se.alten.schoolproject.exceptions.NoSuchIdException;
import se.alten.schoolproject.exceptions.NoSuchSubjectException;
import se.alten.schoolproject.exceptions.WrongHttpMethodException;
import se.alten.schoolproject.model.StudentModel;
import se.alten.schoolproject.model.SubjectModel;
import se.alten.schoolproject.transaction.StudentTransactionAccess;
import se.alten.schoolproject.transaction.SubjectTransactionAccess;
import se.alten.schoolproject.util.ReflectionUtil;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
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
                List<Subject> subjects = getSubjectsFromDbThatMatchesSubjectList(studentToAdd);
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
    public void removeStudent(String uuid) {
       Student foundStudent = studentTransactionAccess.findStudentByUuid(uuid).orElseThrow(NoSuchIdException::new);
       studentTransactionAccess.removeStudent(foundStudent);

    }

    @Override
    public StudentModel updateStudentPartial(String uuid, Student updateInfo) {
        Student foundStudent = studentTransactionAccess.findStudentByUuid(uuid).orElseThrow(() -> new NoSuchIdException("No student with uuid: " +uuid+  " found"));
        updateTargetFieldIfRequestFieldIsPresentAndNotBlank(foundStudent, updateInfo);
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
    public StudentModel updateStudentFull(String uuid, Student student) {
        if(student.allMutableFieldsExistsAndNotEmpty()) {
            Student foundStudent = studentTransactionAccess.findStudentByUuid(uuid).orElseThrow(() -> new NoSuchIdException("No student with uuid: " +uuid+  " found"));
            student.setId(foundStudent.getId());
            student.setUuid(uuid);
            List<Subject> subjects = getSubjectsFromDbThatMatchesSubjectList(student);
            subjects.forEach(sub -> {
                foundStudent.getSubject().add(sub);
            });

           return StudentModel.toModel(studentTransactionAccess.updateStudent(student));
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
            if(optUpdateInfo.map(Student::getSubjects).filter(Predicate.not(List::isEmpty)).isPresent()){

                List<Subject> subjects = subjectTransactionAccess.getSubjectByName(optUpdateInfo.get().getSubjects());
                subjects.forEach(sub -> {
                    foundStudent.getSubject().add(sub);
                });

            }
        }
        else
            throw new IllegalArgumentException("updateInfo is null in update");
    }


    private void handleMismatchErrorsInSubjectsStringListAndDb(Student student, List<Subject> subjects){
        List<String> notfoundSubjects = compareIncomingSubjectsWithSubjectsInDbAndOutputDiff(student.getSubjects(),subjects);
        if(!notfoundSubjects.isEmpty()){
            throw new NoSuchSubjectException("the subjects: " + notfoundSubjects.toString() + " where not found in the database, add them as subjects before using them here");
        }
    }
    private List<Subject> getSubjectsFromDbThatMatchesSubjectList(Student student){
        Optional.ofNullable(student).map(Student::getSubjects).orElseThrow(MissingFieldException::new);
        List<Subject> subjects = subjectTransactionAccess.getSubjectByName(student.getSubjects());
        handleMismatchErrorsInSubjectsStringListAndDb(student, subjects);
        return subjects;
    }

    private List<String> compareIncomingSubjectsWithSubjectsInDbAndOutputDiff(List<String> stringSubjects, List<Subject> dbSubjects) {
        List<String> dbSubjectsTitleList = new ArrayList<>();
        if(dbSubjects!=null){
            dbSubjects.forEach(s-> Optional.ofNullable(s).map(Subject::getTitle).ifPresent(dbSubjectsTitleList::add));
        }
       stringSubjects.removeAll(dbSubjectsTitleList);
       return stringSubjects;
    }











}
