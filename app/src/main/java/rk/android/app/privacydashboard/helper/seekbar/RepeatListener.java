package rk.android.app.privacydashboard.helper.seekbar;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class RepeatListener implements View.OnTouchListener {

    private final Handler handler = new Handler();

    private final int initialInterval;
    private final int normalInterval;
    private final View.OnClickListener clickListener;
    private View touchedView;

    private final Runnable handlerRunnable = new Runnable() {
        @Override
        public void run() {
            if(touchedView.isEnabled()) {
                handler.postDelayed(this, normalInterval);
                clickListener.onClick(touchedView);
            } else {
                // if the view was disabled by the clickListener, remove the callback
                handler.removeCallbacks(handlerRunnable);
                touchedView.setPressed(false);
                touchedView = null;
            }
        }
    };

    /**
     * @param initialInterval The interval after first click event
     * @param normalInterval The interval after second and subsequent click
     *       events
     * @param clickListener The OnClickListener, that will be called
     *       periodically
     */
    public RepeatListener(int initialInterval, int normalInterval,
                          View.OnClickListener clickListener) {
        if (clickListener == null)
            throw new IllegalArgumentException("null runnable");
        if (initialInterval < 0 || normalInterval < 0)
            throw new IllegalArgumentException("negative interval");

        this.initialInterval = initialInterval;
        this.normalInterval = normalInterval;
        this.clickListener = clickListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(handlerRunnable);
                handler.postDelayed(handlerRunnable, initialInterval);
                touchedView = view;
                touchedView.setPressed(true);
                clickListener.onClick(view);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(handlerRunnable);
                touchedView.setPressed(false);
                touchedView = null;
                return true;
        }

        return false;
    }


}
