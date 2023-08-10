package com.app.threads.util;

import android.os.Handler;
import android.view.View;

public class DoubleClickListener implements View.OnClickListener {
    private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Maximum time between two clicks to consider as a double click
    private long lastClickTime = 0;
    private final Handler handler = new Handler();
    private boolean isSingleClick = true;

    @Override
    public void onClick(final View view) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            // Double click detected
            isSingleClick = false;
            handler.removeCallbacksAndMessages(null);
            onDoubleClick(view);
        } else {
            isSingleClick = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSingleClick) {
                        // Single click detected
                        onSingleClick(view);
                    }
                }
            }, DOUBLE_CLICK_TIME_DELTA);
        }
        lastClickTime = clickTime;
    }

    public void onSingleClick(View view) {
        // Implement your single click action here
    }

    public void onDoubleClick(View view) {
        // Implement your double click action here
    }
}

