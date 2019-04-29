package com.lyt.injectview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyt.injectview.library.InjectManager;
import com.lyt.injectview.library.annotation.ContentView;
import com.lyt.injectview.library.annotation.InjectBean;
import com.lyt.injectview.library.annotation.InjectView;
import com.lyt.injectview.library.annotation.OnClick;
import com.lyt.injectview.library.annotation.OnLongClick;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectManager.inject(this);

    }

    @OnClick(R.id.tv_main_test)
    public void onClick(View v){
        startActivity(new Intent(this,TwoActivity.class));
    }


}
