package se.alten.schoolproject.entity;

import lombok.*;
import se.alten.schoolproject.model.StudentModel;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.*;
import java.io.Serializable;
import java.io.StringReader;


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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "forename")
    private String forename;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email", unique = true)
    private String email;

    public Student toEntity(String studentJsonString) {
        JsonReader reader = Json.createReader(new StringReader(studentJsonString));
        JsonObject jsonObject = reader.readObject();

        Student student = new Student();

        if ( jsonObject.containsKey("forename")) {
            student.setForename(jsonObject.getString("forename"));
        }
        if ( jsonObject.containsKey("lastname")) {
            student.setLastname(jsonObject.getString("lastname"));
        }
        if ( jsonObject.containsKey("email")) {
            student.setEmail(jsonObject.getString("email"));
        }
        return student;
    }

}
