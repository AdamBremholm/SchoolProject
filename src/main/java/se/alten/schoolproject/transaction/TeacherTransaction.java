package se.alten.schoolproject.transaction;

import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.entity.Teacher;
import se.alten.schoolproject.exceptions.DuplicateException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class TeacherTransaction implements TeacherTransactionAccess {

    @PersistenceContext(unitName = "school")
    private EntityManager entityManager;

    @Override
    public List<Teacher> listTeachers() {
        return entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    }

    @Override
    public Teacher addTeacher(Teacher teacher) {
        try {
            entityManager.persist(teacher);
            entityManager.flush();
            return teacher;
        } catch (PersistenceException e) {
            throw new DuplicateException();
        }
    }

    @Override
    public void removeTeacher(Teacher teacher) {
        entityManager.remove(teacher);
        entityManager.flush();
    }

    @Override
    public Teacher updateTeacher(Teacher updateInfo) {
        entityManager.merge(updateInfo);
        entityManager.flush();
        return updateInfo;
    }

    @Override
    public Optional<Teacher> findTeacherById(Long id) {
        return Optional.ofNullable(entityManager.find(Teacher.class, id));
    }

    @Override
    public Optional<Teacher> findTeacherByUuid(String uuid) {
        TypedQuery<Teacher> query = entityManager.createQuery("SELECT t from Teacher t WHERE t.uuid = :uuid", Teacher.class);
        return Optional.ofNullable(query.setParameter("uuid", uuid).getSingleResult());
    }

    @Override
    public void removeSubjectFromTeacher(Teacher t, Subject target) {

        TypedQuery<Teacher> query = entityManager.createQuery("SELECT t from Teacher t join fetch t.subject WHERE t.id = :id", Teacher.class);
        query.setParameter("id", t.getId());
        Teacher result = query.getSingleResult();
        result.getSubject().removeIf(ts -> ts.getId().equals(target.getId()));
        entityManager.merge(result);
        entityManager.flush();
    }

}


