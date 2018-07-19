package com.udacity.sandarumk.dailydish.datawrappers;

import com.udacity.sandarumk.dailydish.datamodel.QuantityUnit;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroceryItemBreakdownWrapper {
    private int id;
    private Date date;
    private String recipeName;
    private int quantity;
    private QuantityUnit quantityUnit;
    private boolean manual;
    private boolean checked;

    public String quantityText(){
        return quantity + " " + quantityUnit.getSymbol();
    }
}
