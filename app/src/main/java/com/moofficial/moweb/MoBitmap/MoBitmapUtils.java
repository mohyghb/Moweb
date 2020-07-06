package com.moofficial.moweb.MoBitmap;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.moofficial.moweb.MoDynamicUnit.MoDynamicUnit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MoBitmapUtils {

    public
    static Bitmap createBitmapFromView(@NonNull View view, int width, int height) {
        if (width > 0 && height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(MoDynamicUnit
                            .convertDpToPixels(width), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(MoDynamicUnit
                            .convertDpToPixels(height), View.MeasureSpec.EXACTLY));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();

        if(w<=0 || h<=0){
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(w,
                h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();

        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);

        return bitmap;
    }



//    public static Bitmap getScreenShot(WebView webView, int scrollTo){
//        // the minimum bitmap height needs to be the view height
//        int bitmapHeight = (webView.getMeasuredHeight() < webView.getContentHeight())
//                ? webView.getContentHeight() : webView.getMeasuredHeight();
//
//        Bitmap bitmap = Bitmap.createBitmap(
//                webView.getMeasuredWidth(), bitmapHeight, Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
//        webView.draw(canvas);
//
//        // prevent IllegalArgumentException for createBitmap:
//        // y + height must be <= bitmap.height()
//        int scrollOffset = (scrollTo + webView.getMeasuredHeight() > bitmap.getHeight())
//                ? bitmap.getHeight() : scrollTo;
//
//        Bitmap resized = Bitmap.createBitmap(
//                bitmap, 0, scrollOffset, bitmap.getWidth(), webView.getMeasuredHeight());
//        return resized;
//    }

}
