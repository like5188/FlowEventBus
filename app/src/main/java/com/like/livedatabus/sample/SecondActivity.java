package com.like.livedatabus.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.like.livedatabus.LiveDataBus;
import com.like.livedatabus.sample.databinding.ActivitySecondBinding;
import com.like.livedatabus_annotations.BusObserver;

public class SecondActivity extends AppCompatActivity {
    private ActivitySecondBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second);
        LiveDataBus.register(this);
        Log.e("LiveDataBus", "SecondActivity onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveDataBus.unregister(this);
        Log.e("LiveDataBus", "SecondActivity onDestroy");
    }

    public void changeData1(View view) {
        LiveDataBus.post("like1", 100);
    }

    public void changeData2(View view) {
        LiveDataBus.post("like2", new User("name", 18));
    }

    @BusObserver(value = "like1", isSticky = true)
    public void observer1(int i) {
        Log.e("LiveDataBus", "SecondActivity onChanged tag=like1");
        mBinding.tv1.setText(String.valueOf(i));
    }

    @BusObserver(value = "like2", requestCode = "re")
    public void observer2(User u) {
        Log.e("LiveDataBus", "SecondActivity onChanged tag=like2");
        mBinding.tv2.setText(u.toString());
    }

}