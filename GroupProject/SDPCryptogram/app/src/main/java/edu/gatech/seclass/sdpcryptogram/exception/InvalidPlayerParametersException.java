package edu.gatech.seclass.sdpcryptogram.exception;

import java.security.InvalidParameterException;

/**
 * Created by Nick Marsh on 7/3/2017.
 */

public class InvalidPlayerParametersException extends InvalidParameterException {
    private String usernameError;
    private String passwordError;

    public InvalidPlayerParametersException(String usernameError, String passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
    }

    public InvalidPlayerParametersException(String msg, String usernameError, String passwordError) {
        super(msg);
        this.usernameError = usernameError;
        this.passwordError = passwordError;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }
}
