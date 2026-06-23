package com.paschoalick.cleanarch.dataprovider;

import com.paschoalick.cleanarch.core.dataprovider.UpdateCustomer;
import com.paschoalick.cleanarch.core.domain.Customer;
import com.paschoalick.cleanarch.dataprovider.repository.CustomerRepository;
import com.paschoalick.cleanarch.dataprovider.repository.mapper.CustomerEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateCustomerImpl implements UpdateCustomer {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerEntityMapper customerEntityMapper;

    @Override
    public void update(Customer customer) {
        var customerEntity = customerEntityMapper.toCustomerEntity(customer);
        customerRepository.save(customerEntity);
    }
}
