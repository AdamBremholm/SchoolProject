package se.alten.schoolproject.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException
public class NoSuchEmailException extends RuntimeException {
    public NoSuchEmailException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public NoSuchEmailException(Throwable err) {
        super(err);
    }

    public NoSuchEmailException(String errorMessage) {
        super(errorMessage);
    }

    public NoSuchEmailException() {
        super();
    }
}
