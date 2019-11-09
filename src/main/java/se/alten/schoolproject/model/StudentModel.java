package se.alten.schoolproject.model;

import lombok.*;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentModel implements Serializable {


    private static final long serialVersionUID = 1L;
    private Long id;
    private String forename;
    private String lastname;
    private String email;

    public static StudentModel toModel(Student student) {
        StudentModel studentModel = new StudentModel();
        Optional<Student> studentOptional = Optional.ofNullable(student);
        if(studentOptional.isPresent()) {
           studentOptional.map(Student::getId).ifPresent(studentModel::setId);
           studentOptional.map(Student::getForename).filter(Predicate.not(String::isBlank)).ifPresent(studentModel::setForename);
           studentOptional.map(Student::getLastname).filter(Predicate.not(String::isBlank)).ifPresent(studentModel::setLastname);
           studentOptional.map(Student::getEmail).filter(Predicate.not(String::isBlank)).ifPresent(studentModel::setEmail);
           return studentModel;
        } else
            throw new IllegalArgumentException("student not present in toModel");
    }

    public static List<StudentModel> toModel(List<Student> students){
        List<StudentModel> studentModelList = new ArrayList<>();
        students.forEach(student -> studentModelList.add(toModel(student)));
        return studentModelList;
    }


}
