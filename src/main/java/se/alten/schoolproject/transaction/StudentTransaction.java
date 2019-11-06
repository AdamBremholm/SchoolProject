package se.alten.schoolproject.transaction;


import se.alten.schoolproject.entity.Student;


import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.persistence.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Stateless
@Default
public class StudentTransaction implements TransactionAccess<Student> {


    @PersistenceContext(unitName="school")
    private EntityManager entityManager;


    @Override
    public List list() {
     return entityManager.createQuery("SELECT s from Student s").getResultList();
    }

    @Override
    public Student add(Student studentToAdd) {
            entityManager.persist(studentToAdd);
            entityManager.flush();
            return studentToAdd;
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
    public void remove(Long id) {
        entityManager.remove(id);
        entityManager.flush();
    }

    @Override
    public void update(Long id, Student updateInfo) {
        entityManager.merge(updateInfo);
        entityManager.flush();
     }



}
