package com.psyngo.michael.symondstimetableplus;

/**
 * Created by Michael on 09/11/2014.
 */
public class FriendList {
    String key;
    FriendDatabaseObject value;

    public FriendList(String key, FriendDatabaseObject value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FriendDatabaseObject getValue() {
        return value;
    }

    public void setValue(FriendDatabaseObject value) {
        this.value = value;
    }
}
