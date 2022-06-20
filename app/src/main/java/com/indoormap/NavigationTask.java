package com.indoormap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayDeque;
import java.util.Deque;

public class NavigationTask extends AsyncTask<TaskParameters, Node,String>{
    @SuppressLint("StaticFieldLeak")
    private static ImageView map;
    private Bitmap temp;
    private Paint paint;
    private Canvas canvas;
    private ProgressDialog mDialog;
    public NavigationTask(ProgressDialog dialog) {
        mDialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mDialog != null)
            mDialog.show();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected String doInBackground(TaskParameters... taskParameters) {
        Log.d("NavigationTask","doInbackground");
        //pathfinding
        int[][] mapMatrix = taskParameters[0].MapMatrix;
        map=taskParameters[0].map;
        int[] current = taskParameters[0].current;
        int[] destination = taskParameters[0].destination;
        temp=TaskParameters.getStore_map();
        //The bitmap needs to be set to 0, I don't know why.
        for(int i=0;i<temp.getWidth();i++){
            for(int j=0;j<temp.getHeight();j++){
                temp.setPixel(i,j,0);
            }
        }
        AStar aStar=new AStar(mapMatrix, current, destination);
        Boolean result=aStar.search();
        System.out.println(result);
        Deque<Node> path = new ArrayDeque<>();
        if(result) path =aStar.findpath();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //draw
        paint = new Paint(Paint.ANTI_ALIAS_FLAG); // Brush Antialiasing
        int color = Color.parseColor("#008B00");
        paint.setColor(color);
        if(canvas==null){
            Log.d("canvas","newCanavs");
            canvas = new Canvas(temp);
        }
        //start
        canvas.drawCircle(current[0], current[1], 2.1f, paint);
        //path
        while(!path.isEmpty()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Node curretloc= path.pollLast();
            publishProgress(curretloc);
        }
        //end
        canvas.drawCircle(destination[0], destination[1], 5f, paint);
        return null;
    }

    @Override
    protected void onProgressUpdate(Node... nodes) {
        //bitmap should be mutable
        //location draw a circle
        canvas.drawCircle(nodes[0].getRow(),nodes[0].getColumn(),5f,paint);
        map.setImageBitmap(temp);
        //super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("PostExecute",temp.toString());
        map.setImageBitmap(temp);
        map.setVisibility(View.VISIBLE);
        if(mDialog != null)
            mDialog.dismiss();
        //You cannot recycle the Bitmap while using it on in the UI, the Bitmap has to be kept in memory.
        // Android will in most cases handle recycling just fine,
        // but if you need to recycle yourself you need to make sure to not use the Bitmap instance afterwards
        // (as in this case where the Bitmap instance will be rendered later on).
        //imageview holds bitmap reference
//        if (!temp.isRecycled()) {
//            temp.recycle();
//        }
        super.onPostExecute(s);
    }

    @Override
    protected void onCancelled() {
        Log.d("NavigationTask","onCancelled");
        super.onCancelled();
        if(mDialog != null)
            mDialog.dismiss();
    }
}
