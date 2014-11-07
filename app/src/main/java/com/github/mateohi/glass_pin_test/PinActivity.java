package com.github.mateohi.glass_pin_test;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mSelection = 0;
        mGestureDetector = createGestureDetector();

        mView = new CardBuilder(this, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.pin)
                .setFootnote("scroll to change selection")
                .getView();

        updateView();
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

                changeNumbers(velocity);
                return true;
            }
        });
        return gestureDetector;
    }

    private void selectedNumber() {
        mSelected.add(mSelection);

        if (mSelected.size() == PIN_SIZE) {
            String pin = StringUtils.join(mSelected);
            // terminamos
            Log.i("PIN-", pin);
            mSelected.clear();
        }

        String mask = StringUtils.repeat('*', mSelected.size());
        String rest = StringUtils.repeat('_', PIN_SIZE - mSelected.size());

        mask = StringUtils.join(mask.toCharArray(), " ");
        rest = StringUtils.join(rest.toCharArray(), " ");

        setTextViewText(mask + rest, R.id.mask);
    }

    private void changeNumbers(float velocity) {
        int change;
        if (velocity > 0) {
            change = 1;
        } else {
            change = -1;
        }

        mSelection = Math.abs((mSelection + change) % TEN);
        updateView();
    }

    private void updateView() {
        setTextViewText(circular(-2), R.id.first);
        setTextViewText(circular(-1), R.id.second);
        setTextViewText(circular(0), R.id.third);
        setTextViewText(circular(1), R.id.fourth);
        setTextViewText(circular(2), R.id.fifth);

        setContentView(mView);
    }

    private void setTextViewText(String text, int id) {
        TextView tv = (TextView) mView.findViewById(id);
        tv.setText(text);
    }

    private String circular(int n) {
        return String.valueOf((mSelection + n + TEN) % TEN);
    }

}
