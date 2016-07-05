package cn.edu.dhu.figures;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class DrawViewActivity extends AppCompatActivity {

    private FloatingActionButton m_fab;
    public static final int REQUEST_CROP_IMAGE = 100;
    private DrawView m_drawView;

    //Uri转Bitmap
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, R.string.shoot_canceled, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode)
        {
            case REQUEST_CROP_IMAGE:
                Uri uri= getIntent().getData();
                m_drawView.m_bitmap = decodeUriAsBitmap(uri);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_draw_view);

        m_fab=(FloatingActionButton)findViewById(R.id.fab);
        m_drawView=(DrawView)findViewById(R.id.drawView);


        Uri uri= this.getIntent().getData();

        m_drawView.m_bitmap = decodeUriAsBitmap(uri);



        m_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   finish();
             //   Intent itCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              //  startActivityForResult(itCapture, RESULT_SHOT_IMAGE_FRONT);
                Uri uri= getIntent().getData();

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                //        intent.putExtra("aspectX", 2);
                //        intent.putExtra("aspectY", 1);
                //        intent.putExtra("outputX", outputX);
                //        intent.putExtra("outputY", outputY);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                //  intent.putExtra("return-data", false);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                startActivityForResult(intent, REQUEST_CROP_IMAGE);
            }
        });

       // DrawView mView=new DrawView(this);
       // setContentView(mView);

       // TouchImageView   m_imgView = new TouchImageView(this);
       // setContentView(m_imgView);

       /* setContentView(R.layout.activity_draw_view);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
