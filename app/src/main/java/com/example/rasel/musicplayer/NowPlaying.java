package com.example.rasel.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class NowPlaying extends AppCompatActivity {

    private static final String TAG = "NowPlaying";

    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 4000;

    private TextView tvTitle, tvArtist, tvTotalDuration, tvCurrentDuration;
    private SeekBar seekBar = null;
    private static MediaPlayer player = null;
    private ImageView prev = null;
    private ImageView play = null;
    private ImageView next = null;
    private ImageView imgNowPlayingPoster;

    private boolean isStarted = true;
    private boolean isMovingSeekBar = false;
    private final Handler handler = new Handler();
    private ArrayList<SongDetails> songList;
    private static int position = -2;
    private int newPosition = -1;

    private final Runnable updatePositinRunnable = new Runnable() {
        @Override
        public void run() {
            updatePosition();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Now Playing");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tvTitle = findViewById(R.id.tvNowPlayingTitle);
        tvArtist = findViewById(R.id.tvNowPlayingArtist);
        tvCurrentDuration = findViewById(R.id.tvCurrentDuration);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        imgNowPlayingPoster = findViewById(R.id.imgNowPlayingPoster);

        seekBar = findViewById(R.id.seekBar);
        prev = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);

        prev.setOnClickListener(OnButtonClick);
        play.setOnClickListener(OnButtonClick);
        next.setOnClickListener(OnButtonClick);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            songList = bundle.getParcelableArrayList("songList");
            newPosition = Integer.valueOf(bundle.getString("position"));
        } else {
            Log.d("rsl", "Now Playing is terminating...");
            finish();
        }
        if (player == null) {
            player = new MediaPlayer();
            position = newPosition;
            startPlay(position);
        } else {
            if (newPosition == position) {
                tvTitle.setText(songList.get(position).getTitle());
                tvArtist.setText(songList.get(position).getArtist());
                seekBar.setProgress(player.getCurrentPosition());

                String data = songList.get(position).getUri();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(data);
                Bitmap bitmap = BitmapFactory.decodeByteArray(mmr.getEmbeddedPicture(), 0, mmr.getEmbeddedPicture().length);
                mmr.release();
                imgNowPlayingPoster.setImageBitmap(bitmap);

                seekBar.setMax(player.getDuration());
                play.setImageResource(android.R.drawable.ic_media_pause);
                updatePosition();
                isStarted = true;

            } else {
                position = newPosition;
                startPlay(position);
            }
        }
        player.setOnCompletionListener(onCompletion);
        player.setOnErrorListener(onError);
        seekBar.setOnSeekBarChangeListener(seekBarChanged);
    }

    private void startPlay(int position) {

        tvTitle.setText(songList.get(position).getTitle());
        tvArtist.setText(songList.get(position).getArtist());

        seekBar.setProgress(0);
        player.stop();
        player.reset();

        try {
            String data = songList.get(position).getUri();
            player.setDataSource(data);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(data);

            byte[] artBytes = mmr.getEmbeddedPicture();
            if(artBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
                imgNowPlayingPoster.setImageBitmap(bitmap);
            }else {
                imgNowPlayingPoster.setImageDrawable(getResources().getDrawable(R.drawable.album));
            }
            mmr.release();

            player.prepare();
            player.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(player.getDuration());
        play.setImageResource(android.R.drawable.ic_media_pause);
        updatePosition();
        isStarted = true;
    }

    private void stopPlay() {
        player.stop();
        player.reset();
        play.setImageResource(android.R.drawable.ic_media_play);
        handler.removeCallbacks(updatePositinRunnable);
        seekBar.setProgress(0);
        isStarted = false;
    }


    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updatePositinRunnable);
        player.stop();
        player.reset();
        player.release();
        player = null;
    }

    private void updatePosition() {
        handler.removeCallbacks(updatePositinRunnable);

        long totalDuration = player.getDuration();
        long currentDuration = player.getCurrentPosition();

        tvCurrentDuration.setText(milliSecondsToTimer(currentDuration));
        tvTotalDuration.setText(milliSecondsToTimer(totalDuration));

        seekBar.setProgress(player.getCurrentPosition());
        handler.postDelayed(updatePositinRunnable, UPDATE_FREQUENCY);
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    private View.OnClickListener OnButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play: {
                    if (player.isPlaying()) {
                        handler.removeCallbacks(updatePositinRunnable);
                        player.pause();
                        play.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        if (isStarted) {
                            player.start();
                            play.setImageResource(android.R.drawable.ic_media_pause);
                            updatePosition();
                        } else {
                            startPlay(position);
                        }
                    }
                    break;
                }

                case R.id.next: {
                    int seekto = player.getCurrentPosition() + STEP_VALUE;
                    if (seekto > player.getDuration())
                        seekto = player.getDuration();
                    player.pause();
                    player.seekTo(seekto);
                    player.start();
                    break;
                }

                case R.id.previous: {
                    int seekto = player.getCurrentPosition() - STEP_VALUE;
                    if (seekto < 0)
                        seekto = 0;
                    player.pause();
                    player.seekTo(seekto);
                    player.start();
                    break;
                }
            }
        }
    };
    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            stopPlay();
        }

        ;
    };
    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "onError: error occurred");
            return false;
        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarChanged =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (isMovingSeekBar) {
                        player.seekTo(progress);
                        Log.i("OnSeekBarChangeListener", "OnProgressChanged");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isMovingSeekBar = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isMovingSeekBar = false;
                }
            };
}
