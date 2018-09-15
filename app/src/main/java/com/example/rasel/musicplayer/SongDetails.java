package com.example.rasel.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class SongDetails implements Parcelable {

    private String title;
    private String artist;
    private String uri;

    public SongDetails(String title, String artist, String uri) {
        this.title = title;
        this.artist = artist;
        this.uri = uri;
    }

    protected SongDetails(Parcel in) {
        title = in.readString();
        artist = in.readString();
        uri = in.readString();
    }

    public static final Creator<SongDetails> CREATOR = new Creator<SongDetails>() {
        @Override
        public SongDetails createFromParcel(Parcel in) {
            return new SongDetails(in);
        }

        @Override
        public SongDetails[] newArray(int size) {
            return new SongDetails[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
    public String getUri() {
        return uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(uri);
    }
}
