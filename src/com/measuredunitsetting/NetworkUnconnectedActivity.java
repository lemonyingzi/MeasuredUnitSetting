package com.measuredunitsetting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/1/2.
 */

public class NetworkUnconnectedActivity extends  Activity {
    TextView backTv;
    LinearLayout backLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networkunconnected);

        //返回
        backTv=(TextView)findViewById(R.id.backTv);
        backTv.setOnClickListener(bottomListener);
        //返回
        backLl=(LinearLayout)findViewById(R.id.backLl);
        backLl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                ((Activity)NetworkUnconnectedActivity.this).finish();
            }
        });
    }

    /**
     * 顶部返回键
     */
    TextView.OnClickListener bottomListener = new TextView.OnClickListener(){//创建监听对象
        public void onClick(View v)
        {
            int id = v.getId();
            if (id==R.id.backTv || id==R.id.backLl) {
                ((Activity)NetworkUnconnectedActivity.this).finish();
            }
        }
    };
}
