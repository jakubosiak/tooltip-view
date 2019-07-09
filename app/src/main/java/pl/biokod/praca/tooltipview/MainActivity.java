package pl.biokod.praca.tooltipview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.rectangle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TooltipView) findViewById(R.id.toolTip)).setClickedViewCoords(Math.round(v.getX()), Math.round(v.getY()), v.getWidth(), v.getHeight());
            }
        });
    }
}
