package com.lh.sms.client.ui.plug;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.lh.sms.client.R;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @do 列表左划动操作
 * @author liuhua
 * @date 2020/5/24 3:11 PM
 */
public class HorizontalScroll {
    /**
     * @do 封装滚动操作
     * @author liuhua
     * @date 2020/5/24 3:11 PM
     */
    private HorizontalScrollView lastScrollView = null;
    /**
     * @do 绑定listView事件
     * @author liuhua
     * @date 2020/5/24 3:52 PM
     */
    public HorizontalScroll(ListView listView) {
        listView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> closeScroll());
    }

    public HorizontalScrollView initScroll(View view) {
        return initScroll(view,null);
    }
    @SuppressLint("ClickableViewAccessibility")
    public HorizontalScrollView initScroll(View view, View.OnClickListener onClickListener) {
        LinearLayout leftBody = view.findViewById(R.id.left_body);
        leftBody.setOnClickListener(onClickListener);
        //获取屏幕宽度
        int widthPixels = view.getContext().getResources().getDisplayMetrics().widthPixels;
        //设置左容器为屏幕宽度
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(widthPixels, LinearLayout.LayoutParams.WRAP_CONTENT);
        leftBody.setLayoutParams(lp);
        //获取水平划动元素
        HorizontalScrollView horizontalScrollView = view.findViewById(R.id.hor_scroll_view);
        //首次按下时的坐标
        float[] firstX = {0};
        //是否首次
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        horizontalScrollView.setOnTouchListener((v, event) -> {
            if(MotionEvent.ACTION_MOVE==event.getAction()){
                if(atomicBoolean.get()){
                    //首次按下时初始化
                    firstX[0] = event.getX();
                    atomicBoolean.set(false);
                }
                if(lastScrollView==null||lastScrollView == horizontalScrollView){
                    lastScrollView = horizontalScrollView;
                    return false;
                }
                //关闭其它
                lastScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                lastScrollView = horizontalScrollView;
            }else if(MotionEvent.ACTION_UP==event.getAction()){
                atomicBoolean.set(true);
                float x = event.getX() - firstX[0];
                if(x==0){
                    return false;
                }
                float distance = Math.abs(x);
                v.post(()-> {
                    if (distance > 50) {
                        //划动越过50
                        if (x < 0) {
                            horizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                        } else {
                            horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                        }
                    } else {
                        if (x < 0) {
                            horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT);
                        } else {
                            horizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                        }
                    }
                });
            }
            return false;
        });
        return horizontalScrollView;
    }
    /**
     * @do 关闭最后一次滑动
     * @author liuhua
     * @date 2020/5/24 3:50 PM
     */
    public void closeScroll(){
        if(lastScrollView!=null) {
            lastScrollView.fullScroll(ScrollView.FOCUS_LEFT);
        }
    }
}
