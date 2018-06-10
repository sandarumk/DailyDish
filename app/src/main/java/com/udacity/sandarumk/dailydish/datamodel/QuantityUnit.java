package com.udacity.sandarumk.dailydish.datamodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
enum QuantityUnit {
    GRAMS("g"),
    KILO_GRAMS("kg"),
    LITRES("l"),
    MILLI_LITRES("ml"),
    UNITS("units"),
    OUNCE("oz"),
    POUND("p"),
    PINT("pint");

    @Getter
    private final String symbol;
}
