package com.example.recyclerlist;

public class Person {

    private String id;
    private String name;
    private int age;
    private String profilePicture;

    public Person() {    }

    public Person( String id, String name, int age, String profilePicture) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", profilePicture='" + profilePicture + '\'' +
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


}
