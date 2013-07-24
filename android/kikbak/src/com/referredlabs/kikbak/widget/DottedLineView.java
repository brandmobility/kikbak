package com.referredlabs.kikbak.widget;

import com.referredlabs.kikbak.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An ImageView displaying a "dotted" line representing a jagged edge of a coupon.
 * It creates a continuous line effect by repeating a single "notch" pattern
 * loaded from resources (R.drawable.dotted_line).
 * This "edge of a coupon" uses R.drawable.dotted_line drawable for a pattern what makes it
 * always white in color but the background on which the "edge of a coupon" is laid is
 * transparent in R.drawable.dotted_line hence it may be customized in color by
 * setting this ImageView's background to a specific color. 
 *
 */
public class DottedLineView extends ImageView {

	public DottedLineView(Context context) {
		super(context);
		init();
	}
	
	public DottedLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public DottedLineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.dotted_line);
		BitmapDrawable tiles = new BitmapDrawable(getResources(), bmp);
		tiles.setTileModeX(Shader.TileMode.REPEAT);
		setImageDrawable(tiles);
		setScaleType(ScaleType.FIT_XY);
	}
}
