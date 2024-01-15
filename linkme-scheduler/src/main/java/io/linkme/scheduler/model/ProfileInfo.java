package io.linkme.scheduler.model;

import java.util.List;

public class ProfileInfo {

    private String userEmail;
    private String firstName;

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

    private String lastName;
    private List<String> userSkills;

    public ProfileInfo(String userEmail, String firstName, String lastName, List<String> userSkills) {
        this.userEmail = userEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userSkills = userSkills;
    }

    // Getters and setters

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<String> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(List<String> userSkills) {
        this.userSkills = userSkills;
    }
}