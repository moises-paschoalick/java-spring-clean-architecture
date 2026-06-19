package com.paschoalick.cleanarch.dataprovider.client.mapper;

import com.paschoalick.cleanarch.core.domain.Address;
import com.paschoalick.cleanarch.dataprovider.client.response.AddressResponse;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface AddressResponseMapper {

    Address toAddress(AddressResponse addressResponse);



}
