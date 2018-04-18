//package com.example.bluetooth.combinedchart;
//
//import com.github.mikephil.charting.charts.CombinedChart;
//import com.github.mikephil.charting.components.YAxis;
//
//import android.content.Context;
//import android.util.AttributeSet;
//
//public class MyCombinedChart extends CombinedChart {
//	public MyCombinedChart(Context context) {
//        super(context);
//    }
//
//    public MyCombinedChart(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public MyCombinedChart(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//    }
//
//    @Override
//    public YAxis getAxisRight() {
//        return super.getAxisRight();
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//
//        mAxisLeft = new YAxis(YAxis.AxisDependency.LEFT);
//        mAxisRight = new MyYAxis(MyYAxis.AxisDependency.RIGHT);
//
//        mLeftAxisTransformer = new Transformer(mViewPortHandler);
//        mRightAxisTransformer = new Transformer(mViewPortHandler);
//
//        mAxisRendererLeft = new YAxisRenderer(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
//        mAxisRendererRight = new YAxisRenderer(mViewPortHandler, mAxisRight, mRightAxisTransformer);
//
//        mXAxisRenderer = new XAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
//
//        setHighlighter(new ChartHighlighter(this));
//
//        mChartTouchListener = new BarLineChartTouchListener(this, mViewPortHandler.getMatrixTouch(), 3f);
//
//        mGridBackgroundPaint = new Paint();
//        mGridBackgroundPaint.setStyle(Paint.Style.FILL);
//        // mGridBackgroundPaint.setColor(Color.WHITE);
//        mGridBackgroundPaint.setColor(Color.rgb(240, 240, 240)); // light
//        // grey
//
//        mBorderPaint = new Paint();
//        mBorderPaint.setStyle(Paint.Style.STROKE);
//        mBorderPaint.setColor(Color.BLACK);
//        mBorderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));
//    }
//}
