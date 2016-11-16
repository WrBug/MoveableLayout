package com.wrbug.MoveableLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wrbug on 16-11-15.
 */

public class Layout extends ViewGroup implements View.OnTouchListener {
    private SparseArray<List<Integer>> map;
    private long keyDowndelay = 0;
    private int[] xCache = new int[4];
    int s[];

    public Layout(Context context) {
        super(context);
    }

    public Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Layout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int size = getChildCount();
        int h = 0;
        s = new int[2];
        map = new SparseArray<>();
        getLocationInWindow(s);
        for (int i4 = 0; i4 < size; i4++) {
            View view = getChildAt(i4);
            view.layout(0, h, view.getMeasuredWidth(), h + view.getMeasuredHeight());
            h += view.getMeasuredHeight() + 10;
            view.setOnTouchListener(this);
        }

    }

    @SuppressWarnings("Range")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("aa", "action:" + motionEvent.getAction());

        int dx = (int) (motionEvent.getRawX() - xCache[2]);
        int dy = (int) (motionEvent.getRawY()) - xCache[3] - s[1];
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            view.layout(dx, dy, dx + view.getMeasuredWidth(), dy + view.getMeasuredHeight());
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - keyDowndelay < 200) {
                return false;
            }
            if (view.getId() < 0) {
                view.setId((int) System.currentTimeMillis());
            }
            List<Integer> array = map.get(view.getId(), new LinkedList<Integer>());
            array.addAll(Arrays.asList((int) motionEvent.getRawX(), (int) motionEvent.getRawY() - s[1], (int) motionEvent.getRawX() + view.getMeasuredWidth(), (int) motionEvent.getRawY() + view.getMeasuredHeight() - s[1]));
            map.put(view.getId(), array);
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            view.getLocationInWindow(xCache);
            xCache[2] = (int) motionEvent.getRawX() - xCache[0];
            xCache[3] = (int) motionEvent.getRawY() - xCache[1];
            keyDowndelay = System.currentTimeMillis();
        }
        return true;
    }

}
