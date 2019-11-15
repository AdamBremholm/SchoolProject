package se.alten.schoolproject.model;

import lombok.*;
import se.alten.schoolproject.entity.Teacher;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeacherModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String uuid;
    private String forename;
    private String lastname;
    private Set<String> subjects = new HashSet<>();

    public static TeacherModel toModel(Teacher teacher) {
        TeacherModel teacherModel = new TeacherModel();
        Optional.ofNullable(teacher).map(Teacher::getForename).ifPresent(teacherModel::setForename);
        Optional.ofNullable(teacher).map(Teacher::getLastname).ifPresent(teacherModel::setLastname);
        Optional.ofNullable(teacher).map(Teacher::getUuid).ifPresent(teacherModel::setUuid);
        Optional.ofNullable(teacher).map(Teacher::getSubject).ifPresent(s -> s.forEach(su -> teacherModel.getSubjects().add(su.getTitle())));
        return teacherModel;
    }

    public static List<TeacherModel> toModel(List<Teacher> teachers){
        List<TeacherModel> teacherModelList = new ArrayList<>();
        teachers.forEach(teacher -> teacherModelList.add(toModel(teacher)));
        return teacherModelList;
    }
}
