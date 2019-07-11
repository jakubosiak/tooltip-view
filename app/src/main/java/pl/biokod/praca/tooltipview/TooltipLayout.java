package pl.biokod.praca.tooltipview;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class TooltipLayout extends ConstraintLayout implements View.OnClickListener {

    public TooltipLayout(Context context) {
        super(context);
    }

    public TooltipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TooltipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private TooltipDialog tooltipDialog;

    private TooltipView tooltipView;
    private ImageView closeIMV;

    private boolean showCloseButton;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
    }

    @Override
    public void onClick(View v) {
        if (v == this || v == closeIMV) {
            if (tooltipDialog != null)
                tooltipDialog.dismiss();
        }
    }

    private void initView() {
        tooltipView = findViewById(R.id.toolTip);
        closeIMV = findViewById(R.id.closeIMV);
        closeIMV.setOnClickListener(this);
        this.setOnClickListener(this);
    }

    public int getYOnScreenPointingTheClickedView() {
        return tooltipView.getYOnScreenPointingTheClickedView();
    }

    public void setTooltipViewDataAndRefresh(TooltipDialog tooltipDialog, final TooltipData tooltipData, boolean showCloseButton) {
        this.tooltipDialog = tooltipDialog;
        this.showCloseButton = showCloseButton;

        tooltipView.setTooltipData(tooltipData);
        tooltipView.refresh();
        resolveCloseButtonPlacement();
        tooltipView.setVisibility(View.VISIBLE);
    }

    private void resolveCloseButtonPlacement() {
        if (!showCloseButton) {
            closeIMV.setVisibility(View.GONE);
            return;
        }

        switch (tooltipView.getAnchorSide()) {
            case TOP:
                MarginLayoutParams layoutParams = (MarginLayoutParams) closeIMV.getLayoutParams();
                layoutParams.topMargin = layoutParams.topMargin + (int) tooltipView.getAnchorHeight();
                closeIMV.setLayoutParams(layoutParams);
                break;
            case BOTTOM:
                break;
        }
    }
}
