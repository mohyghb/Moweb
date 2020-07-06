package com.moofficial.moweb.MoTouchPosition;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

// tracks the position of touch on view
// it does not consume the touch
public class MoTouchPosition {

    private View view;
    private Runnable onTouchEvent, onLongClickListener;
    private Point point;

    public MoTouchPosition(View v){
        this.view = v;
        init();
    }

    public MoTouchPosition setTouchEvent(Runnable r){
        this.onTouchEvent = r;
        return this;
    }

    public MoTouchPosition setLongClickListener(Runnable r){
        this.onLongClickListener = r;
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(){
        this.view.setOnTouchListener((view, motionEvent) -> {
            point = new Point((int) motionEvent.getX(),(int) motionEvent.getY());
            if(onTouchEvent!=null)
                onTouchEvent.run();
            return false;
        });
        this.view.setOnLongClickListener(view -> {
            if(onLongClickListener!=null)
                onLongClickListener.run();
            return false;
        });
    }

    public int getX(){
        return this.point.x;
    }

    public int getY(){
        return this.point.y;
    }

}
