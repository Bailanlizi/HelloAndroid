package com.example.helloandroid;

public class Currency {
    private String name;
    private String rate;

    public Currency(String name, String rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public String getRate() {
        return rate;
    }
}