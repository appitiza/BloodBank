package net.appitiza.android.lifedrop.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ahammed.hafees on 17-07-2017.
 */

public class SignInModel implements Serializable{

    @SerializedName("address")
    @Expose
    private String address = "";
    @SerializedName("lat")
    @Expose
    private String lat = "";
    @SerializedName("lon")
    @Expose
    private String lon = "";
    @SerializedName("id")
    @Expose
    private String id = "";
    @SerializedName("first_name")
    @Expose
    private String firstName = "";
    @SerializedName("last_name")
    @Expose
    private String lastName = "";
    @SerializedName("email")
    @Expose
    private String email = "";
    @SerializedName("password")
    @Expose
    private String password = "";
    @SerializedName("number")
    @Expose
    private String number = "";
    @SerializedName("gender")
    @Expose
    private String gender = "";
    @SerializedName("blood")
    @Expose
    private String blood = "";
    @SerializedName("last_donation")
    @Expose
    private String lastDonation = "";
    @SerializedName("medical_issue")
    @Expose
    private String medicalIssue = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getLastDonation() {
        return lastDonation;
    }

    public void setLastDonation(String lastDonation) {
        this.lastDonation = lastDonation;
    }

    public String getMedicalIssue() {
        return medicalIssue;
    }

    public void setMedicalIssue(String medicalIssue) {
        this.medicalIssue = medicalIssue;
    }

}
