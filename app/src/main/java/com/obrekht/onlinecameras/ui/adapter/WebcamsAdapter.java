package com.obrekht.onlinecameras.ui.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.obrekht.onlinecameras.R;
import com.obrekht.onlinecameras.model.Category;
import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamLocation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebcamsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_WEBCAM = 0;
    public static final int VIEW_TYPE_PROGRESS = 1;

    private List<Webcam> webcams = new ArrayList<>();

    public WebcamsAdapter() {
    }

    private Webcam getItem(int position) {
        return webcams.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder holder;

        if (viewType == VIEW_TYPE_WEBCAM) {
            View itemView = inflater.inflate(R.layout.item_webcam, parent, false);

            holder = new WebcamViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_progress, parent, false);

            holder = new ProgressViewHolder(itemView);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WebcamViewHolder) {
            WebcamViewHolder webcamHolder = (WebcamViewHolder) holder;
            Webcam webcam = getItem(position);

            webcamHolder.text1
                    .setText(getCategoriesString(webcam.getCategories()));
            webcamHolder.text2
                    .setText(webcam.getTitle());
            webcamHolder.location
                    .setText(getLocationString(webcam.getLocation()));

            Picasso.with(webcamHolder.webcamPicture.getContext())
                    .load(webcam.getImage().getCurrent().imageUrl)
                    .placeholder(R.color.grey_light)
                    .into(webcamHolder.webcamPicture);

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == webcams.size() ? VIEW_TYPE_PROGRESS : VIEW_TYPE_WEBCAM;
    }

    @Override
    public int getItemCount() {
        return webcams.size();
    }

    private static String getCategoriesString(List<Category> categories) {
        String categoriesStr = "";

        for (int i = 0; i < categories.size(); i++) {
            categoriesStr += categories.get(i).getName();
            if (i != categories.size() - 1) {
                categoriesStr += ", ";
            }
        }

        return categoriesStr;
    }

    private static String getLocationString(WebcamLocation location) {
        return String.format(Locale.getDefault(),
                "%s, %s, %s", location.getCountry(), location.getRegion(), location.getCity());
    }

    public void setWebcams(List<Webcam> webcams, boolean maybeMore) {
        final WebcamDiffCallback diffCallback = new WebcamDiffCallback(this.webcams, webcams);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.webcams.clear();
        this.webcams.addAll(webcams);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addWebcams(List<Webcam> webcams, boolean maybeMore) {
        final int oldSize = this.webcams.size();
        this.webcams.addAll(webcams);
        notifyItemRangeInserted(oldSize, webcams.size());
    }

    // DiffCallback
    private static class WebcamDiffCallback extends DiffUtil.Callback {

        private final List<Webcam> oldList;
        private final List<Webcam> newList;

        public WebcamDiffCallback(List<Webcam> oldList, List<Webcam> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            final Webcam oldItem = oldList.get(oldItemPosition);
            final Webcam newItem = newList.get(newItemPosition);

            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final Webcam oldItem = oldList.get(oldItemPosition);
            final Webcam newItem = newList.get(newItemPosition);

            return oldItem.getLocation().equals(newItem.getLocation()) &&
                    oldItem.getTitle().equals(newItem.getTitle()); // location & title
        }
    }

    // ViewHolders
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public static class WebcamViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text1)
        TextView text1;
        @BindView(R.id.text2)
        TextView text2;
        @BindView(R.id.webcam_picture)
        AppCompatImageView webcamPicture;
        @BindView(R.id.location_text)
        TextView location;
        @BindView(R.id.location_layout)
        View locationLayout;

        @OnClick(R.id.location_layout)
        public void showLocation() {
        }

        public WebcamViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}