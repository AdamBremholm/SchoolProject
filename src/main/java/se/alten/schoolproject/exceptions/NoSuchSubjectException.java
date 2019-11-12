package se.alten.schoolproject.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException
public class NoSuchSubjectException extends RuntimeException {
    public NoSuchSubjectException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public NoSuchSubjectException(Throwable err) {
        super(err);
    }

    public NoSuchSubjectException(String errorMessage) {
        super(errorMessage);
    }

    public NoSuchSubjectException() {
        super();
    }
}
