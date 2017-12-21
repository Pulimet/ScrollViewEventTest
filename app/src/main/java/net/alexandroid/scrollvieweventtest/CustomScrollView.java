package net.alexandroid.scrollvieweventtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    private boolean mFling;
    private boolean mScrolled;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnScrollStoppedListener {
        void onScrollStopped();

        void onTouchActionUp();
    }

    private OnScrollStoppedListener onScrollStoppedListener;

    public void setOnScrollStoppedListener(OnScrollStoppedListener listener) {
        onScrollStoppedListener = listener;
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        mFling = true;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        mScrolled = true;

        if (mFling && Math.abs(y - oldy) < 2) {
            mFling = false;
            if (onScrollStoppedListener != null) {
                onScrollStoppedListener.onScrollStopped();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_SCROLL:
                Log.d("WebViewLogs", "ACTION_SCROLL");
                break;
            case MotionEvent.ACTION_UP:
                if (onScrollStoppedListener != null && mScrolled) {
                    Log.d("WebViewLogs", "ACTION_UP, mScrolled: true");
                    mScrolled = false;
                    onScrollStoppedListener.onTouchActionUp();
                }
                break;
        }
        return super.onTouchEvent(event);
    }


}