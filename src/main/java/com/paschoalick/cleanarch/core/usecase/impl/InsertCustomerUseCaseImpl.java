package com.paschoalick.cleanarch.core.usecase.impl;

import com.paschoalick.cleanarch.core.dataprovider.FindAddressByZipCode;
import com.paschoalick.cleanarch.core.dataprovider.InsertCustomer;
import com.paschoalick.cleanarch.core.domain.Customer;
import com.paschoalick.cleanarch.core.usecase.InsertCustomerUseCase;

public class InsertCustomerUseCaseImpl implements InsertCustomerUseCase {

    // Não usa o autowired para ficar livre de framework
    private final FindAddressByZipCode findAddressByZipCode;

    private final InsertCustomer insertCustomer;

    public InsertCustomerUseCaseImpl(
            FindAddressByZipCode findAddressByZipCode,
            InsertCustomer insertCustomer) {
        this.findAddressByZipCode = findAddressByZipCode;
        this.insertCustomer = insertCustomer;
    }


    @Override
    public void insert(Customer customer, String zipCode) {
        var addres = findAddressByZipCode.find(zipCode);
        customer.setAddress(addres);
        insertCustomer.insert(customer);
    }

}
