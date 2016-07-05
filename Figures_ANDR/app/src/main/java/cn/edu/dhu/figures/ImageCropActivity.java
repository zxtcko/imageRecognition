package cn.edu.dhu.figures;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageCropActivity extends AppCompatActivity {

    private Button m_btCropLargeImg;
    private Button m_btCropSmallImg;
    private Button m_btCropShootImg;

    private ImageView m_imageView;

    public static final int CHOOSE_BIG_PICTURE   = 1001;
    public static final int CHOOSE_SMALL_PICTURE = 1002;
    public static final int TAKE_BIG_PICTURE     = 1003;
    public static final int CROP_BIG_PICTURE     = 1004;
    public static final int CROP_SMALL_PICTURE   = 1005;
    public static final int TAKE_SMALL_PICTURE   = 1006;


    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";//temp file
    private static final String TEMP_PHOTO_FILE = "temp.jpg";//temp file

    private Uri m_imageUri= Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /////////////////
        m_btCropLargeImg=(Button) findViewById(R.id.cropLargeImg);
        m_btCropSmallImg=(Button) findViewById(R.id.cropSmallImg);
        m_btCropShootImg=(Button) findViewById(R.id.cropShootImg);

        m_imageView     =(ImageView)findViewById(R.id.imageView);

        //拍摄侧面
        m_btCropLargeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 2);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 600);
                intent.putExtra("outputY", 300);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, m_imageUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                startActivityForResult(intent, CHOOSE_BIG_PICTURE);
            }
        });

        //拍摄侧面
        m_btCropSmallImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 2);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 100);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                startActivityForResult(intent, CHOOSE_SMALL_PICTURE);
            }
        });

        //拍摄侧面
        m_btCropShootImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                intent.putExtra(MediaStore.EXTRA_OUTPUT, m_imageUri);
                startActivityForResult(intent, TAKE_BIG_PICTURE);//or TAKE_SMALL_PICTURE
            }
        });

    }

    @Override
    protected  void onActivityResult(int requestCode,int resCode,Intent data)    {
        super.onActivityResult(requestCode, resCode, data);
        switch (requestCode) {
            case CHOOSE_BIG_PICTURE:
                //Log.d(TAG, "CHOOSE_BIG_PICTURE: data = " + data);//it seems to be null
                if(m_imageUri != null){

                    m_imageUri=data.getData();
                    cropImageUri(m_imageUri, 800, 400, CROP_BIG_PICTURE);
                    //Bitmap bitmap = decodeUriAsBitmap(m_imageUri);//decode bitmap
                    // m_imageView.setImageBitmap(bitmap);
                }
                break;
            case CHOOSE_SMALL_PICTURE:
                if(data != null){
                    m_imageUri=data.getData();
                   cropImageUri(m_imageUri, 800, 400, CROP_BIG_PICTURE);
                   // Bitmap bitmap = data.getParcelableExtra("data");
                   // m_imageView.setImageBitmap(bitmap);
                }else{
                  //  Log.e(TAG, "CHOOSE_SMALL_PICTURE: data = " + data);
                }
                break;
            case TAKE_BIG_PICTURE:
                //Log.d(TAG, "TAKE_BIG_PICTURE: data = " + data);//it seems to be null
                //TODO sent to crop
                cropImageUri(m_imageUri, 800, 400, CROP_BIG_PICTURE);
                break;
            case TAKE_SMALL_PICTURE:
               // Log.i(TAG, "TAKE_SMALL_PICTURE: data = " + data);
                //TODO sent to crop
                cropImageUri(m_imageUri, 300, 150, CROP_SMALL_PICTURE);
                break;

            case CROP_BIG_PICTURE://from crop_big_picture
                //Log.d(TAG, "CROP_BIG_PICTURE: data = " + data);//it seems to be null
                if(m_imageUri != null){
                    Bitmap bitmap = decodeUriAsBitmap(m_imageUri);
                    m_imageView.setImageBitmap(bitmap);
                }
                break;
            case CROP_SMALL_PICTURE:
                if(m_imageUri != null){
                    Bitmap bitmap = decodeUriAsBitmap(m_imageUri);
                    m_imageView.setImageBitmap(bitmap);
                } else {
                   // Log.e(TAG, "CROP_SMALL_PICTURE: data = " + data);
                }
                break;
            default:
                break;
        }
    }

    //裁剪图片
    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        //intent.putExtra("aspectX", 2);
       // intent.putExtra("aspectY", 1);
       // intent.putExtra("outputX", outputX);
       // intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    //Uri转Bitmap
    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

}
