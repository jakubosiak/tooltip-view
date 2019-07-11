package pl.biokod.praca.tooltipview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

public class TooltipDialog extends DialogFragment {

    public static final String TAG = "TooltipDialogTag";

    private TooltipLayout tooltipLayout;

    private TooltipData tooltipData;
    private boolean showCloseButton = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.partial_tooltip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
    }

    private void retrieveArguments() {
        Bundle args = getArguments();
        if (args != null) {
            tooltipData = args.getParcelable(Builder.TOOLTIP_DATA_KEY);
            showCloseButton = args.getBoolean(Builder.TOOLTIP_SHOW_CLOSE_BUTTON);
        } else
            throw new RuntimeException("Build dialog with Builder class");
    }

    private void initView() {
        tooltipLayout = (TooltipLayout) getView();
        tooltipLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tooltipLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                tooltipLayout.setTooltipViewDataAndRefresh(TooltipDialog.this, tooltipData, showCloseButton);
                setupDialogStyle();
            }
        });

    }

    private void setupDialogStyle() {
        Window window = getDialog().getWindow();
        if (window == null) {
            dismiss();
            return;
        }

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.TOP);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setupWindowLayoutParams(window);
    }

    private void setupWindowLayoutParams(Window window) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.y = tooltipLayout.getYOnScreenPointingTheClickedView();
        window.setAttributes(layoutParams);
    }

    static class Builder {

        static final String TOOLTIP_DATA_KEY = "tooltip_data";
        static final String TOOLTIP_SHOW_CLOSE_BUTTON = "tooltip_show_close_button_key";

        private TooltipDialog tooltipDialog;

        Builder setupTooltipDialog(TooltipData tooltipData, boolean showCloseButton) {
            tooltipDialog = new TooltipDialog();

            Bundle bundle = new Bundle();
            bundle.putParcelable(TOOLTIP_DATA_KEY, tooltipData);
            bundle.putBoolean(TOOLTIP_SHOW_CLOSE_BUTTON, showCloseButton);
            tooltipDialog.setArguments(bundle);

            return this;
        }

        void show(FragmentManager fragmentManager) {
            tooltipDialog.show(fragmentManager, TooltipDialog.TAG);
        }
    }
}
