package se.alten.schoolproject.model;

import lombok.*;
import org.jboss.logging.Logger;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.rest.StudentController;

import java.util.ArrayList;
import java.util.List;

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
        switch (student.getForename()) {
            case "empty":
                studentModel.setForename("empty");
                return studentModel;
            case "duplicate":
                studentModel.setForename("duplicate");
                return studentModel;
            default:
                studentModel.setId(student.getId());
                studentModel.setForename(student.getForename());
                studentModel.setLastname(student.getLastname());
                studentModel.setEmail(student.getEmail());
                return studentModel;
        }
    }

    public List<StudentModel> toModel(List<Student> students){
        List<StudentModel> studentModelList = new ArrayList<>();
        students.forEach(student -> studentModelList.add(toModel(student)));
        return studentModelList;
    }
}
