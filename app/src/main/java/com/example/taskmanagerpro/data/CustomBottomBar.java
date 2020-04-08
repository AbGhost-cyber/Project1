package com.example.taskmanagerpro.data;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.adapter.ItemAdapter;

import java.util.ArrayList;

public class CustomBottomBar {
    private ItemAdapter.ItemSelectorInterface itemSelectorInterface;
    private Context context;
    private final int ITEM_LIMIT = 5;
    private CardView custom_bottom_bar_parent;
    private RecyclerView custom_recycler_view;
    private View custom_divider;
    private ArrayList<CustomBottomItem> items;
    private String
            defaultBackground = "#FFFFFF",
            defaultTint = "#000000";

    public CustomBottomBar(Context context, View view, ItemAdapter.ItemSelectorInterface itemSelectorInterface) {
        setType (view);
        this.context = context;
        this.itemSelectorInterface = itemSelectorInterface;
    }

    private void setType(View view) {
        custom_bottom_bar_parent = view.findViewById (R.id.custom_bottom_bar_parent);
        custom_recycler_view = view.findViewById (R.id.custom_recycler_view);
        custom_divider = view.findViewById (R.id.custom_divider);
        items = new ArrayList<> ();
    }

    //Add menu items
    public void addItem(CustomBottomItem item) {
        if (items.size () <= ITEM_LIMIT - 1) {
            items.add (item);
        }
    }

    //Change methods
    public void changeBackground(String color) {
        custom_bottom_bar_parent.setCardBackgroundColor (Color.parseColor (color));
    }

    public void changeDividerColor(String color) {
        custom_divider.setBackgroundColor (Color.parseColor (color));
    }

    public void hideDivider() {
        custom_divider.setVisibility (View.GONE);
    }

    //Getter-Setters
    private String getDefaultBackground() {
        return defaultBackground;
    }

    public void setDefaultBackground(String defaultBackground) {
        this.defaultBackground = defaultBackground;
    }

    private String getDefaultTint() {
        return defaultTint;
    }

    public void setDefaultTint(String defaultTint) {
        this.defaultTint = defaultTint;
    }

    //Add Adapter
    private void setAdapter(int defaultOpenIndex) {
        ItemAdapter simpleAdapter = new ItemAdapter (defaultOpenIndex, items, itemSelectorInterface);
        simpleAdapter.setDefaultBackground (getDefaultBackground ());
        simpleAdapter.setDefaultTint (getDefaultTint ());
        custom_recycler_view.setLayoutManager (new LinearLayoutManager (context, LinearLayoutManager.HORIZONTAL, false));
        custom_recycler_view.setAdapter (simpleAdapter);
    }

    //Apply
    public void apply(int defaultOpenIndex) {
        setAdapter (defaultOpenIndex);
    }
}
