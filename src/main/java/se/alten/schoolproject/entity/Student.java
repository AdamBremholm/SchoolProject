package se.alten.schoolproject.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
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
    @Column(name="id")
    private Long id;

    @Column(name = "forename")
    private String forename;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email", unique = true)
    private String email;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "student_subject",
            joinColumns=@JoinColumn(name="stud_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subj_id", referencedColumnName = "id"))
    private Set<Subject> subject = new HashSet<>();

    @Transient
    private List<String> subjects = new ArrayList<>();


    public boolean allFieldsExistsAndNotEmpty() {
       boolean emailExists =  Optional.of(this).map(Student::getEmail).filter(Predicate.not(String::isBlank)).isPresent();
       boolean forenameExists =  Optional.of(this).map(Student::getForename).filter(Predicate.not(String::isBlank)).isPresent();
       boolean lastNameExists =  Optional.of(this).map(Student::getLastname).filter(Predicate.not(String::isBlank)).isPresent();
       return emailExists && forenameExists && lastNameExists;
   }
}
