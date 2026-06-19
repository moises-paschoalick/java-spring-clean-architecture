package com.paschoalick.cleanarch.core.usecase;

import com.paschoalick.cleanarch.core.domain.Customer;

public interface InsertCustomerUseCase {

    void insert(Customer customer, String zipCode);

}
