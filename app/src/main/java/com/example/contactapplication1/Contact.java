package com.example.contactapplication1;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "contact_name")
    private String name;
    @ColumnInfo(name = "contact_phoneNumber")

    private String phoneNumber;
    @ColumnInfo(name = "contact_email")

    private String email;
    @ColumnInfo(name = "contact_address")

    private String address;
    private String profilePic;

    public Contact(String name, String phoneNumber, String email, String address, String profilePic)
    {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.profilePic = profilePic;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setProfilePic(String encodedPic) { this.profilePic = encodedPic; }

    public String getProfilePic() { return profilePic; }
}