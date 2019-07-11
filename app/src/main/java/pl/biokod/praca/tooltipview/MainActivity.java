package pl.biokod.praca.tooltipview;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView rectangle = findViewById(R.id.rectangle);
        final ImageView rectangle2 = findViewById(R.id.rectangle2);
        rectangle.setOnClickListener(this);
        rectangle2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        ConstraintLayout constraintLayout = findViewById(R.id.root);
        int[] locInWindow = new int[2];
        constraintLayout.getLocationInWindow(locInWindow);
        int[] locationsOnScreen = new int[2];
        v.getLocationInWindow(locationsOnScreen);
        int clickedViewX = locationsOnScreen[0];
        int clickedViewY = locationsOnScreen[1] - locInWindow[1];
        TooltipData tooltipData = new TooltipData(clickedViewX, clickedViewY, v.getWidth(), v.getHeight(), v.getPaddingStart(), v.getPaddingTop(), v.getPaddingEnd(), v.getPaddingBottom());
        tooltipData.setIncludeViewPaddingCalculations(true);
        tooltipData.setTooltipText("It looks like your AMEX account has been locked.\n\nLogin or contact AMEX directly to unlock your account ");
        new TooltipDialog.Builder()
                .setupTooltipDialog(tooltipData, false)
                .show(getSupportFragmentManager());
    }
}
