package com.paschoalick.cleanarch.entrypoint.consumer.mapper;

import com.paschoalick.cleanarch.core.domain.Customer;
import com.paschoalick.cleanarch.entrypoint.consumer.message.CustomerMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "string")
public interface CustomerMessageMapper {

    @Mapping(target = "address", ignore = true)
    Customer toCustomer(CustomerMessage customerMessage);


}
