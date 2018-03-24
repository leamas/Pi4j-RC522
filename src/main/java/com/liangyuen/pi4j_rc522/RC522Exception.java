package com.liangyuen.pi4j_rc522;


/** A pi4j_rc522 call not returning MI_OK. */
public class RC522Exception extends Exception {


    private static final long serialVersionUID = 1L;

    protected int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Construct an exception with message and code.
     * @param errorCode A pi4j error code.
     * @param msg The error message.
     */
    public RC522Exception(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public String toString() {
        return super.toString() + "code: " + errorCode;
    }

}
