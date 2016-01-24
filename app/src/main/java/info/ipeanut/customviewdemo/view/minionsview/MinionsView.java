package info.ipeanut.customviewdemo.view.minionsview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
  http://www.tuicool.com/articles/EBfu2m2
 * Created by chenshao on 16/1/22.
 */
public class MinionsView extends View {

    private Paint paint;
    private float bodyWidth;
    private float bodyHeight;
    private static final float BODY_SCALE = 0.6f;//身体主干占整个view的比重
    private static final float BODY_WIDTH_HEIGHT_SCALE = 0.6f; //        身体的比例设定为 w:h = 3:5


    private float mStrokeWidth = 4;//描边宽度
    private int colorStroke = Color.BLACK;


    private float offset;//阴影向上偏移

    private float footHeight;//脚的高度，用来画脚部阴影时用
    private float radius;//身体上下半圆的半径
    private float handsHeight;//计算出吊带的高度时，可以用来做手的高度
    private RectF bodyRect;//不包括脚  和 阴影

    public MinionsView(Context context) {
        this(context,null);
    }

    public MinionsView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MinionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private static final int DEFAULT_SIZE = 200; //View默认大小
    private int widthForUnspecified;
    private int heightForUnspecified;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }
    /**
     * @param origin
     * @param isWidth 是否在测量宽
     * @return
     */
    private int measure(int origin, boolean isWidth) {
        int result ;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        switch (specMode){
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:

                result = specSize;
                if (isWidth){
                    widthForUnspecified = result;
                } else {
                    heightForUnspecified = result;
                }
                break;

            case MeasureSpec.UNSPECIFIED:
            default:
                if (isWidth) {//宽未指定的情况下，可以由高推算出来 - -
                    result = (int) (heightForUnspecified * BODY_WIDTH_HEIGHT_SCALE);
                } else {
                    result = (int) (widthForUnspecified / BODY_WIDTH_HEIGHT_SCALE);
                }
                //如果两边都没指定就用默认值
                if (result == 0) {
                    result = DEFAULT_SIZE;
                }
                break;

        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        initParams();
        initPaint();
        drawFeetShadow(canvas);//脚下的阴影
        drawFeet(canvas);//脚
        drawBody(canvas);
        drawHands(canvas);//手
    }

    private void initParams(){
        bodyWidth = Math.min(getWidth(),(getHeight() * BODY_WIDTH_HEIGHT_SCALE)) * BODY_SCALE;
        bodyHeight = BODY_SCALE * Math.min(getWidth(),getHeight()*BODY_WIDTH_HEIGHT_SCALE) / BODY_WIDTH_HEIGHT_SCALE;
        mStrokeWidth = Math.max(bodyWidth/50 ,mStrokeWidth);

        offset = mStrokeWidth/2;

        //居中
        bodyRect = new RectF();
        bodyRect.left = (getWidth() - bodyWidth)/2;
        bodyRect.top = (getHeight() - bodyHeight)/2;
        bodyRect.right = bodyRect.left + bodyWidth;
        bodyRect.bottom = bodyRect.top + bodyHeight;

        radius = bodyWidth/2;
        footHeight = radius * 0.4333f;

        handsHeight =  (getHeight() + bodyHeight) / 2   + offset - radius * 1.65f ;

    }
    private void initPaint(){
        if (null == paint){
            paint = new Paint();
        } else {
            paint.reset();
        }
        paint.setAntiAlias(true);
    }

    /**
     * 阴影，        //画椭圆
     * @param canvas
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawFeetShadow(Canvas canvas) {
        paint.setColor(getResources().getColor(android.R.color.darker_gray));
        canvas.drawOval(bodyRect.left + bodyWidth * 0.15f, bodyRect.bottom + footHeight - offset
                , bodyRect.right - bodyWidth * 0.15f, bodyRect.bottom + footHeight - offset + mStrokeWidth * 1.3f
                , paint);
    }

    /**
     * 脚，  drawPath
     * @param canvas
     */
    private void drawFeet(Canvas canvas) {
        paint.setStrokeWidth(mStrokeWidth);
        paint.setColor(colorStroke);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        float radiusFoot = radius / 3 * 0.4f;//脚 扇形部分的 半径
        float leftFootStartX = bodyRect.left + radius - offset * 2;
        float leftFootStartY = bodyRect.bottom - offset;
        float footWidthA = radius * 0.5f;//脚宽度大-到半圆结束
        float footWidthB = footWidthA / 3;//脚宽度-比较细的部分
        //      左脚
        Path path = new Path();
        path.moveTo(leftFootStartX, leftFootStartY);
        //下
        path.lineTo(leftFootStartX, leftFootStartY + footHeight);
        //左
        path.lineTo(leftFootStartX - footWidthA + radiusFoot, leftFootStartY + footHeight);
        //扇形
        RectF rectF = new RectF();
        rectF.left = leftFootStartX - footWidthA;
        rectF.top = leftFootStartY + footHeight - radiusFoot * 2;
        rectF.right = rectF.left + radiusFoot * 2;
        rectF.bottom = rectF.top + radiusFoot * 2;
        path.addArc(rectF, 90, 180);
        //右
        path.lineTo(rectF.left + radiusFoot + footWidthB, rectF.top);
        //上
        path.lineTo(rectF.left + radiusFoot + footWidthB, leftFootStartY);
        path.lineTo(leftFootStartX, leftFootStartY);

        canvas.drawPath(path, paint);


//      右脚
        float rightFootStartX = bodyRect.left + radius + offset * 2;
        float rightFootStartY = leftFootStartY;
        path.reset();
        path.moveTo(rightFootStartX, rightFootStartY);
        path.lineTo(rightFootStartX, rightFootStartY + footHeight);
        path.lineTo(rightFootStartX + footWidthA - radiusFoot, rightFootStartY + footHeight);

        rectF.left = rightFootStartX + footWidthA - radiusFoot * 2;
        rectF.top = rightFootStartY + footHeight - radiusFoot * 2;
        rectF.right = rectF.left + radiusFoot * 2;
        rectF.bottom = rectF.top + radiusFoot * 2;
        path.addArc(rectF, 90, -180);
        path.lineTo(rectF.right - radiusFoot - footWidthB, rectF.top);
        path.lineTo(rectF.right - radiusFoot - footWidthB, rightFootStartY);
        path.lineTo(rightFootStartX, rightFootStartY);
        canvas.drawPath(path, paint);


    }
    /**
     * 身体是一个矩形加上，上下半圆，这边只要用一个圆角矩形，然后圆角的弧度半径用身体宽度的一半
     * @param canvas
     */
    private void drawBody(Canvas canvas) {

    }

}
