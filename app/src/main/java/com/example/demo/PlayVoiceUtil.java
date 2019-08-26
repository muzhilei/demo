package com.example.demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

/**
 * 音乐播放器
 */
public class PlayVoiceUtil {

    private static MediaPlayer mediaPlayer;

    public static void playVoice(final Context context, int rawMusic) {
        try {
            mediaPlayer = MediaPlayer.create(context, rawMusic);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
//                    Toast.makeText(context, what + "what" + extra + "extra");
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //停止播放声音
    public static void stopVoice() {

        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
