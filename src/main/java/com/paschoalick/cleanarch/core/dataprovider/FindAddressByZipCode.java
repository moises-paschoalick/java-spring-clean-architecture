package com.paschoalick.cleanarch.core.dataprovider;

import com.paschoalick.cleanarch.core.domain.Address;

public interface FindAddressByZipCode {

    Address find(final String zipCode);


}
