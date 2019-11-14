package se.alten.schoolproject.transaction;


import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.exceptions.DuplicateException;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.persistence.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


@Stateless
@Default
public class SubjectTransaction implements SubjectTransactionAccess{

    @PersistenceContext(unitName="school")
    private EntityManager entityManager;

    @Override
    public List listAllSubjects() {
        return entityManager.createQuery("SELECT s FROM Subject s", Subject.class).getResultList();
    }

    @Override
    public Subject addSubject(Subject subject) {
        try {
            entityManager.persist(subject);
            entityManager.flush();
            return subject;
        } catch (PersistenceException e) {
            throw new DuplicateException();
        }
    }

    @Override
    public List<Subject> getSubjectByName(List<String> subject) {

        String queryStr = "SELECT sub FROM Subject sub WHERE sub.title IN :subject";
        TypedQuery<Subject> query = entityManager.createQuery(queryStr, Subject.class);
        query.setParameter("subject", subject);

        return query.getResultList();
    }

    public Optional<Subject> findSubjectByUuid(String uuid) {
        TypedQuery<Subject> query = entityManager.createQuery("SELECT s from Subject s WHERE s.uuid = :uuid", Subject.class);
        return Optional.ofNullable(query.setParameter("uuid", uuid).getSingleResult());
    }

    @Override
    public void removeSubject(Subject subject) {
        entityManager.remove(subject);
        entityManager.flush();

    }


}
