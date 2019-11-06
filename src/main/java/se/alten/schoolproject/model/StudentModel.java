package se.alten.schoolproject.model;

import lombok.*;
import org.jboss.logging.Logger;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.rest.StudentController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentModel {

    public static final Logger logger = Logger.getLogger(StudentModel.class);

    private Long id;
    private String forename;
    private String lastname;
    private String email;

    public StudentModel toModel(Student student) {
        StudentModel studentModel = new StudentModel();
        Optional<Student> studentOptional = Optional.ofNullable(student);
        if(studentOptional.isPresent()) {
           studentOptional.map(Student::getId).ifPresent(studentModel::setId);
           studentOptional.map(Student::getForename).ifPresent(studentModel::setForename);
           studentOptional.map(Student::getLastname).ifPresent(studentModel::setLastname);
           studentOptional.map(Student::getEmail).ifPresent(studentModel::setEmail);
           return studentModel;
        } else
            throw new IllegalArgumentException("student not present in toModel");
    }

    public List<StudentModel> toModel(List<Student> students){
        List<StudentModel> studentModelList = new ArrayList<>();
        students.forEach(student -> studentModelList.add(toModel(student)));
        return studentModelList;
    }

    public List<String> listNullOrEmptyFieldsExceptId(){
        List<String> results = new ArrayList<>();
        for (Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.get(this) == null || f.get(this) instanceof String && ((String) f.get(this)).isBlank()) {
                    if(!f.getName().equals("id"))
                    results.add(f.getName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
