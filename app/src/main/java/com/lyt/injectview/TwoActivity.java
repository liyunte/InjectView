package com.lyt.injectview;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.lyt.injectview.library.InjectManager;
import com.lyt.injectview.library.annotation.ContentView;
import com.lyt.injectview.library.annotation.InjectBean;
import com.lyt.injectview.library.annotation.InjectView;

@ContentView(R.layout.activity_two)
public class TwoActivity extends AppCompatActivity {
    @InjectBean
    private TestFragment testFragment;


    @InjectView(R.id.frame_test)
    private FrameLayout frame_test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectManager.inject(this);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_test, testFragment).commit();


    }


}
