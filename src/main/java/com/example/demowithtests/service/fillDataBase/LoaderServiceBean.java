package com.example.demowithtests.service.fillDataBase;

import com.example.demowithtests.domain.Address;
import com.example.demowithtests.domain.Document;
import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.repository.EmployeeRepository;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@Service
public class LoaderServiceBean implements LoaderService {

    private final EmployeeRepository employeeRepository;

    /**
     *
     */
    @Override
    public void generateData() {
        List<Employee> employees = createListEmployees();
        employeeRepository.saveAll(employees);
    }

    /**
     * @return
     */
    @Override
    public long count() {
        return employeeRepository.count();
    }

    public List<Employee> createListEmployees() {

        List<Employee> employees = new ArrayList<>();
        long seed = 1;

        Faker faker = new Faker(new Locale("en"), new Random(seed));
        for (int i = 0; i < 10000; i++) {

            String name = faker.name().name();
            String country = faker.country().name();
            String email = getFakerEmail(name, faker);
            Boolean isActive = faker.random().nextBoolean();
            UUID uuid = UUID.randomUUID();
            LocalDateTime expireDate ;
            expireDate = LocalDateTime.now();
            Set<Address> addresses = Set.copyOf(Arrays.asList(
                    Address.builder()
                            .addressHasActive(isActive)
                            .country(country)
                            .city(faker.address().city())
                            .street(faker.address().streetName()+" "+faker.random().nextInt(1,300))
                            .build()
                    , Address.builder()
                            .addressHasActive(!isActive)
                            .country(faker.address().country())
                            .city(faker.address().city())
                            .street(faker.address().streetName()+" "+faker.random().nextInt(1,300))
                            .build()));

            Employee employee = Employee
                    .builder()
                    .name(name)
                    .country(country)
                    .email(email)
                    .addresses(addresses)
                    .gender(Gender.valueOf(faker.regexify("M|F")))
                    .build();

            Document document = Document
                    .builder()
                    .number(uuid.toString())
                    .expireDate(expireDate)
                    .isHandled(faker.random().nextBoolean())
                    .employee(employee)
                    .build();

            employee.setDocument(document);

            employees.add(employee);
        }

        return employees;
    }
    private static String getFakerEmail(String personName, Faker faker) {
        return (personName+"@mail."+faker.country().countryCode2())
                .toLowerCase()
                .replaceAll(" ", "_");
    }
}
