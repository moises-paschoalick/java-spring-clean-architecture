package com.paschoalick.cleanarch.config;

import com.paschoalick.cleanarch.core.usecase.impl.FindCustomerByIdUseCaseImpl;
import com.paschoalick.cleanarch.core.usecase.impl.UpdateCustomerUseCaseImpl;
import com.paschoalick.cleanarch.dataprovider.FindAddressByZipCodeImp;
import com.paschoalick.cleanarch.dataprovider.UpdateCustomerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateCustomerConfig {

    @Bean
    public UpdateCustomerUseCaseImpl updateCustomerUseCase(
            FindCustomerByIdUseCaseImpl findCustomerByIdUseCase,
            FindAddressByZipCodeImp findAddressByZipCode,
            UpdateCustomerImpl updateCustomer
    ){
        return new UpdateCustomerUseCaseImpl(findCustomerByIdUseCase, findAddressByZipCode, updateCustomer);
    }

}
