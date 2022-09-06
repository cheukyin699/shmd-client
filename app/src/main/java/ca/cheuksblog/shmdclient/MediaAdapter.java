package ca.cheuksblog.shmdclient;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private ArrayList<Media> dataset;
    private SHMDApi.QueryParams params = null;
    private int total = 0;
    private Activity rootActivity;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < dataset.size()) {
            rootActivity.runOnUiThread(() -> holder.setMedia(dataset.get(position)));
        } else {
            SHMDApi.getInstance().queryMedia(params, result -> {
                if (!result.hasError()) {
                    final SHMDApi.QueryResponse response = result.getData();
                    if (response.success) {
                        params.offset += params.limit;
                        dataset.addAll(response.data);
                        rootActivity.runOnUiThread(() -> notifyItemRangeChanged(dataset.size(), params.limit));
                        onBindViewHolder(holder, position);
                    } else {
                        Log.e("shmd", response.error);
                    }
                } else {
                    final Exception e = result.getError();
                    Log.e("shmd", e.toString());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return total;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumImageView;
        private TextView titleTextView;
        private TextView artistTextView;

        public ViewHolder(final View v) {
            super(v);

            albumImageView = v.findViewById(R.id.albumImageView);
            titleTextView = v.findViewById(R.id.titleTextView);
            artistTextView = v.findViewById(R.id.artistTextView);
        }

        public void setMedia(final Media data) {
            Glide.with(albumImageView)
                    .load(SHMDApi.getInstance().getAlbumThumbnailPath(data))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.not_found)
                    .fallback(R.drawable.not_found)
                    .fitCenter()
                    .into(albumImageView);

            if (data.title != null) {
                titleTextView.setText(data.title);
            } else {
                titleTextView.setText("No title");
            }

            if (data.artist != null) {
                artistTextView.setText(data.artist);
            } else {
                artistTextView.setText("No artist");
            }
        }
    }

    public MediaAdapter(final Activity activity) {
        this.dataset = new ArrayList<>();
        this.rootActivity = activity;
        setParams(new SHMDApi.QueryParams());
    }

    /**
     * Set query parameters and update the dataset.
     *
     * Cuz it changed.
     * @param params
     */
    public void setParams(SHMDApi.QueryParams params) {
        this.params = params;

        SHMDApi.getInstance().countMedia(params, result -> {
            if (!result.hasError()) {
                rootActivity.runOnUiThread(() -> {
                    // Using notifyItemRangeDeleted crashes the app
                    notifyDataSetChanged();
                    this.total = result.getData().total;
                    this.dataset = new ArrayList<>();
                });
            } else {
                Log.e("shmd", result.getError().toString());
            }
        });
    }
}
