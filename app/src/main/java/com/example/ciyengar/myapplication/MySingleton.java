package com.example.ciyengar.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by ciyengar on 3/2/16.
 */
public final class MySingleton {
    /**
     * Instance
     */
    private static MySingleton mInstance;
    /**
     * Request Queue
     */
    private RequestQueue mRequestQueue;
    /**
     * Loads Image
     */
    private ImageLoader mImageLoader;
    /**
     * Context
     */
    private static Context mCtx;

    /**
     * My Singleton
     * @param context context
     */
    private MySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Singleton Instance
     * @param context context
     * @return instance
     */
    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    /**
     * Gets the Request
     * @return the request
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * adds request to queue
     * @param req request
     * @param <T> T
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Loads Image
     * @return the Image Loader
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}