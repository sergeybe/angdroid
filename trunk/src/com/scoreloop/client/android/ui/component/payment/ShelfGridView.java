package com.scoreloop.client.android.ui.component.payment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.GridView;

import org.angdroid.angband.R;

public class ShelfGridView extends GridView {

	private final Drawable	background;

	public ShelfGridView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		background = context.getResources().getDrawable(R.drawable.sl_shop_shelf);
	}

	@Override
	protected void dispatchDraw(final Canvas canvas) {
		if (getChildCount() > 0) {
			final int childH = getChildAt(0).getHeight();

			int top = getChildAt(0).getTop();

			while (top > 0) {
				top -= childH;
			}

			while (top < getHeight()) {
				background.setBounds(0, top, getWidth(), top + childH);

				background.draw(canvas);
				top += childH;
			}
		}

		super.dispatchDraw(canvas);
	}
}
