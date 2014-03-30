Dex 65536
========


> Unable to execute dex: method ID not in [0, 0xffff]: 65536) 

When you get this message, normally it is not because your project itself has too much methods, but you are importing some big .jar libraries.

So the easy solution is to pack your .jar libraries in libs/ folder into a secondary .dex file and load that file before your application starts.

NOTE: you can write code in eclipse, but you need to build/run in ant.

### Step1: build tools

	Dex65536/custom_rules.xml
	Dex65536/pathtool.jar

Copy these two files in your android project, and execute the following command to generate build.xml.

	android update project -p .

(Make sure your android_sdk/tools is in the $PATH)

### Step2: add some code

You need to add some code to load the secondary .dex file before your application starts.

	public class App extends Application {
	
		@Override
		public void onCreate() {
			super.onCreate();
			dexTool();
		}
	
		/**
		 * Copy the following code and call dexTool() after super.onCreate() in
		 * Application.onCreate()
		 * <p>
		 * This method hacks the default PathClassLoader and load the secondary dex
		 * file as it's parent.
		 */
		@SuppressLint("NewApi")
		private void dexTool() {
	
			File dexDir = new File(getFilesDir(), "dlibs");
			dexDir.mkdir();
			File dexFile = new File(dexDir, "libs.apk");
			File dexOpt = new File(dexDir, "opt");
			dexOpt.mkdir();
			try {
				InputStream ins = getAssets().open("libs.apk");
				if (dexFile.length() != ins.available()) {
					FileOutputStream fos = new FileOutputStream(dexFile);
					byte[] buf = new byte[4096];
					int l;
					while ((l = ins.read(buf)) != -1) {
						fos.write(buf, 0, l);
					}
					fos.close();
				}
				ins.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	
			ClassLoader cl = getClassLoader();
			ApplicationInfo ai = getApplicationInfo();
			String nativeLibraryDir = null;
			if (Build.VERSION.SDK_INT > 8) {
				nativeLibraryDir = ai.nativeLibraryDir;
			} else {
				nativeLibraryDir = "/data/data/" + ai.packageName + "/lib/";
			}
			DexClassLoader dcl = new DexClassLoader(dexFile.getAbsolutePath(),
					dexOpt.getAbsolutePath(), nativeLibraryDir, cl.getParent());
	
			try {
				Field f = ClassLoader.class.getDeclaredField("parent");
				f.setAccessible(true);
				f.set(cl, dcl);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	
	}

If you don't have a custom Application class, register one in your AndroidManifest.xml like:

    <application
        android:name="com.github.mmin18.dex65536.App"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name" >

Othersise you just need to copy dexTool() method into your own custom Application and call it after super.onCreate().

### Step3: ant build and run

Make sure you have ant installed.

	cd /Dex65536
	ant debug install run

Your project should be compile and runnable. Good luck.
