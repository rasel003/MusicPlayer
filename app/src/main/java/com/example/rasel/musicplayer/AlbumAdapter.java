package com.example.rasel.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private ArrayList<Album> albumList;
    private Context context;

    AlbumAdapter(Context context, ArrayList<Album> albumList) {
        this.albumList = albumList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvAlbumTitle.setText(albumList.get(position).getAlbumName());
        holder.tvAlbumArtist.setText(albumList.get(position).getAlbumArtistName());

        String data = albumList.get(position).getSongList().get(0).getUri();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(data);
        Bitmap bitmap = BitmapFactory.decodeByteArray(mmr.getEmbeddedPicture(), 0, mmr.getEmbeddedPicture().length);
        mmr.release();
        holder.imgAlbumPoster.setImageBitmap(bitmap);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllSongsFragment allSongsFragment = new AllSongsFragment();
                Bundle data = new Bundle();
                data.putParcelableArrayList("songList", albumList.get(position).getSongList());
                allSongsFragment.setArguments(data);
                MainActivity myActivity = (MainActivity) context;
                myActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, allSongsFragment).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAlbumTitle, tvAlbumArtist;
        private ImageView imgAlbumPoster;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAlbumTitle = itemView.findViewById(R.id.tvAlbumTitle);
            tvAlbumArtist = itemView.findViewById(R.id.tvAlbumArtist);
            imgAlbumPoster = itemView.findViewById(R.id.imgAlbumPoster);
            cardView = itemView.findViewById(R.id.cvAlbum);
        }
    }
}