package cn.edu.dhu.figures;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


//
public class MeasureFragment extends Fragment {
    /// webservice 设置
    private WebServicer m_WSR = new WebServicer();
    private ImageCroper m_imgCorper = new ImageCroper();

    //界面控件
    private ImageButton m_btShootFront;
    private ImageButton m_btSelectFront; //控件
    private ImageButton m_btShootSide;
    private ImageButton m_btSelectSide;
    private Button m_btMeasure;
    private ImageView m_ivFrontPhoto;
    private ImageView m_ivSidePhoto;

    private ProgressBar m_pbMain;
    private TextView m_tvInfo;

    private EditText m_etWeight;//体重
    private EditText m_etHeight;//身高

    private Handler m_Handler; //线程消息处理
    //private  String     m_strInfo;

    Boolean m_bFrontPicLoaded = false;
    Boolean m_bSidePicLoaded = false;
    float m_fHeight = 160;
    float m_fWeight = 52;

    private String m_imgCachePath; //图片本地存储路径
    private int m_picCompressRate = 70; //图片文件压缩比

    private Uri m_frontImgUri;
    private Uri m_sideImgUri;

    private static final String TAG = "MeasureFragment";

    public static final int RESULT_SHOT_IMAGE_FRONT = 100;
    public static final int RESULT_LOAD_IMAGE_FRONT = 101;
    public static final int RESULT_SHOT_IMAGE_SIDE = 102;
    public static final int RESULT_LOAD_IMAGE_SIDE = 103;
    public static final int RESULT_CROP_BIG_PICTURE = 104;


    public static final int UPDATE_INFO_MESSAGE = 1001;
    public static final int FINISH_MEASURE_MESSAGE = 1002;

    private void writeBitmap2File(Bitmap bitmap, String fname) {
        if (bitmap == null)
            return;

        FileOutputStream b = null;
        File file = new File(m_imgCachePath);
        file.mkdirs();//myimage
        String fileName = m_imgCachePath + fname;
        try {
            b = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, m_picCompressRate, b);// ?????д?????
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //初始化
    private void init() {

        Activity act=getActivity();
       // m_imgCachePath = Environment.getExternalStorageDirectory() +"/Figures/";
        m_imgCachePath=Setting.getInstance().m_dataPath;
        //设置图片文件路径
      //  m_imgCachePath = act.getApplicationContext().getFilesDir().getAbsolutePath() + "/imgCache/";
        //关联控件变量
        m_btShootFront = (ImageButton) act.findViewById(R.id.btShootFront);
        m_btSelectFront = (ImageButton) act.findViewById(R.id.btSelectFront);
        m_btShootSide = (ImageButton) act.findViewById(R.id.btShootSide);
        m_btSelectSide = (ImageButton) act.findViewById(R.id.btSelectSide);
        m_ivFrontPhoto = (ImageView) act.findViewById(R.id.ivFrontPhoto);
        m_ivSidePhoto = (ImageView) act.findViewById(R.id.ivSidePhoto);
        m_btMeasure = (Button) act.findViewById(R.id.btStartMeasure);
        m_etHeight = (EditText) act.findViewById(R.id.etHeight);
        m_etWeight = (EditText) act.findViewById(R.id.etWeight);

        m_etHeight.setText(Float.toString(m_fHeight));
        m_etWeight.setText(Float.toString(m_fWeight));

        m_tvInfo = (TextView) act.findViewById(R.id.tvINFO);
        m_tvInfo.setText("");

        m_bFrontPicLoaded = false;
        m_bSidePicLoaded = false;



        View.OnClickListener clkTV = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NumberDialog numberDialog = new NumberDialog(getActivity());
                numberDialog.setDateFormat(NumberDialog.YEAR_MONTH_DATE);
                numberDialog.show();
                numberDialog.setOnclickLinsterDialog(new NumberDialog.OnclickLinsterDialog() {

                    @Override
                    public void choseDate(String height,String weight) {
                        // TODO Auto-generated method stub
                        // Toast.makeText(getActivity(), height, 1000).show();
                        m_etHeight.setText(height);
                        m_etWeight.setText(weight);
                    }
                });
            }
        };


        TextView tvHeight= (TextView)act.findViewById(R.id.tvHeight);
        tvHeight.setOnClickListener(clkTV);

        TextView tvWeight= (TextView)act.findViewById(R.id.tvWeight);
        tvWeight.setOnClickListener(clkTV);


        // m_bmpFront=BitmapFactory.decodeResource(getResources(),R.drawable.outline_front);
        // m_bmpSide=BitmapFactory.decodeResource(getResources(),R.drawable.outline_side);
        // m_ivFrontPhoto.setImageBitmap(m_bmpFront);

        // 处理相应消息
        m_Handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MeasureFragment.UPDATE_INFO_MESSAGE://TestHandler是Activity的类名
                        //得到Handle的通知了 这个时候你可以做相应的操作，本例是使用TextView 来显示时间
                        m_tvInfo.setText(msg.obj.toString());
                        break;
                    case MeasureFragment.FINISH_MEASURE_MESSAGE://TestHandler是Activity的类名
                        MainActivity act=(MainActivity)getActivity();
                        act.ShowSizes();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void updataFrontImage(Uri frontUri) {
        m_frontImgUri = frontUri;
        Bitmap bmp = decodeUriAsBitmap(frontUri);
        releaseImageViewData(m_ivFrontPhoto);
        m_ivFrontPhoto.setImageBitmap(bmp);
        writeBitmap2File(bmp, "frontView.jpg");
        bmp = null;
    }

    private void updataSideImage(Uri sideUri) {
        m_sideImgUri = sideUri;
        Bitmap bmp = decodeUriAsBitmap(sideUri);
        releaseImageViewData(m_ivSidePhoto);
        m_ivSidePhoto.setImageBitmap(bmp);
        writeBitmap2File(bmp, "sideView.jpg");
        bmp = null;
    }

    //检查输入的参数是否正确
    private boolean checkInputParams() {
        String edHeight = m_etHeight.getText().toString();
        String etWeight = m_etWeight.getText().toString();
        try {
            m_fHeight = Float.valueOf(edHeight);
            m_fWeight = Float.valueOf(etWeight);
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "检查身高和体重输入", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(m_fHeight > 140 && m_fHeight < 200)) {
            String mes;
            mes = "身高" + Float.toString(m_fHeight) + "不在范围内：140~200";
            Toast.makeText(getActivity().getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(m_fWeight > 30 && m_fWeight < 150)) {
            String mes;
            mes = "体重" + Float.toString(m_fWeight) + "不在范围内：30~150";
            Toast.makeText(getActivity().getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
            return false;
        }

//        if (Datas.m_bmFrontView==null)
//        {
//            Toast.makeText(getApplicationContext(),"请检查正面照片",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (Datas.m_bmSideView==null)
//        {
//            Toast.makeText(getApplicationContext(),"请检查侧面照片",Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    //
    protected void loadImageViewData() {
        Log.i(TAG, "loadImageViewData: ");
        try {
            String fvFileName = m_imgCachePath + "frontView.jpg";
            String svFileName = m_imgCachePath + "sideView.jpg";

            m_frontImgUri = Uri.fromFile(new File(fvFileName));
            m_sideImgUri  = Uri.fromFile(new File(svFileName));

            //http://blog.csdn.net/cctcc/article/details/40118477
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inTempStorage = new byte[100 * 1024];
            opts.inSampleSize = 4;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;

            InputStream is = new FileInputStream(fvFileName);
            m_ivFrontPhoto.setImageBitmap(BitmapFactory.decodeStream(is, null, opts));

            is = new FileInputStream(svFileName);
            m_ivSidePhoto.setImageBitmap(BitmapFactory.decodeStream(is, null, opts));
        } catch (Exception e) {

        }
    }

    public static void releaseImageViewData(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();


        Log.i(TAG, "onStart: ");
        //加载图片信息
        loadImageViewData();

    }


    @Override
    public void onStop() {
        super.onStop();
           Log.i(TAG, "onStop: ");

        releaseImageViewData(m_ivFrontPhoto);
        releaseImageViewData(m_ivSidePhoto);

    }

    private void setListener() {
        //???ü?????
        m_btShootFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                intent.putExtra(MediaStore.EXTRA_OUTPUT, m_imgCorper.m_imageUri);
                startActivityForResult(intent, RESULT_SHOT_IMAGE_FRONT);//or TAKE_SMALL_PICTURE
            }
        });
        //拍摄侧面
        m_btShootSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                intent.putExtra(MediaStore.EXTRA_OUTPUT, m_imgCorper.m_imageUri);
                startActivityForResult(intent, RESULT_SHOT_IMAGE_SIDE);//or TAKE_SMALL_PICTURE

            }
        });

        //
        m_btSelectFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(it, RESULT_LOAD_IMAGE_FRONT);
            }
        });

        //
        m_btSelectSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(it, RESULT_LOAD_IMAGE_SIDE);
            }
        });

        //测量
        m_btMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMeasure();
            }
        });


        m_ivFrontPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // if (Datas.m_bmFrontView != null) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), DrawViewActivity.class);
                intent.setData(m_frontImgUri);
                // intent.putExtra("picUri", m_frontImgUri);
                MeasureFragment.this.startActivity(intent);
//                return;
//                } else {
//                    Toast.makeText(getApplicationContext(), R.string.shoot_first, Toast.LENGTH_SHORT).show();
//                }
            }
        });

        m_ivSidePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), DrawViewActivity.class);
                intent.setData(m_sideImgUri);
                // intent.putExtra("picUri", m_frontImgUri);
                MeasureFragment.this.startActivity(intent);
                return;

//                //启动activity显示结果
//                Intent itShowResult = new Intent();
//                itShowResult.setClass(MeasureFragment.this, ImageCropActivity.class);
//                MeasureFragment.this.startActivity(itShowResult);
            }
        });



    }


    //开始测量
    private void startMeasure() {
        //检查输入
        if (!checkInputParams())
            return;

        Thread thread = new Thread() {
            public void run() {
                Message msg = new Message();
                msg.what = UPDATE_INFO_MESSAGE;
                msg.obj = "正在处理正面图片...";
                m_Handler.sendMessage(msg);
                ///
                String fvFileName = m_imgCachePath + "frontView.jpg";
                if (m_WSR.uploadImage(fvFileName, true)) {
                    Log.i("main", "上传正面文件成功！");

                    msg = new Message();
                    msg.what = UPDATE_INFO_MESSAGE;
                    msg.obj = "处理正面图片完成...";
                    m_Handler.sendMessage(msg);
                } else {
                    Log.i("main", "上传正面文件失败！");
                    msg = new Message();
                    msg.what = UPDATE_INFO_MESSAGE;
                    msg.obj = "处理正面图片失败...";
                    m_Handler.sendMessage(msg);
                }

                msg = new Message();
                msg.what = UPDATE_INFO_MESSAGE;
                msg.obj = "正在处理侧面图片...";
                m_Handler.sendMessage(msg);
                //
                String svFileName = m_imgCachePath + "sideView.jpg";
                if (m_WSR.uploadImage(svFileName, false)) {
                    Log.i("main", "上传侧面文件成功！");

                    msg = new Message();
                    msg.what = UPDATE_INFO_MESSAGE;
                    msg.obj = "处理侧面图片完成...";
                    m_Handler.sendMessage(msg);
                } else {
                    Log.i("main", "上传侧面文件失败！");

                    msg = new Message();
                    msg.what = UPDATE_INFO_MESSAGE;
                    msg.obj = "处理侧面图片失败...";
                    m_Handler.sendMessage(msg);
                }

                msg = new Message();
                msg.what = UPDATE_INFO_MESSAGE;
                msg.obj = "开始测量...";
                m_Handler.sendMessage(msg);
                //m_pbMain.setProgress(100);
                //开始测量
                if (!m_WSR.startRemoteMeasure(m_fHeight, m_fWeight)) {

                    msg = new Message();
                    msg.what = UPDATE_INFO_MESSAGE;
                    msg.obj = "测量失败！请检查图片是否正确。";
                    m_Handler.sendMessage(msg);
                    //Toast.makeText(getApplicationContext(),"测量出错！",Toast.LENGTH_SHORT).show();
                    return;
                }

                msg = new Message();
                msg.what = UPDATE_INFO_MESSAGE;
                msg.obj = "";
                m_Handler.sendMessage(msg);

                msg = new Message();
                msg.what = FINISH_MEASURE_MESSAGE;
                msg.obj = "";
                m_Handler.sendMessage(msg);

                //m_pbMain.setProgress(100);
//                MainActivity act=(MainActivity)getActivity();
//                act.ShowSizes();
                //act.m_resultFragment.refreshWebView();




                //启动activity显示结果
//                Intent itShowResult = new Intent();
//                itShowResult.setClass(getActivity(), ResultFragment.class);
//                MeasureFragment.this.startActivity(itShowResult);

            }
        };
        thread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_measure, null);


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化
        init();
        //设置事件监听
        setListener();
        //加载图片信息
        //loadImageViewData();
        Log.i(TAG, "onActivityCreated: ");
    }


//    public String getRealPathFromURI(Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        String result = null;
//
//        CursorLoader cursorLoader = new CursorLoader(
//                this,
//                contentUri, proj, null, null, null);
//        Cursor cursor = cursorLoader.loadInBackground();
//
//        if(cursor != null){
//            int column_index =
//                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            result = cursor.getString(column_index);
//        }
//
//        return result;
//    }

    @Override
    public void onActivityResult(int requestCode, int resCode, Intent data) {
        super.onActivityResult(requestCode, resCode, data);

        Log.i(TAG, "onActivityResult: ");

        if (resCode != Activity.RESULT_OK) {
            Toast.makeText(getActivity(), R.string.shoot_canceled, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case RESULT_SHOT_IMAGE_FRONT: {
                //
                if (!m_imgCorper.isSDCARDMounted()) {
                    return;
                }
                updataFrontImage(m_imgCorper.m_imageUri);
            }
            break;
            case RESULT_SHOT_IMAGE_SIDE: {
                if (!m_imgCorper.isSDCARDMounted()) {
                    return;
                }
                updataSideImage(m_imgCorper.m_imageUri);
            }
            break;

            case RESULT_LOAD_IMAGE_FRONT: {
                updataFrontImage(data.getData());
            }
            break;
            case RESULT_LOAD_IMAGE_SIDE: {
                updataSideImage(data.getData());
            }
            break;

            case RESULT_CROP_BIG_PICTURE:

                break;
            default:
                ;
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    public void resetPictures()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定要重置吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //AccoutList.this.finish();
                Uri fileUri = Uri.parse("android.resource://cn.edu.dhu.figures/" + R.drawable.outline_front1);
                updataFrontImage(fileUri);
                fileUri = Uri.parse("android.resource://cn.edu.dhu.figures/" + R.drawable.outline_side1);
                updataSideImage(fileUri);
                loadImageViewData();
            }
        });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
                resetPictures();

//            releaseImageViewData(m_ivFrontPhoto);
//            releaseImageViewData(m_ivSidePhoto);
//
//            InputStream is = getResources().openRawResource(R.drawable.outline_front);
//            m_ivFrontPhoto.setImageBitmap(BitmapFactory.decodeStream(is));
//            is = getResources().openRawResource(R.drawable.outline_side);
//            m_ivSidePhoto.setImageBitmap(BitmapFactory.decodeStream(is));


            // m_ivFrontPhoto.setImageResource(R.drawable.outline_front);
            // m_ivSidePhoto.setImageResource(R.drawable.outline_side);

            // m_ivFrontPhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.outline_front));
            //m_ivSidePhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.outline_side));

            //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.outline_front);
            //m_ivFrontPhoto.setImageBitmap(null);
            //m_ivFrontPhoto.setImageBitmap(m_bmpFront);
            // m_ivSidePhoto.setImageBitmap(m_bmpSide);
            //m_bFrontPicLoaded = false;
            //m_bSidePicLoaded  = false;

            // deleteFiles(m_imgCachePath, ".jpg");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Uri转Bitmap
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    //删除指定类型文件
    private boolean deleteFiles(String path, String typeStr) {
        File f = new File(path);
        File[] fl = f.listFiles();

        boolean bRes = false;
        for (int i = 0; i < fl.length; i++) {
            //if(fl[i].toString().endsWith(".pdf") || fl[i].toString().endsWith(".PDF"))
            if (fl[i].toString().endsWith(typeStr)) {
                if (fl[i].delete()) {
                    //textview.setTextSize(20);
                    // textview.setTextColor(Color.GREEN);
                    // textview.append("\n" + fl[i].toString()+ "--- Success");
                    Log.i("deleteFiles", "\n" + fl[i].toString() + "--- Success");
                    // return true;
                    bRes &= true;
                } else {
                    // textview.setTextSize(20);
                    // textview.setTextColor(Color.RED);
                    // textview.append("\n" + fl[i].toString()+ "--- Failed");
                    // return false;
                    Log.i("deleteFiles", "\n" + fl[i].toString() + "--- Failed");
                }
            }
        }

        return bRes;
    }
}
