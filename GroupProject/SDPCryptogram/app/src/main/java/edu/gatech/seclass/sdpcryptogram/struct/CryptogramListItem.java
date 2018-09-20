package edu.gatech.seclass.sdpcryptogram.struct;

/**
 * Helper structure to hold single cryptogram statistics (Req.#11)
 */

public class CryptogramListItem {

    // added UID returned by EWS
    private String cryptogramUID;

    // added cipherPhrase to show in Player dashboard
    private String cipherPhrase;

    // initial struct
    private long cryptogramId;
    private boolean isSolved;
    private int incorrectSubmissionsCount;

    public CryptogramListItem(long cryptogramId, boolean isSolved, int incorrectSubmissionsCount) {
        this.cryptogramId = cryptogramId;
        this.isSolved = isSolved;
        this.incorrectSubmissionsCount = incorrectSubmissionsCount;
    }

    public CryptogramListItem(long cryptogramId, String cryptogramUID, boolean isSolved, int incorrectSubmissionsCount) {
        this.cryptogramId = cryptogramId;
        this.cryptogramUID = cryptogramUID;
        this.isSolved = isSolved;
        this.incorrectSubmissionsCount = incorrectSubmissionsCount;
    }

    public CryptogramListItem(long cryptogramId, String cryptogramUID, boolean isSolved, int incorrectSubmissionsCount, String cipherPhrase) {
        this.cryptogramId = cryptogramId;
        this.cryptogramUID = cryptogramUID;
        this.isSolved = isSolved;
        this.incorrectSubmissionsCount = incorrectSubmissionsCount;
        this.cipherPhrase = cipherPhrase;
    }

    public long getCryptogramId() {
        return cryptogramId;
    }

    public void setCryptogramId(long cryptogramId) {
        this.cryptogramId = cryptogramId;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public int getIncorrectSubmissionsCount() {
        return incorrectSubmissionsCount;
    }

    public void setIncorrectSubmissionsCount(int incorrectSubmissionsCount) {
        this.incorrectSubmissionsCount = incorrectSubmissionsCount;
    }

    public String getCryptogramUID() {
        return cryptogramUID;
    }

    public void setCryptogramUID(String cryptogramUID) {
        this.cryptogramUID = cryptogramUID;
    }

    public String getCipherPhrase() {
        return cipherPhrase;
    }

    public void setCipherPhrase(String cipherPhrase) {
        this.cipherPhrase = cipherPhrase;
    }
}
