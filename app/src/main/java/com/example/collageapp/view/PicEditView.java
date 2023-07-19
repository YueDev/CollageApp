package com.example.collageapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by Yue on 2022/10/31.
 */
public class PicEditView extends View {

   private final Matrix mMatrix = new Matrix();

   private final Paint mPaint = new Paint();

   private Bitmap mBitmap;

   //返回的结果 记录各种操作
   private final Matrix mBitmapMatrix = new Matrix();


   public PicEditView(Context context) {
      super(context);
      init();

   }

   public PicEditView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public PicEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   private void init() {
      mPaint.setAntiAlias(true);
      mPaint.setFilterBitmap(true);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      if (mBitmap == null) return;
      canvas.drawBitmap(mBitmap, mMatrix, mPaint);
   }

   public void setBitmap(Bitmap bitmap) {
      if (bitmap == null) return;
      post(() -> {
         mBitmap = bitmap;
         mMatrix.reset();

         float w = getMeasuredWidth();
         float h = getMeasuredHeight();

         float scaleW = w / bitmap.getWidth();
         float scaleH = h / bitmap.getHeight();
         float scale = Math.min(scaleW, scaleH);

         float centerX = w / 2f;
         float centerY = h / 2f;

         mMatrix.postTranslate(centerX - bitmap.getWidth() / 2f, centerY - bitmap.getHeight() / 2f);


         mMatrix.postScale(scale, scale, centerX, centerY);


         invalidate();
      });

   }

   public void rotate(float degree) {
      Matrix matrix = new Matrix();
      matrix.postRotate(degree);
      Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
      setBitmap(bitmap);
      mBitmapMatrix.postRotate(degree);
   }

   public void flip() {
      Matrix matrix = new Matrix();
      matrix.setScale(1f, -1f);
      Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
      setBitmap(bitmap);
      mBitmapMatrix.postScale(1f, -1f);
   }

   public void mirror() {
      Matrix matrix = new Matrix();
      matrix.setScale(-1f, 1f);
      Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
      setBitmap(bitmap);
      mBitmapMatrix.postScale(-1f, 1f);
   }

   public Matrix getBitmapMatrix() {
      return mBitmapMatrix;
   }

   public Bitmap getBitmap() {
      return mBitmap;
   }

}
