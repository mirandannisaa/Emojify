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
    private static double SMILING_PROP_THRESHOLD=.15;
    private static double EYE_OPEN_PROP_THRESHOLD=.5;

    // Enum for all possible Emojis
    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }
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
                whichEmoji(face);
            }
        }
        detector.release();
    }

    private static void whichEmoji(Face face) {
        Log.e(TAG, "getClassifications: smilingProb = " + face.getIsSmilingProbability());
        Log.e(TAG, "getClassifications: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.e(TAG, "getClassifications: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());
        boolean smiling = face.getIsSmilingProbability() > SMILING_PROP_THRESHOLD;
        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROP_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROP_THRESHOLD;
        // Determine and log the appropriate emoji
        Emoji emoji;
        if(smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }
        // Log the chosen Emoji
        Log.e(TAG, "whichEmoji: " + emoji.name());
    }

}
