package com.project.market.infra.exception;

public class NoPrincipalException extends RuntimeException{

    public NoPrincipalException() {
        super();
    }

    public NoPrincipalException(String message) {
        super(message);
    }

    public NoPrincipalException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPrincipalException(Throwable cause) {
        super(cause);
    }
}
