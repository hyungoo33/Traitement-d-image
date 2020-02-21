package com.qps.projettp1;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

public class BasicModifications {
    Tools tools = new Tools();

    Bitmap toGrayFast(Bitmap bp){

        int[] pixels = new int[bp.getHeight()*bp.getWidth()];
        bp.getPixels(pixels, 0, bp.getWidth(), 0, 0, bp.getWidth(), bp.getHeight());
        for(int i = 0; i<bp.getHeight()*bp.getWidth();i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int gray = (int)(0.3*red + 0.59*green + 0.11*blue);
            pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
        }
        Bitmap result = Bitmap.createBitmap(bp.getWidth(),bp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }



    Bitmap colorize(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        int hue = new Random().nextInt(360);
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i<bmp.getHeight()*bmp.getWidth();i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            tools.RGBtoHSV(red,green,blue,hsv);
            hsv[0]= hue;
            pixels[i]=tools.HSVtoRGB(hsv);

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }



    Bitmap isolateColor(Bitmap bmp){
        int range = 30;
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        int hue = new Random().nextInt(360);

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i<bmp.getHeight()*bmp.getWidth();i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            tools.RGBtoHSV(red,green,blue,hsv);
            int gray = (int)(0.3*red + 0.59*green + 0.11*blue);
            float H = hsv[0];
            if(hue>=range && hue<360-range) {
                if (H >= (hue + range) || H <= hue - range) {
                    pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                }
            }
            else
            if(H >= (hue + range) % 360 && H <= (hue - range + 360)%360){
                pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
            }
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }
}
