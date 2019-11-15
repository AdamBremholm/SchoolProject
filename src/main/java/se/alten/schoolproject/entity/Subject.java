package se.alten.schoolproject.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="subject")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column
    private String title;

    @ManyToMany(mappedBy = "subject", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();

    @ManyToMany(mappedBy = "subject", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Teacher> teachers = new HashSet<>();

    @PrePersist
    public void initializeUUID() {
        if (getUuid() == null) {
            setUuid(UUID.randomUUID().toString());
        }
    }

}
