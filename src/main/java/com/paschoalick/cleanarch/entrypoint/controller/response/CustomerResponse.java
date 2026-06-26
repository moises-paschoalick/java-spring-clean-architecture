package com.paschoalick.cleanarch.entrypoint.controller.response;

import lombok.Data;

@Data
public class CustomerResponse {

    private String name;

    private String cpf;

    private Boolean isVAlidCpf;

    private AddressResponse address;

}
