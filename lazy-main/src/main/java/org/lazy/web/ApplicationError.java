package org.lazy.web;

public class ApplicationError {
    private int status;
    private String message;
    private String[] errors;

    public ApplicationError(int status, String message, String ...errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApplicationError(int status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }
}
