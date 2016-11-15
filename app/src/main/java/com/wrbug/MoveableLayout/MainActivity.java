package com.wrbug.MoveableLayout;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wrbug.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.parse("weixin://dl/scan");
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        Layout layout = (Layout) findViewById(R.id.layout);
        Button button = new Button(this);
        button.setText("ssss");
        layout.addView(button);
        TextView textView = new TextView(this);
        textView.setText("sdasdasd");
        layout.addView(textView);
    }

}
