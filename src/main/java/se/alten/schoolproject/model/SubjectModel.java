package se.alten.schoolproject.model;

import lombok.*;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubjectModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String uuid;
    private String title;
    private List<String> students = new ArrayList<>();

    public static SubjectModel toModel(Subject subjectToAdd) {
        SubjectModel subjectModel = new SubjectModel();
        Optional.ofNullable(subjectToAdd).map(Subject::getTitle).ifPresent(subjectModel::setTitle);
        Optional.ofNullable(subjectToAdd).map(Subject::getStudents).ifPresent(students -> students.forEach(s -> subjectModel.getStudents().add(s.getUuid())));
        Optional.ofNullable(subjectToAdd).map(Subject::getUuid).ifPresent(subjectModel::setUuid);
        return subjectModel;
    }

    public static List<SubjectModel> toModel(List<Subject> subjects){
        List<SubjectModel> subjectModelList = new ArrayList<>();
        subjects.forEach(subject -> subjectModelList.add(toModel(subject)));
        return subjectModelList;
    }
}
