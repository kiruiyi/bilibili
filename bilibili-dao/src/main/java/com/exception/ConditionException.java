package com.exception;

public class ConditionException extends RuntimeException {
    private String code;


    public ConditionException(String msg, String code) {
        super(msg);
        this.code = code;
    }

    public ConditionException(String msg) {
        super(msg);
        code = "500";
    }


    public String getCode() {
        return code;
    }

}
