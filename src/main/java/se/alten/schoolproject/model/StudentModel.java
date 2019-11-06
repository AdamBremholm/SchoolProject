package se.alten.schoolproject.model;

import lombok.*;
import org.jboss.logging.Logger;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.rest.StudentController;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /**
     * Uses reflection to check if any of the declared fields in this instance of StudentModel(except id) is null or empty.
     * Put this method here instead of in Student so that the Student fields are not exposed
     * @return list will null and empty field names.
     */
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
