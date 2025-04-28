package aammo.ppv;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataBase db = new DataBase();
        List<Person> persons = db.fetchAllPersons();
        for (Person person : persons) {
            System.out.println("Name: " + person.getName());
            System.out.println("Location: " + person.getLocation());
            System.out.println("Phone: " + person.getPhone());
        }
    }
}
