package com.example.recyclerlist;

public class Person {

    private String id;
    private String name;
    private int age;
    private String profilePicture;
    private String phoneNumber;

    public Person() {    }

    public Person(String id, String name, int age, String profilePicture, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.profilePicture = profilePicture;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", profilePicture='" + profilePicture + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    public String getId() {        return id;    }

    public void setId(String id) {        this.id = id;    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = "";
        }
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
