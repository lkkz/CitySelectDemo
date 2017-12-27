package com.cool.cityselectdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cool.selectlibrary.CitySelect;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainer = (LinearLayout) findViewById(R.id.ll_container);
    }

    public void dialog(View view){
        new CitySelect(this)
                .setMainColor(Color.RED)
                .listener(new CitySelect.OnSelectListener() {
                    @Override
                    public void onSelect(String province, String city, String area) {
                        Log.e("399",province + "  " + city + "  " + area);
                        Toast.makeText(MainActivity.this,province + "  " + city + "  " + area,Toast.LENGTH_SHORT).show();
                    }
                }).dialog()
                .show();
    }

    public void layout(View view){
        if(mContainer.getChildCount() != 0){
            mContainer.removeAllViews();
        }
        CitySelect citySelect = new CitySelect(this)
                .setMainColor(Color.RED);
        citySelect.listener(new CitySelect.OnSelectListener() {
            @Override
            public void onSelect(String province, String city, String area) {
                Log.e("399",province + "  " + city + "  " + area);
                Toast.makeText(MainActivity.this,province + "  " + city + "  " + area,Toast.LENGTH_SHORT).show();
            }
        });
        mContainer.addView(citySelect.getView());
    }
}
