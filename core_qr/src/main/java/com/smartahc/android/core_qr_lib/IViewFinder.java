package com.smartahc.android.core_qr_lib;

import android.graphics.Rect;

public interface IViewFinder {
    void setLaserColor(int var1);

    void setMaskColor(int var1);

    void setBorderColor(int var1);

    void setBorderStrokeWidth(int var1);

    void setBorderLineLength(int var1);

    void setLaserEnabled(boolean var1);

    void setBorderCornerRounded(boolean var1);

    void setBorderAlpha(float var1);

    void setBorderCornerRadius(int var1);

    void setViewFinderOffset(int var1);

    void setSquareViewFinder(boolean var1);

    void setupViewFinder();

    Rect getFramingRect();

    int getWidth();

    int getHeight();
}