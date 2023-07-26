package edu.hitsz.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

import edu.hitsz.R;

public class AudioManager {
    private static boolean InAudio = false;
    private static final Map<Integer, Integer> soundPoolMap = new HashMap<>();
    private static SoundPool mysp;
    public static MediaPlayer BGM;
    public static MediaPlayer BGM_BOSS;

    public static boolean isInAudio() {
        return InAudio;
    }

    public static void setInMusic(boolean inMusic) {
        AudioManager.InAudio = inMusic;
    }

    public static void initAudio(Context context){
        AudioManager.BGM = MediaPlayer.create(context, R.raw.bgm);
        AudioManager.BGM_BOSS = MediaPlayer.create(context, R.raw.bgm_boss);

        AudioAttributes audioAttributes;
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mysp = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPoolMap.put(1, mysp.load(context, R.raw.bullet_hit, 1));
        soundPoolMap.put(2, mysp.load(context, R.raw.get_supply, 1));
        soundPoolMap.put(3, mysp.load(context, R.raw.bomb_explosion, 1));
        soundPoolMap.put(4, mysp.load(context, R.raw.game_over, 1));
    }

    public static void bgmStart() {
        if(isInAudio()){
            AudioManager.BGM.setLooping(true);
            AudioManager.BGM.setVolume(6.0f, 6.0f);
            AudioManager.BGM.start();
        }
    }

    public static void bossStart() {
        if(isInAudio()) {
            AudioManager.BGM_BOSS.setLooping(true);
            AudioManager.BGM_BOSS.setVolume(6.0f, 6.0f);
            AudioManager.BGM_BOSS.start();
            AudioManager.BGM.pause();
        }
    }

    public static void bossDead() {
        if(isInAudio()) {
            AudioManager.BGM_BOSS.stop();
            AudioManager.BGM.start();
        }
    }

    public static void bombStart() {
        if(isInAudio()) {
            mysp.play(soundPoolMap.get(3), 0.5f, 0.5f, 0, 0, 1.0f);
        }
    }

    public static void hitStart() {
        if(isInAudio()) {
            mysp.play(soundPoolMap.get(1), 1, 1, 0, 0,1.0f);
        }
    }

    public static void propStart() {
        if(isInAudio()) {
            mysp.play(soundPoolMap.get(2), 1, 1, 0, 0,1.0f);
        }
    }

    public static void gameOver() {
        if(isInAudio()) {
            mysp.play(soundPoolMap.get(4), 1, 1, 0, 0,1.0f);
            AudioManager.BGM.stop();
            AudioManager.BGM_BOSS.stop();
        }
    }
}
