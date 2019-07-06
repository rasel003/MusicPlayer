package com.example.rasel.musicplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private int STORAGE_PERMISSION_CODE = 1;

    private ArrayList<SongDetails> songList;
    private ArrayList<Album> albumList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        songList = new ArrayList<>(3);
        albumList = new ArrayList<>(3);

        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
        Cursor cursor = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, null);
        } else {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, null);
            } else {
                requestStoragePermission();
            }
        }

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    SongDetails songDetails = new SongDetails(name, artist, uri);
                    songList.add(songDetails);

                    String albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));
                    String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));

                    if(albumList.size()==0){
                        Album album = new Album(albumName, artist, albumKey);
                        album.addSong(songDetails);
                        albumList.add(album);
                    }else {
                        boolean isFound = false;
                        for (Album album: albumList
                             ) {
                            if(album.getAlbumName().trim().toLowerCase().equals(albumName.trim().toLowerCase())){
                                album.addSong(songDetails);
                                isFound = true;
                            }
                        }
                        if( ! isFound ){
                            Album newAlbum = new Album(albumName, artist, albumKey);
                            newAlbum.addSong(songDetails);
                            albumList.add(newAlbum);
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            BottomNavigationView bottomNav = findViewById(R.id.bottonNavigationView);
            bottomNav.setOnNavigationItemSelectedListener(navListener);

            if (savedInstanceState == null) {
                Fragment fragment = new AllSongsFragment();
                Bundle data = new Bundle();
                data.putParcelableArrayList("songList", songList);//put string, int, etc in bundle with a key value
                fragment.setArguments(data);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        fragment).commit();
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_songs:
                            selectedFragment = new AllSongsFragment();
                            Bundle data = new Bundle();
                            data.putParcelableArrayList("songList", songList);
                            selectedFragment.setArguments(data);
                            break;
                        case R.id.nav_album:
                            selectedFragment = new AlbumFragment();
                            Bundle dataAlbum = new Bundle();
                            dataAlbum.putParcelableArrayList("albumList", albumList);
                            selectedFragment.setArguments(dataAlbum);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

                    return true;
                }
            };

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to retrieve the music files")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}