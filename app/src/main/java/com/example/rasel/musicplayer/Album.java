package com.example.rasel.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Album implements Parcelable{
    private String albumName;
    private String albumArtistName;
    private String albumKey;

    private ArrayList<SongDetails> songList;

    public Album(String albumName, String albumArtistName, String albumKey) {
        this.albumName = albumName;
        this.albumArtistName = albumArtistName;
        this.albumKey = albumKey;
        songList = new ArrayList<>(3);
    }
    public void addSong(SongDetails songDetails){
        songList.add(songDetails);
    }

    public ArrayList<SongDetails> getSongList() {
        return songList;
    }
    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumArtistName() {
        return albumArtistName;
    }

    public String getAlbumKey() {
        return albumKey;
    }

    protected Album(Parcel in) {
        albumName = in.readString();
        albumArtistName = in.readString();
        albumKey = in.readString();
        songList = in.createTypedArrayList(SongDetails.CREATOR);
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumName);
        dest.writeString(albumArtistName);
        dest.writeString(albumKey);
        dest.writeTypedList(songList);
    }
}
