package com.paschoalick.cleanarch.dataprovider.repository.mapper;

import com.paschoalick.cleanarch.core.domain.Customer;
import com.paschoalick.cleanarch.dataprovider.repository.entity.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "string")
public interface CustomerEntityMapper {

    CustomerEntity toCustomerEntity(Customer costomer);

}
