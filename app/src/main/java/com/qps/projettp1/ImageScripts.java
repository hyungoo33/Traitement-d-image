package com.qps.projettp1;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ImageScripts extends AppCompatActivity {







    public void toGrayRs(Bitmap bp){

        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(MainActivity.getContext());

        Allocation input = Allocation.createFromBitmap(rs,bp);
        Allocation output = Allocation.createTyped(rs,input.getType());
        ScriptC_gray grayScript = new ScriptC_gray(rs);

        grayScript.forEach_toGray(input, output);

        output.copyTo(bp);

        input.destroy();output.destroy();
        grayScript.destroy();rs.destroy();


    }
    public void coloriseRS(Bitmap bp){
        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(MainActivity.getContext());

        Allocation input = Allocation.createFromBitmap(rs,bp);
        Allocation output = Allocation.createTyped(rs,input.getType());
        ScriptC_colorise colorScript = new ScriptC_colorise(rs);
        int hue = new Random().nextInt(360);

        colorScript.set_hue((short)hue);

        output.copyTo(bp);

        input.destroy();output.destroy();
        colorScript.destroy();rs.destroy();

    }
    public void convolutionMoyenneRs(Bitmap bp){
        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(MainActivity.getContext());

        Allocation input = Allocation.createFromBitmap(rs,bp);
        Allocation output = Allocation.createTyped(rs,input.getType());
        ScriptC_convolutionmoyenne moyScript = new ScriptC_convolutionmoyenne(rs);

        moyScript.set_filterSize(11);
        moyScript.set_imageHeight(bp.getHeight());
        moyScript.set_imageWidth(bp.getWidth());
        moyScript.forEach_moyConv(input,output);

        output.copyTo(bp);

        input.destroy();output.destroy();
        moyScript.destroy();rs.destroy();
    }
    public void convolution3x3Rs(Bitmap bp,float[] usedFiltre,boolean align){
        float div = 0;
        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(MainActivity.getContext());

        Allocation input = Allocation.createFromBitmap(rs,bp);
        Allocation output = Allocation.createTyped(rs,input.getType());
        ScriptC_convfiltre3x3 prewScript = new ScriptC_convfiltre3x3(rs);

        Allocation filtre = Allocation.createSized(rs, Element.F32(rs),usedFiltre.length);
        filtre.copyFrom(usedFiltre);
        prewScript.bind_filter(filtre);

        for(float i : usedFiltre) {
            div += i;
        }

        prewScript.set_align(align);
        prewScript.set_div(div);
        prewScript.set_imageHeight(bp.getHeight());
        prewScript.set_imageWidth(bp.getWidth());
        prewScript.forEach_conv(input,output);

        output.copyTo(bp);

        input.destroy();output.destroy();
        prewScript.destroy();rs.destroy();


    }




}
