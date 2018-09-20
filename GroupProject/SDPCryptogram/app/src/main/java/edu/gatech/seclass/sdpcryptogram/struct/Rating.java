package edu.gatech.seclass.sdpcryptogram.struct;

/**
 * Helper structure to hold all ratings (Req.#12)
 */

public class Rating {
    // added to match EWS.PlayerRating
    public String firstname;
    public String lastname;

    // initial struct fields
    public String username;
    public int attemptedCryptogramsCount;
    public int incorrectSubmissionsCount;
    public int solvedCryptogramsCount;

    public Rating() {
    }

    public Rating(String firstname, String lastname, String username, int attemptedCryptogramsCount, int incorrectSubmissionsCount, int solvedCryptogramsCount) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.attemptedCryptogramsCount = attemptedCryptogramsCount;
        this.incorrectSubmissionsCount = incorrectSubmissionsCount;
        this.solvedCryptogramsCount = solvedCryptogramsCount;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAttemptedCryptogramsCount() {
        return attemptedCryptogramsCount;
    }

    public void setAttemptedCryptogramsCount(int attemptedCryptogramsCount) {
        this.attemptedCryptogramsCount = attemptedCryptogramsCount;
    }

    public int getIncorrectSubmissionsCount() {
        return incorrectSubmissionsCount;
    }

    public void setIncorrectSubmissionsCount(int incorrectSubmissionsCount) {
        this.incorrectSubmissionsCount = incorrectSubmissionsCount;
    }

    public int getSolvedCryptogramsCount() {
        return solvedCryptogramsCount;
    }

    public void setSolvedCryptogramsCount(int solvedCryptogramsCount) {
        this.solvedCryptogramsCount = solvedCryptogramsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rating rating = (Rating) o;

        if (getAttemptedCryptogramsCount() != rating.getAttemptedCryptogramsCount()) return false;
        if (getIncorrectSubmissionsCount() != rating.getIncorrectSubmissionsCount()) return false;
        if (getSolvedCryptogramsCount() != rating.getSolvedCryptogramsCount()) return false;
        if (getFirstname() != null ? !getFirstname().equals(rating.getFirstname()) : rating.getFirstname() != null)
            return false;
        if (getLastname() != null ? !getLastname().equals(rating.getLastname()) : rating.getLastname() != null)
            return false;
        return getUsername() != null ? getUsername().equals(rating.getUsername()) : rating.getUsername() == null;

    }

    @Override
    public int hashCode() {
        int result = getFirstname() != null ? getFirstname().hashCode() : 0;
        result = 31 * result + (getLastname() != null ? getLastname().hashCode() : 0);
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + getAttemptedCryptogramsCount();
        result = 31 * result + getIncorrectSubmissionsCount();
        result = 31 * result + getSolvedCryptogramsCount();
        return result;
    }
}
