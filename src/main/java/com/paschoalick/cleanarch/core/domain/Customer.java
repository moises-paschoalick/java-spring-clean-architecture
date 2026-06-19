package com.paschoalick.cleanarch.core.domain;

// Precisa ser livre de bibliotecas
// Por isso não usar o loombok
public class Customer {

    public Customer() {
        this.isValidCpf = false;
    }

    public Customer(String id, String name, String cpf, Address address, Boolean isValidCpf) {
        this.id = id;
        Name = name;
        this.cpf = cpf;
        this.address = address;
        this.isValidCpf = isValidCpf;
    }

    private String id;

    private String Name;

    private String cpf;

    private Address address;

    private Boolean isValidCpf;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Boolean getValidCpf() {
        return isValidCpf;
    }

    public void setValidCpf(Boolean validCpf) {
        isValidCpf = validCpf;
    }
}
