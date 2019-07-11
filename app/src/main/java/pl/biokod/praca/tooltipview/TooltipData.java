package pl.biokod.praca.tooltipview;

import android.os.Parcel;
import android.os.Parcelable;

public class TooltipData implements Parcelable {

    private int clickedViewX;
    private int clickedViewY;
    private int clickedViewWidth;
    private int clickedViewHeight;

    private int clickedViewPaddingStart;
    private int clickedViewPaddingTop;
    private int clickedViewPaddingEnd;
    private int clickedViewPaddingBottom;

    private String tooltipText;

    private boolean includeViewPaddingCalculations = false;

    public TooltipData(int clickedViewX,
                       int clickedViewY,
                       int clickedViewWidth,
                       int clickedViewHeight,
                       int clickedViewPaddingStart,
                       int clickedViewPaddingTop,
                       int clickedViewPaddingEnd,
                       int clickedViewPaddingBottom) {
        this.clickedViewX = clickedViewX;
        this.clickedViewY = clickedViewY;
        this.clickedViewWidth = clickedViewWidth;
        this.clickedViewHeight = clickedViewHeight;
        this.clickedViewPaddingStart = clickedViewPaddingStart;
        this.clickedViewPaddingTop = clickedViewPaddingTop;
        this.clickedViewPaddingEnd = clickedViewPaddingEnd;
        this.clickedViewPaddingBottom = clickedViewPaddingBottom;
    }

    protected TooltipData(Parcel in) {
        clickedViewX = in.readInt();
        clickedViewY = in.readInt();
        clickedViewWidth = in.readInt();
        clickedViewHeight = in.readInt();
        clickedViewPaddingStart = in.readInt();
        clickedViewPaddingTop = in.readInt();
        clickedViewPaddingEnd = in.readInt();
        clickedViewPaddingBottom = in.readInt();
        tooltipText = in.readString();
        includeViewPaddingCalculations = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(clickedViewX);
        dest.writeInt(clickedViewY);
        dest.writeInt(clickedViewWidth);
        dest.writeInt(clickedViewHeight);
        dest.writeInt(clickedViewPaddingStart);
        dest.writeInt(clickedViewPaddingTop);
        dest.writeInt(clickedViewPaddingEnd);
        dest.writeInt(clickedViewPaddingBottom);
        dest.writeString(tooltipText);
        dest.writeByte((byte) (includeViewPaddingCalculations ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TooltipData> CREATOR = new Creator<TooltipData>() {
        @Override
        public TooltipData createFromParcel(Parcel in) {
            return new TooltipData(in);
        }

        @Override
        public TooltipData[] newArray(int size) {
            return new TooltipData[size];
        }
    };

    public void setIncludeViewPaddingCalculations(boolean includeViewPaddingCalculations) {
        this.includeViewPaddingCalculations = includeViewPaddingCalculations;
    }

    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }

    public int getClickedViewX() {
        if (includeViewPaddingCalculations)
            return clickedViewX;
        else
            return clickedViewX + clickedViewPaddingStart;
    }

    public int getClickedViewY() {
        if (includeViewPaddingCalculations)
            return clickedViewY;
        else
            return clickedViewY + clickedViewPaddingTop;
    }

    public int getClickedViewWidth() {
        if (includeViewPaddingCalculations)
            return clickedViewWidth;
        else
            return clickedViewWidth - clickedViewPaddingStart - clickedViewPaddingEnd;
    }

    public int getClickedViewHeight() {
        if (includeViewPaddingCalculations)
            return clickedViewHeight;
        else
            return clickedViewHeight - clickedViewPaddingTop - clickedViewPaddingBottom;
    }

    public String getTooltipText() {
        return tooltipText;
    }
}
