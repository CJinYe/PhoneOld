package com.example.administrator.tf_phone.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016-12-28 10:23
 * @des  不可以滚动的ViewPager
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class NoScrollViewPager extends ViewPager {


    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //是否派发
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    //是否消费
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    //是否拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
}
}
