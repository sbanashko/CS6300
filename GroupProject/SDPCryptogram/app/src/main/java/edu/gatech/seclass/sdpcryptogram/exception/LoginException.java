package edu.gatech.seclass.sdpcryptogram.exception;

/**
 * Created by Nick on 7/10/2017.
 */

public class LoginException extends Exception {
    private String userError;
    private String passwordError;

    public LoginException(String userError, String passwordError) {
        this.userError = userError;
        this.passwordError = passwordError;
    }

    public String getUserError() {
        return userError;
    }

    public void setUserError(String userError) {
        this.userError = userError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }
}
