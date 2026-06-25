package com.paschoalick.cleanarch.core.usecase.impl;

import com.paschoalick.cleanarch.core.dataprovider.FindAddressByZipCode;
import com.paschoalick.cleanarch.core.dataprovider.InsertCustomer;
import com.paschoalick.cleanarch.core.dataprovider.SendCpfForValidation;
import com.paschoalick.cleanarch.core.domain.Customer;
import com.paschoalick.cleanarch.core.usecase.InsertCustomerUseCase;

public class InsertCustomerUseCaseImpl implements InsertCustomerUseCase {

    // Não usa o autowired para ficar livre de framework
    private final FindAddressByZipCode findAddressByZipCode;

    private final InsertCustomer insertCustomer;

    private final SendCpfForValidation sendCpfForValidation;

    public InsertCustomerUseCaseImpl(
            FindAddressByZipCode findAddressByZipCode,
            InsertCustomer insertCustomer,
            SendCpfForValidation sendCpfForValidation
    ) {
        this.findAddressByZipCode = findAddressByZipCode;
        this.insertCustomer = insertCustomer;
        this.sendCpfForValidation = sendCpfForValidation;
    }


    @Override
    public void insert(Customer customer, String zipCode) {
        var addres = findAddressByZipCode.find(zipCode);
        customer.setAddress(addres);
        insertCustomer.insert(customer);
        sendCpfForValidation.send(customer.getCpf());
    }

}
