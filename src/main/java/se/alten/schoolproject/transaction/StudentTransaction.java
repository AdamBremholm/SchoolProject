package se.alten.schoolproject.transaction;


import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.exceptions.DuplicateException;
import se.alten.schoolproject.exceptions.MissingFieldException;
import se.alten.schoolproject.exceptions.NoSuchIdException;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Stateless
@Default
public class StudentTransaction implements StudentTransactionAccess {


    @PersistenceContext(unitName="school")
    private EntityManager entityManager;


    @Override
    public List<Student> listStudents() {
     return entityManager.createQuery("SELECT s from Student s", Student.class).getResultList();
    }

    @Override
    public Student addStudent(Student student) {
        try {
            entityManager.persist(student);
            entityManager.flush();
            return student;
        } catch (PersistenceException e) {
            throw new DuplicateException();
        }

    }

    @Override
    public Optional<Student> findStudentById(Long id) {
        return Optional.ofNullable(entityManager.find(Student.class, id));
    }

    @Override
    public Optional<Student> findStudentByUuid(String uuid) {
        TypedQuery<Student> query = entityManager.createQuery("SELECT s from Student s WHERE s.uuid = :uuid", Student.class);
        return Optional.ofNullable(query.setParameter("uuid", uuid).getSingleResult());
    }

    @Override
    public List<Student> findStudentByName(String name) {
       TypedQuery<Student> query = entityManager.createQuery("SELECT s from Student s WHERE s.forename = :name OR s.lastname = :name", Student.class);
       query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public Optional<Student> findStudentByEmail(String email) {
        TypedQuery<Student> query = entityManager.createQuery("SELECT s from Student s WHERE s.email = :email", Student.class);
        query.setParameter("email", email);
        return Optional.ofNullable(query.getSingleResult());
    }


    @Override
    public void removeStudent(Student student) {
        entityManager.remove(student);
        entityManager.flush();
    }

    @Override
    public Student updateStudent(Student updateInfo) {
        entityManager.merge(updateInfo);
        entityManager.flush();
        return updateInfo;
     }



}
