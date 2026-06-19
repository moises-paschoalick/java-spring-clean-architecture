package com.paschoalick.cleanarch.dataprovider;

import com.paschoalick.cleanarch.core.dataprovider.FindAddressByZipCode;
import com.paschoalick.cleanarch.core.domain.Address;
import com.paschoalick.cleanarch.dataprovider.client.FindAddressByZipCodeClient;
import com.paschoalick.cleanarch.dataprovider.client.mapper.AddressResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindAddressByZipCodeImp implements FindAddressByZipCode {

    @Autowired
    private FindAddressByZipCodeClient findAddressByZipCodeClient;

    @Autowired
    private AddressResponseMapper addressResponseMapper;

    @Override
    public Address find(String zipCode) {
        var addressResponse = findAddressByZipCodeClient.find(zipCode);
        return addressResponseMapper.toAddress(addressResponse);
    }


}
