package com.obrekht.onlinecameras.ui.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.obrekht.onlinecameras.R;
import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamCategory;
import com.obrekht.onlinecameras.model.WebcamLocation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebcamsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_WEBCAM = 0;
    public static final int VIEW_TYPE_PROGRESS = 1;

    private List<Webcam> webcams = new ArrayList<>();
    private boolean loading;

    private OnWebcamClickListener webcamClickListener;
    private OnLocationClickListener locationClickListener;

    public WebcamsAdapter() {
    }

    public Webcam getItem(int position) {
        return webcams.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder holder;

        if (loading && viewType == VIEW_TYPE_PROGRESS) {
            holder = new ProgressViewHolder(
                    inflater.inflate(R.layout.item_progress, parent, false));
        } else {
            holder = new WebcamViewHolder(
                    inflater.inflate(R.layout.item_webcam, parent, false));
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof WebcamViewHolder) {
            WebcamViewHolder webcamHolder = (WebcamViewHolder) holder;
            Webcam webcam = getItem(position);

            webcamHolder.text1.setText(getCategoriesString(webcam.getCategories()));
            webcamHolder.text2.setText(webcam.getTitle());
            webcamHolder.text2.setSelected(true);
            webcamHolder.location.setText(getLocationString(webcam.getLocation()));

            webcamHolder.itemView.setOnClickListener(view -> {
                if (webcamClickListener != null) {
                    webcamClickListener.onWebcamClick(position, webcam);
                }
            });

            webcamHolder.locationLayout.setOnClickListener(view -> {
                if (locationClickListener != null) {
                    locationClickListener.onLocationClick(position, webcam);
                }
            });

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
        return (loading && position == webcams.size()) ? VIEW_TYPE_PROGRESS : VIEW_TYPE_WEBCAM;
    }

    @Override
    public int getItemCount() {
        return webcams.size() + (loading ? 1 : 0);
    }

    public int getWebcamsCount() {
        return webcams.size();
    }

    public void setWebcamClickListener(OnWebcamClickListener webcamClickListener) {
        this.webcamClickListener = webcamClickListener;
    }

    public void setLocationClickListener(OnLocationClickListener locationClickListener) {
        this.locationClickListener = locationClickListener;
    }

    private static String getCategoriesString(List<WebcamCategory> categories) {
        String categoriesStr = "";

        for (int i = 0; i < categories.size(); i++) {
            categoriesStr += categories.get(i).getName();
            if (i != (categories.size() - 1)) {
                categoriesStr += ", ";
            }
        }

        return categoriesStr;
    }

    private static String getLocationString(WebcamLocation location) {
        return String.format(Locale.getDefault(),
                "%s, %s, %s", location.getCountry(), location.getRegion(), location.getCity());
    }

    public void setWebcams(List<Webcam> webcams) {
        final WebcamDiffCallback diffCallback = new WebcamDiffCallback(this.webcams, webcams);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.webcams.clear();
        this.webcams.addAll(webcams);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addWebcams(List<Webcam> webcams) {
        final int oldSize = this.webcams.size();
        this.webcams.addAll(webcams);
        notifyItemRangeInserted(oldSize, webcams.size());
    }

    public void setLoading(boolean loading) {
        Log.d("WebcamsAdapter", "Loading: " + loading + ", " + (getItemViewType(webcams.size()) == VIEW_TYPE_PROGRESS ? "VIEW_TYPE_PROGRESS" : "VIEW_TYPE_WEBCAM"));
        this.loading = loading;
        if (loading) {
            notifyItemInserted(webcams.size());
        } else {
            notifyItemRemoved(webcams.size());
        }
    }

    public boolean isLoading() {
        return loading;
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

        public WebcamViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnWebcamClickListener {
        void onWebcamClick(int position, Webcam webcam);
    }

    public interface OnLocationClickListener {
        void onLocationClick(int position, Webcam webcam);
    }
}