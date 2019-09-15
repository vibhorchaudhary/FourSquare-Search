package com.vibhor.yulusearch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vibhor.yulusearch.R;
import com.vibhor.yulusearch.model.Venues;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ViewHolder> {

    private List<Venues> places;

    public PlacesListAdapter(List<Venues> places) {
        this.places = places;
    }

    public void updateList(List<Venues> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Venues venue = places.get(position);
        holder.placeName.setText(venue.getName());
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.place_name_textview)
        TextView placeName;

        ViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);

        }

    }
}
