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

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "forename")
    private String forename;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email", unique = true)
    private String email;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "student_subject",
            joinColumns=@JoinColumn(name="stud_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subj_id", referencedColumnName = "id"))
    private Set<Subject> subject = new HashSet<>();

    @Transient
    private List<String> subjects = new ArrayList<>();


    public boolean allMutableFieldsExistsAndNotEmpty() {
       boolean emailExists =  Optional.of(this).map(Student::getEmail).filter(Predicate.not(String::isBlank)).isPresent();
       boolean forenameExists =  Optional.of(this).map(Student::getForename).filter(Predicate.not(String::isBlank)).isPresent();
       boolean lastNameExists =  Optional.of(this).map(Student::getLastname).filter(Predicate.not(String::isBlank)).isPresent();
       boolean subjectsExists =  Optional.of(this).map(Student::getSubjects).filter(Predicate.not(List::isEmpty)).isPresent();
       return emailExists && forenameExists && lastNameExists && subjectsExists;
   }

    @PrePersist
    public void initializeUUID() {
        if (getUuid() == null) {
            setUuid(UUID.randomUUID().toString());
        }
    }
}
