package com.lyt.injectview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyt.injectview.library.InjectManager;
import com.lyt.injectview.library.annotation.ContentView;
import com.lyt.injectview.library.annotation.InjectBean;
import com.lyt.injectview.library.annotation.InjectView;
import com.lyt.injectview.library.annotation.OnClick;
import com.lyt.injectview.library.annotation.OnLongClick;

import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.fragment_test)
public class TestFragment extends BaseFragment{

    @InjectView(R.id.tv_main_test)
    private TextView tv_main_test;
    @InjectBean
    private String name;
    @InjectBean(ArrayList.class)
    private List<String> list;
    @InjectBean
    private TestBean bean;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.tv_main_test,R.id.tv_main_test_2})
    public void onClick(View view){
        Log.e("liyunte","------------------点击了"+view.getId());
    }

    @OnLongClick(R.id.tv_main_test)
    public void onLongClick(View view){
        Log.e("liyunte","------------------长按了");

    }
}
