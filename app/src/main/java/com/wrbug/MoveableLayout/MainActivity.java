package com.wrbug.MoveableLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("aa", "click");
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.i("aa", "longclick");
                return false;
            }
        });
        MoveableLayout moveableLayout = (MoveableLayout) findViewById(R.id.layout);
        Button button = new Button(this);
        button.setText("ssss");
        moveableLayout.addView(button);
        TextView textView = new TextView(this);
        textView.setText("sdasdasd");
        moveableLayout.addView(textView);
    }

}
