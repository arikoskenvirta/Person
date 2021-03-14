package com.example.recyclerlist;

public class Person {

    private String id;
    private String name;
    private int age;
    private String imageurl;

    public Person() {    }

    public Person( String id, String name, int age, String imageurl) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.imageurl = imageurl;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", imageurl='" + imageurl + '\'' +
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

    public String getImageURL() {
        return imageurl;
    }

    public void setImageURL(String imageurl) {
        this.imageurl = imageurl;
    }
}
