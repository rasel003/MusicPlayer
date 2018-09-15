package com.example.rasel.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AllSongsFragment extends Fragment {
    private RecyclerView recyclerView;

    public AllSongsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_songs, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.rcAllSongs);

        ArrayList<SongDetails> songList  = getArguments().getParcelableArrayList("songList");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MediaCursorAdapter adapter = new MediaCursorAdapter(getContext(), songList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return rootView;
    }

}
