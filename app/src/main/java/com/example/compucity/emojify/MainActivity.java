package com.example.compucity.emojify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private boolean IMAGESAVED = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";


    private String mTempPhotoPath;

    private Bitmap mResultsBitmap;

    @BindView(R.id.image_view)ImageView mImageView;
    @BindView(R.id.emojify_button) Button mEmojifyButton;
    @BindView(R.id.share_button) FloatingActionButton mShareFab;
    @BindView(R.id.save_button) FloatingActionButton mSaveFab;
    @BindView(R.id.clear_button) FloatingActionButton mClearFab;
    @BindView(R.id.title_text_view) TextView mTitleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
    }




    private void IMAGECAPTUREREADYMODE() {
        mEmojifyButton.setVisibility(View.VISIBLE);
        mTitleTextView.setVisibility(View.VISIBLE);
        mShareFab.setVisibility(View.GONE);
        mSaveFab.setVisibility(View.GONE);
        mClearFab.setVisibility(View.GONE);
    }

    private void SHOWIMAGEMODE() {
        mEmojifyButton.setVisibility(View.GONE);
        mTitleTextView.setVisibility(View.GONE);
        mShareFab.setVisibility(View.VISIBLE);
        mSaveFab.setVisibility(View.VISIBLE);
        mClearFab.setVisibility(View.VISIBLE);
    }

    /* launch the camera and take photo
    * @param view the emojifyme button
    */
    @OnClick(R.id.emojify_button)
    public void emojifyMe(View view) {
        //set IMAGESAVED to false to enable saving it
        IMAGESAVED = false;
        // check for  write external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
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
                   /* Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");*/
                    setImage();
                }
            }
            break;
        }

    }

    private void setImage() {
        //setup the activity to show the image
        SHOWIMAGEMODE();
        //get the image
        mResultsBitmap = BitmapUtils.setPic(this, mTempPhotoPath);
        mResultsBitmap=Emojifier.detectFaces(this,mResultsBitmap);
        mImageView.setImageBitmap(mResultsBitmap);
    }

    private void launchCamera() {
        //get the camera capture intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if there is an app to handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            //get the temporary image file
            File imagefile = null;
            try {
                imagefile = BitmapUtils.createImageFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //if file created successfully
            if (imagefile != null) {
                //get the full path of file
                mTempPhotoPath = imagefile.getAbsolutePath();
                //get uri for the file
                Uri uriForFile = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        imagefile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }

        }

    }
    @OnClick(R.id.share_button)
    public void shareMe(View view) {
        //saveMe(view);
        BitmapUtils.shareImage(this, mTempPhotoPath);
    }
    @OnClick(R.id.save_button)
    public void saveMe(View view) {
        if (!IMAGESAVED) {
            IMAGESAVED = true;
            BitmapUtils.deleteImageFile(this, mTempPhotoPath);
            BitmapUtils.saveImage(this, mResultsBitmap);
        } else {
            Toast.makeText(this, "IMAGE SAVED ALREADY", Toast.LENGTH_LONG).show();
        }
    }
    @OnClick(R.id.clear_button)
    public void clearImage(View view) {
        // Clear the image and toggle the view visibility
        mImageView.setImageResource(0);
        //call this mode to make the activity ready to take picture
        IMAGECAPTUREREADYMODE();
        // Delete the temporary image file
        BitmapUtils.deleteImageFile(this, mTempPhotoPath);
    }
}
