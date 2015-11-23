package com.example.FiveACVU;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Calendar;


public class VRActivity extends UnityPlayerNativeActivity {
    //SensorMgr objSensorMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //objSensorMgr = new SensorMgr(getApplicationContext());


        final long delay = 5000;//ms

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                // Setup the camera and the preview object
                Camera mCamera = null;
                mCamera = Camera.open(0);
                CamPreview camPreview = new CamPreview(VRActivity.this,mCamera);
                camPreview.setSurfaceTextureListener(camPreview);

                ViewGroup rootView = (ViewGroup)VRActivity.this.findViewById
                        (android.R.id.content);

                // find the first leaf view (i.e. a view without children)
                // the leaf view represents the topmost view in the view stack
                View topMostView = getLeafView(rootView);

                // let's add a sibling to the leaf view
                ViewGroup leafParent = (ViewGroup)topMostView.getParent();
                Button sampleButton = new Button(VRActivity.this);
                sampleButton.setText("Press Me");

                FrameLayout preview = new FrameLayout(VRActivity.this);

                //leafParent.addView(sampleButton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                //      ViewGroup.LayoutParams.WRAP_CONTENT));
                leafParent.addView(preview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                // Connect the preview object to a FrameLayout in your UI
                // You'll have to create a FrameLayout object in your UI to place this preview in
                preview.addView(camPreview);

                // Attach a callback for preview
                CamCallback camCallback = new CamCallback();
                mCamera.setPreviewCallback(camCallback);

            }
        };

        handler.postDelayed(runnable, delay);

        //setContentView(R.layout.activity_vr);
    }

    private View getLeafView(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup)view;
            for (int i = 0; i < vg.getChildCount(); ++i) {
                View chview = vg.getChildAt(i);
                View result = getLeafView(chview);
                if (result != null)
                    return result;
            }
            return null;
        }
        else {
            Log.v("VRActivity", "Found leaf view");
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        //objSensorMgr.closeSensorManager();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        //objSensorMgr.initSensorManager();
    }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Toast.makeText(this, "onKeyDown", Toast.LENGTH_SHORT).show();
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            Toast.makeText(this, "ACTION_DOWN", Toast.LENGTH_SHORT).show();
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    Toast.makeText(this, "KEYCODE_BACK", Toast.LENGTH_SHORT).show();
                    mUnityPlayer.pause();
                    mUnityPlayer.quit();
                    return true;
            }
        }

        return mUnityPlayer.injectEvent(event);
    }

    private float x1,x2,y1,y2;
    static final int MIN_DISTANCE = 80;
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;


    @Override public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if(clickDuration < MAX_CLICK_DURATION) {
                    //Toast.makeText(this, "Stop Moving", Toast.LENGTH_SHORT).show();
                    UnityDataMgr.TransformX = 0.0f;
                    UnityDataMgr.TransformZ = 0.0f;
                    //CameraService.Save = true;
                    CamCallback.Save = true;
                }
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    if(x2>x1)
                    {
                        //Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show();
                        UnityDataMgr.TransformZ = -0.05f;
                    }
                    else
                    {
                        //Toast.makeText(this, "right2left swipe", Toast.LENGTH_SHORT).show();
                        UnityDataMgr.TransformZ = 0.05f;
                    }

                }
                if (Math.abs(deltaY) > MIN_DISTANCE)
                {
                    if(y2>y1)
                    {
                        //Toast.makeText(this, "down2up swipe", Toast.LENGTH_SHORT).show();
                        UnityDataMgr.TransformX = 0.05f;
                    }
                    else
                    {
                        //Toast.makeText(this, "up2down swipe", Toast.LENGTH_SHORT).show();
                        UnityDataMgr.TransformX = -0.05f;
                    }

                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
