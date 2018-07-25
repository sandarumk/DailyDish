package com.udacity.sandarumk.dailydish.datamodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum QuantityUnit {
    UNITS("units"),
    GRAMS("g"),
    KILO_GRAMS("kg"),
    LITRES("l"),
    MILLI_LITRES("ml"),
    OUNCE("oz"),
    POUND("p"),
    PINT("pint");

    @Getter
    private final String symbol;

    @Override
    public String toString() {
        return this.symbol;
    }

    public static QuantityUnit findBySymbol(String symbol){
        for (QuantityUnit quantityUnit : QuantityUnit.values()) {
            if(quantityUnit.getSymbol().equals(symbol)){
                return quantityUnit;
            }
        }
        return UNITS;
    }
}
