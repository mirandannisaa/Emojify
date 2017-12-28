package com.example.compucity.emojify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private boolean IMAGESAVED = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";

    private ImageView mImageView;

    private Button mEmojifyButton;
    private FloatingActionButton mShareFab;
    private FloatingActionButton mSaveFab;
    private FloatingActionButton mClearFab;

    private TextView mTitleTextView;

    private String mTempPhotoPath;

    private Bitmap mResultsBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the views
        mImageView = (ImageView) findViewById(R.id.image_view);
        mEmojifyButton = (Button) findViewById(R.id.emojify_button);
        mShareFab = (FloatingActionButton) findViewById(R.id.share_button);
        mSaveFab = (FloatingActionButton) findViewById(R.id.save_button);
        mClearFab = (FloatingActionButton) findViewById(R.id.clear_button);
        mTitleTextView = (TextView) findViewById(R.id.title_text_view);
    }


    public void shareMe(View view) {
    }

    public void saveMe(View view) {
    }

    public void clearImage(View view) {
    }

    /* launch the camera and take photo
    * @param view the emojifyme button
    */
    public void emojifyMe(View view) {
        // check for  write external storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                //if permission granted launch camera
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Permission is denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap thumbnail = (Bitmap) extras.get("data");
                    setImage();
                }
            }
        }
    }

    private void setImage() {
        mEmojifyButton.setVisibility(View.GONE);
        mTitleTextView.setVisibility(View.GONE);
        mShareFab.setVisibility(View.VISIBLE);
        mSaveFab.setVisibility(View.VISIBLE);
        mClearFab.setVisibility(View.VISIBLE);
        Bitmap image=BitmapUtils.setPic(this,mTempPhotoPath);
        mImageView.setImageBitmap(image);
    }

    private void launchCamera() {
        //get the camera capture intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if there is an app to handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            //get the temporary image file
            File imagefile=null;
            try {
                imagefile = BitmapUtils.createImageFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //if file created successfully
            if(imagefile!=null){
                //get the full path of file
                mTempPhotoPath=imagefile.getAbsolutePath();
                //get uri for the file
                Uri uriForFile = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imagefile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,uriForFile);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }

        }

    }
}
