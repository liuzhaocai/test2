package com.hopen.darts.ui;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hopen.darts.R;
import com.hopen.darts.base.BaseActivity;
import com.hopen.darts.base.C;
import com.hopen.darts.networks.mapper.Advert;
import com.hopen.darts.utils.DaemonUtil;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.widget.PLVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdvertActivity extends BaseActivity {
    {
        wifiSignalAble = false;//隐藏wifi信号强度显示
        isShowSpreadStarAnim = false;//不显示扩散星星动画
    }

    @BindView(R.id.advert_video_view)
    PLVideoView advertVideoView;
    @BindView(R.id.advert_image_view)
    ImageView advertImageView;
    @BindView(R.id.er_code_iv)
    ImageView erCodeIv;

    private boolean isPrepared = false;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_advert);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        Advert advert = DaemonUtil.getAdvert();
        if (advert == null || TextUtils.isEmpty(advert.getFile_url())) {
            finish();
        } else {
            String uri = C.network.img_url + advert.getFile_url();
            //String uri = "http://video.699pic.com/videos/38/45/00/CaCwReHX45yW1522384500.mp4";
            if (uri.endsWith("mp4") || uri.endsWith("mpeg")) {
                initPlayer();
                advertVideoView.setVideoPath(uri);
                advertImageView.setVisibility(View.GONE);
            } else {
                Glide.with(this).load(uri).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.e("Glide Error", "无法加载此图片。e = " + e.getMessage());
                        finish();
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                }).into(advertImageView);
                advertVideoView.setVisibility(View.GONE);
            }
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) erCodeIv.getLayoutParams();
            int size = 220;
            params.width = size;
            params.height = size;
            params.leftMargin = (int) (advert.getQr_left() / 100 * (displayMetrics.widthPixels - size));
            params.topMargin = (int) (advert.getQr_top() / 100 * (displayMetrics.heightPixels - size));
            erCodeIv.setLayoutParams(params);
            Glide.with(this).load(C.network.img_url + advert.getQrcode_url()).into(erCodeIv);
        }
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected void initListener() {

    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        advertVideoView.setLooping(true);//是否循环
        //准备完成回调
        advertVideoView.setOnPreparedListener(new PLOnPreparedListener() {
            @Override
            public void onPrepared(int i) {
                isPrepared = true;
                advertVideoView.start();// 播放
            }
        });
        //播放错误回调
        advertVideoView.setOnErrorListener(new PLOnErrorListener() {
            @Override
            public boolean onError(int i) {
                Log.e("VideoView Error", "无法播放此视频。i = " + i);
                finish();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (isPrepared && !advertVideoView.isPlaying()) {
                advertVideoView.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (advertVideoView.canPause()) {
                advertVideoView.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            advertVideoView.stopPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onKeyOperate(long instruction) {
        DaemonUtil.resetLastOperationTime();
        finish();
    }
}
