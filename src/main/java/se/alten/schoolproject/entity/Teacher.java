package se.alten.schoolproject.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name="teacher")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Teacher implements Serializable, Person {

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


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "subject_teacher",
            joinColumns=@JoinColumn(name="subj_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "teach_id", referencedColumnName = "id"))
    private Set<Subject> subject = new HashSet<>();

    @Transient
    private List<String> subjects = new ArrayList<>();

    @PrePersist
    public void initializeUUID() {
        if (getUuid() == null) {
            setUuid(UUID.randomUUID().toString());
        }
    }
}
