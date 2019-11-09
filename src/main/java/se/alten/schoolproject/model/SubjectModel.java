package se.alten.schoolproject.model;

import lombok.*;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubjectModel {

    private Long id;
    private String title;

    public static SubjectModel toModel(Subject subjectToAdd) {
        SubjectModel subjectModel = new SubjectModel();
        subjectModel.setTitle(subjectToAdd.getTitle());
        return subjectModel;
    }

    public static List<SubjectModel> toModel(List<Subject> subjects){
        List<SubjectModel> subjectModelList = new ArrayList<>();
        subjects.forEach(subject -> subjectModelList.add(toModel(subject)));
        return subjectModelList;
    }
}
