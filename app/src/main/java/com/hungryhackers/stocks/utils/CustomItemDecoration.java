package com.hungryhackers.stocks.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

/**
 * Created by YourFather on 29-01-2018.
 */

public class CustomItemDecoration extends DividerItemDecoration {

    private final Paint mPaint;

    public CustomItemDecoration(Context context, int orientation, int color) {
        super(context, orientation);
        mPaint = new Paint();
        mPaint.setColor(color);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        c.drawPaint(mPaint);
        super.onDraw(c, parent, state);
    }


}
