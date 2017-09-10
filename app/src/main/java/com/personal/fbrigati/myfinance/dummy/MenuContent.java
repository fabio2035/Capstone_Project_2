package com.personal.fbrigati.myfinance.dummy;

import com.personal.fbrigati.myfinance.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MenuContent {

    /**
     * An array of sample (menu) items.
     */
    public static final List<MenuItem> ITEMS = new ArrayList<MenuItem>();

    /**
     * A map of sample (menu) items, by ID.
     */
    public static final Map<String, MenuItem> ITEM_MAP = new HashMap<String, MenuItem>();

    public static final String[] menu_items = {"Statement", "Budget", "Stats", "Currencies"};

    public static final int[] icon_items = {R.drawable.menu_icon_statement,R.drawable.menu_icon_budget, R.drawable.menu_icon_stats, R.drawable.menu_icon_curex };


    private static final int COUNT = 3;

    static {
        // Add some sample items.
        for (int i = 0; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(MenuItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static MenuItem createDummyItem(int position) {
        return new MenuItem(String.valueOf(position), menu_items[position], makeDetails(position), icon_items[position]);
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
     * A menu item representing a piece of content.
     */
    public static class MenuItem {
        public final String id;
        public final String content;
        public final String details;
        public final int icon;

        public MenuItem(String id, String content, String details, int icon) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
