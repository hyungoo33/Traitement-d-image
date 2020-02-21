package com.qps.projettp1;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    ImageView iv;
    int idImage = R.drawable.convolution;                                                           //set the image you want to use
    BitmapFactory.Options options = new BitmapFactory.Options();
    Button cameraButton;
    Button galleryButton;
    Button saveButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.image);
        Button buttonReset = findViewById(R.id.reset_btn);
        cameraButton = findViewById(R.id.camera_btn);
        galleryButton = findViewById(R.id.load_btn);
        saveButton = findViewById(R.id.buttonSave);
        options.inSampleSize = 1;
        options.inMutable = true;
        bitmap = BitmapFactory.decodeResource(getResources(),idImage,options);

        buttonReset.setOnClickListener(clickListenerReset);
        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 0);
            }

        });
        galleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1000);
            }

        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("jjjg");
                System.out.println(bitmap.getWidth());
                Save saveFile = new Save();
                saveFile.SaveImage(MainActivity.this,bitmap);
            }


        });
        iv.setImageBitmap(bitmap);


    }


     @Override
     protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == 1000){
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            iv.setImageURI(imageUri);
        }
        if(requestCode == 0){
            bitmap = (Bitmap ) data.getExtras().get("data");
            iv.setImageBitmap(bitmap);
        }
     }


    private float mod(float x, float y)                                                             //two mod methods for the math needed
    {
        float result = x % y;
        return result < 0? result + y : result;
    }
    int mod ( int x , int y )
    {
        return x >= 0 ? x % y : y - 1 - ((-x-1) % y) ;
    }




    private Bitmap toGrayFast(Bitmap bp){

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



    private Bitmap colorize(Bitmap bmp){
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
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }



    private Bitmap isolateColor(Bitmap bmp){
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
            RGBtoHSV(red,green,blue,hsv);
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

    //----------------------------------------------------------

    //--------------------------HISTOGRAM-----------------------

    //----------------------------------------------------------

    private int[] histogram(Bitmap bmp){
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


    private int[][]histogramRGB(Bitmap bmp){
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


    private int[][]histogramHSV(Bitmap bmp){
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
            RGBtoHSV(red,green,blue,hsv);
            histH[(int)hsv[0]]++;
            histS[(int)(hsv[1]*359.0f)]++;
            histV[(int)(hsv[2]*359.0f)]++;
        }
        hist[0] = histH;
        hist[1] = histS;
        hist[2] = histV;
        return hist;
    }



    //----------------------------------------------------------

    //--------------------------DYNAMIQUE-----------------------

    //----------------------------------------------------------



    private int[] dynamique(Bitmap bmp){
        int min = 255;
        int max = 0;
        int[] dyn = new int[2];
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
            if(gray < min) min = gray;
            if(gray > max) max = gray;

        }
        dyn[0] = min;
        dyn[1] = max;
        return dyn;
    }

    private int[] dynamiqueRGB(Bitmap bmp){
        int minR = 255;
        int maxR = 0;
        int minG = 255;
        int maxG = 0;
        int minB = 255;
        int maxB = 0;
        int[] dyn = new int[6];
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];


        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            if(red < minR) minR = red;
            if(red > maxR) maxR = red;
            if(green < minG) minG = green;
            if(green > maxG) maxG = green;
            if(blue < minB) minB = blue;
            if(blue > maxB) maxB = blue;
        }

        dyn[0] = minR;
        dyn[1] = maxR;
        dyn[2] = minG;
        dyn[3] = maxG;
        dyn[4] = minB;
        dyn[5] = maxB;

        return dyn;
    }
    private int[] dynamiqueHSV(Bitmap bmp){
        float minH = 360.0f;
        float maxH = 0.0f;
        float minS = 1.0f;
        float maxS = 0.0f;
        float minV = 1.0f;
        float maxV = 0.0f;

        int[] dyn = new int[6];
        float [] hsv = new float[3];
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            if(hsv[0] < minH) minH = hsv[0];
            if(hsv[0] > maxH) maxH = hsv[0];
            if(hsv[1] < minS) minS = hsv[1];
            if(hsv[1] > maxS) maxS = hsv[1];
            if(hsv[2] < minV) minV = hsv[2];
            if(hsv[2] > maxV) maxV = hsv[2];

        }
        dyn[0] =(int)minH;
        dyn[1] =(int)maxH;
        dyn[2] =(int)(minS*360.0f);
        dyn[3] =(int)(maxS*360.0f);
        dyn[4] =(int)(minV*360.0f);
        dyn[5] =(int)(maxV*360.0f);
        return dyn;
    }



    //--------------------------------------------------------------

    //-------------------------- CONTRAST --------------------------

    //--------------------------------------------------------------


    private Bitmap contrastAugment(Bitmap bmp){

        int[] dynamique = dynamique(bmp);
        int min = dynamique[0];
        int max = dynamique[1];
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];

        int[] LUT = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUT[ng] = (255*(ng - min))/(max - min);
        }

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
            int newGray = LUT[gray];
            pixels[i] = (alpha << 24) | (newGray << 16) | (newGray << 8) | newGray;
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    private Bitmap contrastDecrease(Bitmap bmp,int range){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int[] dynamique = dynamique(bmp);
        int min = dynamique[0];
        int max = dynamique[1];
        int[] LUT = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUT[ng] = (ng*((max-range)-(min+range))/255) + min + range ;
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
            int newGray = LUT[gray];
            pixels[i] = (alpha << 24) | (newGray << 16) | (newGray << 8) | newGray;
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    //----------------------------------------
    //-----------contrastAugment : RGB--------
    //----------------------------------------




    private Bitmap contrastAugmentR(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int[] dynamique = dynamiqueRGB(bmp);
        int minR = dynamique[0];
        int maxR = dynamique[1];
        int[] LUTR = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUTR[ng] = (255*(ng - minR))/(maxR - minR);
        }

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int newRed = LUTR[red];
            pixels[i] = (alpha << 24) | (newRed << 16) | (green << 8) | blue;
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }
    private Bitmap contrastAugmentG(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int[] dynamique = dynamiqueRGB(bmp);
        int minG = dynamique[2];
        int maxG = dynamique[3];

        int[] LUTG = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUTG[ng] = (255*(ng - minG))/(maxG - minG);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int newGreen = LUTG[green];
            pixels[i] = (alpha << 24) | (red << 16) | (newGreen << 8) | blue;
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    private Bitmap contrastAugmentB(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int[] dynamique = dynamiqueRGB(bmp);
        int minB = dynamique[4];
        int maxB = dynamique[5];

        int[] LUTB = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUTB[ng] = (255*(ng - minB))/(maxB - minB);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int newBlue = LUTB[blue];
            pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | newBlue;
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    private Bitmap contrastAugmentRGB(Bitmap bmp){                                                    //function to augment the contrast of R,G, and B at once
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int[] dynamique = dynamiqueRGB(bmp);
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
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int newRed = LUTR[red];
            int newGreen = LUTG[green];
            int newBlue = LUTB[blue];
            pixels[i] = (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }




    //----------------------------------------
    //-----------contrastAugment : HSV--------
    //----------------------------------------



    private Bitmap contrastAugmentH(Bitmap bmp){

        float[] hsv = new float[3];
        int[] dyn = dynamiqueHSV(bmp);
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int minH = dyn[0];
        int maxH = dyn[1];

        int[] LUTH = new int[360];

        for(int ng = 0; ng < 360; ng++){
            LUTH[ng] = (359*(ng - minH))/(maxH - minH);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[0] = (LUTH[(int)(hsv[0])]);
            pixels[i] = HSVtoRGB(hsv);
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }
    private Bitmap contrastAugmentS(Bitmap bmp){

        float[] hsv = new float[3];
        int[] dyn = dynamiqueHSV(bmp);
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int minS = dyn[2];
        int maxS = dyn[3];

        int[] LUTS = new int[360];

        for(int ng = 0; ng < 360; ng++){
            LUTS[ng] = (359*(ng - minS))/(maxS - minS);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[1] = (LUTS[(int)(hsv[1]*359.0f)]/359.0f);
            pixels[i] = HSVtoRGB(hsv);
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }
    private Bitmap contrastAugmentV(Bitmap bmp){

        float[] hsv = new float[3];
        int[] dyn = dynamiqueHSV(bmp);
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int minV = dyn[4];
        int maxV = dyn[5];

        int[] LUTV = new int[360];

        for(int ng = 0; ng < 360; ng++){
            LUTV[ng] = (359*(ng - minV))/(maxV - minV);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[2] = (LUTV[(int)(hsv[2]*359.0f)]/359.0f);
            pixels[i] = HSVtoRGB(hsv);
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    private Bitmap contrastAugmentHSV(Bitmap bmp){                                                    //function to augment the contrast of H,S and V at once

        float[] hsv = new float[3];
        int[] dyn = dynamiqueHSV(bmp);
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int minH = dyn[0];
        int maxH = dyn[1];
        int minS = dyn[2];
        int maxS = dyn[3];
        int minV = dyn[4];
        int maxV = dyn[5];

        int[] LUTH = new int[360];
        int[] LUTS = new int[360];
        int[] LUTV = new int[360];

        for(int ng = 0; ng < 360; ng++){
            LUTH[ng] = (359*(ng - minH))/(maxH - minH);
        }
        for(int ng = 0; ng < 360; ng++){
            LUTS[ng] = (359*(ng - minS))/(maxS - minS);
        }
        for(int ng = 0; ng < 360; ng++){
            LUTV[ng] = (359*(ng - minV))/(maxV - minV);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[0] = LUTH[(int)(hsv[0])];
            hsv[1] = (LUTS[(int)(hsv[1]*359.0f)]/359.0f);
            hsv[2] = (LUTV[(int)(hsv[2]*359.0f)]/359.0f);
            pixels[i] = HSVtoRGB(hsv);
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }



    //--------------------------------------------------------------

    //----------------------- EQUALIZATION --------------------------

    //--------------------------------------------------------------


    private Bitmap equalizate(Bitmap bmp){
        long N = bmp.getWidth()*bmp.getHeight();
        int[] h = histogram(bmp);
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        long[] C = new long[256];

        for(int k = 0; k < 255; k++){
            for(int i = 0;i <= k; i++){
                C[k] += h[i];
            }
        }

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int gray =(int)(0.3*red + 0.59*green + 0.11*blue);
            long newGray = C[gray]*255/N;
            pixels[i]= (alpha << 24) | ((int)newGray << 16) | ((int)newGray << 8) | (int)newGray;
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }


    //----------------------------------------
    //------------equalization : RGB-----------
    //----------------------------------------


    private Bitmap equalizationR(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogramRGB(bmp);
        int[] hR = h[0];
        long[] C = new long[256];
        for(int k = 0; k < 255; k++){
            for(int i = 0;i <= k; i++){
                C[k] += hR[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            long newRed = (C[red]*255)/N;
            pixels[i] = (alpha << 24) | ((int)newRed << 16) | (green << 8) | blue;

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }
    private Bitmap equalizationG(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogramRGB(bmp);
        int[] hG = h[1];
        long[] C = new long[256];
        for(int k = 0; k < 255; k++){
            for(int i = 0;i <= k; i++){
                C[k] += hG[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            long newGreen = (C[green]*255)/N;
            pixels[i] = (alpha << 24) | (red << 16) | ((int)newGreen << 8) | blue;

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }
    private Bitmap equalizationB(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogramRGB(bmp);
        int[] hB = h[2];
        long[] C = new long[256];
        for(int k = 0; k < 255; k++){
            for(int i = 0;i <= k; i++){
                C[k] += hB[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            long newBlue = (C[blue]*255)/N;
            pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | (int)newBlue;

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }
    private Bitmap equalizationRGB(Bitmap bmp){                                                       // function to equalize R, G and B at once
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
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
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            long newRed = (CR[red]*255)/N;
            long newGreen = (CG[green]*255)/N;
            long newBlue = (CB[blue]*255)/N;
            pixels[i] = (alpha << 24) | ((int)newRed << 16) | ((int)newGreen << 8) | (int)newBlue;

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }


    //------------------------------------------
    //------------equalization : HSV-------------
    //------------------------------------------


    private Bitmap equalizationH(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogramHSV(bmp);
        int[] hH = h[0];
        long[] CH = new long[360];
        for(int k = 0; k < 360; k++){
            for(int i = 0;i <= k; i++){
                CH[k] += hH[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[0] = CH[(int)hsv[0]]*359.0f/N;
            pixels[i] = HSVtoRGB(hsv);

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    private Bitmap equalizationS(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogramHSV(bmp);
        int[] hS = h[1];
        long[] CS = new long[360];
        for(int k = 0; k < 360; k++){
            for(int i = 0;i <= k; i++){
                CS[k] += hS[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[1] = CS[(int)(hsv[1]*359)]*1.0f/N;

            pixels[i] = HSVtoRGB(hsv);

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    private Bitmap equalizationV(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogramHSV(bmp);
        int[] hV = h[2];
        long[] CV = new long[360];
        for(int k = 0; k < 360; k++){
            for(int i = 0;i <= k; i++){
                CV[k] += hV[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[2] = CV[(int)(hsv[2]*359)]*1.0f/N;

            pixels[i] = HSVtoRGB(hsv);

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    private Bitmap equalizationHSV(Bitmap bmp){                                                       // function to equalize H, S and V at once
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogramHSV(bmp);
        int[] hH = h[0];
        int[] hS = h[1];
        int[] hV = h[2];
        long[] CH = new long[360];
        long[] CS = new long[360];
        long[] CV = new long[360];
        for(int k = 0; k < 360; k++){
            for(int i = 0;i <= k; i++){
                CH[k] += hH[i];
                CS[k] += hS[i];
                CV[k] += hV[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            RGBtoHSV(red,green,blue,hsv);
            hsv[0] = CH[(int)hsv[0]]*360.0f/N;
            hsv[1] = CS[(int)(hsv[1]*359)]*1.0f/N;
            hsv[2] = CV[(int)(hsv[2]*359)]*1.0f/N;
            pixels[i] = HSVtoRGB(hsv);

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }



    //--------------------------------------------------------------

    //----------------------- RGB/HSV HSV/RGB--------------------------

    //--------------------------------------------------------------




    private void RGBtoHSV(int red,int green, int blue, float[] hsv){
        float r = red/255.0f;
        float g = green/255.0f;
        float b = blue/255.0f;
        float Cmax = Math.max(Math.max(r,g),b);
        float Cmin = Math.min(Math.min(r,g),b);
        float delta = Cmax-Cmin;

        if(delta == 0)hsv[0]=0;
        else if(Cmax == r) hsv[0] = 60*(mod((g-b)/delta,6));
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
        else if(H >= 240 && H < 300){ r = X; b = C;}
        else if(H >= 300 ){ r = C; b = X;}
        float R =(r+m)*255 ,G = (g+m)*255 ,B=(b+m)*255;
        int RGB =(0xFF << 24)|(Math.round(R) << 16)|(Math.round(G) << 8)|Math.round(B);
        return (RGB);


    }



    //------------------------------------------------------------
    //------------------------------------------------------------
    //-------------------------CONVOLUTION------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------


    int[] gauss5 = {1,2,3,2,1,  2,6,8,6,2,  3,8,10,8,3,  2,6,8,6,2,  1,2,3,2,1};                    //different filters used

    int[] h1Prewitt = {-1,0,1,-1,0,1,-1,0,1};
    int[] h2Prewitt = {-1,-1,-1,0,0,0,1,1,1};

    int[] h1Sobel = {-1,0,1,-1,0,1,-1,0,1};
    int[] h2Sobel = {-1,-1,-1,0,0,0,1,1,1};

    int[] hLaplace4 ={0,1,0,1,-4,1,0,1,0};
    int[] hLaplace8 ={1,1,1,1,-8,1,1,1,1};

    private Bitmap convolution(Bitmap bmp,int n,int filtre){
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        int[] newPixels = new int[bmp.getHeight() * bmp.getWidth()];
        int N = (2*n+1)*(2*n+1);
        int taille = bmp.getWidth()*bmp.getHeight();
        int trueGray;

        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i = 0;i < bmp.getHeight()*bmp.getWidth();i++){
            double newGray =0.0;
            int compteur = 0;
            for(int x = -n;x < n + 1; x++){
                for(int y = -n;y < n + 1; y++){

                    int red = Color.red(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    int green = Color.green(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    int blue = Color.blue(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    double gray =(0.3*red + 0.59*green + 0.11*blue);
                    if(filtre == 0)newGray += gray/N;
                    if(filtre == 1){
                        newGray += gauss5[compteur]*gray/98;
                        compteur++;
                    }
                    if(filtre == 2){
                        newGray += hLaplace4[compteur]*gray;
                        compteur++;
                    }
                    if(filtre == 3){
                        newGray += hLaplace8[compteur]*gray;
                        compteur++;
                    }

                }

            }
            if(filtre == 0 || filtre == 1)newPixels[i] = (0xFF << 24) | (((int)newGray) << 16) | (((int)newGray) << 8) | ((int)newGray);

            if (filtre == 2){
                trueGray = ((int) newGray + 4 * 255) / 8;
                newPixels[i] = (0xFF << 24) | (trueGray << 16) | (trueGray << 8) | trueGray;
            }
            if (filtre == 3){
                trueGray = ((int) newGray + 8 * 255) / 16;
                newPixels[i] = (0xFF << 24) | (trueGray << 16) | (trueGray << 8) | trueGray;
            }

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(newPixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    private Bitmap convDerivationHorizontal(Bitmap bmp,int filtre) {
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        int[] newPixels = new int[bmp.getHeight() * bmp.getWidth()];
        int taille = bmp.getWidth() * bmp.getHeight();
        int trueGray = 0;

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int i = 0; i < bmp.getHeight() * bmp.getWidth(); i++) {
            double newGray = 0.0;
            int compteur = 0;
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {

                    int red = Color.red(pixels[mod((i + x * bmp.getWidth() + y), taille)]);
                    int green = Color.green(pixels[mod((i + x * bmp.getWidth() + y), taille)]);
                    int blue = Color.blue(pixels[mod((i + x * bmp.getWidth() + y), taille)]);
                    double gray = (0.3 * red + 0.59 * green + 0.11 * blue);

                    if (filtre == 0) newGray += gray * h1Prewitt[compteur];
                    if (filtre == 1) newGray += gray * h1Sobel[compteur];

                    compteur++;
                }

            }
            if(filtre == 0)trueGray =((int)newGray + 255/2);
            if(filtre == 1)trueGray =((int) newGray + 255/2);

            if(trueGray < 0 )trueGray = 0;
            if(trueGray > 255) trueGray = 255;
            newPixels[i] = (0xFF << 24) | (trueGray << 16) | (trueGray << 8) | trueGray;


        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(newPixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }


    private Bitmap convDerivationVertical(Bitmap bmp,int filtre){
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        int[] newPixels = new int[bmp.getHeight() * bmp.getWidth()];
        int taille = bmp.getWidth()*bmp.getHeight();
        int trueGray = 0;

        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i = 0;i < bmp.getHeight()*bmp.getWidth();i++){
            double newGray =0.0;
            int compteur = 0;
            for(int x = -1;x < 2; x++){
                for(int y = -1;y < 2; y++){

                    int red = Color.red(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    int green = Color.green(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    int blue = Color.blue(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    double gray =(0.3*red + 0.59*green + 0.11*blue);

                    if(filtre == 0)newGray += gray*h2Prewitt[compteur];
                    if(filtre == 1)newGray += gray*h2Sobel[compteur];

                    compteur++;
                }
            }
            if(filtre == 0)trueGray =((int)newGray + 255/2);
            if(filtre == 1)trueGray =((int) newGray + 255/2);

            if(trueGray < 0 )trueGray = 0;
            if(trueGray > 255) trueGray = 255;
            newPixels[i] = (0xFF << 24) | (trueGray << 16) | (trueGray << 8) | trueGray;

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(newPixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }


    private Bitmap contour(Bitmap bmp,int filtre){
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        int[] newPixels = new int[bmp.getHeight() * bmp.getWidth()];
        int taille = bmp.getWidth()*bmp.getHeight();
        int trueGray = 0;

        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        for(int i = 0;i < bmp.getHeight()*bmp.getWidth();i++){
            double xGray = 0.0;
            double yGray = 0.0;
            int compteur = 0;
            for(int x = -1;x < 2; x++){
                for(int y = -1;y < 2; y++){

                    int red = Color.red(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    int green = Color.green(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    int blue = Color.blue(pixels[mod((i+x*bmp.getWidth()+y),taille)]);
                    double gray =(0.3*red + 0.59*green + 0.11*blue);

                    if(filtre == 0){
                        xGray += gray*h1Prewitt[compteur];
                        yGray += gray*h2Prewitt[compteur];
                    }
                    if(filtre == 1){
                        xGray += gray*h1Sobel[compteur];
                        yGray += gray*h2Sobel[compteur];
                    }

                    compteur++;
                }
            }
            if(filtre == 0){
                trueGray = (int)Math.sqrt((xGray*xGray)+(yGray*yGray));
            }
            if(filtre == 1){
                trueGray = (int)Math.sqrt((xGray*xGray)+(yGray*yGray));
            }
            if(trueGray > 255){
                trueGray = 255;
            }
            newPixels[i] = (0xFF << 24) | (trueGray << 16) | (trueGray << 8) | trueGray;

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(newPixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.contrast_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gray:
                bitmap = toGrayFast(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.colorize:
                bitmap = colorize(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.isolate:
                bitmap = isolateColor(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_augment:
                bitmap = contrastAugment(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_decrease:
                bitmap = contrastDecrease(bitmap,20);
                iv.setImageBitmap(bitmap);
                return true;

//----------------------------------------------------------

            case R.id.contrast_R:
                bitmap = contrastAugmentR(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_G:
                bitmap = contrastAugmentG(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_B:
                bitmap = contrastAugmentB(bitmap);
                iv.setImageBitmap(bitmap);
                return true;


//----------------------------------------------------------

            case R.id.contrast_H:
                bitmap =contrastAugmentH(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_S:
                bitmap = contrastAugmentS(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_V:
                bitmap = contrastAugmentV(bitmap);
                iv.setImageBitmap(bitmap);
                return true;

//----------------------------------------------------------

            case R.id.egalise:
                bitmap = equalizate(bitmap);
                iv.setImageBitmap(bitmap);
                return true;

            case R.id.egalise_R:
                bitmap = equalizationR(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_G:
                bitmap = equalizationG(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_B:
                bitmap = equalizationB(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_RGB:
                return true;

//----------------------------------------------------------

            case R.id.egalise_H:
                bitmap = equalizationH(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_S:
                bitmap = equalizationS(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_V:
                bitmap = equalizationV(bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_HSV:
                return true;

//----------------------------------------------------------

            case R.id.moyenneur:
                bitmap = convolution(bitmap,5,0);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.gauss5:
                bitmap = convolution(bitmap,2,1);
                iv.setImageBitmap(bitmap);
                return true;

            case R.id.detectHoriP:
                bitmap = convDerivationHorizontal(bitmap,0);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.detectVertP:
                bitmap = convDerivationVertical(bitmap,0);
                iv.setImageBitmap(bitmap);
                return true;

            case R.id.detectHoriS:
                bitmap = convDerivationHorizontal(bitmap,1);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.detectVertS:
                bitmap = convDerivationVertical(bitmap,1);
                iv.setImageBitmap(bitmap);
                return true;

            case R.id.contourPrewitt:
                bitmap = contour(bitmap,0);
                iv.setImageBitmap(bitmap);
                return true;

            case R.id.contourSobel:
                bitmap = contour(bitmap,1);
                iv.setImageBitmap(bitmap);
                return true;

            case R.id.laplace4:
                bitmap = convolution(bitmap,1,2);
                iv.setImageBitmap(bitmap);
                return true;
            case R.id.laplace8:
                bitmap = convolution(bitmap,1,3);
                iv.setImageBitmap(bitmap);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }










    private View.OnClickListener clickListenerReset = new OnClickListener(){
        @Override
        public void onClick(View view) {
            bitmap = BitmapFactory.decodeResource(getResources(),idImage,options);
            iv.setImageBitmap(bitmap);
        }
    };
}
