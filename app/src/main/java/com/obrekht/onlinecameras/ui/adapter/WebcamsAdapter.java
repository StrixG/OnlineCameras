package com.obrekht.onlinecameras.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.obrekht.onlinecameras.R;
import com.obrekht.onlinecameras.databinding.ItemProgressBinding;
import com.obrekht.onlinecameras.databinding.ItemWebcamBinding;
import com.obrekht.onlinecameras.model.Webcam;
import com.obrekht.onlinecameras.model.WebcamCategory;
import com.obrekht.onlinecameras.model.WebcamLocation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import moxy.MvpDelegate;

public class WebcamsAdapter extends MvpRecyclerViewAdapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_WEBCAM = 0;
    public static final int VIEW_TYPE_PROGRESS = 1;

    private List<Webcam> webcams = new ArrayList<>();
    private boolean loading;

    private OnWebcamClickListener webcamClickListener;
    private OnLocationClickListener locationClickListener;

    public WebcamsAdapter(MvpDelegate<?> parentDelegate) {
        super(parentDelegate, String.valueOf(0));
    }

    public Webcam getItem(int position) {
        return webcams.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder holder;

        if (loading && viewType == VIEW_TYPE_PROGRESS) {
            holder = new ProgressViewHolder(
                    ItemProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            holder = new WebcamViewHolder(
                    ItemWebcamBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof WebcamViewHolder) {
            WebcamViewHolder webcamHolder = (WebcamViewHolder) holder;
            Webcam webcam = getItem(position);

            ItemWebcamBinding webcamBinding = webcamHolder.binding;
            webcamBinding.category.setText(getCategoriesString(webcam.getCategories()));
            webcamBinding.name.setText(webcam.getTitle());
            webcamBinding.name.setSelected(true);
            webcamBinding.locationText.setText(getLocationString(webcam.getLocation()));

            webcamBinding.getRoot().setOnClickListener(view -> {
                if (webcamClickListener != null) {
                    webcamClickListener.onWebcamClick(position, webcam);
                }
            });

            webcamBinding.locationLayout.setOnClickListener(view -> {
                if (locationClickListener != null) {
                    locationClickListener.onLocationClick(position, webcam);
                }
            });

            Picasso.get()
                    .load(webcam.getImage().getCurrent().imageUrl)
                    .placeholder(R.color.grey_light)
                    .into(webcamBinding.webcamPicture);

        } else {
            ((ProgressViewHolder) holder).binding.progressBar.setIndeterminate(true);
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
        StringBuilder categoriesStr = new StringBuilder();

        for (int i = 0; i < categories.size(); i++) {
            categoriesStr.append(categories.get(i).getName());
            if (i != (categories.size() - 1)) {
                categoriesStr.append(", ");
            }
        }

        return categoriesStr.toString();
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

        if (!loading) {
            notifyItemChanged(webcams.size());
        }
        notifyItemRangeInserted(oldSize + 1, webcams.size());
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        if (loading) {
            notifyItemInserted(webcams.size());
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
        ItemProgressBinding binding;

        public ProgressViewHolder(ItemProgressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class WebcamViewHolder extends RecyclerView.ViewHolder {
        ItemWebcamBinding binding;

        public WebcamViewHolder(ItemWebcamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnWebcamClickListener {
        void onWebcamClick(int position, Webcam webcam);
    }

    public interface OnLocationClickListener {
        void onLocationClick(int position, Webcam webcam);
    }
}