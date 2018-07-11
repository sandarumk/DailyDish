package com.udacity.sandarumk.dailydish.fragments.dummy;

import com.udacity.sandarumk.dailydish.datamodel.GroceryListItem;
import com.udacity.sandarumk.dailydish.datamodel.Ingredient;
import com.udacity.sandarumk.dailydish.datamodel.QuantityUnit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class GroceryListContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<GroceryListItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, GroceryListItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(GroceryListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getGroceryListItemName(), item);
    }

    private static GroceryListItem createDummyItem(int position) {
        GroceryListItem dummyRecipe = new GroceryListItem();
        dummyRecipe.setGroceryListItemID(0+position);
        dummyRecipe.setGroceryListItemName("Dummy Recipe"+position);
        dummyRecipe.setDate(new Date());
        Ingredient ingredient = new Ingredient();
        ingredient.setQuantity(3);
        ingredient.setQuantityUnit(QuantityUnit.UNITS);
        dummyRecipe.setIngredient(ingredient);
        dummyRecipe.setMealScheduleID(position);
        dummyRecipe.setStatus(true);

        return dummyRecipe;
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
