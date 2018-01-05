package com.example.compucity.emojify;/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class BitmapUtils {

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";
    public static String  getFileName(){
        //get the formatted time now
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
        //set the file name
        return "JPEG_"+timestamp+"_";
    }
    public static File createImageFile(Context context) throws IOException {
     //get the file name
     String filename=getFileName();
     //get the external directory
     File dir=context.getExternalCacheDir();
     //return new temporary file
     return  File.createTempFile(filename,".jpg",dir);
    }
    public static void galleryAddPic(Context context,String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    public static Bitmap setPic(Context context, String imagePath) {
        // Get device screen size information
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);

        int targetH = metrics.heightPixels;
        int targetW = metrics.widthPixels;

        // Get the dimensions of the original bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath);
    }

    /**
     * Helper method for sharing an image.
     *
     * @param context   The image context.
     * @param imagePath The path of the image to be shared.
     */
    static void shareImage(Context context, String imagePath) {
        // Create the share intent and start the share activity
        File imageFile = new File(imagePath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri photoURI = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, imageFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.setType("image/*");
        context.startActivity(shareIntent);
    }

    public static String saveImage(Context context, Bitmap image) {
        String savedImagePath=null;
        //getfileName
        String fileName = getFileName()+".jpg";
        //get the directory
        File dir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/Emojify");
        boolean mkdirsuccess=true;
        if(!dir.exists()){
            mkdirsuccess = dir.mkdirs();
        }
        //if directory exist save file into it
        String SavedImagePath=null;
        if(mkdirsuccess){
            File imagefile = new File(dir, fileName);
            SavedImagePath=imagefile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imagefile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
                //add the image to gallery
                galleryAddPic(context,SavedImagePath);

                //show toast with location
                Toast.makeText(context,
                        context.getString(R.string.saved_message,SavedImagePath),
                        Toast.LENGTH_LONG)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return savedImagePath;
    }

    public static void deleteImageFile(Context context, String mTempPhotoPath) {
        File file = new File(mTempPhotoPath);
        boolean deleted = file.delete();
        if(!deleted){
            Toast.makeText(context,context.getString(R.string.error),Toast.LENGTH_LONG).show();
        }
    }
}
