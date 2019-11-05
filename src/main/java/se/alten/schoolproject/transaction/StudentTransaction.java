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
    private EntityTransaction transaction = entityManager.getTransaction();

    @Override
    public List list() {
        Query query = entityManager.createQuery("SELECT s from Student s");
        return query.getResultList();
    }

    @Override
    public Student add(Student studentToAdd) {
        try {
            transaction.begin();
            entityManager.persist(studentToAdd);
            transaction.commit();
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
             transaction.begin();
             entityManager.remove(id);
             transaction.commit();
         } else {
             throw new NoSuchElementException("No user with that id found");
         }

    }

    @Override
    public void update(Long id, Student updateInfo) {
        Optional<Student> optStudent = findById(id);
        Optional<Student> optUpdateInfo = Optional.ofNullable(updateInfo);
        if (optStudent.isPresent() && optUpdateInfo.isPresent()){
            transaction.begin();
            optUpdateInfo.map(Student::getEmail).ifPresent(optStudent.get()::setEmail);
            optUpdateInfo.map(Student::getForename).ifPresent(optStudent.get()::setForename);
            optUpdateInfo.map(Student::getLastname).ifPresent(optStudent.get()::setLastname);
            transaction.commit();
        } else
            throw new NoSuchElementException("No user with that id found");
     }



}
