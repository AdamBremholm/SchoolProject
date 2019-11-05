package se.alten.schoolproject.transaction;


import se.alten.schoolproject.entity.Student;


import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.persistence.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

@Stateless
@Default
public class StudentTransaction implements TransactionAccess<Student> {


    @PersistenceContext(unitName="school")
    private EntityManager entityManager;


    @Override
    public List list() {
        Query query = entityManager.createQuery("SELECT s from Student s");
        return query.getResultList();
    }

    @Override
    public Student add(Student studentToAdd) {

        try {
            entityManager.persist(studentToAdd);
            entityManager.flush();
            return studentToAdd;
        } catch ( PersistenceException pe ) {
            //TODO() error handling
            studentToAdd.setForename("duplicate");
            return studentToAdd;
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
    public void remove(Long id) {
         if (findById(id).isPresent()) {
             entityManager.getTransaction().begin();
             entityManager.remove(id);
             entityManager.getTransaction().commit();
         } else {
             throw new NoSuchElementException("No user with that id found");
         }

    }

    @Override
    public void update(Long id, Student updateInfo) {
        Optional<Student> optStudent = findById(id);
        Optional<Student> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optStudent.isPresent() && optUpdateInfo.isPresent()){
            entityManager.getTransaction().begin();
            optUpdateInfo.map(Student::getEmail).ifPresent(optStudent.get()::setEmail);
            optUpdateInfo.map(Student::getForename).ifPresent(optStudent.get()::setForename);
            optUpdateInfo.map(Student::getLastname).ifPresent(optStudent.get()::setLastname);
            entityManager.getTransaction().commit();
        } else
            throw new NoSuchElementException("No user with that id found");
     }



}
