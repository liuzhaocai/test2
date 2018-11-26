package com.hopen.darts.utils.Sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.hopen.darts.R;
import com.hopen.darts.base.BaseApplication;


/**
 * 公共播放音频类
 */

public class SoundPlayUtils {

    // SoundPool对象
    private static SoundPool mSoundPlayer = new SoundPool(6, AudioManager.STREAM_MUSIC, 10);
    private static SoundPlayUtils soundPlayUtils;
    // 上下文
    private static Context mContext;
    private SoundType soundType;
    /**
     * 初始化
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }
        soundPlayUtils.soundType = new SoundType();
        // 初始化声音
        mContext = context;
        soundPlayUtils.soundType.btn_click = mSoundPlayer.load(mContext, R.raw.btn_click, 1);// 1
        soundPlayUtils.soundType.bust = mSoundPlayer.load(mContext, R.raw.bust, 1);// 2
        soundPlayUtils.soundType.shoot_center_score = mSoundPlayer.load(mContext, R.raw.shoot_center_score, 1);// 3
        soundPlayUtils.soundType.shoot_one_score = mSoundPlayer.load(mContext, R.raw.shoot_one_score, 1);// 4
        soundPlayUtils.soundType.shoot_ter_score = mSoundPlayer.load(mContext, R.raw.shoot_ter_score, 1);// 5
        soundPlayUtils.soundType.shoot_double_score = mSoundPlayer.load(mContext, R.raw.shoot_double_score, 1);// 6
        soundPlayUtils.soundType.shoot_twenty_double_score = mSoundPlayer.load(mContext, R.raw.shoot_twenty_double_score, 1);// 7
        soundPlayUtils.soundType.message = mSoundPlayer.load(mContext, R.raw.message, 1);// 8
        soundPlayUtils.soundType.miss = mSoundPlayer.load(mContext, R.raw.miss, 1);// 9
        soundPlayUtils.soundType.scroll_menu = mSoundPlayer.load(mContext, R.raw.scroll_menu, 1);// 10
        soundPlayUtils.soundType.win = mSoundPlayer.load(mContext, R.raw.win, 1);// 11

        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(SoundEnum soundID) {
//        MediaPlayer mp = MediaPlayer.create(BaseApplication.getApplication(),R.raw.btn_click);
//        mp.start();
        AudioManager mgr = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        mSoundPlayer.play(soundPlayUtils.soundType.getSoundId(soundID), volume, volume, 1, 0, 1);
    }
}
