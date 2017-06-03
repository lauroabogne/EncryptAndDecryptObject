package com.blogspot.justsimpleinfo.encryptanddecryptobject;

import java.io.Serializable;

/**
 * Created by Lauro-PC on 5/29/2017.
 */

public class CredentialObject implements Serializable {

    private String userName;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
