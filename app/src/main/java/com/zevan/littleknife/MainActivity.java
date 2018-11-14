package com.zevan.littleknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zevan.annotation.FakeActivity;
import com.zevan.annotation.bindView;

@FakeActivity
public class MainActivity extends AppCompatActivity {

    @bindView(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FakeMainActivity.bindView(this);
    }
}
