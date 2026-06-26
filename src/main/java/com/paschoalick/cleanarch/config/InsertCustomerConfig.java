package com.paschoalick.cleanarch.config;

import com.paschoalick.cleanarch.core.usecase.impl.InsertCustomerUseCaseImpl;
import com.paschoalick.cleanarch.dataprovider.FindAddressByZipCodeImp;
import com.paschoalick.cleanarch.dataprovider.InsertCustomerImpl;
import com.paschoalick.cleanarch.dataprovider.SendCpfForValidationImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InsertCustomerConfig {

    @Bean
    public InsertCustomerUseCaseImpl insertCustomerUseCase(
            FindAddressByZipCodeImp findAddressByZipCode,
            InsertCustomerImpl insertCustomer,
            SendCpfForValidationImpl sendCpfForValidation
    ){
        return new InsertCustomerUseCaseImpl(findAddressByZipCode, insertCustomer, sendCpfForValidation);
    }
}
