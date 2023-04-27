package com.example.cryptoinvestment.controller.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndicatorsDto {

    private String name;
    private Double min;
    private Double max;
    private Double oldest;
    private Double newest;
}
