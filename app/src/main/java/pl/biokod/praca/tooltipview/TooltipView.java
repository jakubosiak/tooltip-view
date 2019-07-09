package pl.biokod.praca.tooltipview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class TooltipView extends View {

    public TooltipView(Context context) {
        super(context);
        initView(null);
    }

    public TooltipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public TooltipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    Paint paintBackgroundStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintBackgroundFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint2 = new Paint();

    private float anchorHeight = getResources().getDimension(R.dimen.TooltipAnchorHeight);
    private float anchorWidth = getResources().getDimension(R.dimen.TooltipAnchorHeight);
    private float textPaddingTop = getResources().getDimension(R.dimen.TooltipAnchorHeight);
    private float textPaddingBottom = getResources().getDimension(R.dimen.TooltipAnchorHeight);
    private float textPaddingEnd = getResources().getDimension(R.dimen.TooltipAnchorHeight);
    private float textPaddingStart = getResources().getDimension(R.dimen.TooltipAnchorHeight);

    void initView(AttributeSet attrs) {
        getDeclaredAttrs(attrs);
        paintBackgroundStroke.setColor(ContextCompat.getColor(getContext(), R.color.gray));
        paintBackgroundStroke.setStrokeCap(Paint.Cap.ROUND);
        paintBackgroundStroke.setStrokeWidth(12f);
        paintBackgroundStroke.setStyle(Paint.Style.STROKE);

        paintBackgroundFill.setColor(ContextCompat.getColor(getContext(), R.color.blue));
        paintBackgroundFill.setStrokeCap(Paint.Cap.ROUND);
        paintBackgroundFill.setStyle(Paint.Style.FILL);

        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(ContextCompat.getColor(getContext(), R.color.red));
        paint2.setTextSize(100f);
    }

    private int clickedViewX, clickedViewY;
    private int clickedViewWidth, clickedViewHeight;

    public void setClickedViewCoords(int x, int y, int width, int height) {
        clickedViewX = x;
        clickedViewY = y;
        clickedViewWidth = width;
        clickedViewHeight = height;
        requestLayout();
        invalidate();
    }

    private void getDeclaredAttrs(AttributeSet attrs) {
        TypedArray customAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.TooltipView);
        anchorHeight = customAttrs.getDimension(R.styleable.TooltipView_anchorHeight, getResources().getDimension(R.dimen.TooltipAnchorHeight));
        anchorWidth = customAttrs.getDimension(R.styleable.TooltipView_anchorWidth, getResources().getDimension(R.dimen.TooltipAnchorHeight));
        customAttrs.recycle();
    }

    Path path = new Path();

    float arcSize = getResources().getDimension(R.dimen.TooltipBackgroundCorner);

    private void drawViewPath(Canvas canvas, StaticLayout textStaticLayout) {
        if (drawAnchorTop) {
            moveTo(path, getClickedViewCenterX(), getClickedViewCenterYBottom());
            addLine(path, 0, anchorHeight);
            addLine(path, 0, textPaddingTop);
            addLine(path, 0, textStaticLayout.getHeight() - arcSize);
            addLine(path, 0, textPaddingBottom);
            addArcClockwise(path, arcSize, ArcPosition.BOTTOM_RIGHT);
            addLine(path, -textPaddingEnd, 0);
            addLine(path, -textStaticLayout.getWidth() + arcSize * 2, 0);
            addLine(path, -textPaddingStart, 0);
            addArcClockwise(path, arcSize, ArcPosition.BOTTOM_LEFT);
            addLine(path, 0, -textPaddingBottom);
            addLine(path, 0, -textStaticLayout.getHeight() + arcSize * 2);
            addLine(path, 0, -textPaddingTop);
            addArcClockwise(path, arcSize, ArcPosition.TOP_LEFT);
            addLine(path, textPaddingStart, 0);
            addLine(path, textStaticLayout.getWidth() - arcSize, 0);
            addLine(path, textPaddingEnd - anchorWidth, 0);
            path.close();
            canvas.drawPath(path, paintBackgroundFill);
            canvas.drawPath(path, paintBackgroundStroke);
        } else {
            moveTo(path, getClickedViewCenterX(), getClickedViewCenterYTop());
            addLine(path, 0, -anchorHeight);
            addLine(path, 0, -textPaddingBottom);
            addLine(path, 0, -textStaticLayout.getHeight() + arcSize);
            addLine(path, 0, -textPaddingTop);
            addArcCounterClockwise(path, arcSize, ArcPosition.TOP_RIGHT);
            addLine(path, -textPaddingEnd, 0);
            addLine(path, -textStaticLayout.getWidth() + arcSize * 2, 0);
            addLine(path, -textPaddingStart, 0);
            addArcCounterClockwise(path, arcSize, ArcPosition.TOP_LEFT);
            addLine(path, 0, textPaddingTop);
            addLine(path, 0, textStaticLayout.getHeight() - arcSize * 2);
            addLine(path, 0, textPaddingBottom);
            addArcCounterClockwise(path, arcSize, ArcPosition.BOTTOM_LEFT);
            addLine(path, textPaddingStart, 0);
            addLine(path, textStaticLayout.getWidth() - arcSize, 0);
            addLine(path, textPaddingEnd - anchorWidth, 0);
            path.close();
            canvas.drawPath(path, paintBackgroundFill);
            canvas.drawPath(path, paintBackgroundStroke);
        }
    }

    private int pathLastX = 0, pathLastY = 0;

    private void moveTo(Path path, int x, int y) {
        path.moveTo(x, y);
        pathLastX = x;
        pathLastY = y;
    }

    private void addLine(Path path, float x, float y) {
        path.lineTo(pathLastX + x, pathLastY + y);
        pathLastX = pathLastX + Math.round(x);
        pathLastY = pathLastY + Math.round(y);
    }

    enum ArcPosition {
        BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT
    }

    private void addArcClockwise(Path path, float arcSize, ArcPosition arcPosition) {
        switch (arcPosition) {
            case BOTTOM_RIGHT:
                RectF rectBR = new RectF(pathLastX - arcSize, pathLastY, pathLastX, pathLastY + arcSize);
                path.arcTo(rectBR, 0, 90);
                pathLastX = pathLastX - Math.round(arcSize);
                pathLastY = pathLastY + Math.round(arcSize);
                break;
            case BOTTOM_LEFT:
                RectF rectBL = new RectF(pathLastX - arcSize, pathLastY - arcSize, pathLastX, pathLastY);
                path.arcTo(rectBL, 90, 90);
                pathLastX = pathLastX - Math.round(arcSize);
                pathLastY = pathLastY - Math.round(arcSize);
                break;
            case TOP_LEFT:
                RectF rectTL = new RectF(pathLastX, pathLastY - arcSize, pathLastX + arcSize, pathLastY);
                path.arcTo(rectTL, 180, 90);
                pathLastX = pathLastX + Math.round(arcSize);
                pathLastY = pathLastY - Math.round(arcSize);
                break;
            case TOP_RIGHT:
                RectF rectTR = new RectF(pathLastX, pathLastY, pathLastX + arcSize, pathLastY + arcSize);
                path.arcTo(rectTR, 270, 90);
                pathLastX = pathLastX + Math.round(arcSize);
                pathLastY = pathLastY + Math.round(arcSize);
                break;
        }
    }


    private void addArcCounterClockwise(Path path, float arcSize, ArcPosition arcPosition) {
        switch (arcPosition) {
            case BOTTOM_RIGHT:
                RectF rectBR = new RectF(pathLastX, pathLastY - arcSize, pathLastX + arcSize, pathLastY);
                path.arcTo(rectBR, 90, -90);
                pathLastX = pathLastX + Math.round(arcSize);
                pathLastY = pathLastY - Math.round(arcSize);
                break;
            case BOTTOM_LEFT:
                RectF rectBL = new RectF(pathLastX, pathLastY, pathLastX + arcSize, pathLastY + arcSize);
                path.arcTo(rectBL, 180, -90);
                pathLastX = pathLastX + Math.round(arcSize);
                pathLastY = pathLastY + Math.round(arcSize);
                break;
            case TOP_LEFT:
                RectF rectTL = new RectF(pathLastX - arcSize, pathLastY, pathLastX, pathLastY + arcSize);
                path.arcTo(rectTL, 270, -90);
                pathLastX = pathLastX - Math.round(arcSize);
                pathLastY = pathLastY + Math.round(arcSize);
                break;
            case TOP_RIGHT:
                RectF rectTR = new RectF(pathLastX - arcSize, pathLastY - arcSize, pathLastX, pathLastY);
                path.arcTo(rectTR, 0, -90);
                pathLastX = pathLastX - Math.round(arcSize);
                pathLastY = pathLastY - Math.round(arcSize);
                break;
        }
    }

    private boolean drawAnchorTop = false;

    private int getClickedViewCenterX() {
        return clickedViewX + clickedViewWidth / 2;
    }

    private int getClickedViewCenterY() {
        return clickedViewY + clickedViewHeight / 2;
    }

    private int getClickedViewCenterYBottom() {
        return getClickedViewCenterY() + clickedViewHeight / 2;
    }

    private int getClickedViewCenterYTop() {
        return getClickedViewCenterY() - clickedViewHeight / 2;
    }

    private int getViewPaddingSide() {
        return Math.abs(getDeviceWidth() - getClickedViewCenterX());
    }

    private int getDeviceWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        StaticLayout staticLayout = createStaticLayoutWithText();
        if (staticLayout == null)
            return;
        drawViewPath(canvas, staticLayout);
        drawStaticLayoutText(canvas, staticLayout);
    }

    private void drawStaticLayoutText(Canvas canvas, StaticLayout textStaticLayout) {
        if (drawAnchorTop)
            canvas.translate(getViewPaddingSide() + textPaddingStart, anchorHeight + getClickedViewCenterYBottom() + textPaddingTop);
        else
            canvas.translate(getViewPaddingSide() + textPaddingStart, getClickedViewCenterYTop() - anchorHeight - textPaddingBottom - textStaticLayout.getHeight());

        textStaticLayout.draw(canvas);
        canvas.save();
    }

    private StaticLayout createStaticLayoutWithText() {
        String text = "It looks like your AMEX account has been locked.\n\nLogin or contact AMEX directly to unlock your account and re-enter your credentials into Wayfarer Points app.";


        TextPaint myTextPaint = new TextPaint();
        myTextPaint.setAntiAlias(true);
        myTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
        myTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.red));

        int width = getDeviceWidth() - getViewPaddingSide() * 2 - Math.round(textPaddingStart) - Math.round(textPaddingEnd);
        if (width < 0)
            return null;

        Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
        float spacingMultiplier = 1;
        float spacingAddition = 0;

        return new StaticLayout(text, myTextPaint, width, alignment, spacingMultiplier, spacingAddition, false);
    }
}