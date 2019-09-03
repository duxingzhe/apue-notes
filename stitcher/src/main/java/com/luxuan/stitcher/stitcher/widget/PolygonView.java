package com.luxuan.stitcher.stitcher.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.luxuan.stitcher.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolygonView extends FrameLayout {

    private Context mContext;
    private Paint paint;
    private ImageView pointer1;
    private ImageView pointer2;
    private ImageView pointer3;
    private ImageView pointer4;
    private ImageView midPointer13;
    private ImageView midPointer12;
    private ImageView midPointer34;
    private ImageView midPointer24;

    public PolygonView(Context context){
        super(context);
        mContext=context;
        init();
    }

    public PolygonView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext=context;
        init();
    }

    public PolygonView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }

    private void init(){
        pointer1=getImageView(0,0);
        pointer2=getImageView(getWidth(), 0);
        pointer3=getImageView(0, getHeight());
        pointer4=getImageView(getWidth(), getHeight());

        midPointer13=getImageView(0, getHeight()/2);
        midPointer13.setOnTouchListener(new MidPointTouchListenerImpl(pointer1, pointer3));

        midPointer12=getImageView(getWidth()/2, 0);
        midPointer12.setOnTouchListener(new MidPointTouchListenerImpl(pointer1, pointer2));

        midPointer34=getImageView(getWidth()/2, getHeight());
        midPointer34.setOnTouchListener(new MidPointTouchListenerImpl(pointer3, pointer4));

        midPointer24=getImageView(getWidth(), getHeight()/2);
        midPointer24.setOnTouchListener(new MidPointTouchListenerImpl(pointer2, pointer4));

        addView(pointer1);
        addView(pointer2);
        addView(midPointer13);
        addView(midPointer12);
        addView(midPointer34);
        addView(midPointer24);
        addView(pointer3);
        addView(pointer4);

        initPaint();
    }

    @Override
    public void attachViewToParent(View child, int index, ViewGroup.LayoutParams params){
        super.attachViewToParent(child, index, params);
    }

    private void initPaint(){
        paint=new Paint();
        paint.setColor(getResources().getColor(R.color.blue));
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
    }

    public Map<Integer, PointF> getPoints(){
        List<PointF> points=new ArrayList<>();
        points.add(new PointF(pointer1.getX(), pointer1.getY()));
        points.add(new PointF(pointer2.getX(), pointer2.getY()));
        points.add(new PointF(pointer3.getX(), pointer3.getY()));
        points.add(new PointF(pointer4.getX(), pointer4.getY()));

        return getOrderedPoints(points);
    }

    public Map<Integer, PointF> getOrderedPoints(List<PointF> points){
        PointF centerPoint=new PointF();
        int size=points.size();
        for(PointF pointF: points){
            centerPoint.x+=pointF.x/size;
            centerPoint.y+=pointF.y/size;
        }

        Map<Integer, PointF> orderedPoints=new HashMap<>();
        for(PointF pointF: points){
            int index=-1;
            if(pointF.x<centerPoint.x&&pointF.y<centerPoint.y){
                index=0;
            }else if(pointF.x>centerPoint.x&&pointF.y<centerPoint.y){
                index=1;
            }else if(pointF.x<centerPoint.x&&pointF.y>centerPoint.y){
                index=2;
            }else if(pointF.x>centerPoint.x&&pointF.y>centerPoint.y){
                index=3;
            }

            orderedPoints.put(index, pointF);
        }

        return orderedPoints;
    }

    public void setPoints(Map<Integer, PointF> pointFMap){
        if(pointFMap.size()==4){
            setPointsCoordinates(pointFMap);
        }
    }

    private void setPointsCoordinates(Map<Integer, PointF> pointFMap){
        pointer1.setX(pointFMap.get(0).x);
        pointer1.setY(pointFMap.get(0).y);

        pointer2.setX(pointFMap.get(1).x);
        pointer2.setY(pointFMap.get(1).y);

        pointer3.setX(pointFMap.get(2).x);
        pointer3.setY(pointFMap.get(2).y);

        pointer4.setX(pointFMap.get(3).x);
        pointer4.setY(pointFMap.get(3).y);
    }

    @Override
    public void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
        canvas.drawLine(pointer1.getX()+(pointer1.getWidth()/2), pointer1.getY()+(pointer1.getHeight()/2), pointer3.getX()+(pointer3.getWidth()/2), pointer3.getY()+(pointer3.getHeight()/2), paint);
        canvas.drawLine(pointer1.getX()+(pointer1.getWidth()/2), pointer1.getY()+(pointer1.getHeight()/2), pointer2.getX()+(pointer2.getWidth()/2), pointer2.getY()+(pointer2.getHeight()/2), paint);
        canvas.drawLine(pointer2.getX()+(pointer2.getWidth()/2), pointer2.getY()+(pointer2.getHeight()/2), pointer4.getX()+(pointer4.getWidth()/2), pointer4.getY()+(pointer4.getHeight()/2), paint);
        canvas.drawLine(pointer3.getX()+(pointer3.getWidth()/2), pointer3.getY()+(pointer3.getHeight()/2), pointer4.getX()+(pointer4.getWidth()/2), pointer4.getY()+(pointer4.getHeight()/2), paint);

        midPointer13.setX(pointer3.getX()-((pointer3.getX()-pointer1.getX())/2));
        midPointer13.setY(pointer3.getY()-((pointer3.getY()-pointer1.getY())/2));
        midPointer24.setX(pointer4.getX()-((pointer4.getX()-pointer2.getY())/2));
        midPointer24.setY(pointer4.getY()-((pointer4.getY()-pointer2.getY())/2));
        midPointer34.setX(pointer4.getX()-((pointer4.getX()-pointer3.getY())/2));
        midPointer34.setY(pointer4.getY()-((pointer4.getY()-pointer3.getY())/2));
        midPointer12.setX(pointer2.getX()-((pointer2.getX()-pointer1.getY())/2));
        midPointer12.setY(pointer2.getY()-((pointer2.getY()-pointer1.getY())/2));
    }

    private ImageView getImageView(int x, int y){
        ImageView imageView=new ImageView(mContext);
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(R.drawable.circle);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setOnTouchListener(new TouchListenerImpl());
        return imageView;
    }

    public class MidPointTouchListenerImpl implements OnTouchListener {

        PointF DownPT=new PointF();
        PointF StartPT=new PointF();

        private ImageView mainPointer1;
        private ImageView mainPointer2;

        public MidPointTouchListenerImpl(ImageView mainPointer1, ImageView mainPointer2){
            this.mainPointer1=mainPointer1;
            this.mainPointer2=mainPointer2;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event){
            int eid=event.getAction();
            switch(eid){
                case MotionEvent.ACTION_MOVE:
                    PointF motionEvent=new PointF(event.getX()-DownPT.x, event.getY()-DownPT.y);

                    if(Math.abs(mainPointer1.getX()-mainPointer2.getX())> Math.abs(mainPointer1.getY()-mainPointer2.getY())){
                        if(((mainPointer2.getY()+motionEvent.y+view.getHeight()<getHeight())&&(mainPointer2.getY()+motionEvent.y>0))){
                            view.setX((int)(StartPT.y+motionEvent.y));
                            StartPT=new PointF(view.getX(), view.getY());
                            mainPointer2.setY((int)(mainPointer2.getY()+motionEvent.y));
                        }
                        if(((mainPointer1.getY()+motionEvent.y+view.getHeight()<getHeight())&&(mainPointer1.getY()+motionEvent.y>0))){
                            view.setX((int)(StartPT.y+motionEvent.y));
                            StartPT=new PointF(view.getX(), view.getY());
                            mainPointer1.setY((int)(mainPointer1.getY()+motionEvent.y));
                        }
                    }else{
                        if(((mainPointer2.getY()+motionEvent.y+view.getWidth()<getWidth())&&(mainPointer2.getX()+motionEvent.x>0))){
                            view.setX((int)(StartPT.x+motionEvent.x));
                            StartPT=new PointF(view.getX(), view.getY());
                            mainPointer2.setX((int)(mainPointer2.getX()+motionEvent.x));
                        }
                        if(((mainPointer1.getX()+motionEvent.x+view.getWidth()<getWidth())&&(mainPointer1.getX()+motionEvent.x>0))){
                            view.setX((int)(StartPT.x+motionEvent.x));
                            StartPT=new PointF(view.getX(), view.getY());
                            mainPointer1.setY((int)(mainPointer1.getX()+motionEvent.x));
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    DownPT.x=event.getX();
                    DownPT.y=event.getY();
                    StartPT=new PointF(view.getX(), view.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    int color=0;
                    if(isValidShape(getPoints())){
                        color=getResources().getColor(R.color.blue);
                    }else{
                        color=getResources().getColor(R.color.orange);
                    }
                    paint.setColor(color);
                    break;
                default:
                    break;
            }
            invalidate();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return super.onTouchEvent(event);
    }

    public boolean isValidShape(Map<Integer, PointF> pointFMap){
        return pointFMap.size()==4;
    }

    private class TouchListenerImpl implements OnTouchListener{
        PointF DownPT=new PointF();
        PointF StartPT=new PointF();

        @Override
        public boolean onTouch(View view, MotionEvent event){
            int eid=event.getAction();
            switch(eid){
                case MotionEvent.ACTION_MOVE:
                    PointF motionEvent=new PointF(event.getX()-DownPT.x, event.getY()-DownPT.y);
                    if (((StartPT.x + motionEvent.x + view.getWidth()) < getWidth() && (StartPT.y + motionEvent.y + view.getHeight() < getHeight())) && ((StartPT.x + motionEvent.x) > 0 && ((StartPT.x + motionEvent.x) > 0 && StartPT.y + motionEvent.y > 0))) {
                        view.setX((int)(StartPT.x=motionEvent.x));
                        view.setY((int)(StartPT.y+motionEvent.y));
                        StartPT=new PointF(view.getX(), view.getY());
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    DownPT.x=event.getX();
                    DownPT.y=event.getY();
                    StartPT=new PointF(view.getX(), view.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    int color=0;
                    if(isValidShape(getPoints())){
                        color=getResources().getColor(R.color.blue);
                    }else{
                        color=getResources().getColor(R.color.orange);
                    }
                    paint.setColor(color);
                    break;
                default:
                    break;
            }

            invalidate();
            return true;
        }
    }
}
