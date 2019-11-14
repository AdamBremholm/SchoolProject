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
import java.sql.SQLOutput;
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
                List<Subject> subjects = fetchAndCreateSubjects(studentToAdd);
                subjects.forEach(sub -> {
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
    public StudentModel updateStudentFull(String uuid, Student student) {
        if(student.allMutableFieldsExistsAndNotEmpty()) {
            Student foundStudent = studentTransactionAccess.findStudentByUuid(uuid).orElseThrow(() -> new NoSuchIdException("No student with uuid: " +uuid+  " found"));
            student.setId(foundStudent.getId());
            student.setUuid(uuid);
            List<Subject> subjects = fetchAndCreateSubjects(student);
            Set<Subject> subjectsNoLongerTakenByStudent = getSubjectsDiff(student.getSubjects(),foundStudent.getSubject());
            subjectTransactionAccess.deleteOrphanedStudentInNoLongerTakenSubjects(foundStudent, subjectsNoLongerTakenByStudent);
            foundStudent.getSubject().clear();
            subjects.forEach(s -> foundStudent.getSubject().add(s));
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

                List<Subject> subjects = fetchAndCreateSubjects(updateInfo);
                subjects.forEach(s -> System.out.println("returned subjects 2: " + s.getTitle()));
                System.out.println("asb");
                //1.Hämta subjects om finns i db
                //2. Om det finns nya subjects med i request skapa dom i db //behöver inte ha med students
                //3. Om subjects ska tas bort

                Set<Subject> subjectsNoLongerTakenByStudent = getSubjectsDiff(updateInfo.getSubjects(),foundStudent.getSubject());
                subjectTransactionAccess.deleteOrphanedStudentInNoLongerTakenSubjects(foundStudent, subjectsNoLongerTakenByStudent);
                foundStudent.getSubject().clear();
                subjects.forEach(s -> System.out.println("returned subjects: 3 " + s.getTitle()));
                subjects.forEach(s -> foundStudent.getSubject().add(s));
          }
        }
        else
            throw new IllegalArgumentException("updateInfo is null in update");
    }

    private Set<Subject> getSubjectsDiff(List<String> updateRequestSubjects, Set<Subject> foundSubjects) {
       return foundSubjects.stream().filter(s -> !updateRequestSubjects.contains(s.getTitle())).collect(Collectors.toSet());
    }


    private void createSubjectsIfNotAlreadyInDb(Student student, List<Subject> subjects){
        List<String> notfoundSubjects = compareIncomingSubjectsWithSubjectsInDbAndOutputDiff(student.getSubjects(),subjects);
        if(!notfoundSubjects.isEmpty()){
            notfoundSubjects.forEach(s -> {
                Subject subject = new Subject();
                subject.setTitle(s);
                subject.getStudents().add(student);
                subjectTransactionAccess.addSubject(subject);
                //:TODO before it was subjects.add(subject) and worked
            });
            fetchAndCreateSubjects(student);
        }
    }
    private List<Subject> fetchAndCreateSubjects(Student student){
        Optional.ofNullable(student).map(Student::getSubjects).orElseThrow(MissingFieldException::new);
        List<Subject> subjects = subjectTransactionAccess.getSubjectByName(student.getSubjects());
        createSubjectsIfNotAlreadyInDb(student, subjects);
        subjects.forEach(s -> System.out.println("returned subjects  1: " + s.getTitle()));
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
