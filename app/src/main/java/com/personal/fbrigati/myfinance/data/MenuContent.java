package com.personal.fbrigati.myfinance.data;

import com.personal.fbrigati.myfinance.ui.StatementFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MenuContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<MenuItem> ITEMS = new ArrayList<MenuItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, MenuItem> ITEM_MAP = new HashMap<String, MenuItem>();

    public static final String[] menu_items = {"Statement", "Budget", "Stats", "Currencies"};

    //Total menu items
    private static final int COUNT = 3;

    static {
        // Add some sample items.
        for (int i = 0; i <= COUNT; i++){
            addItem(createMenuItem(i));
        }
    }

    private static void addItem(MenuItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static MenuItem createMenuItem(int position) {
        return new MenuItem(String.valueOf(position), menu_items[position], makeID(position));
    }

    private static String makeID(int position) {

        switch(position) {
            case 0:
                return StatementFragment.ID_MESSAGE;
        }
        return "";
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class MenuItem {
        public final String id;
        public final String title;
        public final String uri;

        public MenuItem(String id, String title, String uri) {
            this.id = id;
            this.title = title;
            this.uri = uri;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
