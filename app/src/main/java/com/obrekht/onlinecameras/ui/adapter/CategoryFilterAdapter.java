package com.obrekht.onlinecameras.ui.adapter;

import android.content.Context;
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

    public static final int VIEW_TYPE_CATEGORY = 0;
    public static final int VIEW_TYPE_CATEGORY_ALL = 1;

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
        return categories.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? null : categories.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_category, parent, false);
        }

        TextView categoryName = (TextView) view.findViewById(R.id.category_name);
        TextView categoryCount = (TextView) view.findViewById(R.id.category_count);

        if (getItemViewType(position) == VIEW_TYPE_CATEGORY_ALL) {
            categoryName.setText(R.string.category_all);
            categoryCount.setText("");
        } else {
            WebcamCategory category = (WebcamCategory) getItem(position);

            categoryName.setText(category.getName());
            categoryCount.setText(String.valueOf(category.getCount()));
        }

        if (selectedCategory == position) {
            int color = context.getResources().getColor(R.color.selected_category);
            categoryName.setTextColor(color);
        } else {
            int color = context.getResources().getColor(R.color.unselected_category);
            categoryName.setTextColor(color);
        }

        return view;
    }

    public void selectCategory(int position) {
        this.selectedCategory = position;
        notifyDataSetChanged();
    }

    public void selectCategoryById(String webcamCategory) {
        int position = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(webcamCategory)) {
                position = i + 1;
            }
        }
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
