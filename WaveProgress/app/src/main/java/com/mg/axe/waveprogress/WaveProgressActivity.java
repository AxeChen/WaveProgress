package com.mg.axe.waveprogress;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;
import android.widget.SeekBar;

/**
 * Created by Chen on 2017/6/4.
 */

public class WaveProgressActivity extends AppCompatActivity {

    private SeekBar mProgressBar;
    private WaveProgressView mWaveView;
    private RadioGroup mTextColorGroup;
    private RadioGroup mWaveColorGroup;
    private RadioGroup mBorderColorGroup;
    private RadioGroup mHideTextGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waveprogress);
        initView();
        initListener();
    }

    private void initView() {
        mProgressBar = (SeekBar) findViewById(R.id.seekProgressBar);
        mWaveView = (WaveProgressView) findViewById(R.id.waveView);
        mTextColorGroup = (RadioGroup) findViewById(R.id.textColorGroup);
        mTextColorGroup.check(R.id.tcColorB);

        mWaveColorGroup = (RadioGroup) findViewById(R.id.waveColorGroup);
        mWaveColorGroup.check(R.id.wcColorR);

        mBorderColorGroup = (RadioGroup) findViewById(R.id.borderColorGroup);
        mBorderColorGroup.check(R.id.bcColorR);

        mHideTextGroup = (RadioGroup) findViewById(R.id.hideTextGroup);
        mHideTextGroup.check(R.id.htFalse);
    }

    private void initListener() {
        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTextColorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.tcColorB:
                        mWaveView.setTextColor(Color.BLACK);
                        break;
                    case R.id.tcColorR:
                        mWaveView.setTextColor(Color.RED);
                        break;
                    case R.id.tcColorY:
                        mWaveView.setTextColor(Color.YELLOW);
                        break;
                }
            }
        });

        mWaveColorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.wcColorB:
                        mWaveView.setWaveColor(Color.BLACK);
                        break;
                    case R.id.wcColorR:
                        mWaveView.setWaveColor(Color.RED);
                        break;
                    case R.id.wcColorY:
                        mWaveView.setWaveColor(Color.YELLOW);
                        break;
                }
            }
        });

        mBorderColorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.bcColorB:
                        mWaveView.setBorderColor(Color.BLACK);
                        break;
                    case R.id.bcColorR:
                        mWaveView.setBorderColor(Color.RED);
                        break;
                    case R.id.bcColorY:
                        mWaveView.setBorderColor(Color.YELLOW);
                        break;
                }
            }
        });

        mHideTextGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.htTrue:
                        mWaveView.hideProgressText(true);
                        break;
                    case R.id.htFalse:
                        mWaveView.hideProgressText(false);
                        break;
                }
            }
        });
    }

}
