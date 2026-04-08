package com.team2073.eagleforcescoutingapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * A ViewPager that can optionally block swipe gestures from the Info tab (position 0)
 * to the Auto tab (position 1) when remote start is enabled.
 * Programmatic navigation via {@link #setCurrentItem} is never blocked.
 */
public class NonSwipeableViewPager extends ViewPager {

    private boolean lockInfoToAuto = false;
    private float initialTouchX = 0f;

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * When {@code true} and the current page is 0 (Info), left swipes (towards Auto)
     * are intercepted and discarded. All other navigation is unaffected.
     */
    public void setLockInfoToAuto(boolean lock) {
        this.lockInfoToAuto = lock;
    }

    public boolean isInfoToAutoLocked() {
        return lockInfoToAuto;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (lockInfoToAuto && getCurrentItem() == 0) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                initialTouchX = ev.getX();
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                // Positive delta means swipe right (going back); negative means swipe left (going forward)
                if (ev.getX() < initialTouchX) {
                    // Block the forward swipe from Info → Auto
                    return false;
                }
            }
        }
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (lockInfoToAuto && getCurrentItem() == 0) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                initialTouchX = ev.getX();
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (ev.getX() < initialTouchX) {
                    return false;
                }
            }
        }
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
