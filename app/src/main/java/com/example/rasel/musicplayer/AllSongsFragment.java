package com.example.rasel.musicplayer;

import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;

public class AllSongsFragment extends Fragment {

    public AllSongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getActivity()).setTitle("All Songs");
        }

        View rootView = inflater.inflate(R.layout.fragment_all_songs, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.rcAllSongs);

        ArrayList<SongDetails> songList;
        if (getArguments() != null) {
            songList = getArguments().getParcelableArrayList("songList");
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            MediaCursorAdapter adapter = new MediaCursorAdapter(getContext(), songList);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        return rootView;
    }

}
