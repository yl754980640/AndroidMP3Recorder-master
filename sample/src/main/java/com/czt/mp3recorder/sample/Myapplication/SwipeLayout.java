package com.czt.mp3recorder.sample.Myapplication;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 类名:      SwipeLayout
 * 创建者:    PoplarTang
 * 创建时间:  2016/8/12.
 * 描述：     侧拉删除控件
 */
public class SwipeLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private ViewGroup      mBackLayout;   // 后布局
    private ViewGroup      mFrontLayout; // 前布局
    private int            mHeight;    // 控件的高度
    private int            mWidth;     // 控件的宽度
    private int            mRange;     // 拖拽范围 / 后布局宽度
    boolean isOpen = false; // 控件默认的状态, 关闭
    private Status status = Status.Close;
    public enum Status {
        Open,
        Close,
        Swiping;
    }
    OnSwipeListener onSwipeListener;
    public interface OnSwipeListener {

        void onClose(SwipeLayout layout);
        void onOpen(SwipeLayout layout);

        void onStartOpen(SwipeLayout layout);
    }


    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 1. 创建ViewDragHelper
        mDragHelper = ViewDragHelper.create(this, callback);
    }

    // 2. 重写事件回调
    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        // 返回值, 决定了子控件是否可以拖拽
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        // 返回值, 决定了将要移动到的位置
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if(child == mFrontLayout){
                // 限定前布局的左右边界
                if(left < -mRange){
                    left = -mRange;
                }else if(left > 0){
                    left = 0;
                }
            }else if(child == mBackLayout){
                // 限定后布局的左右边界
                if(left < mWidth - mRange){
                    left = mWidth - mRange;
                }else if(left > mWidth){
                    left = mWidth;
                }

            }

            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            if(changedView == mFrontLayout){
                // 如果前布局位置变化, 传递给后布局
                ViewCompat.offsetLeftAndRight(mBackLayout, dx);
            }else if(changedView == mBackLayout){
                // 如果后布局位置变化, 传递给前布局
                ViewCompat.offsetLeftAndRight(mFrontLayout, dx);
            }

            dispatchChangeEvent();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            if(xvel == 0 && mFrontLayout.getLeft() < -mRange / 2.0f){
                open();
            } else if(xvel < 0){
                open();
            } else {
                close();
            }

        }
    };

    private void dispatchChangeEvent() {

        Status lastStatus = status; // Swiping
        // 更新控件的状态
        status = updateStatus();

        // 状态发生切换的时候, 才需要执行监听回调.
        System.out.println("status: " + status);
        if(lastStatus != status && onSwipeListener != null){
            if(status == Status.Close){
                // 最新状态是关闭状态
                onSwipeListener.onClose(this);
            }else if(status == Status.Open){
                // 最新状态是打开状态
                onSwipeListener.onOpen(this);
            }else {
                // 最新状态是滑动中
                if(lastStatus == Status.Close){
                    onSwipeListener.onStartOpen(this);
                }
            }

        }
    }

    private Status updateStatus() {
        int left = mFrontLayout.getLeft();
        if(left == -mRange){
            return Status.Open;
        }else if(left == 0){
            return Status.Close;
        }
        return Status.Swiping;
    }

    public void open(){
        open(true);
    }

    public void open(boolean isSmooth){
        if(isSmooth){
            // 触发平滑动画
            if(mDragHelper.smoothSlideViewTo(mFrontLayout, -mRange, 0)){
                // true 需要重绘界面 -> drawChild -> child.draw -> computeScroll
                ViewCompat.postInvalidateOnAnimation(this);
            }

        }else {
            isOpen = true;
            layoutContent(isOpen);
        }
    }


    public void close(){
        close(true);
    }

    public void close(boolean isSmooth){
        if(isSmooth){
            // 触发平滑动画
            if(mDragHelper.smoothSlideViewTo(mFrontLayout, 0, 0)){
                // true 需要重绘界面 -> drawChild -> child.draw -> computeScroll
                ViewCompat.postInvalidateOnAnimation(this);
            }

        }else {
            isOpen = false;
            layoutContent(isOpen);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        // 维持动画的继续
        if(mDragHelper.continueSettling(true)){
            // true 需要重绘界面 -> drawChild -> child.draw -> computeScroll
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    // 2. 转交拦截判断, 处理触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBackLayout = (ViewGroup) getChildAt(0);
        mFrontLayout = (ViewGroup) getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 指定子控件位置
        layoutContent(isOpen);
    }

    /**
     * 根据控件的开关状态布局内容
     * @param isOpen
     */
    private void layoutContent(boolean isOpen) {
        // 摆放前布局
        Rect frontRect = computeFrontRect(isOpen);
        mFrontLayout.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);

        // 摆放后布局
        Rect backRect = computeBackRect(frontRect);
        mBackLayout.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);

        // 将指定控件前置
        bringChildToFront(mFrontLayout);
    }

    private Rect computeBackRect(Rect frontRect) {
        int left = frontRect.right;
        return new Rect(left, 0 , left + mRange, 0 + mHeight );
    }

    // 计算前布局矩形区域
    private Rect computeFrontRect(boolean isOpen) {
        int left = 0;
        if(isOpen){
            left = -mRange;
        }
        return new Rect(left, 0,  left + mWidth, 0 + mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        mRange = mBackLayout.getMeasuredWidth();

    }
    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }
}
