package com.wrbug.MoveableLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by wrbug on 16-11-15.
 */

public class MoveableLayout extends ViewGroup implements View.OnTouchListener {
    private Map<View, List<Integer>> map;
    private long keyDownDelay = 0;
    private int[] xCache = new int[4];
    int s[];
    private static String TAG = "MoveableLayout";

    public MoveableLayout(Context context) {
        super(context);
    }

    public MoveableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
        map = new HashMap<>();
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
        int dx = (int) (motionEvent.getRawX() - xCache[2]);
        int dy = (int) (motionEvent.getRawY()) - xCache[3] - s[1];
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            view.layout(dx, dy, dx + view.getMeasuredWidth(), dy + view.getMeasuredHeight());
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            List<Integer> array;
            if (map.containsKey(view)) {
                array = map.get(view);
            } else {
                array = new LinkedList<>(Arrays.asList(dx, dy, dx + view.getMeasuredWidth(), dy + view.getMeasuredHeight()));
                map.put(view, array);
            }
            if (dx - array.get(0) < 10 && dy - array.get(1) < 10) {
                try {
                    Field field = View.class.getDeclaredField("mListenerInfo");
                    field.setAccessible(true);
                    Object object = field.get(view);
                    long t = System.currentTimeMillis();
                    if (t - keyDownDelay < 500) {
                        field = object.getClass().getDeclaredField("mOnClickListener");
                        field.setAccessible(true);
                        object = field.get(object);
                        Log.i(TAG, view.getClass() + " : clicked");
                        if (object != null && object instanceof OnClickListener) {
                            ((OnClickListener) object).onClick(view);
                        }
                    } else {
                        field = object.getClass().getDeclaredField("mOnLongClickListener");
                        field.setAccessible(true);
                        object = field.get(object);
                        Log.i(TAG, view.getClass() + " : longclicked");
                        if (object != null && object instanceof OnLongClickListener) {
                            ((OnLongClickListener) object).onLongClick(view);
                        }
                    }
                } catch (Exception e) {

                }
            }

            array.addAll(Arrays.asList(dx, dy, dx + view.getMeasuredWidth(), dy + view.getMeasuredHeight()));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            view.getLocationInWindow(xCache);
            xCache[2] = (int) motionEvent.getRawX() - xCache[0];
            xCache[3] = (int) motionEvent.getRawY() - xCache[1];
            keyDownDelay = System.currentTimeMillis();
        }
        return true;
    }

}
