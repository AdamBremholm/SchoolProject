package se.alten.schoolproject.entity;

import lombok.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.*;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="student")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "forename")
    private String forename;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email", unique = true)
    private String email;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "student_subject",
                joinColumns=@JoinColumn(name="stud_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "subj_id", referencedColumnName = "id"))
    private List<Subject> subject = new ArrayList<>();

    public Student toEntity(String studentModel) {
        JsonReader reader = Json.createReader(new StringReader(studentModel));

        JsonObject jsonObject = reader.readObject();

        Student student = new Student();
        if ( jsonObject.containsKey("forename")) {
            student.setForename(jsonObject.getString("forename"));
        } else {
            student.setForename("");
        }

        if ( jsonObject.containsKey("lastname")) {
            student.setLastname(jsonObject.getString("lastname"));
        } else {
            student.setLastname("");
        }

        if ( jsonObject.containsKey("email")) {
            student.setEmail(jsonObject.getString("email"));
        } else {
            student.setEmail("");
        }

        return student;
    }
}
