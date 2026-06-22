package com.paschoalick.cleanarch.core.dataprovider;

import com.paschoalick.cleanarch.core.domain.Customer;

import java.util.Optional;

public interface FindCustomerById {

    Optional<Customer> find(final String id);

}
