package com.whatsaround.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DecodeTask extends AsyncTask<byte[], Void, Bitmap> {

	public Bitmap getBitmap() {
		return bitmap;
	}

	// private static int IMAGE_MAX_SIZE = 72;

	public ImageView v;
	private Bitmap bitmap;

	public DecodeTask(ImageView iv) {
		v = iv;
	}

	protected Bitmap doInBackground(byte[]... params) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPurgeable = true;
	
		Bitmap bitmap = null;
		//if (isCancelled()) {
			//return bitmap;
		//}

		//opt.inJustDecodeBounds = true;
		//BitmapFactory.decodeByteArray(params[0], 0, params[0].length, opt);
		
		//int scale = 1;
		
		//if (opt.outHeight > IMAGE_MAX_SIZE || opt.outWidth > IMAGE_MAX_SIZE) {
	           // scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / 
	           // (double) Math.max(opt.outHeight, opt.outWidth)) / Math.log(0.5)));
	      // }
		
		 //BitmapFactory.Options o2 = new BitmapFactory.Options();
	       //o2.inSampleSize = scale;

		 bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(params[0], 0, params[0].length, opt), 150, 150,
				true);
		//bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length, opt);
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (v != null) {
			v.setImageBitmap(bitmap);
		}
	}
}
