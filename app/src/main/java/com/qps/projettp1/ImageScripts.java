package com.qps.projettp1;

import android.graphics.Bitmap;
import android.renderscript.Allocation;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ImageScripts extends AppCompatActivity {


    public void toGrayRs(Bitmap bp){

        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs,bp);
        Allocation output = Allocation.createTyped(rs,input.getType());
        ScriptC_gray grayScript = new ScriptC_gray(rs);

        grayScript.forEach_toGray(input, output);

        output.copyTo(bp);

        input.destroy();output.destroy();
        grayScript.destroy();rs.destroy();


    }
    public void ColoriseRS(Bitmap bp){
        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(this);

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
        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs,bp);
        Allocation output = Allocation.createTyped(rs,input.getType());
        ScriptC_convolutionmoyenne moyScript = new ScriptC_convolutionmoyenne(rs);

        moyScript.set_in(input);
        moyScript.set_filterSize(3);
        output.copyTo(bp);

        input.destroy();output.destroy();
        moyScript.destroy();rs.destroy();


    }




}
