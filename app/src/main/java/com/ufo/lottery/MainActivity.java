package com.ufo.lottery;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ufo.lottery.utils.BmFactory;
import com.ufo.lottery.view.SlotView;
import com.ufo.lottery.view.ISlotStopListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SlotView slotView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BmFactory.init(this);
        slotView = findViewById(R.id.slot_main);
        Button btnGo = findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slotView.startRun();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slotView.endRun(new Random().nextInt(16) - 1);
                    }
                }, 5000);
            }
        });
    }
}
