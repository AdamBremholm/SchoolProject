package se.alten.schoolproject.exceptions;

import javax.ejb.ApplicationException;


@ApplicationException
public class WrongHttpMethodException extends RuntimeException {
    public WrongHttpMethodException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public WrongHttpMethodException(Throwable err) {
        super(err);
    }

    public WrongHttpMethodException(String errorMessage) {
        super(errorMessage);
    }

    public WrongHttpMethodException() {
        super();
    }
}
