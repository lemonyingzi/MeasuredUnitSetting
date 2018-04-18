//package com.example.bluetooth.combinedchart;
//import com.github.mikephil.charting.components.AxisBase;
//import com.github.mikephil.charting.formatter.*;
//
//import android.icu.text.DecimalFormat;
//
//class MyAxisValueFormatter implements IAxisValueFormatter
//{
//
//    private DecimalFormat mFormat;
//
//    public MyAxisValueFormatter() {
//        mFormat = new DecimalFormat("###,###,###,##0.0");
//    }
//
//    @Override
//    public String getFormattedValue(float value, AxisBase axis) {
//        return mFormat.format(value) + "%";
//    }
//
//
//    //小数点后数字个数
//    @Override
//    public int getDecimalDigits() {
//        return 0;
//    }
//}