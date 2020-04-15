package com.qps.projettp1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity  {

    Bitmap bitmap;
    //ImageView iv;
    PhotoView photoView;
    int idImage;// = R.drawable.convolution;                                                           //set the image you want to use
    BitmapFactory.Options options = new BitmapFactory.Options();
    Button cameraButton;
    Button galleryButton;
    Button saveButton;
    Button buttonReset;
    Button drawButton;
    int[] restoreImage;
    ImageScripts scripts = new ImageScripts();


    Tools tools = new Tools();
    BasicModifications basicModifications = new BasicModifications();
    Histogram histogram = new Histogram();
    private static Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        context = getApplicationContext();
        photoView = findViewById(R.id.image);
        buttonReset = findViewById(R.id.reset_btn);
        cameraButton = findViewById(R.id.camera_btn);
        galleryButton = findViewById(R.id.load_btn);
        saveButton = findViewById(R.id.buttonSave);
        drawButton = findViewById(R.id.draw_btn);
        options.inSampleSize = 1;
        options.inMutable = true;
        bitmap = BitmapFactory.decodeResource(getResources(),idImage,options);
        String filename = getIntent().getStringExtra("bitmapMain3Activity.jpg");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null)
                photoView.setImageBitmap(bitmap);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonReset.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (bitmap != null){
                    bitmap.setPixels(restoreImage,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
                }
                else {
                    Toast.makeText(context,"Empty Picture",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                Save saveFile = new Save();
                if(bitmap != null){
                    saveFile.SaveImage(Main2Activity.this,bitmap);
                }
                else {
                    Toast.makeText(context,"Empty Picture",Toast.LENGTH_LONG).show();

                }
            }


        });



        photoView.setImageBitmap(bitmap);
    }


     @Override
     protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == 1000){
            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 1080,1000,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            photoView.setImageBitmap(bitmap);
            //photoView.setImageURI(imageUri);

        }
        if(requestCode == 0){
            bitmap = (Bitmap ) data.getExtras().get("data");
            bitmap = Bitmap.createScaledBitmap(bitmap, 1080,1000,false);

            photoView.setImageBitmap(bitmap);


        }
         /**sauvgarde de l'image initiale */
         restoreImage = new int[bitmap.getWidth()*bitmap.getHeight()];
         bitmap.getPixels(restoreImage,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
     }

    public static Context getContext(){
        return context;
    }


    //----------------------------------------------------------

    //------------------------ Dynamic -------------------------

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
            tools.RGBtoHSV(red,green,blue,hsv);
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

    //-------------------------- Contrast --------------------------

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

    //----------------------------------------------------------

    //---------------- Contrast augment : RGB ------------------

    //----------------------------------------------------------

    // The RGB parameter is used to select the component to modify :
    // 0 = R ; 1 = G ; 2 = B
    private Bitmap contrastAugmentSelectRGB(Bitmap bmp, int RGB) {
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int[] dynamique = dynamiqueRGB(bmp);
        int minVal = dynamique[2*RGB];
        int maxVal = dynamique[2*RGB + 1];
        int[] LUT = new int[256];
        for(int ng = 0; ng < 256; ng++){
            LUT[ng] = (255*(ng - minVal))/(maxVal - minVal);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            switch (RGB) {
                case 0:
                    int newRed = LUT[red];
                    pixels[i] = (alpha << 24) | (newRed << 16) | (green << 8) | blue;
                    break;
                case 1:
                    int newGreen = LUT[green];
                    pixels[i] = (alpha << 24) | (red << 16) | (newGreen << 8) | blue;
                    break;
                case 2:
                    int newBlue = LUT[blue];
                    pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | newBlue;
                    break;
            }
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


    //----------------------------------------------------------

    //---------------- Contrast augment : HSV ------------------

    //----------------------------------------------------------

    // The HSV parameter is used to select the component to modify :
    // 0 = H ; 1 = S ; 2 = V
    private Bitmap contrastAugmentSelectHSV(Bitmap bmp, int HSV) {
        float[] hsv = new float[3];
        int[] dyn = dynamiqueHSV(bmp);
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        int minVal = dyn[2*HSV];
        int maxVal = dyn[2*HSV + 1];
        int[] LUT = new int[360];
        for(int ng = 0; ng < 360; ng++){
            LUT[ng] = (359*(ng - minVal))/(maxVal - minVal);
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            tools.RGBtoHSV(red,green,blue,hsv);
            switch (HSV) {
                case 0:
                    hsv[0] = (LUT[(int)(hsv[0])]);
                    break;
                case 1:
                    hsv[1] = (LUT[(int)(hsv[1]*359.0f)]/359.0f);
                    break;
                case 2:
                    hsv[2] = (LUT[(int)(hsv[2]*359.0f)]/359.0f);
                    break;
            }
            pixels[i] = tools.HSVtoRGB(hsv);
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
            tools.RGBtoHSV(red,green,blue,hsv);
            hsv[0] = LUTH[(int)(hsv[0])];
            hsv[1] = (LUTS[(int)(hsv[1]*359.0f)]/359.0f);
            hsv[2] = (LUTV[(int)(hsv[2]*359.0f)]/359.0f);
            pixels[i] = tools.HSVtoRGB(hsv);
        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }



    //--------------------------------------------------------------

    //----------------------- Equalization -------------------------

    //--------------------------------------------------------------


    private Bitmap equalizate(Bitmap bmp){
        long N = bmp.getWidth()*bmp.getHeight();
        int[] h = histogram.histogram(bmp);
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


    //----------------------------------------------------------

    //------------------ Equalization : RGB --------------------

    //----------------------------------------------------------

    // The RGB parameter is used to select the component to modify :
    // 0 = R ; 1 = G ; 2 = B
    private Bitmap equalizationSelectRGB(Bitmap bmp, int RGB) {
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogram.histogramRGB(bmp);
        int[] hVal = h[RGB];
        long[] C = new long[256];
        for(int k = 0; k < 255; k++){
            for(int i = 0;i <= k; i++){
                C[k] += hVal[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);

            switch (RGB) {
                case 0:
                    long newRed = (C[red]*255)/N;
                    pixels[i] = (alpha << 24) | ((int)newRed << 16) | (green << 8) | blue;
                    break;
                case 1:
                    long newGreen = (C[green]*255)/N;
                    pixels[i] = (alpha << 24) | (red << 16) | ((int)newGreen << 8) | blue;
                    break;
                case 2:
                    long newBlue = (C[blue]*255)/N;
                    pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | (int)newBlue;
                    break;
            }
        }

        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }


    private Bitmap equalizationRGB(Bitmap bmp){                                                       // function to equalize R, G and B at once
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogram.histogramRGB(bmp);
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


    //----------------------------------------------------------

    //------------------ Equalization : HSV --------------------

    //----------------------------------------------------------

    // The HSV parameter is used to select the component to modify :
    // 0 = H ; 1 = S ; 2 = V
    private Bitmap equalizationSelectHSV(Bitmap bmp, int HSV) {
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogram.histogramHSV(bmp);
        int[] hVal = h[HSV];
        long[] C = new long[360];
        for(int k = 0; k < 360; k++){
            for(int i = 0;i <= k; i++){
                C[k] += hVal[i];
            }
        }
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for(int i = 0; i < bmp.getWidth()*bmp.getHeight(); i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            tools.RGBtoHSV(red,green,blue,hsv);

            switch (HSV) {
                case 0:
                    hsv[0] = C[(int)hsv[0]]*359.0f/N;
                    break;
                case 1:
                    hsv[1] = C[(int)(hsv[1]*359)]*1.0f/N;
                    break;
                case 2:
                    hsv[2] = C[(int)(hsv[2]*359)]*1.0f/N;
                    break;
            }
            pixels[i] = tools.HSVtoRGB(hsv);
        }

        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }


    private Bitmap equalizationHSV(Bitmap bmp){                                                       // function to equalize H, S and V at once
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        float[] hsv = new float[3];
        long N = bmp.getWidth()*bmp.getHeight();
        int[][] h = histogram.histogramHSV(bmp);
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
            tools.RGBtoHSV(red,green,blue,hsv);
            hsv[0] = CH[(int)hsv[0]]*360.0f/N;
            hsv[1] = CS[(int)(hsv[1]*359)]*1.0f/N;
            hsv[2] = CV[(int)(hsv[2]*359)]*1.0f/N;
            pixels[i] = tools.HSVtoRGB(hsv);

        }
        Bitmap result = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888);
        result.setPixels(pixels,0,result.getWidth(),0,0,result.getWidth(),result.getHeight());
        return result;
    }




    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------ Convolution -----------------------
    //------------------------------------------------------------
    //------------------------------------------------------------


    int[] gauss5 = {1,2,3,2,1,  2,6,8,6,2,  3,8,10,8,3,  2,6,8,6,2,  1,2,3,2,1};                    //different filters used

    int[] h1Prewitt = {-1,0,1,-1,0,1,-1,0,1};
    int[] h2Prewitt = {-1,-1,-1,0,0,0,1,1,1};

    int[] h1Sobel = {-1,0,1,-2,0,2,-1,0,1};
    int[] h2Sobel = {-1,-2,-1,0,0,0,1,2,1};

    int[] hLaplace4 ={0,1,0,1,-4,1,0,1,0};
    int[] hLaplace8 ={1,1,1,1,-8,1,1,1,1};

    float[] h1Prewittf = {-1.f,0.f,1.f,-1.f,0.f,1.f,-1.f,0.f,1.f};
    float[] h2Prewittf = {-1.f,-1.f,-1.f,0.f,0.f,0.f,1.f,1.f,1.f};
    float[] h1Sobelf = {-1.f,0.f,1.f,-2.f,0.f,2.f,-1.f,0.f,1.f};
    float[] h2Sobelf = {-1.f,-2.f,-1.f,0.f,0.f,0.f,2.f,1.f,1.f};
    float[] hLaplace4f = {0.f,1.f,0.f,1.f,-4.f,1.f,0.f,1.f,0.f};
    float[] hLaplace8f = {1.f,1.f,1.f,1.f,-8.f,1.f,1.f,1.f,1.f};


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

                    int red = Color.red(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
                    int green = Color.green(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
                    int blue = Color.blue(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
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

                    int red = Color.red(pixels[tools.mod((i + x * bmp.getWidth() + y), taille)]);
                    int green = Color.green(pixels[tools.mod((i + x * bmp.getWidth() + y), taille)]);
                    int blue = Color.blue(pixels[tools.mod((i + x * bmp.getWidth() + y), taille)]);
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

                    int red = Color.red(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
                    int green = Color.green(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
                    int blue = Color.blue(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
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

                    int red = Color.red(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
                    int green = Color.green(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
                    int blue = Color.blue(pixels[tools.mod((i+x*bmp.getWidth()+y),taille)]);
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
               scripts.toGrayRs(bitmap);
                return true;
            case R.id.colorize:
                scripts.coloriseRS(bitmap);
                return true;
            case R.id.isolate:
                bitmap = basicModifications.isolateColor(bitmap);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_augment:
                bitmap = contrastAugment(bitmap);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_decrease:
                bitmap = contrastDecrease(bitmap,20);
                photoView.setImageBitmap(bitmap);
                return true;

//----------------------------------------------------------

            case R.id.contrast_R:
                bitmap = contrastAugmentSelectRGB(bitmap, 0);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_G:
                bitmap = contrastAugmentSelectRGB(bitmap, 1);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_B:
                bitmap = contrastAugmentSelectRGB(bitmap, 2);
                photoView.setImageBitmap(bitmap);
                return true;


//----------------------------------------------------------

            case R.id.contrast_H:
                bitmap = contrastAugmentSelectHSV(bitmap, 0);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_S:
                bitmap = contrastAugmentSelectHSV(bitmap, 1);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.contrast_V:
                bitmap = contrastAugmentSelectHSV(bitmap, 2);
                photoView.setImageBitmap(bitmap);
                return true;

//----------------------------------------------------------

            case R.id.egalise:
                bitmap = equalizate(bitmap);
                photoView.setImageBitmap(bitmap);
                return true;

            case R.id.egalise_R:
                bitmap = equalizationSelectRGB(bitmap, 0);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_G:
                bitmap = equalizationSelectRGB(bitmap, 1);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_B:
                bitmap = equalizationSelectRGB(bitmap, 2);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_RGB:
                return true;

//----------------------------------------------------------

            case R.id.egalise_H:
                bitmap = equalizationSelectHSV(bitmap, 0);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_S:
                bitmap = equalizationSelectHSV(bitmap, 1);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_V:
                bitmap = equalizationSelectHSV(bitmap, 2);
                photoView.setImageBitmap(bitmap);
                return true;
            case R.id.egalise_HSV:
                return true;

//----------------------------------------------------------

            case R.id.moyenneur:
                scripts.convolutionMoyenneRs(bitmap);
                return true;
            case R.id.gauss5:
                bitmap = convolution(bitmap,2,1);
                photoView.setImageBitmap(bitmap);
                return true;

            case R.id.detectHoriP:
                scripts.convolution3x3Rs(bitmap,h1Prewittf,true);
                return true;
            case R.id.detectVertP:
                scripts.convolution3x3Rs(bitmap,h2Prewittf,true);
                return true;

            case R.id.detectHoriS:
                scripts.convolution3x3Rs(bitmap,h1Sobelf,true);
                return true;
            case R.id.detectVertS:
                scripts.convolution3x3Rs(bitmap,h2Sobelf,true);
                return true;

            case R.id.contourPrewitt:
                bitmap = contour(bitmap,0);
                photoView.setImageBitmap(bitmap);
                return true;

            case R.id.contourSobel:
                bitmap = contour(bitmap,1);
                photoView.setImageBitmap(bitmap);
                return true;

            case R.id.laplace4:
                scripts.convolution3x3Rs(bitmap,hLaplace4f,true);
                return true;
            case R.id.laplace8:
                scripts.convolution3x3Rs(bitmap,hLaplace8f,true);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
    /**
     * Simuler un effet dessin avec les doigts*/


    public void toMainActivity3(View view) {//draw With Fingers Button
        if (bitmap == null){
            Toast.makeText(context,"Empty Picture",Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                //Write file
                String filename = "bitmap.jpg";
                FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                //Cleanup
                stream.close();
                bitmap.recycle();

                //Pop intent
                Intent in1 = new Intent(this, Main3Activity.class);
                in1.putExtra("image", filename);
                startActivity(in1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }







    /*private View.OnClickListener clickListenerReset = new OnClickListener(){
        @Override
        public void onClick(View view) {

        }
    };*/
}
