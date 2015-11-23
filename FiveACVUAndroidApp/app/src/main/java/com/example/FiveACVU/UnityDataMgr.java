package com.example.FiveACVU;

/**
 * Created by swaroop on 3/30/2015.
 */

public class UnityDataMgr
{
    //Rotational Data
    static float RotationX = 0.0f;
    static float RotationY = 0.0f;
    static float RotationZ = 0.0f;

    //Transform Data
    static float TransformX = 0.0f;
    static float TransformY = 0.0f;
    static float TransformZ = 0.0f;

    //Methods to get the rotational and transformation Data in t
    public static float GetRotationX()
    {
        return RotationX;
    }

    public static float GetRotationY()
    {
        return RotationY;
    }

    public static float GetRotationZ()
    {
        return RotationZ;
    }

    public static float GetTransformX()
    {
        return TransformX;
    }

    public static float GetTransformY()
    {
        return TransformY;
    }

    public static float GetTransformZ()
    {
        return TransformZ;
    }
}
