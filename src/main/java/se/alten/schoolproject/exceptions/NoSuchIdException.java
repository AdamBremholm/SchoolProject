package se.alten.schoolproject.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException
public class NoSuchIdException extends RuntimeException {
    public NoSuchIdException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public NoSuchIdException(Throwable err) {
        super(err);
    }

    public NoSuchIdException(String errorMessage) {
        super(errorMessage);
    }

    public NoSuchIdException() {
        super();
    }
}
