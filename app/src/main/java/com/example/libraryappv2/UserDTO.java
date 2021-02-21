package com.example.libraryappv2;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDTO implements Parcelable {
    private int userType;
    private String username;
    private String password;
    private String name;

    public UserDTO(){

    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected UserDTO(Parcel in) {
        userType = in.readInt();
        username = in.readString();
        password = in.readString();
        name = in.readString();
    }

    public static final Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userType);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(name);
    }
}
