package com.paschoalick.cleanarch.core.usecase;

import com.paschoalick.cleanarch.core.domain.Customer;

public interface FindCustomerByIdUseCase {

    Customer find(final String id);


}
