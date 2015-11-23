package com.example.FiveACVU;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CamCallback implements Camera.PreviewCallback{
    Mat mRgba;
    Mat mYuv;
    Mat mCanny;
    Mat mEdge;
    Mat Draw;
    static long Frame=0;
    public static boolean Save = false;

    static TermCriteria  termcrit;
    static Size subPixWinSize;
    static Size winSize;
    static Size invalidSize;

    final int MAX_COUNT = 500;
    static boolean needToInit;

     Mat gray;
     static Mat prevGray;
     static Mat image;
     static Mat frame;
     static Mat mask;

    static MatOfPoint pointsFeature[];
    static List<Point> pointsFeaturelist;
    static MatOfPoint2f points[];
    static List<Point> pointslist;
    static Scalar color;

    static{
        System.loadLibrary("opencv_java");
        Init();
    }

    static void Init()
    {
        termcrit = new TermCriteria(TermCriteria.COUNT|TermCriteria.EPS,20,0.03);
        subPixWinSize = new Size(10,10);
        winSize = new Size(31,31);
        invalidSize = new Size(-1,-1);


        needToInit = true;
        pointsFeature = new MatOfPoint[2];
        pointsFeature[0] = new MatOfPoint();
        pointsFeature[1] = new MatOfPoint();
        pointsFeaturelist = new ArrayList<Point>();
        points = new MatOfPoint2f[2];
        points[0] = new MatOfPoint2f();
        points[1] = new MatOfPoint2f();
        pointslist = new ArrayList<Point>();

        prevGray = new Mat();
        image = new Mat();
        frame = new Mat();
        mask = new Mat();
        color = new Scalar(68,206,174);
    }

    public void onPreviewFrame(byte[] data, Camera camera){

        // Process the camera data here
        Log.d("CamCallback","OnPreviewFrame");
        int format = camera.getParameters().getPreviewFormat();
        //YUV formats require more conversion
        if (format == ImageFormat.NV21 || format == ImageFormat.YUY2 || format == ImageFormat.NV16)
        {
            Log.d("imageFormat", Integer.toString(format));
            Log.d("imageNV21", Integer.toString(ImageFormat.NV21));
            Log.d("imageYUY2", Integer.toString(ImageFormat.YUY2));
            Log.d("imageNV16", Integer.toString(ImageFormat.NV16));
        }
        else
        {
            Log.d("CamCallback","invalid format");
        }

        /*if(Save)
        {
            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;
            //mRgba = new Mat(height, width, CvType.CV_8UC4);
            mRgba = new Mat();
            mCanny = new Mat();
            mEdge = new Mat();
            mYuv = new Mat(height + height/2, width, CvType.CV_8UC1);
            mYuv.put(0, 0, data);
            //COLOR_YUV2RGBA_NV21
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV2BGRA_NV21);

            Imgproc.Canny(mYuv, mCanny, 80, 100);
            Imgproc.cvtColor(mCanny, mEdge, Imgproc.COLOR_GRAY2RGBA, 4);




            Log.d("CameraService", "handleActionBaz3");

            File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
            path.mkdirs();
            String filename = "Test_" +  Long.toString(Frame)+".png";
            String filename1 = "TestEdge_" +  Long.toString(Frame)+".png";
            File file = new File(path, filename);
            File file1 = new File(path, filename1);

            filename = file.toString();
            filename1 = file1.toString();
            Boolean bool = Highgui.imwrite(filename, mRgba);
            bool = Highgui.imwrite(filename1, mEdge);

            if (bool) {
                Log.d("CamCallback", "SUCCESS writing image to external storage");
                Save = false;
                Frame++;
            }
            else
                Log.d("CamCallback", "Fail writing image to external storage");

        }*/


        if(Save)
        {
            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;

            mRgba = new Mat(height + height/2, width, CvType.CV_8UC4);
            Draw = new Mat(height + height/2, width, CvType.CV_8UC4);
            mYuv = new Mat(height + height/2, width, CvType.CV_8UC1);

            gray = new Mat(height + height/2, width, CvType.CV_8UC1);


            mYuv.put(0, 0, data);
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV2BGRA_NV21);
            Imgproc.cvtColor(mYuv, Draw, Imgproc.COLOR_YUV2BGRA_NV21);
            Imgproc.cvtColor(mRgba, gray, Imgproc.COLOR_BGRA2GRAY,0);
            if( needToInit )
            {
                // automatic initialization
                //Imgproc.goodFeaturesToTrack(mYuv, pointsFeature[1], MAX_COUNT, 0.01, 10, mask, 3, false, 0.04);
                Log.d("CamCallback", "needToInit");
                File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
                path.mkdirs();
                String filename = "FIVEAcVUGray.png";
                String filename1 = "FIVEAcVURbga.png";
                String filename2 = "FIVEAcVUYUV.png";

                File file = new File(path, filename);
                File file1 = new File(path, filename1);
                File file2 = new File(path, filename2);

                filename = file.toString();
                filename1 = file1.toString();
                filename2 = file2.toString();

                Boolean bool = Highgui.imwrite(filename, gray);
                Boolean bool1 = Highgui.imwrite(filename1, mRgba);
                Boolean bool2 = Highgui.imwrite(filename2, mYuv);

                if (bool) {
                    Log.d("CamCallback", "SUCCESS writing gray to external storage");
                }
                else
                    Log.d("CamCallback", "Fail writing gray to external storage");

                if (bool1) {
                    Log.d("CamCallback", "SUCCESS writing RGB to external storage");
                }
                else
                    Log.d("CamCallback", "Fail writing RGB to external storage");

                Imgproc.goodFeaturesToTrack(gray, pointsFeature[1], MAX_COUNT, 0.01, 10);
                pointsFeaturelist = pointsFeature[1].toList();
                points[1].fromList(pointsFeaturelist);

                Mat tmpImg = gray;
                gray = prevGray;
                prevGray = tmpImg;

                List<Point> tmp = points[1].toList();
                points[1].fromList(points[0].toList());
                points[0].fromList(tmp);
                Log.d("CamCallback", "Swapped");


                Scalar color1 = new Scalar(176,224,234);
                int i,k;
                Log.d("CamCallback","PointListSize "+((Integer)pointsFeaturelist.size()).toString());
                for(  i = k = 0; i < pointsFeaturelist.size(); i++ )
                {
                    Core.circle(Draw, pointsFeaturelist.get(i), 10,color1 ,-1,8,0);
                }

                String filename3 = "FIVEAcVU.png";
                File file3 = new File(path, filename3);
                filename3 = file3.toString();
                Boolean bool3 = Highgui.imwrite(filename3, Draw);

                if (bool3)
                {
                    Log.d("CamCallback", "SUCCESS writing to external storage");
                }
                else
                    Log.d("CamCallback", "Fail writing to external storage");
                needToInit = false;
            }
            else if( !points[0].empty() )
            {
                Log.d("CamCallback", "Processing");
                MatOfByte status = new MatOfByte();
                MatOfFloat err = new MatOfFloat();
                if(prevGray.empty())
                    gray.copyTo(prevGray);
                //Video.calcOpticalFlowPyrLK(prevGray, gray, points[0], points[1], status, err, winSize,3, termcrit, 0, 0.001);
                Video.calcOpticalFlowPyrLK(prevGray, gray, points[0], points[1], status, err);
                pointslist = points[1].toList();
                int i, k;
                Log.d("CamCallback","PointListSize "+((Integer)pointslist.size()).toString());
                for( i = k = 0; i < pointslist.size(); i++ )
                {
                    if( 0.0 == status.toList().get(i).floatValue() )
                        continue;

                    pointslist.set(k++, pointslist.get(i));

                }
                points[0].fromList(pointslist);
                //pointslist.subList(k,pointslist.size());
                //pointslist.resize(k);

                Mat tmpImg = gray;
                gray = prevGray;
                prevGray = tmpImg;

                List<Point> tmp = points[1].toList();
                points[1].fromList(points[0].toList());
                points[0].fromList(tmp);
                Log.d("CamCallback", "Swapped1");
            }

        }
    }

    void ProcessImage()
    {

    }

}