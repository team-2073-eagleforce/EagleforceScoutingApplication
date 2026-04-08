package com.team2073.eagleforcescoutingapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * A ViewPager that can optionally block all swipe gestures when on the Info tab (position 0)
 * and remote start is enabled.  Since position 0 has no page to its left, locking swiping on
 * page 0 is equivalent to locking the Info→Auto transition specifically.
 * Programmatic navigation via {@link #setCurrentItem} is never blocked.
 */
public class NonSwipeableViewPager extends ViewPager {

    private boolean lockInfoToAuto = false;

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * When {@code true} and the current page is 0 (Info), all swipe gestures are blocked.
     * Tab-bar clicks are unaffected and can still navigate to any page.
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
            // Return false: do not intercept touch events so ViewPager never starts scrolling.
            // ACTION_DOWN is still forwarded to super so ViewPager resets its internal state.
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    super.onInterceptTouchEvent(ev);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return false;
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
            // Return true to consume the event without scrolling, preventing
            // the gesture from reaching any other handler.
            return true;
        }
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
