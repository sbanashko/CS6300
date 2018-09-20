package edu.gatech.seclass.sdpcryptogram.entity;

/**
 * Created by Nick Marsh on 7/4/2017.
 */

public class CryptogramAttempt {
    private Long id;
    private Boolean isSolved;
    private String mostRecentSubmission;
    private Integer incorrectSubmissionCount;
    private Long cryptogramId;
    private Long playerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getCryptogramId() {
        return cryptogramId;
    }

    public void setCryptogramId(Long cryptogramId) {
        this.cryptogramId = cryptogramId;
    }

    public Boolean getSolved() {
        return isSolved;
    }

    public void setSolved(Boolean solved) {
        isSolved = solved;
    }

    public String getMostRecentSubmission() {
        return mostRecentSubmission;
    }

    public void setMostRecentSubmission(String mostRecentSubmission) {
        this.mostRecentSubmission = mostRecentSubmission;
    }

    public Integer getIncorrectSubmissionCount() {
        return incorrectSubmissionCount;
    }

    public void setIncorrectSubmissionCount(Integer incorrectSubmissionCount) {
        this.incorrectSubmissionCount = incorrectSubmissionCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CryptogramAttempt that = (CryptogramAttempt) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (isSolved != null ? !isSolved.equals(that.isSolved) : that.isSolved != null)
            return false;
        if (getMostRecentSubmission() != null ? !getMostRecentSubmission().equals(that.getMostRecentSubmission()) : that.getMostRecentSubmission() != null)
            return false;
        if (getIncorrectSubmissionCount() != null ? !getIncorrectSubmissionCount().equals(that.getIncorrectSubmissionCount()) : that.getIncorrectSubmissionCount() != null)
            return false;
        if (getCryptogramId() != null ? !getCryptogramId().equals(that.getCryptogramId()) : that.getCryptogramId() != null)
            return false;
        return getPlayerId() != null ? getPlayerId().equals(that.getPlayerId()) : that.getPlayerId() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (isSolved != null ? isSolved.hashCode() : 0);
        result = 31 * result + (getMostRecentSubmission() != null ? getMostRecentSubmission().hashCode() : 0);
        result = 31 * result + (getIncorrectSubmissionCount() != null ? getIncorrectSubmissionCount().hashCode() : 0);
        result = 31 * result + (getCryptogramId() != null ? getCryptogramId().hashCode() : 0);
        result = 31 * result + (getPlayerId() != null ? getPlayerId().hashCode() : 0);
        return result;
    }
}
