package com.qps.projettp1;

public class Tools {

    //--------------------------------------------------------------

    //---------------------------- Mod -----------------------------

    //--------------------------------------------------------------

    float mod(float x, float y)
    {
        float result = x % y;
        return result < 0? result + y : result;
    }
    int mod ( int x , int y )
    {
        return x >= 0 ? x % y : y - 1 - ((-x-1) % y) ;
    }

    //--------------------------------------------------------------

    //---------------------- RGB/HSV HSV/RGB -----------------------

    //--------------------------------------------------------------

    void RGBtoHSV(int red,int green, int blue, float[] hsv){
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
    int HSVtoRGB(float[] hsv){
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
}
