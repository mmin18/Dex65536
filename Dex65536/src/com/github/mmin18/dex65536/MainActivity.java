package com.github.mmin18.dex65536;

import java.lang.reflect.Method;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	Class<?> curC;
	int curI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new Thread() {
			@Override
			public void run() {
				Class<?>[] cs = new Class[] {
						com.github.mmin18.methodpool1.A.class,
						com.github.mmin18.methodpool1.B.class,
						com.github.mmin18.methodpool1.C.class,
						com.github.mmin18.methodpool1.D.class,
						com.github.mmin18.methodpool1.E.class,
						com.github.mmin18.methodpool1.F.class,
						com.github.mmin18.methodpool2.A.class };

				for (Class<?> c : cs) {
					curC = c;
					try {
						Object instance = c.newInstance();
						for (curI = 0; curI < 10000; curI++) {
							Method m = c.getDeclaredMethod("method_" + curI);
							m.invoke(instance);
						}
					} catch (Exception e) {
						throw new RuntimeException("Fail to load class " + c, e);
					}
				}

				curI = -1;
			}
		}.start();

		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				TextView text = (TextView) findViewById(R.id.progress);
				if (curI != -1) {
					text.setText(curC + " / " + curI);
					sendEmptyMessageDelayed(1, 100);
				} else {
					text.setText("DONE");
				}
			}
		}.sendEmptyMessage(1);
	}
}
