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
import android.util.Log;
import android.view.View;

public class TooltipView extends View {

    enum ArcPosition {
        BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT
    }

    enum AnchorSide {
        TOP, BOTTOM
    }

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

    private Paint paintBackgroundStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintBackgroundFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private Path backgroundPath = new Path();

    private float backgroundCornerSize;
    private float anchorHeight;
    private float anchorWidth;

    private float textPaddingTop;
    private float textPaddingBottom;
    private float textPaddingEnd;
    private float textPaddingStart;

    private int pathLastX = 0;
    private int pathLastY = 0;

    private AnchorSide anchorSide = AnchorSide.TOP;

    private int clickedViewX = -1;
    private int clickedViewY = -1;
    private int clickedViewWidth;
    private int clickedViewHeight;

    private StaticLayout staticLayout;
    private String text = "";

    public void setTooltipData(TooltipData tooltipData) {
        if (tooltipData == null)
            return;

        clickedViewX = tooltipData.getClickedViewX();
        clickedViewY = tooltipData.getClickedViewY();
        clickedViewWidth = tooltipData.getClickedViewWidth();
        clickedViewHeight = tooltipData.getClickedViewHeight();
        text = tooltipData.getTooltipText();
    }

    public void refresh() {
        staticLayout = createStaticLayoutWithText();
        resolveAnchorSidePlacement();
        requestLayout();
        invalidate();
    }

    public int getYOnScreenPointingTheClickedView() {
        switch (anchorSide) {
            case TOP:
                return getClickedViewCenterYBottom();
            case BOTTOM:
                return getClickedViewCenterYTop() - getViewHeight();
        }
        return 0;
    }

    public AnchorSide getAnchorSide() {
        return anchorSide;
    }

    public float getAnchorHeight() {
        return anchorHeight;
    }

    private void initView(AttributeSet attrs) {
        setVisibility(View.GONE);
        getDeclaredAttrs(attrs);
        setPaintBackgroundStroke();
        setPaintBackgroundFill();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("viewClicked", "true");
            }
        });
    }

    private void getDeclaredAttrs(AttributeSet attrs) {
        TypedArray customAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.TooltipView);
        anchorHeight = customAttrs.getDimension(R.styleable.TooltipView_anchorHeight, getResources().getDimension(R.dimen.TooltipAnchorHeight));
        anchorWidth = customAttrs.getDimension(R.styleable.TooltipView_anchorWidth, getResources().getDimension(R.dimen.TooltipAnchorWidth));
        paintBackgroundStroke.setStrokeWidth(customAttrs.getDimension(R.styleable.TooltipView_strokeWidth, getResources().getDimension(R.dimen.TooltipStrokeWidth)));
        paintBackgroundStroke.setColor(customAttrs.getColor(R.styleable.TooltipView_strokeColor, ContextCompat.getColor(getContext(), R.color.gray)));
        paintBackgroundFill.setColor(customAttrs.getColor(R.styleable.TooltipView_backgroundColor, ContextCompat.getColor(getContext(), R.color.blue)));
        textPaint.setTextSize(customAttrs.getDimension(R.styleable.TooltipView_textSize, getResources().getDimension(R.dimen.TooltipTextSize)));
        textPaint.setColor(customAttrs.getColor(R.styleable.TooltipView_textColor, ContextCompat.getColor(getContext(), R.color.white)));
        backgroundCornerSize = customAttrs.getDimension(R.styleable.TooltipView_backgroundCorner, getResources().getDimension(R.dimen.TooltipBackgroundCorner));
        textPaddingTop = customAttrs.getDimension(R.styleable.TooltipView_textPaddingTop, getResources().getDimension(R.dimen.TooltipTextPaddingTop));
        textPaddingBottom = customAttrs.getDimension(R.styleable.TooltipView_textPaddingBottom, getResources().getDimension(R.dimen.TooltipTextPaddingBottom));
        textPaddingStart = customAttrs.getDimension(R.styleable.TooltipView_textPaddingSide, getResources().getDimension(R.dimen.TooltipTextPaddingSide));
        textPaddingEnd = customAttrs.getDimension(R.styleable.TooltipView_textPaddingSide, getResources().getDimension(R.dimen.TooltipTextPaddingSide));
        customAttrs.recycle();
    }

    private void setPaintBackgroundStroke() {
        paintBackgroundStroke.setStrokeCap(Paint.Cap.ROUND);
        paintBackgroundStroke.setStyle(Paint.Style.STROKE);
    }

    private void setPaintBackgroundFill() {
        paintBackgroundFill.setStrokeCap(Paint.Cap.ROUND);
        paintBackgroundFill.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (hideViewIfDataNotSet()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        setMeasuredDimension(getDeviceWidth() - getViewPaddingSide() * 2 + (getBackgroundStrokeWidth() * 2), getViewHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hideViewIfDataNotSet())
            return;

        drawBackground(canvas, staticLayout);
        drawStaticLayoutText(canvas, staticLayout);
    }

    private boolean hideViewIfDataNotSet() {
        if (clickedViewX == -1) {
            setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private void resolveAnchorSidePlacement() {
        if (getClickedViewCenterY() < getResources().getDisplayMetrics().heightPixels / 2f)
            anchorSide = AnchorSide.TOP;
        else
            anchorSide = AnchorSide.BOTTOM;
    }

    private StaticLayout createStaticLayoutWithText() {
        return new StaticLayout(text, textPaint, getTextStaticLayoutWidth(), Layout.Alignment.ALIGN_CENTER, 1, 0, false);
    }

    private void drawBackground(Canvas canvas, StaticLayout textStaticLayout) {
        switch (anchorSide) {
            case TOP:
                drawBackgroundWithAnchorTop(canvas, textStaticLayout);
                break;
            case BOTTOM:
                drawBackgroundWithAnchorBottom(canvas, textStaticLayout);
                break;
        }
    }

    private void drawStaticLayoutText(Canvas canvas, StaticLayout textStaticLayout) {
        canvas.save();
        translateCanvasToDrawText(canvas);
        textStaticLayout.draw(canvas);
        canvas.restore();
    }

    private void translateCanvasToDrawText(Canvas canvas) {
        switch (anchorSide) {
            case TOP:
                canvas.translate(textPaddingStart, anchorHeight + textPaddingTop);
                break;
            case BOTTOM:
                canvas.translate(textPaddingStart, textPaddingTop);
                break;
        }
    }

    private void drawBackgroundWithAnchorTop(Canvas canvas, StaticLayout textStaticLayout) {
        moveTo(backgroundPath, getCanvasStartPositionX(), getCanvasStartPositionY());
        addLine(backgroundPath, 0, anchorHeight);
        addLine(backgroundPath, 0, textPaddingTop);
        addLine(backgroundPath, 0, textStaticLayout.getHeight() - backgroundCornerSize);
        addLine(backgroundPath, 0, textPaddingBottom);
        addArcClockwise(backgroundPath, backgroundCornerSize, ArcPosition.BOTTOM_RIGHT);
        addLine(backgroundPath, -textPaddingEnd, 0);
        addLine(backgroundPath, -textStaticLayout.getWidth() + backgroundCornerSize * 2, 0);
        addLine(backgroundPath, -textPaddingStart, 0);
        addArcClockwise(backgroundPath, backgroundCornerSize, ArcPosition.BOTTOM_LEFT);
        addLine(backgroundPath, 0, -textPaddingBottom);
        addLine(backgroundPath, 0, -textStaticLayout.getHeight() + backgroundCornerSize * 2);
        addLine(backgroundPath, 0, -textPaddingTop);
        addArcClockwise(backgroundPath, backgroundCornerSize, ArcPosition.TOP_LEFT);
        addLine(backgroundPath, textPaddingStart, 0);
        addLine(backgroundPath, textStaticLayout.getWidth() - backgroundCornerSize - anchorWidth, 0);
        addLine(backgroundPath, textPaddingEnd, 0);
        backgroundPath.close();
        canvas.drawPath(backgroundPath, paintBackgroundFill);
        canvas.drawPath(backgroundPath, paintBackgroundStroke);
    }

    private void drawBackgroundWithAnchorBottom(Canvas canvas, StaticLayout textStaticLayout) {
        moveTo(backgroundPath, getCanvasStartPositionX(), getCanvasStartPositionY());
        addLine(backgroundPath, 0, -anchorHeight);
        addLine(backgroundPath, 0, -textPaddingBottom);
        addLine(backgroundPath, 0, -textStaticLayout.getHeight() + backgroundCornerSize);
        addLine(backgroundPath, 0, -textPaddingTop);
        addArcCounterClockwise(backgroundPath, backgroundCornerSize, ArcPosition.TOP_RIGHT);
        addLine(backgroundPath, -textPaddingEnd, 0);
        addLine(backgroundPath, -textStaticLayout.getWidth() + backgroundCornerSize * 2, 0);
        addLine(backgroundPath, -textPaddingStart, 0);
        addArcCounterClockwise(backgroundPath, backgroundCornerSize, ArcPosition.TOP_LEFT);
        addLine(backgroundPath, 0, textPaddingTop);
        addLine(backgroundPath, 0, textStaticLayout.getHeight() - backgroundCornerSize * 2);
        addLine(backgroundPath, 0, textPaddingBottom);
        addArcCounterClockwise(backgroundPath, backgroundCornerSize, ArcPosition.BOTTOM_LEFT);
        addLine(backgroundPath, textPaddingStart, 0);
        addLine(backgroundPath, textStaticLayout.getWidth() - backgroundCornerSize - anchorWidth, 0);
        addLine(backgroundPath, textPaddingEnd, 0);
        backgroundPath.close();
        canvas.drawPath(backgroundPath, paintBackgroundFill);
        canvas.drawPath(backgroundPath, paintBackgroundStroke);
    }

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


    private int getViewHeight() {
        return (int) (staticLayout.getHeight() + textPaddingTop + textPaddingBottom + anchorHeight + getBackgroundStrokeWidth() * 2);
    }

    private int getTextStaticLayoutWidth() {
        return getDeviceWidth() - getViewPaddingSide() * 2 - Math.round(textPaddingStart) - Math.round(textPaddingEnd);
    }

    private int getCanvasStartPositionX() {
        return getClickedViewCenterX() - getViewPaddingSide() + getBackgroundStrokeWidth();
    }

    private int getCanvasStartPositionY() {
        switch (anchorSide) {
            case TOP:
                return getBackgroundStrokeWidth();
            case BOTTOM:
                return getHeight() - getBackgroundStrokeWidth();
            default:
                return getBackgroundStrokeWidth();
        }
    }

    private int getBackgroundStrokeWidth() {
        return (int) paintBackgroundStroke.getStrokeWidth();
    }

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
        return Math.max(0, getDeviceWidth() - getClickedViewCenterX());
    }

    private int getDeviceWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }
}