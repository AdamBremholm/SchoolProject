package se.alten.schoolproject.exceptions;


import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public DuplicateStudentException(Throwable err) {
        super(err);
    }

    public DuplicateStudentException(String errorMessage) {
        super(errorMessage);
    }

    public DuplicateStudentException() {
        super();
    }
}
