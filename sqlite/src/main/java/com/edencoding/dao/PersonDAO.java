package com.edencoding.dao;

import com.edencoding.models.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonDAO {

    private static final String tableName = "Persons";
    private static final String firstNameColumn = "firstName";
    private static final String lastNameColumn = "lastName";
    private static final String ageColumn = "Age";
    private static final String idColumn = "id";

    private static final ObservableList<Person> persons;

    static {
        persons = FXCollections.observableArrayList();
        updatePersonsFromDB();
    }

    public static ObservableList<Person> getPersons() {
        return FXCollections.unmodifiableObservableList(persons);
    }

    private static void updatePersonsFromDB() {

        String query = "SELECT * FROM " + tableName;

        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            persons.clear();
            while (rs.next()) {
                persons.add(new Person(
                        rs.getString(firstNameColumn),
                        rs.getString(lastNameColumn),
                        rs.getInt(ageColumn),
                        rs.getInt(idColumn)));
            }
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not load Persons from database ");
            persons.clear();
        }
    }

    public static void update(Person newPerson) {
        //udpate database
        int rows = CRUDHelper.update(
                tableName,
                new String[]{firstNameColumn, lastNameColumn, ageColumn},
                new Object[]{newPerson.getFirstName(), newPerson.getLastName(), newPerson.getAge()},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER},
                idColumn,
                Types.INTEGER,
                newPerson.getId()
        );

        if (rows == 0)
            throw new IllegalStateException("Person to be updated with id " + newPerson.getId() + " didn't exist in database");

        //update cache
        Optional<Person> optionalPerson = getPerson(newPerson.getId());
        optionalPerson.ifPresentOrElse((oldPerson) -> {
            persons.remove(oldPerson);
            persons.add(newPerson);
        }, () -> {
            throw new IllegalStateException("Person to be updated with id " + newPerson.getId() + " didn't exist in database");
        });
    }

    public static void insertPerson(String firstName, String lastName, int age) {
        //update database
        int id = (int) CRUDHelper.create(
                tableName,
                new String[]{"LastName", "FirstName", "Age"},
                new Object[]{lastName, firstName, age},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER});

        //update cache
        persons.add(new Person(
                firstName,
                lastName,
                age,
                id
        ));
    }

    public static void delete(int id) {
        //update database
        CRUDHelper.delete(tableName, id);

        //update cache
        Optional<Person> person = getPerson(id);
        person.ifPresent(persons::remove);

    }

    public static Optional<Person> getPerson(int id) {
        for (Person person : persons) {
            if (person.getId() == id) return Optional.of(person);
        }
        return Optional.empty();
    }

}