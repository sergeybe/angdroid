package com.scoreloop.client.android.ui.component.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundCornerImageView extends ImageView {

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable instanceof BitmapDrawable && ((BitmapDrawable) drawable).getBitmap() != null) {
			Bitmap b = ((BitmapDrawable) drawable).getBitmap();
			Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

			Bitmap roundBitmap = getRoundedCornerBitmap(bitmap, 5, getWidth(), getHeight());
			canvas.drawBitmap(roundBitmap, 0, 0, null);
		} else {
			super.onDraw(canvas);
		}
	}

	private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels, int w, int h) {
		Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		final int color = 0xff424242;
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);

		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, pixels, pixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

		int imgWidth = bitmap.getWidth();
		int imgHeight = bitmap.getHeight();
		float scaleFactor = Math.min(((float) w) / imgWidth, ((float) h) / imgHeight);
		Matrix scale = new Matrix();
		scale.postScale(scaleFactor, scaleFactor);
		final Bitmap scaledImage = Bitmap.createBitmap(bitmap, 0, 0, imgWidth, imgHeight, scale, false);
		canvas.drawBitmap(scaledImage, rect, rect, paint);

		return output;
	}
}
