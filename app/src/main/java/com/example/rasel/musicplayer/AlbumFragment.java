package com.example.rasel.musicplayer;

import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class AlbumFragment extends Fragment {

    public AlbumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().setTitle("Albums");
        }
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.rcAllAlbum);

        ArrayList<Album> albumList = null;

        if (getArguments() != null) {
            albumList = getArguments().getParcelableArrayList("albumList");
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            AlbumAdapter adapter = new AlbumAdapter(getContext(), albumList);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            return rootView;
        }else {
            Log.d("rsl", "Album Adaper is Null");
            return null;
        }
    }
}
