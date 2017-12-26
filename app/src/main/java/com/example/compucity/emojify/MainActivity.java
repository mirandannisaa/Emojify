package com.example.compucity.emojify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;
    private Button goButton;
    private FloatingActionButton msharefab;
    private FloatingActionButton msavefab;
    private FloatingActionButton mclearfab;
    private TextView titleTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView=(ImageView)findViewById(R.id.image_view);
        goButton=(Button)findViewById(R.id.emojify_button);
        msharefab=(FloatingActionButton)findViewById(R.id.share_button);
        mclearfab=(FloatingActionButton)findViewById(R.id.clear_button);
        msavefab=(FloatingActionButton)findViewById(R.id.save_button);
    }
}
