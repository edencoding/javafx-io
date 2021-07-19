package com.edencoding.models;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Person {
    private final ReadOnlyStringProperty firstName;
    private final ReadOnlyStringProperty lastName;
    private final ReadOnlyIntegerProperty age;
    private final int id;

    public Person(String firstName, String lastName, Integer age, int id) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.age = new SimpleIntegerProperty(age);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public ReadOnlyStringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public ReadOnlyStringProperty lastNameProperty() {
        return lastName;
    }

    public int getAge() {
        return age.get();
    }

    public ReadOnlyIntegerProperty ageProperty() {
        return age;
    }

    @Override
    public String toString() {
        return "Person [" + firstName.get() + " " + lastName.get() + ", aged " + age.get() + " with id " + id + "]";
    }
}
