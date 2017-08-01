package com.onlylemi.mapview.library.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by patny on 2017-08-01.
 */

public abstract class BaseGraphics {

    /**
     * Represents the position of this graphic
     */
    public PointF position;

    /**
     * Represents the model matrix of this graphic
     */
    protected Matrix mMatrix;

    /**
     * Represents the transformed matrix used to render
     */
    protected Matrix tMatrix;

    /**
     * Called each refresh before drawing
     * @param m transform matrix
     */
    public abstract void update(Matrix m);

    /**
     * Called each refresh after updating
     * @param canvas to draw on
     * @param paint object
     */
    public abstract void draw(final Canvas canvas, Paint paint);

}