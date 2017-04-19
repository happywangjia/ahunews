package com.example.wangjia.news.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wangjia.news.R;

import java.util.List;

/**
 * Created by wangjia on 2017/4/9.
 */

public class ViewPagerIndicator extends LinearLayout {
    private static final int COLOR_TEXT_NORMAL = 0x77ffffff;
    private static final int COLOR_TEXT_HIGHLIGHT=0xffffffff;
    private ViewPager mViewPager;

    private Paint mPaint;
    private Path mPath;
    private int mTriangleWidth;
    private int mTriangleHeight;

    private static final float RADIO_TRIANGLE_WIDTH=1/6F;
    private int mInitTranslationX;
    private int mTranslationX;

    private int mTablVisibleCount;
    private static final int COUNT_DEFAULT_TAB=4;

    private List<String> mTitles;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth= (int) (w/mTablVisibleCount*RADIO_TRIANGLE_WIDTH);
        mInitTranslationX=w/mTablVisibleCount/2-mTriangleWidth/2;
        initTriangle();

    }

    //指示器跟随手指进行滚动
    public void scroll(int position,float offset){
        int tabWidth=getWidth()/mTablVisibleCount;
        mTranslationX= (int) (tabWidth*(offset+position));
        //容器移动，在tab处于移动至最后一个时
        if(position>=(mTablVisibleCount-1)&&offset>0&&getChildCount()>mTablVisibleCount){
            if(mTablVisibleCount!=1){
                this.scrollTo((position-(mTablVisibleCount-1))*tabWidth+(int)(tabWidth*offset),0);
            }else{
                this.scroll(position*tabWidth+(int)(tabWidth*offset),0);
            }
        }
        invalidate();
    }

    //初始化三角形
    private void initTriangle() {
        mTriangleHeight=mTriangleWidth/2;
        mPath=new Path();
        mPath.moveTo(0,0);
        mPath.lineTo(mTriangleWidth,0);
        mPath.lineTo(mTriangleWidth/2,-mTriangleHeight);
        mPath.close();

    }

    public ViewPagerIndicator(Context context) {
        this(context,null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获取可见Tab数量
        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTablVisibleCount=a.getInt(R.styleable.ViewPagerIndicator_visible_tab_count,COUNT_DEFAULT_TAB);
        if(mTablVisibleCount<0){
            mTablVisibleCount=COUNT_DEFAULT_TAB;
        }

        a.recycle();


        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount=getChildCount();
        if(cCount==0) return;
        for(int i=0;i<cCount;i++){
            View view=getChildAt(i);
            LinearLayout.LayoutParams lp= (LayoutParams) view.getLayoutParams();
            lp.weight=0;
            lp.width=getScreenWidth()/mTablVisibleCount;
            view.setLayoutParams(lp);
        }
        setItemClickEvent();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslationX+mTranslationX,getHeight()+2);
        canvas.drawPath(mPath,mPaint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    //获得屏幕宽度
    public int getScreenWidth() {
        WindowManager wm= (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

//--------------动态创建tab-----------------------------
    public void setTabItemTitles(List<String> titles){
        if(titles!=null&&titles.size()>0){
            this.removeAllViews();
            mTitles=titles;
            for(String title:mTitles){
                addView(generationTextView(title));
            }
            setItemClickEvent();
        }
    }

    //根据title创建Tab
    private View generationTextView(String title) {
        TextView tv=new TextView(getContext());
        LinearLayout.LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width=getScreenWidth()/mTablVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        return tv;
    }


    //设置可见tab数量
    public void setVisibleTabCount(int count){
        mTablVisibleCount=count;
    }
//--------------------------------------------
    //高亮某个Tab
    private void highLightTextView(int pos){
        resetTextViewColor();
        View view=getChildAt(pos);
        if(view instanceof TextView){
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }
    //重制Tab文本颜色
    private void resetTextViewColor(){
        for(int i=0;i<getChildCount();i++){
            View view=getChildAt(i);
            if(view instanceof TextView){
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }
    //设置Tab的点击事件
    private void setItemClickEvent(){
        int cCount=getChildCount();
        for(int i=0;i<cCount;i++){
            final int j=i;
            View view=getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }


    public void setViewPager(ViewPager viewPager, final int pos){
        mViewPager=viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //tabWidth*positionOffset+position*tabWidth;
                scroll(position,positionOffset);
                if(mListener!=null){
                    mListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(mListener!=null){
                    mListener.onPageSelected(position);
                }
                highLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mListener!=null){
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setCurrentItem(pos);
        highLightTextView(pos);

    }

    public interface PageOnchangeListener{
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);


        public void onPageSelected(int position);


        public void onPageScrollStateChanged(int state);
    }
    public PageOnchangeListener mListener;
    public void setOnPageChangListener(PageOnchangeListener listener){
        this.mListener=listener;
    }


}
