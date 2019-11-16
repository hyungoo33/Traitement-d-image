package com.qps.projettp1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv = findViewById(R.id.img);
        Button button = findViewById(R.id.button);
        Button buttonGray = findViewById(R.id.button2);
        Button buttonColorize = findViewById(R.id.button3);
        Button buttonIsolate = findViewById(R.id.button4);
        Button buttonContrastAugment = findViewById(R.id.button5);
        Button buttonContrastDecrease = findViewById(R.id.button6);
        Button buttonContrastColor = findViewById(R.id.button7);
        Button buttonContrastHSV = findViewById(R.id.button8);
        Button buttonEgalise = findViewById(R.id.button9);
        Button buttonEgaliseRGB = findViewById(R.id.button10);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inMutable = true;
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.plage,options);

        //fonctions expérimentés sur un asus zenfone 5 avec Android 9 API 28 et avec une image de taille: 1250*807.
        //les temps que mettent les fonctions a s'executer sont indiquées a coté de chacune d'elles.


        button.setOnClickListener(clickListenerGray);
        buttonGray.setOnClickListener(clickListenerGrayS);
        buttonColorize.setOnClickListener(clickListenerColorize);
        buttonIsolate.setOnClickListener(clickListenerIsolate);
        buttonContrastAugment.setOnClickListener(clickListenerContrastAugment);
        buttonContrastDecrease.setOnClickListener(clickListenerContrastDecrease);
        buttonContrastColor.setOnClickListener(clickListenerContrastColor);
        buttonContrastHSV.setOnClickListener(clickListenerContrastHSV);
        buttonEgalise.setOnClickListener(clickListenerEgalise);
        buttonEgaliseRGB.setOnClickListener(clickListenerEgaliseRGB);
        iv.setImageBitmap(bitmap);

    }
    private void toGray(Bitmap bp){ //6.8s.
        int pixel;
        int alpha;
        int red;
        int green;
        int blue;
        int gray;
        for(int i = 0;i<bp.getWidth();i++){
            for(int j = 0;j<bp.getHeight();j++){
                pixel = bp.getPixel(i,j);
                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                gray = (int)(0.3*red + 0.59*green + 0.11*blue);
                int newColor = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                bp.setPixel(i,j,newColor);

            }
        }
    }
    private void toGrayFast(Bitmap bp){ //0.4s

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
        bp.setPixels(pixels, 0, bp.getWidth(), 0, 0, bp.getWidth(), bp.getHeight());
    }
    private void colorize(Bitmap bmp){   //1s
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        int hue = new Random().nextInt(360);
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i<bmp.getHeight()*bmp.getWidth();i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[0]= hue;
            pixels[i]=HSVtoRGB(hsv);

        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    /*private void isolateRed(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i<bmp.getHeight()*bmp.getWidth();i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            int gray = (int)(0.3*red + 0.59*green + 0.11*blue);
            if(hsv[0]>10 && hsv[0]<350) {
                pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
            }
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }*/

    private void isolateColor(Bitmap bmp){ //1s
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        int hue = new Random().nextInt(360);

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i<bmp.getHeight()*bmp.getWidth();i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            int gray = (int)(0.3*red + 0.59*green + 0.11*blue);
            float H = hsv[0];
            if(hue>=30 && hue<330) {
                if (H >= (hue + 30) || H <= hue - 30) {
                    pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                }
            }
            else
                if(H >= (hue + 30) % 360 && H <= (hue - 30 + 360)%360){
                    pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                }
        }
        bmp.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
    }

    private int[] histogram(Bitmap bmp){
        int[] hist = new int[256];
        int pixel;
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j= 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
                hist[gray]++;


            }
        }

        return hist;
    }
    private int[][]histogramRGB(Bitmap bmp){
        int[] histR = new int[256];
        int[] histG = new int[256];
        int[] histB = new int[256];
        int[][] hist = new int[3][256];
        int pixel;
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j= 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                histR[red]++;
                histG[green]++;
                histB[blue]++;
            }
        }
        hist[0] = histR;
        hist[1] = histG;
        hist[2] = histB;
        return hist;
    }
    private int[] Dynamique(Bitmap bmp){
        int min = 255;
        int max = 0;
        int pixel;
        int[] dynamique = new int[2];
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j= 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
                if(gray < min) min = gray;
                if(gray > max) max = gray;

            }
        }
        dynamique[0] = min;
        dynamique[1] = max;
        return dynamique;
    }
    private int[] DynamiqueColor(Bitmap bmp){
        int minR = 255;
        int maxR = 0;
        int minG = 255;
        int maxG = 0;
        int minB = 255;
        int maxB = 0;
        int pixel;
        int[] dynamique = new int[6];
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j= 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                if(red < minR) minR = red;
                if(red > maxR) maxR = red;
                if(green < minG) minG = green;
                if(green > maxG) maxG = green;
                if(blue < minB) minB = blue;
                if(blue > maxB) maxB = blue;

            }
        }
        dynamique[0] = minR;
        dynamique[1] = maxR;
        dynamique[2] = minG;
        dynamique[3] = maxG;
        dynamique[4] = minB;
        dynamique[5] = maxB;
        return dynamique;
    }
    private int[] dynamiqueHSV(Bitmap bmp){
        float min = 255.0f;
        float max = 0.0f;
        int pixel;
        int[] dynamique = new int[2];
        float [] hsv = new float[3];
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j= 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                RGBtoHSV(red,green,blue,hsv);
                if(hsv[2]*255 < min) min = hsv[2]*255;
                if(hsv[2]*255 > max) max = hsv[2]*255;

            }
        }
        dynamique[0] =(int)min;
        dynamique[1] =(int)max;
        return dynamique;
    }

    private void ContrastAugment(Bitmap bmp){ //11.2s

        int pixel;
        int[] dynamique = Dynamique(bmp);
        int min = dynamique[0];
        int max = dynamique[1];

        int[] LUT = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUT[ng] = (255*(ng - min))/(max - min);
        }
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j = 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
                int newGray = LUT[gray];
                int color = (alpha << 24) | (newGray << 16) | (newGray << 8) | newGray;
                bmp.setPixel(i,j,color);

            }
        }

    }
    private void ContrastDecrease(Bitmap bmp){  //11.2s
        int pixel;
        int[] dynamique = Dynamique(bmp);
        int min = dynamique[0];
        int max = dynamique[1];

        int[] LUT = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUT[ng] = (ng*((max-30)-(min+30))/255) + min + 30 ;
        }
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j = 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
                int newGray = LUT[gray];
                int color = (alpha << 24) | (newGray << 16) | (newGray << 8) | newGray;
                bmp.setPixel(i,j,color);

            }
        }
    }
    private void ContrastAugmentRGB(Bitmap bmp){    //11.02s
        int pixel;
        int[] dynamique = DynamiqueColor(bmp);
        int minR = dynamique[0];
        int maxR = dynamique[1];
        int minG = dynamique[2];
        int maxG = dynamique[3];
        int minB = dynamique[4];
        int maxB = dynamique[5];

        int[] LUTR = new int[256];
        int[] LUTG = new int[256];
        int[] LUTB = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUTR[ng] = (255*(ng - minR))/(maxR - minR);
        }
        for(int ng = 0; ng < 256; ng++){
            LUTG[ng] = (255*(ng - minG))/(maxG - minG);
        }
        for(int ng = 0; ng < 256; ng++){
            LUTB[ng] = (255*(ng - minB))/(maxB - minB);
        }
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j = 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int newRed = LUTR[red];
                int newGreen = LUTG[green];
                int newBlue = LUTB[blue];
                int color = (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                bmp.setPixel(i,j,color);

            }
        }
    }
    private void ContrastAugmentHSV(Bitmap bmp){  //12s
        int pixel;
        float[] hsv = new float[3];
        int[] dynamique = dynamiqueHSV(bmp);
        int min = dynamique[0];
        int max = dynamique[1];

        int[] LUT = new int[256];

        for(int ng = 0; ng < 256; ng++){
            LUT[ng] = (255*(ng - min))/(max - min);
        }
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j = 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                RGBtoHSV(red,green,blue,hsv);
                hsv[2] = (LUT[(int)(hsv[2]*255.0f)]/255.0f);
                int color = HSVtoRGB(hsv);
                bmp.setPixel(i,j,color);

            }
        }
    }
    private void egalisation(Bitmap bmp){ //11.2s
        int pixel;
        long N = bmp.getWidth()*bmp.getHeight();
        int[] h = histogram(bmp);
        long[] C = new long[256];
        for(int k = 0; k < 255; k++){
            for(int i = 0;i <= k; i++){
                C[k] += h[i];
            }
        }
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j = 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
                long newGray = (C[gray]*255)/N;
                int color = (alpha << 24) | ((int)newGray << 16) | ((int)newGray << 8) | (int)newGray;
                bmp.setPixel(i,j,color);

            }
        }

    }
    private void egalisationRGB(Bitmap bmp){//11.2s
        int pixel;
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogramRGB(bmp);
        int[] hR = h[0];
        int[] hG = h[1];
        int[] hB = h[2];
        long[] CR = new long[256];
        long[] CG = new long[256];
        long[] CB = new long[256];
        for(int k = 0; k < 255; k++){
            for(int i = 0;i <= k; i++){
                CR[k] += hR[i];
                CG[k] += hG[i];
                CB[k] += hB[i];
            }
        }
        for(int i = 0; i < bmp.getWidth(); i++){
            for(int j = 0; j < bmp.getHeight(); j++){
                pixel = bmp.getPixel(i,j);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int alpha = Color.alpha(pixel);
                long newRed = (CR[red]*255)/N;
                long newGreen = (CG[green]*255)/N;
                long newBlue = (CB[blue]*255)/N;
                int color = (alpha << 24) | ((int)newRed << 16) | ((int)newGreen << 8) | (int)newBlue;
                bmp.setPixel(i,j,color);

            }
        }
    }


    private void RGBtoHSV(int red,int green, int blue, float[] hsv){
        float r = red/255.0f;
        float g = green/255.0f;
        float b = blue/255.0f;
        float Cmax = Math.max(Math.max(r,g),b);
        float Cmin = Math.min(Math.min(r,g),b);
        float delta = Cmax-Cmin;

        if(delta == 0)hsv[0]=0;
        else if(Cmax == r) hsv[0] = 60*(((g-b)/delta)%6);
        else if(Cmax == g) hsv[0] = 60*((b-r)/delta + 2);
        else if(Cmax == b) hsv[0] = 60*((r-g)/delta + 4);

        if(Cmax == 0) hsv[1] = 0;
        else if (Cmax != 0) hsv[1]= delta/Cmax;

        hsv[2] = Cmax;
    }
    private int HSVtoRGB(float[] hsv){
        float H = hsv[0], S = hsv[1], V = hsv[2];
        float C = V*S;
        float X = C * (1 - Math.abs((H/60)%2 -1));
        float m = V - C;
        float r=0,g=0,b=0;


        if(H < 60){ r = C; g = X;}
        else if(H >= 60&& H < 120) {r = X; g = C;}
        else if(H >= 120 && H < 180){ b = X; g = C;}
        else if(H >= 180 && H < 240){ b = C; g = X;}
        else if(H >= 240 && H < 300){ r = C; b = X;}
        else if(H >= 300 ){ r = X; b = C;}
        float R =(r+m)*255 ,G = (g+m)*255 ,B=(b+m)*255;
        int RGB =(0xFF << 24)|(Math.round(R) << 16)|(Math.round(G) << 8)|Math.round(B);
        return (RGB);


    }

    private View.OnClickListener clickListenerGray = new OnClickListener(){
        @Override
        public void onClick(View view) {
            toGray(bitmap);
        }
    };
    private View.OnClickListener clickListenerGrayS = new OnClickListener(){
        @Override
        public void onClick(View view) {
            toGrayFast(bitmap);
        }
    };
    private View.OnClickListener clickListenerColorize = new OnClickListener() {
        @Override
        public void onClick(View v) {
            colorize(bitmap);
        }
    };
    private View.OnClickListener clickListenerIsolate = new OnClickListener() {
        @Override
        public void onClick(View v) {
            isolateColor(bitmap);
        }
    };
    private View.OnClickListener clickListenerContrastAugment = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ContrastAugment(bitmap);
        }
    };
    private View.OnClickListener clickListenerContrastDecrease = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ContrastDecrease(bitmap);
        }
    };
    private View.OnClickListener clickListenerContrastColor = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ContrastAugmentRGB(bitmap);
        }
    };
    private View.OnClickListener clickListenerContrastHSV = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ContrastAugmentHSV(bitmap);
        }
    };
    private View.OnClickListener clickListenerEgalise = new OnClickListener() {
        @Override
        public void onClick(View v) {
            egalisation(bitmap);
        }
    };
    private View.OnClickListener clickListenerEgaliseRGB = new OnClickListener() {
        @Override
        public void onClick(View v) {
            egalisationRGB(bitmap);
        }
    };
}
