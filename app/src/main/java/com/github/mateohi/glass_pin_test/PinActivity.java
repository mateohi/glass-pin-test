package com.github.mateohi.glass_pin_test;

import com.google.android.glass.widget.CardBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class PinActivity extends Activity {

    private View mView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mView = buildView();
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

    private View buildView() {
        return new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Test")
                .getView();
    }

}
