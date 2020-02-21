package com.qps.projettp1;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Histogram {
    Tools tools = new Tools();

    int[] histogram(Bitmap bmp){
        int[] hist = new int[256];
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
            hist[gray]++;

        }

        return hist;
    }


    int[][]histogramRGB(Bitmap bmp){
        int[] histR = new int[256];
        int[] histG = new int[256];
        int[] histB = new int[256];
        int[][] hist = new int[3][256];
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            histR[red]++;
            histG[green]++;
            histB[blue]++;
        }
        hist[0] = histR;
        hist[1] = histG;
        hist[2] = histB;
        return hist;
    }


    int[][]histogramHSV(Bitmap bmp){
        int[] histH = new int[360];
        int[] histS = new int[360];
        int[] histV = new int[360];
        int[][] hist = new int[3][360];
        float[] hsv = new float[3];
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            tools.RGBtoHSV(red,green,blue,hsv);
            histH[(int)hsv[0]]++;
            histS[(int)(hsv[1]*359.0f)]++;
            histV[(int)(hsv[2]*359.0f)]++;
        }
        hist[0] = histH;
        hist[1] = histS;
        hist[2] = histV;
        return hist;
    }
}
