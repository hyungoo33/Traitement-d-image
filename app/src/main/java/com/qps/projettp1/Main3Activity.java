package com.qps.projettp1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main3Activity extends AppCompatActivity {
    //le dessin
    ImageView imageResult;
    Bitmap bitmapInitial;
    Bitmap bitmap;
    Bitmap mutableBitmap;
    Canvas canvasMaster;
    int prvX, prvY;
    Paint paintDraw;
    BitmapDrawable drawable;
    Bitmap bitmapToStore;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        imageResult = findViewById(R.id.result);
        paintDraw = new Paint();
        paintDraw.setStyle(Paint.Style.FILL);
        paintDraw.setColor(Color.WHITE);
        paintDraw.setStrokeWidth(10);
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, 1080,1200,false);



        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasMaster = new Canvas(mutableBitmap);
        bitmapInitial = Bitmap.createBitmap(bitmap);

        imageResult.setImageBitmap(mutableBitmap);


        imageResult.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        prvX = x;
                        prvY = y;
                        drawOnProjectedBitMap((ImageView) v, mutableBitmap, prvX, prvY, x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        drawOnProjectedBitMap((ImageView) v, mutableBitmap, prvX, prvY, x, y);
                        prvX = x;
                        prvY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        drawOnProjectedBitMap((ImageView) v, mutableBitmap, prvX, prvY, x, y);
                        break;
                }
                /*
                 * Return 'true' to indicate that the event have been consumed.
                 * If auto-generated 'false', your code can detect ACTION_DOWN only,
                 * cannot detect ACTION_MOVE and ACTION_UP.
                 */
                return true;
            }
        });
    }

    /*
    Project position on ImageView to position on Bitmap draw on it
     */

    private void drawOnProjectedBitMap(ImageView iv, Bitmap bm,
                                       float x0, float y0, float x, float y){
        if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
            //outside ImageView
            return;
        }else{
            float ratioWidth = (float)bm.getWidth()/(float)iv.getWidth();
            float ratioHeight = (float)bm.getHeight()/(float)iv.getHeight();
            canvasMaster.drawLine(
                    x0 * ratioWidth,
                    y0 * ratioHeight,
                    x * ratioWidth,
                    y * ratioHeight,
                    paintDraw);
            imageResult.invalidate();
            drawable = (BitmapDrawable) imageResult.getDrawable();
            bitmapToStore = drawable.getBitmap();
        }
    }

    public void toMainActivity2(View view) {
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
    }

    public void saveImageGallery(View view) {
        Save saveFile = new Save();
        saveFile.SaveImage(Main3Activity.this,bitmapToStore);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.pencolor_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Black:
                paintDraw.setColor(Color.BLACK);
                break;
            case R.id.White:
                paintDraw.setColor(Color.WHITE);
                break;
            case R.id.Blue:
                paintDraw.setColor(Color.BLUE);
                break;
            case R.id.Green:
                paintDraw.setColor(Color.GREEN);
                break;
            case R.id.Red:
                paintDraw.setColor(Color.RED);
                break;
            case R.id.Gray:
                paintDraw.setColor(Color.GRAY);
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }
    public void effacerDessin(View view) {
        bitmap = Bitmap.createBitmap(bitmapInitial);
        canvasMaster = new Canvas(bitmap);
        imageResult.setImageBitmap(bitmap);
    }
}
