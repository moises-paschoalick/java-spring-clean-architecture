package com.paschoalick.cleanarch.config;

import com.paschoalick.cleanarch.core.usecase.FindCustomerByIdUseCase;
import com.paschoalick.cleanarch.core.usecase.impl.FindCustomerByIdUseCaseImpl;
import com.paschoalick.cleanarch.dataprovider.FindCustomerByIdImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FindCustomerByIdConfig {

    @Bean
    public FindCustomerByIdUseCaseImpl findCustomerByIdUseCase(
            FindCustomerByIdImpl findCustomerById
    ) {
        return new FindCustomerByIdUseCaseImpl(findCustomerById);
    }

}
