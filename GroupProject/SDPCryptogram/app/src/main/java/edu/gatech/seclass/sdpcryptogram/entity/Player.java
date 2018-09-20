package edu.gatech.seclass.sdpcryptogram.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Player entity class
 */

public class Player extends User {
    private String firstName;
    private String lastName;
    private Long playerId;

    public Player(String username, String password, String firstName, String lastName, Long playerId) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.playerId = playerId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeLong(this.playerId);
    }

    public Player() {
    }

    protected Player(Parcel in) {
        super(in);
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.playerId = in.readLong();
    }

    // getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getPlayerId() {
        return playerId;
    }

    //setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel source) {
            return new Player(source);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Player player = (Player) o;

        if (getFirstName() != null ? !getFirstName().equals(player.getFirstName()) : player.getFirstName() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(player.getLastName()) : player.getLastName() != null)
            return false;
        return getPlayerId() != null ? getPlayerId().equals(player.getPlayerId()) : player.getPlayerId() == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getFirstName() != null ? getFirstName().hashCode() : 0);
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getPlayerId() != null ? getPlayerId().hashCode() : 0);
        return result;
    }
}
