package com.example.compucity.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


/**
 * Created by CompuCity on 1/5/2018.
 */

public class Emojifier {
    private static final String TAG=Emojifier.class.getSimpleName();
    public static void detectFaces(Context context,Bitmap image){
        //get the detector
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        Frame frame=new Frame.Builder().setBitmap(image).build();
        SparseArray<Face> faces = detector.detect(frame);
        Log.e(TAG,"number of faces= "+faces.size());
        if(faces.size()==0){
            Toast.makeText(context,R.string.no_faces_message,Toast.LENGTH_SHORT).show();
        }else{
            for(int i=0;i<faces.size();i++){
                Face face=faces.valueAt(i);
                getClassification(face);
            }
        }
        detector.release();
    }

    private static void getClassification(Face face) {
        Log.e(TAG, "getClassifications: smilingProb = " + face.getIsSmilingProbability());
        Log.e(TAG, "getClassifications: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.e(TAG, "getClassifications: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());
    }

}
