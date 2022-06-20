package com.indoormap;

import static android.graphics.Bitmap.Config.RGBA_F16;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;

import java.util.HashMap;

public class NaviagationService extends Service {
    int[][] MapMatrix;
    Bitmap bitmap,temp;
    TaskParameters taskParameters;

    public NavigationTask navigationTask;

    private pathfindBinder mBinder=new pathfindBinder();
    public NaviagationService() {
        super();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("NavigationService","Start");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("NavigationService","onbind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d("NavigationService","Destroy");
        if(navigationTask!=null) navigationTask.cancel(true);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("NavigationService","unbind");
        return super.onUnbind(intent);
    }

    class pathfindBinder extends Binder{
        pathfindBinder(){
            super();
        }
        public void imagetoArray(ImageView map){
            //Convert the image to bitmap, convert it to int[], and convert the image to transparent
            bitmap = ((BitmapDrawable) map.getDrawable()).getBitmap();
            temp=bitmap.copy(RGBA_F16,true);
            int height=bitmap.getHeight();
            int width=bitmap.getWidth();
            MapMatrix=new int[width][height];
            int count=0;
            for(int i=0;i<width;i++){
                for(int j=0;j<height;j++){
                    int pixel  = (bitmap.getPixel(i,j));
                    count++;
                    if(pixel == Color.RED) {//white path
                        MapMatrix[i][j] = 1;
                        //temp.setPixel(i,j,0);
                    }else {
                        MapMatrix[i][j] = 0;
                        //temp.setPixel(i,j,1);
                    }
                }
            }
            map.setImageBitmap(temp);
            TaskParameters.setStore_map(temp);
        }

        public void findpath(ImageView map, ProgressDialog dialog, int[] current, int[] destination){
            //After setting, the path can be displayed asynchronously
            Log.e("findPath",current.toString()+" "+destination.toString());
            navigationTask=new NavigationTask(dialog);
            taskParameters=new TaskParameters(MapMatrix,current,destination,map,TaskParameters.getStore_map());
            navigationTask.execute(taskParameters);
        }

        public void imagetoPath(ImageView map, HashMap<Integer,int[]> locations) {
            bitmap = ((BitmapDrawable) map.getDrawable()).getBitmap();
            temp=bitmap.copy(RGBA_F16,true);
            int height=bitmap.getHeight();
            int width=bitmap.getWidth();
            MapMatrix=new int[width][height];
            Path path = new Path();
            Paint mPaint = new Paint();
            mPaint.setDither(true);
            mPaint.setColor(0xFF660000);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(3);
            Canvas mCanvas = new Canvas(temp);
            mCanvas.drawPath(path, mPaint);
            for(int i=0;i<locations.size();i++){
                path.moveTo(locations.get(i)[0], locations.get(i)[1]);
                path.lineTo(locations.get(i)[0]+locations.get(i)[2], locations.get(i)[1]+locations.get(i)[3]);
                mCanvas.drawPath(path, mPaint);
            }
            map.setImageBitmap(temp);
        }
    }
}
