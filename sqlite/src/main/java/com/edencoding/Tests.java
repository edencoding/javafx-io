package com.edencoding;

import com.edencoding.dao.PersonDAO;
import com.edencoding.models.Person;
import com.edencoding.security.PasswordAuthentication;

import java.util.List;

public class Tests {
    public static void main(String[] args) {

        PersonDAO.getPersons().get(1);

        PersonDAO.update(new Person("Daddy", "Bottomface", 2352, 1));

        List<Person> personList = PersonDAO.getPersons();
        for (Person person : personList) {
            System.out.println(person);
        }


    }

    private static void checkPasswordAlgorithm() {
        char[] password = "ThsIsMyPassword".toCharArray();
        PasswordAuthentication auth = new PasswordAuthentication();
        String hash = auth.hash(password);
        System.out.println(auth.authenticate(password, hash));
    }
}
