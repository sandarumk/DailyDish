package com.udacity.sandarumk.dailydish.datamodel.typeconverters;

import android.arch.persistence.room.TypeConverter;

import com.udacity.sandarumk.dailydish.datamodel.QuantityUnit;

public class QuantityUnitTypeConverter {

    @TypeConverter
    public static String toQuantityUnitString(QuantityUnit quantityUnit) {
        return quantityUnit.getSymbol();
    }

    @TypeConverter
    public static QuantityUnit toQuantityUnit(String quantityUnitAsString) {
        for (QuantityUnit quantityUnit : QuantityUnit.values()) {
            if (quantityUnit.getSymbol().equalsIgnoreCase(quantityUnitAsString)) {
                return quantityUnit;
            }
        }
        return null;
    }

}
