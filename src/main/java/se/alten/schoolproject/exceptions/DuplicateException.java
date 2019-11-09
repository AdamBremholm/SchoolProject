package se.alten.schoolproject.exceptions;


import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class DuplicateException extends RuntimeException {
    public DuplicateException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public DuplicateException(Throwable err) {
        super(err);
    }

    public DuplicateException(String errorMessage) {
        super(errorMessage);
    }

    public DuplicateException() {
        super();
    }
}
