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
    int windowInfo[];
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
        windowInfo = new int[2];
        map = new HashMap<>();
        getLocationInWindow(windowInfo);
        LayoutParams params;
        for (int i4 = 0; i4 < size; i4++) {
            View view = getChildAt(i4);
            params = (LayoutParams) view.getLayoutParams();
            view.layout(params.leftMargin, params.topMargin + h, view.getMeasuredWidth() + params.leftMargin, h + view.getMeasuredHeight() + params.topMargin);
            h += view.getMeasuredHeight() + 10;
            view.setOnTouchListener(this);
        }

    }

    @SuppressWarnings("Range")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int dx = (int) (motionEvent.getRawX() - xCache[2]);
        int dy = (int) (motionEvent.getRawY()) - xCache[3] - windowInfo[1];
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
                    boolean longClickReturn = false;
                    if (t - keyDownDelay > 500) {
                        field = object.getClass().getDeclaredField("mOnLongClickListener");
                        field.setAccessible(true);
                        Object longClickListener = field.get(object);
                        if (longClickListener != null && longClickListener instanceof OnLongClickListener) {
                            longClickReturn = ((OnLongClickListener) longClickListener).onLongClick(view);
                        }
                    }
                    if (!longClickReturn) {
                        field = object.getClass().getDeclaredField("mOnClickListener");
                        field.setAccessible(true);
                        Object clickListener = field.get(object);
                        if (clickListener != null && clickListener instanceof OnClickListener) {
                            ((OnClickListener) clickListener).onClick(view);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(
            AttributeSet attrs) {
        return new MoveableLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(
            android.view.ViewGroup.LayoutParams p) {
        return new MoveableLayout.LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof MoveableLayout.LayoutParams;
    }
}
