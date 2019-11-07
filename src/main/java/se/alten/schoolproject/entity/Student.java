package se.alten.schoolproject.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Predicate;


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


    public boolean allFieldsExistsAndNotEmpty() {
       boolean emailExists =  Optional.of(this).map(Student::getEmail).filter(Predicate.not(String::isBlank)).isPresent();
       boolean forenameExists =  Optional.of(this).map(Student::getForename).filter(Predicate.not(String::isBlank)).isPresent();
       boolean lastNameExists =  Optional.of(this).map(Student::getLastname).filter(Predicate.not(String::isBlank)).isPresent();
       return emailExists && forenameExists && lastNameExists;
   }
}
