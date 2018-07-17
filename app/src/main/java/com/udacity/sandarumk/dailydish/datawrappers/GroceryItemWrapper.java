package com.udacity.sandarumk.dailydish.datawrappers;

import com.udacity.sandarumk.dailydish.datamodel.QuantityUnit;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroceryItemWrapper {
    protected boolean checked;
    protected String ingredientName;
    protected int totalQuantity;
    protected QuantityUnit quantityUnit;
    private List<GroceryItemBreakdownWrapper> breakdownWrappers;
}
