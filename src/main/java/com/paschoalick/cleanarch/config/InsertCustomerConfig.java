package com.paschoalick.cleanarch.config;

import com.paschoalick.cleanarch.core.usecase.impl.InsertCustomerUseCaseImpl;
import com.paschoalick.cleanarch.dataprovider.FindAddressByZipCodeImp;
import com.paschoalick.cleanarch.dataprovider.InsertCustomerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InsertCustomerConfig {

    @Bean
    public InsertCustomerUseCaseImpl insertCustomerUseCase(
            FindAddressByZipCodeImp findAddressByZipCode,
            InsertCustomerImpl insertCustomer
    ){
        return new InsertCustomerUseCaseImpl(findAddressByZipCode, insertCustomer);
    }
}
