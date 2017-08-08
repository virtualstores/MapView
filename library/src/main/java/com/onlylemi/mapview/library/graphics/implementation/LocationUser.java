package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.BuildConfig;
import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.IBaseAnimation;
import com.onlylemi.mapview.library.graphics.implementation.Animations.RotationAnimation;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patny on 2017-07-05.
 */

public class LocationUser extends BaseGraphics {

    //Graphics
    private Bitmap bmp;

    //Rotation - in degrees
    private PointF startDir;

    //Middle position of this graphic in world space
    private PointF worldMidPosition;

    private float radius;
    private float maxRadius;
    private float minRadius;

    //Animation objects
    //We can only rotate and translate
    private IBaseAnimation rotationAnim = null;
    private IBaseAnimation translationAnim = null;

    //Assumes the bmp looks to the right by default
    public LocationUser(Bitmap bmp, PointF position, PointF lookAt) {
        this(bmp, position, new PointF(1, 0), lookAt);
        mMatrix = new Matrix();
        tMatrix = new Matrix();
    }

    /**
     * Direction must be normalized otherwise unexpected results may occur
     * @param bmp bitmap to render
     * @param position start position
     * @param startDir Direction the bmp starts at (normalized direction (Using point as a vector here))
     * @param lookAt Look at direciton, start rotation of bmp (normalized direction (Using point as a vector here))
     */
    public LocationUser(Bitmap bmp, PointF position, PointF startDir, PointF lookAt) {
        this.bmp = bmp;
        this.position = position;
        this.startDir = new PointF(-1.0f, 0.0f);
        this.setLookAt(lookAt);
        this.worldMidPosition = new PointF(bmp.getWidth() / 2, bmp.getHeight() / 2);
        this.radius = bmp.getHeight() > bmp.getWidth() ? bmp.getHeight() / 2 : bmp.getWidth() / 2;
        this.minRadius = radius / 2;
        this.maxRadius = radius * 1.3f;
    }

    public void update(final Matrix m, long deltaTime) {
        worldMidPosition = MapMath.transformPoint(m, position);

        tMatrix.set(mMatrix);
        
        //Handle rotation first
        if(rotationAnim != null && !rotationAnim.isDone()) {
            rotationAnim.update(tMatrix, deltaTime);
        }
        else {
            rotationAnim = null;
            tMatrix.preRotate(this.rotation, bmp.getWidth() / 2, bmp.getHeight() / 2);
        }
        //Translation last
        if(translationAnim != null)
            Log.d("TAG", "No anim exists");
        else
            tMatrix.postTranslate(position.x - bmp.getWidth() / 2, position.y - bmp.getHeight() / 2);

        tMatrix.setValues(MapMath.matrixMultiplication(m, tMatrix));
    }

    float x = 1;

    boolean up = true;

    public void draw(final Canvas canvas, final Paint paint) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setARGB(50, 0, 128, 255);

        if(radius < maxRadius && up) {
            radius+=1.5f;
        }else if(radius > minRadius && !up){
            radius-=1.5f;
        }
        else if(radius >= maxRadius && up)
            up = !up;
        else if(radius <= minRadius && !up)
            up = !up;
        canvas.drawCircle(worldMidPosition.x, worldMidPosition.y , tMatrix.mapRadius(radius), p);

        canvas.drawBitmap(bmp, tMatrix, paint);
    }

    public void debugDraw(final Matrix m, final Canvas canvas) {

    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public PointF getWorldPosition() { return  worldMidPosition; }

    /**
     * Points this graphic directly in the direction of the input vector
     * @param lookAt directional vector (LENGTH == 1)
     */
    public void setLookAt(PointF lookAt) {
        this.rotation = getLookAtAngleFromVector(lookAt);
    }

    /**
     * Animates this graphic to point in the direction of the input vector
     * @param lookAt direction
     * @param duration time to animate to direction
     */
    public void setLookAt(PointF lookAt, float duration) {
        float newRotation = getLookAtAngleFromVector(lookAt);

        if(newRotation != this.rotation) {
            rotationAnim = new RotationAnimation(this, this.rotation, newRotation, new PointF(bmp.getWidth() / 2, bmp.getHeight() / 2), duration);
        }
    }

    //// TODO: 2017-08-08 Move to math?
    private float getLookAtAngleFromVector(PointF lookAt) {
        return (float) Math.toDegrees(Math.atan2(lookAt.x - startDir.x, lookAt.y - startDir.y)) * 2;
    }

}
