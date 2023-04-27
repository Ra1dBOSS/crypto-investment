package com.example.cryptoinvestment.csv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PriceValue {

    private String name;
    private double price;
    private long timestamp;
}
