package edu.gatech.seclass.sdpcryptogram.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Cryptogram entity class
 */

public class Cryptogram implements Parcelable {
    private static final String TRIMMED_CARACTERS_PLACEHOLDER = "...";
    private String cipherPhrase;
    private String solutionPhrase;
    private Long cryptogramId; // local db autoincrement _ID
    private String cryptogramUID; // EWS-generated cryptogram uuid

    private Cryptogram(){}

    public Cryptogram(String solutionPhrase, String cipherPhrase) {
        this.cipherPhrase = cipherPhrase;
        this.solutionPhrase = solutionPhrase;
    }

    public Cryptogram(String cipherPhrase, String solutionPhrase, Long cryptogramId, String cryptogramUID) {
        this.cipherPhrase = cipherPhrase;
        this.solutionPhrase = solutionPhrase;
        this.cryptogramId = cryptogramId;
        this.cryptogramUID = cryptogramUID;
    }

    public boolean isSolvedBy(Player aPlayer) {
        throw new UnsupportedOperationException();
    }

    public int getIncorrectSolutionsSubmittedCount(Player aPlayer) {
        throw new UnsupportedOperationException();
    }

    public boolean verifySolution(String aSolutionPhrase) {
        // hmm... dummy equality?
        return solutionPhrase.equals(aSolutionPhrase);
    }


    public void setCipherPhrase(String cipherPhrase) {
        this.cipherPhrase = cipherPhrase;
    }

    public void setSolutionPhrase(String solutionPhrase) {
        this.solutionPhrase = solutionPhrase;
    }

    public void setCryptogramId(Long cryptogramId) {
        this.cryptogramId = cryptogramId;
    }

    public void setCryptogramUID(String cryptogramUID) {
        this.cryptogramUID = cryptogramUID;
    }

    public String getCipherPhrase() {
        return cipherPhrase;
    }

    public String getSolutionPhrase() {
        return solutionPhrase;
    }

    public String getSolutionPhrase(int charNum) {
        if(charNum >= solutionPhrase.length())
            return solutionPhrase;
        return solutionPhrase.substring(0, charNum) + TRIMMED_CARACTERS_PLACEHOLDER;
    }

    public Long getCryptogramId() {
        return cryptogramId;
    }

    public String getCryptogramUID() {
        return cryptogramUID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cipherPhrase);
        dest.writeString(this.solutionPhrase);
        dest.writeLong(this.cryptogramId);
        dest.writeString(this.cryptogramUID);
    }

    protected Cryptogram(Parcel in) {
        this.cipherPhrase = in.readString();
        this.solutionPhrase = in.readString();
        this.cryptogramId = in.readLong();
        this.cryptogramUID = in.readString();
    }

    public static final Parcelable.Creator<Cryptogram> CREATOR = new Parcelable.Creator<Cryptogram>() {
        @Override
        public Cryptogram createFromParcel(Parcel source) {
            return new Cryptogram(source);
        }

        @Override
        public Cryptogram[] newArray(int size) {
            return new Cryptogram[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cryptogram that = (Cryptogram) o;

        if (getCipherPhrase() != null ? !getCipherPhrase().equals(that.getCipherPhrase()) : that.getCipherPhrase() != null)
            return false;
        if (getSolutionPhrase() != null ? !getSolutionPhrase().equals(that.getSolutionPhrase()) : that.getSolutionPhrase() != null)
            return false;
        if (getCryptogramId() != null ? !getCryptogramId().equals(that.getCryptogramId()) : that.getCryptogramId() != null)
            return false;
        return getCryptogramUID() != null ? getCryptogramUID().equals(that.getCryptogramUID()) : that.getCryptogramUID() == null;

    }

    @Override
    public int hashCode() {
        int result = getCipherPhrase() != null ? getCipherPhrase().hashCode() : 0;
        result = 31 * result + (getSolutionPhrase() != null ? getSolutionPhrase().hashCode() : 0);
        result = 31 * result + (getCryptogramId() != null ? getCryptogramId().hashCode() : 0);
        result = 31 * result + (getCryptogramUID() != null ? getCryptogramUID().hashCode() : 0);
        return result;
    }
}
