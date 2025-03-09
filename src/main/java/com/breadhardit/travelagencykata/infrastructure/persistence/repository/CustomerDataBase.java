package com.breadhardit.travelagencykata.infrastructure.persistence.repository;

import com.breadhardit.travelagencykata.application.port.CustomersRepository;
import com.breadhardit.travelagencykata.domain.Customer;
import com.breadhardit.travelagencykata.infrastructure.persistence.entity.CustomerEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CustomerDataBase implements CustomersRepository {

    private final CustomersJPARepository customersJPARepository;

    @Override
    public void saveCustomer(Customer customer) {
        CustomerEntity customerEntity = CustomerEntity.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surnames(customer.getSurnames())
                .birthDate(customer.getBirthDate())
                .passportNumber(customer.getPassportNumber())
                .enrollmentDate(LocalDate.now())
                .active(true)
                .build();
        customersJPARepository.save(customerEntity);
    }

    //Convertir un CustomerEntity a customer
    private Customer parseCustomer(CustomerEntity customer) {
        return Customer.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surnames(customer.getSurnames())
                .birthDate(customer.getBirthDate())
                .enrollmentDate(customer.getEnrollmentDate())
                .active(customer.getActive())
                .build();
    }

    //Obtengo un el respositorio de customer y mediante this::parseCustomer realizo un parse a Customer
    @Override
    public Optional<Customer> getCustomerById(String id) {
        Optional<CustomerEntity> customerEntity = customersJPARepository.findById(id);
        return customerEntity.map(this::parseCustomer);
    }


    @Override
    public Optional<Customer> getCustomerByPassport(String id) {
        Optional<CustomerEntity> customerEntity = customersJPARepository.findByPassportNumber(id);
        return customerEntity.map(this::parseCustomer);
    }
}
