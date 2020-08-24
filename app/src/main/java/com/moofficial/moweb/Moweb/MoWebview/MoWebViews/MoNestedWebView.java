package com.moofficial.moweb.Moweb.MoWebview.MoWebViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

// todo improve the scrolling effect further
// not for private app usage
class MoNestedWebView extends WebView implements NestedScrollingChild {
//    private int mLastY;
//    private final int[] mScrollOffset = new int[2];
//    private final int[] mScrollConsumed = new int[2];
//    private int mNestedOffsetY;
//    private NestedScrollingChildHelper mChildHelper;


    private int mLastMotionY;

    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];

    private int mNestedYOffset;

    private NestedScrollingChildHelper mChildHelper;

    public MoNestedWebView(Context context) {
        this(context, null);
    }

    public MoNestedWebView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public MoNestedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }




//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        boolean returnValue = false;
//        MotionEvent event = MotionEvent.obtain(ev);
//        ev.getAction();
//        final int action = ev.getActionMasked();
//        if (action == MotionEvent.ACTION_DOWN) {
//            mNestedOffsetY = 0;
//        }
//        int eventY = (int) event.getY();
//        event.offsetLocation(0, mNestedOffsetY);
//        switch (action) {
//            case MotionEvent.ACTION_MOVE:
//                int deltaY = mLastY - eventY;
//                // NestedPreScroll
//                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
//                    deltaY -= mScrollConsumed[1];
//                    mLastY = eventY - mScrollOffset[1];
//                    event.offsetLocation(0, -mScrollOffset[1]);
//                    mNestedOffsetY += mScrollOffset[1];
//                }
//                returnValue = super.onTouchEvent(event);
//
//                // NestedScroll
//                if (dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
//                    event.offsetLocation(0, mScrollOffset[1]);
//                    mNestedOffsetY += mScrollOffset[1];
//                    mLastY -= mScrollOffset[1];
//                }
//                break;
//            case MotionEvent.ACTION_DOWN:
//                returnValue = super.onTouchEvent(event);
//                mLastY = eventY;
//                // start NestedScroll
//                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                returnValue = super.onTouchEvent(event);
//                // end NestedScroll
//                stopNestedScroll();
//                break;
//        }
//        return returnValue;
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;

        MotionEvent trackedEvent = MotionEvent.obtain(event);

        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }

        int y = (int) event.getY();

        event.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastMotionY - y;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                mLastMotionY = y - mScrollOffset[1];

                int oldY = getScrollY();
                int newScrollY = Math.max(0, oldY + deltaY);
                int dyConsumed = newScrollY - oldY;
                int dyUnconsumed = deltaY - dyConsumed;

                if (dispatchNestedScroll(0, dyConsumed, 0, dyUnconsumed, mScrollOffset)) {
                    mLastMotionY -= mScrollOffset[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                result = super.onTouchEvent(trackedEvent);
                trackedEvent.recycle();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
        }
        return result;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
