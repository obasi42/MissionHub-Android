package com.missionhub.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import com.missionhub.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

// FROM: http://codehenge.net/blog/2011/06/android-development-tutorial-asynchronous-lazy-loading-and-caching-of-listview-images/
public class ImageManager {
	
	// Just using a hashmap for the cache. SoftReferences would 
	// be better, to avoid potential OutOfMemory exceptions
	private HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
	
	private File cacheDir;
	private ImageQueue imageQueue = new ImageQueue();
	private Thread imageLoaderThread = new Thread(new ImageQueueManager());
	
	private int defaultImageResource = R.drawable.default_contact;
	
	public ImageManager(Context context) {
		// Make background thread low priority, to avoid affecting UI performance
		imageLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

		// Find the dir to save cached images
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();		
			cacheDir = new File(sdDir,".missionhub/imgcache");
		}
		else
			cacheDir = context.getCacheDir().getAbsoluteFile();
		
		if(!cacheDir.exists())
			cacheDir.mkdirs();
		
		new Thread(new CacheCleaner()).start();
	}
	
	private class CacheCleaner implements Runnable {
		
		public void run() {
			File[] cacheFiles = cacheDir.listFiles();
			for (File cacheFile : cacheFiles) {
				try {
					if (cacheFile.lastModified() + twoWeekMills < System.currentTimeMillis()){
						cacheFile.delete();
					}
				} catch (Exception e) {}
			}
			return;
	    }
	}
	
	   
	public void displayImage(String url, ImageView imageView) {
		if(imageMap.containsKey(url)) {
			imageView.setImageBitmap(imageMap.get(url));
		} else {
			queueImage(url, imageView);
			imageView.setImageResource(defaultImageResource);
		}
	}
	
	public void displayImage(String url, ImageView imageView, int defaultResource) {
		defaultImageResource = defaultResource;
		displayImage(url, imageView);
	}

	private void queueImage(String url, ImageView imageView) {		
		// This ImageView might have been used for other images, so we clear 
		// the queue of old tasks before starting.
		if (url != null) {
			imageQueue.Clean(imageView);
			ImageRef p=new ImageRef(url, imageView);

			synchronized(imageQueue.imageRefs) {
				imageQueue.imageRefs.push(p);
				imageQueue.imageRefs.notifyAll();
			}

			// Start thread if it's not started yet
			if(imageLoaderThread.getState() == Thread.State.NEW)
				imageLoaderThread.start();
		}
	}
	
	public static final long twoWeekMills = 1000 * 60 * 60 * 24 * 14;

	private Bitmap getBitmap(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		
		Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
		try {
			if (f.lastModified() + twoWeekMills < System.currentTimeMillis() || bitmap == null) {
				bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
				writeFile(bitmap, f);
			}
		} catch (Exception e) {}
		
		return bitmap;
	}
	
	private void writeFile(Bitmap bmp, File f) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
		} catch (Exception e) {}
		finally { 
			try { if (out != null ) out.close(); }
			catch(Exception ex) {} 
		}
	}
	
	/** Classes **/
	
	private static class ImageRef {
		public String url;
		public ImageView imageView;
		
		public ImageRef(String u, ImageView i) {
			url=u;
			imageView=i;
		}
	}
	
	//stores list of images to download
	private static class ImageQueue {
		private Stack<ImageRef> imageRefs = new Stack<ImageRef>();
		
		//removes all instances of this ImageView
		public void Clean(ImageView view) {
			
			for(int i = 0 ;i < imageRefs.size();) {
				if(imageRefs.get(i).imageView == view)
					imageRefs.remove(i);
				else ++i;
			}
		}
	}
	
	private class ImageQueueManager implements Runnable {
		//@Override
		public void run() {
			try {
				while(true) {
					// Thread waits until there are images in the 
					// queue to be retrieved
					if(imageQueue.imageRefs.size() == 0) {
						synchronized(imageQueue.imageRefs) {
							imageQueue.imageRefs.wait();
						}
					}
					
					// When we have images to be loaded
					if(imageQueue.imageRefs.size() != 0) {
						ImageRef imageToLoad;

						synchronized(imageQueue.imageRefs) {
							imageToLoad = imageQueue.imageRefs.pop();
						}
						
						Bitmap bmp = getBitmap(imageToLoad.url);
						imageMap.put(imageToLoad.url, bmp);
						Object tag = imageToLoad.imageView.getTag();
						
						// Make sure we have the right view - thread safety defender
						if(tag != null && ((String)tag).equals(imageToLoad.url)) {
							BitmapDisplayer bmpDisplayer = 
								new BitmapDisplayer(bmp, imageToLoad.imageView);
							
							Activity a = 
								(Activity)imageToLoad.imageView.getContext();
							
							a.runOnUiThread(bmpDisplayer);
						}
					}
					
					if(Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {}
		}
	}

	//Used to display bitmap in the UI thread
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		
		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap=b;
			imageView=i;
		}
		
		public void run() {
			if(bitmap != null)
				imageView.setImageBitmap(bitmap);
			else
				imageView.setImageResource(defaultImageResource);
		}
	}
}