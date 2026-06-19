package com.paschoalick.cleanarch.core.domain;

public class Address {

    public Address() {
    }

    public Address(String stree, String city, String state) {
        this.stree = stree;
        this.city = city;
        this.state = state;
    }

    private String stree;

    private String city;

    private String state;


    public String getStree() {
        return stree;
    }

    public void setStree(String stree) {
        this.stree = stree;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
