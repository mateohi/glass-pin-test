package com.github.mateohi.glass_pin_test;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PinActivity extends Activity {

    private static final int TEN = 10;
    private static final int PIN_SIZE = 5;

    private List<Integer> mSelected = new ArrayList<Integer>();
    private int mSelection;
    private View mView;

    private GestureDetector mGestureDetector;
    private AudioManager mAudio;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

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
                    changeNumbers(1);
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    changeNumbers(-1);
                }
                return false;
            }
        });
        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                Log.i("DIS: ", displacement + "");
                Log.i("DEL: ", delta + "");
                Log.i("VEL: ", velocity + "");

                //changeNumbers(velocity);
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
            mSelected.clear();
        }

        String mask = StringUtils.repeat('*', mSelected.size()).replace("", " ").trim();
        String rest = StringUtils.repeat('_', PIN_SIZE - mSelected.size()).replace("", " ").trim();

        TextView tv = (TextView) mView.findViewById(R.id.mask);
        tv.setText(mask + rest);
    }

    private void changeNumbers(float velocity) {
        boolean fromLeft;
        int change;
        if (velocity > 0) {
            change = 1;
            fromLeft = true;
        } else {
            change = -1;
            fromLeft = false;
        }

        mSelection = circularInt(change);
        updateView(fromLeft);
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
                fromLeft ? R.anim.left_to_right : R.anim.right_to_left);

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
