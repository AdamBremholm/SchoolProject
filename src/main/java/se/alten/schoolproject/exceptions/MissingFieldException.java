package se.alten.schoolproject.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException
public class MissingFieldException extends RuntimeException {
    public MissingFieldException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public MissingFieldException(Throwable err) {
        super(err);
    }

    public MissingFieldException(String errorMessage) {
        super(errorMessage);
    }

    public MissingFieldException() {
        super();
    }
}
