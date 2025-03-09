package com.breadhardit.travelagencykata;

import com.breadhardit.travelagencykata.domain.Customer;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class BehavioralPatternExercices {
    /* EXERCISE 1
        Travels has an origin and a destination. Travels have some restrictions:
        - Travels with origin and destination in the same country require only Identity Document
        - Travels with origin and destination in schengen space, requires Passport
        - Travels with origin or destination out of schengen space, requires Visa
     */
    @Data
    public static class Travel {
        String id;
        String name;
        String origin;
        String destination;

        public Travel(String id, String name, String origin, String destination) {
            this.id = id;
            this.name = name;
            this.origin = origin;
            this.destination = destination;
        }
    }

    interface TravelStrategy{
        String applyTravel();
    }

    static class TravelWithVisa implements TravelStrategy{
        public String applyTravel() {
            return "Applying visa...";
        }
    }

    static class TravelWithDNI implements TravelStrategy{
        public String applyTravel() {
            return "Applying DNI...";
        }
    }

    static class TravelWithPassport implements TravelStrategy{
        public String applyTravel() {
            return "Applying passport...";
        }
    }

    static class TravelFactory{
        public static final List<String> SCHENGEN_COUNTRIES = List.of("Spain","France","Iceland","Italy","Portugal");
        public static TravelStrategy getStrategy(String origin, String destination) {
            if (origin.equals(destination)) {
                return new TravelWithDNI();
            } else if (SCHENGEN_COUNTRIES.contains(origin) && SCHENGEN_COUNTRIES.contains(destination)) {
                return new TravelWithPassport();
            } else {
                return new TravelWithVisa();
            }
        }
    }

    static class TravelDocument {
        private final TravelStrategy travelStrategy;

        public TravelDocument(TravelStrategy travelStrategy) {
            this.travelStrategy = travelStrategy;
        }

        public String applyTravel() {
            return travelStrategy.applyTravel();
        }
    }
    @Test
    // When customer buy a new Travel we have to scan the proper documentation
    public void travelAgency() {
        List<Travel> travels = List.of(
                new Travel(UUID.randomUUID().toString(),"PYRAMIDS TOUR","Spain","EGYPT"),
                new Travel(UUID.randomUUID().toString(),"LISBOA TOUR","Spain","Portugal"),
                new Travel(UUID.randomUUID().toString(),"LISBOA TOUR","Portugal","Portugal")
        );

        for (Travel travel : travels) {
            TravelDocument travelDocument = new TravelDocument(TravelFactory.getStrategy(travel.origin, travel.destination));
            log.info("Travel: {} - {}", travel.getName(), travelDocument.applyTravel());
        }
    }







    /*
     * When a new employee is enrolled, company sends a greetins e-mail.
     * A notification service is querying the database every second looking for new employees to notify
     */
    @Builder
    @Data
    public static class Employee {
        final String id;
        final String name;
        final String email;
        @Builder.Default
        Boolean greetingDone = Boolean.FALSE;
    }

    @AllArgsConstructor
    public static class EmployeesRepository{
        private static final ConcurrentHashMap<String,Employee> EMPLOYEES = new ConcurrentHashMap<>();
        private static final List<NotificationObserver> NOTIFICATION_OBSERVERS = new ArrayList<>();
        public void addEmployee(Employee employee) {
            EMPLOYEES.put(employee.getId(),employee);
            NOTIFICATION_OBSERVERS.forEach(e -> e.notify(employee, new Notification(employee.getEmail(), "Hello there")));
        }
        public void addNotificationObservers(NotificationObserver observer){
            NOTIFICATION_OBSERVERS.add(observer);
        }
        public void patchEmployee(Employee employee){
            EMPLOYEES.put(employee.getId(),employee);
        }
    }
    @Value
    public static class Notification{
        public String email;
        public String text;
    }
    public interface NotificationObserver{
        void notify(Employee employee, Notification greetinsNotification);
    }
    @RequiredArgsConstructor
    public static class GreetingsNotificatorObserver implements NotificationObserver{
        final EmployeesRepository employeesRepository;
        @Override
        public void notify(Employee employee, Notification greetinsNotification){
            log.info("Sending email to {} with context: {}", greetinsNotification.getEmail(), greetinsNotification.getText());
            log.info("Updating customer");
            employeesRepository.patchEmployee(employee);
        }
    }

    @Test
    @SneakyThrows
    public void companyTest() {
        EmployeesRepository employeesRepository = new EmployeesRepository();
        GreetingsNotificatorObserver greetingsNotificatorObserver = new GreetingsNotificatorObserver(employeesRepository);
        employeesRepository.addNotificationObservers(greetingsNotificatorObserver);
        Thread.sleep(200);
        employeesRepository.addEmployee(Employee.builder().id("1").name("Pepe").email("pepe@pepemail.com").build());
        Thread.sleep(200);
        employeesRepository.addEmployee(Employee.builder().id("2").name("Juan").email("pepe@pepemail.com").build());
    }
    // Use the proper behavioral pattern to avoid the continuous querying to database
}
