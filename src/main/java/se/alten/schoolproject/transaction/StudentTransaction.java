package se.alten.schoolproject.transaction;


import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.exceptions.DuplicateStudentException;


import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;


@Stateless
@Default
public class StudentTransaction implements TransactionAccess<Student> {


    @PersistenceContext(unitName="school")
    private EntityManager entityManager;


    @Override
    public List<Student> list() {
     return entityManager.createQuery("SELECT s from Student s", Student.class).getResultList();
    }

    @Override
    public void add(Student studentToAdd) {
            entityManager.persist(studentToAdd);
        try {
            entityManager.flush();
        } catch (PersistenceException e) {
            throw new DuplicateStudentException();
        }

    }

    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Student.class, id));
    }

    @Override
    public List<Student> findByName(String name) {
       TypedQuery<Student> query = entityManager.createQuery("SELECT s from Student s WHERE s.forename = :name OR s.lastname = :name", Student.class);
       query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        TypedQuery<Student> query = entityManager.createQuery("SELECT s from Student s WHERE s.email = :email", Student.class);
        query.setParameter("email", email);
        return Optional.ofNullable(query.getSingleResult());
    }


    @Override
    public void remove(Student student) {
        entityManager.remove(student);
        entityManager.flush();
    }

    @Override
    public void update(Student updateInfo) {
        entityManager.merge(updateInfo);
        entityManager.flush();
     }



}
