package com.github.mmin18.dex65536;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.mmin18.dex65536.lib.R;

public class HelloTextView extends TextView {

	public HelloTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setText(R.string.hello);
	}

}
