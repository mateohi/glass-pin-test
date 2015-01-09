package com.github.mateohi.glass_pin_test;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.common.collect.Lists;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PinActivity extends Activity {

    private static final int TEN = 10;
    private static final int PIN_SIZE = 5;

    private List<Integer> mSelected = Lists.newArrayList();
    private double mVelocity;
    private int mSelection;
    private View mView;

    private GestureDetector mGestureDetector;
    private AudioManager mAudio;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSelection = 0;
        mGestureDetector = createGestureDetector();
        mAudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mView = new CardBuilder(this, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.pin)
                .setFootnote("scroll to change selection")
                .getView();

        setContentView(mView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private GestureDetector createGestureDetector() {
        GestureDetector gestureDetector = new GestureDetector(this);
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    selectedNumber();
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    changeNumbers(true);
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    changeNumbers(false);
                }
                return false;
            }
        });
        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                Log.i("displacement : ", displacement + "");
                Log.i("delta : ", delta + "");
                Log.i("velocity : ", velocity + "");
                mVelocity = Math.abs(velocity);
                return true;
            }
        });
        return gestureDetector;
    }

    private void selectedNumber() {
        mAudio.playSoundEffect(Sounds.SELECTED);
        mSelected.add(mSelection);

        if (mSelected.size() == PIN_SIZE) {
            String pin = StringUtils.join(mSelected);
            // terminamos
            Log.i("PIN-", pin);
            Toast.makeText(this, pin, Toast.LENGTH_SHORT).show();
            mSelected.clear();
            mAudio.playSoundEffect(Sounds.SUCCESS);
        }

        String mask = StringUtils.repeat('*', mSelected.size()).replace("", " ").trim();
        String rest = StringUtils.repeat('_', PIN_SIZE - mSelected.size()).replace("", " ").trim();

        TextView tv = (TextView) mView.findViewById(R.id.mask);
        tv.setText(mask + rest);
    }

    private void changeNumbers(final boolean fromLeft) {
        int movements = 1;
        if (mVelocity > 9) {
            movements = 3;
        } else if (mVelocity > 6) {
            movements = 2;
        }

        Handler handler = new Handler();
        for (int i = 0; i < movements; i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSelection = circularInt(fromLeft ? 1 : -1);
                    updateView(fromLeft);
                }
            }, 100 + (100 * i));
        }
    }

    private void updateView(boolean fromLeft) {
        setTextViewTextAndAnimate(circular(-2), R.id.first, fromLeft);
        setTextViewTextAndAnimate(circular(-1), R.id.second, fromLeft);
        setTextViewTextAndAnimate(circular(0), R.id.third, fromLeft);
        setTextViewTextAndAnimate(circular(1), R.id.fourth, fromLeft);
        setTextViewTextAndAnimate(circular(2), R.id.fifth, fromLeft);
    }

    private void setTextViewTextAndAnimate(String text, int id, boolean fromLeft) {
        Animation animation = AnimationUtils.loadAnimation(this,
                fromLeft ? R.anim.right_to_left : R.anim.left_to_right);

        TextView tv = (TextView) mView.findViewById(id);
        tv.startAnimation(animation);
        tv.setText(text);
    }

    private String circular(int n) {
        return String.valueOf(circularInt(n));
    }

    private int circularInt(int n) {
        return (mSelection + n + TEN) % TEN;
    }

}
