package com.obrekht.onlinecameras.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.obrekht.onlinecameras.R;
import com.obrekht.onlinecameras.model.WebcamCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryFilterAdapter extends BaseAdapter {

    public static final int VIEW_TYPE_CATEGORY = 1;
    public static final int VIEW_TYPE_CATEGORY_ALL = 2;

    private final Context context;
    private final LayoutInflater inflater;
    private List<WebcamCategory> categories;
    private int selectedCategory = 0;

    public CategoryFilterAdapter(Context context, List<WebcamCategory> categories) {
        this.context = context;
        this.categories = new ArrayList<>(categories);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_category, parent, false);
        }

        Log.d("CategoryFilterAdapter", "getView");

        TextView categoryName = (TextView) view.findViewById(R.id.category_name);
        TextView categoryCount = (TextView) view.findViewById(R.id.category_count);

        if (getItemViewType(position) == VIEW_TYPE_CATEGORY_ALL) {
            Log.d("CategoryFilterAdapter", "VIEW_TYPE_CATEGORY_ALL");
            categoryName.setText(R.string.category_all);
            categoryCount.setText("");
        } else {
            WebcamCategory category = (WebcamCategory) getItem(position);

            categoryName.setText(category.getName());
            categoryCount.setText(category.getCount());
        }

        if (selectedCategory == position) {
            categoryName.setTextColor(0xFFFFFFFF); // TODO
        } else {
            categoryName.setTextColor(0x8AFFFFFF); // TODO
        }

        return view;
    }

    public void selectCategory(int position) {
        this.selectedCategory = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_CATEGORY_ALL;
        }
        return VIEW_TYPE_CATEGORY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
