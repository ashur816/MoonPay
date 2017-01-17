package com.martin.exception;


public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 3425545575477750228L;

    private String messageCode;
    private String message;

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(String messageCode, String message) {
        this.messageCode = messageCode;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String getMessage() {
        return message;
    }


}
