package cn.edu.dhu.figures;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResultFragment extends Fragment {

    private WebView mWebView;
    private static final String TAG = "ResultFragment";

    protected  String m_localObjFile ; //模型文件


    //后台下载OBJ文件
    class LoadObjFileTask extends AsyncTask<String, Integer, String> {
        private static final String TAG = "LoadObjFileTask";

        public boolean m_bLoaded=false;
        public LoadObjFileTask(){
        }
        @Override
        protected String doInBackground(String... params) {
            String urlStr=params[0];
            String filePath=params[1];
            File file = new File(filePath);

            Log.i(TAG, "doInBackground: 读取OBJ数据"+urlStr);
            //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
            if(file.exists())
            {
                file.delete();
            }
            try {
                // 构造URL
                URL url = new URL(urlStr);
                // 打开连接
                URLConnection con = url.openConnection();
                //获得文件的长度
                int contentLength = con.getContentLength();
                System.out.println("长度 :"+contentLength);
                // 输入流
                InputStream is = con.getInputStream();
                // 1K的数据缓冲
                byte[] buf = new byte[1024];
                // 读取到的数据长度
                int len;
                // 输出的文件流
                OutputStream os = new FileOutputStream(filePath);

                // ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int lenRead=0;
                // 开始读取
                while ((len = is.read(buf)) != -1) {
                    os.write(buf, 0, len);
                    //    baos.write(buf, 0, len);
                    lenRead += len;
                    if(contentLength > 0) {
                        // 如果知道响应的长度，调用publishProgress（）更新进度
                        publishProgress((int) ((lenRead / (float) contentLength) * 100));
                    }
                }
                // 完毕，关闭所有链接
                os.close();
                is.close();

                m_bLoaded=true;

                Log.i(TAG, "doInBackground: 读取OBJ数据成功" + urlStr);
                return "OK";//new String(baos.toByteArray());//
            } catch (Exception e) {
                m_bLoaded=false;
                Log.i(TAG, "doInBackground: 读取OBJ异常：" + e.toString());
                e.printStackTrace();
                return  e.toString();//"FALSE";
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            // 返回HTML页面的内容
            //  message.setText(result);
            //  pdialog.dismiss();
            Log.i(TAG, "onPostExecute: " + result + m_localObjFile);
            //openObjFile(m_localObjFile);

            File file = new File(m_localObjFile);
            if (file.exists()) {
                openFile(file);
            }
        }

        @Override
        protected void onPreExecute() {
            // 任务启动，可以在这里显示一个对话框，这里简单处理
            //  message.setText(R.string.task_started);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度
            System.out.println(""+values[0]);
            //  message.setText(""+values[0]);
            //pdialog.setProgress(values[0]);
        }

    }


    /**
     * 打开文件
     * @param file
     */
    private void openFile(File file){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
//获取文件file的MIME类型
        String type = getMIMEType(file);
//设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
//跳转
        startActivity(intent);
    }
    /**
     * 根据文件后缀名获得对应的MIME类型。
     * @param file
     */
    private String getMIMEType(File file) {
        String type="*/*";
        String fName = file.getName();
//获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
/* 获取文件的后缀名 */
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
//在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private final String[][] MIME_MapTable={
            //{后缀名，	MIME类型}
            {".3gp",	"video/3gpp"},
            {".apk",	"application/vnd.android.package-archive"},
            {".asf",	"video/x-ms-asf"},
            {".avi",	"video/x-msvideo"},
            {".bin",	"application/octet-stream"},
            {".bmp",  	"image/bmp"},
            {".c",	"text/plain"},
            {".class",	"application/octet-stream"},
            {".conf",	"text/plain"},
            {".cpp",	"text/plain"},
            {".doc",	"application/msword"},
            {".docx",	"application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls",	"application/vnd.ms-excel"},
            {".xlsx",	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe",	"application/octet-stream"},
            {".gif",	"image/gif"},
            {".gtar",	"application/x-gtar"},
            {".gz",	"application/x-gzip"},
            {".h",	"text/plain"},
            {".htm",	"text/html"},
            {".html",	"text/html"},
            {".jar",	"application/java-archive"},
            {".java",	"text/plain"},
            {".jpeg",	"image/jpeg"},
            {".jpg",	"image/jpeg"},
            {".js",	"application/x-javascript"},
            {".log",	"text/plain"},
            {".m3u",	"audio/x-mpegurl"},
            {".m4a",	"audio/mp4a-latm"},
            {".m4b",	"audio/mp4a-latm"},
            {".m4p",	"audio/mp4a-latm"},
            {".m4u",	"video/vnd.mpegurl"},
            {".m4v",	"video/x-m4v"},
            {".mov",	"video/quicktime"},
            {".mp2",	"audio/x-mpeg"},
            {".mp3",	"audio/x-mpeg"},
            {".mp4",	"video/mp4"},
            {".mpc",	"application/vnd.mpohun.certificate"},
            {".mpe",	"video/mpeg"},
            {".mpeg",	"video/mpeg"},
            {".mpg",	"video/mpeg"},
            {".mpg4",	"video/mp4"},
            {".mpga",	"audio/mpeg"},
            {".msg",	"application/vnd.ms-outlook"},
            {".ogg",	"audio/ogg"},
            {".pdf",	"application/pdf"},
            {".png",	"image/png"},
            {".pps",	"application/vnd.ms-powerpoint"},
            {".ppt",	"application/vnd.ms-powerpoint"},
            {".pptx",	"application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop",	"text/plain"},
            {".rc",	"text/plain"},
            {".rmvb",	"audio/x-pn-realaudio"},
            {".rtf",	"application/rtf"},
            {".sh",	"text/plain"},
            {".tar",	"application/x-tar"},
            {".tgz",	"application/x-compressed"},
            {".txt",	"text/plain"},
            {".wav",	"audio/x-wav"},
            {".wma",	"audio/x-ms-wma"},
            {".wmv",	"audio/x-ms-wmv"},
            {".wps",	"application/vnd.ms-works"},
            {".xml",	"text/plain"},
            {".z",	"application/x-compress"},
            {".zip",	"application/x-zip-compressed"},
            {".obj","application/*"},
            {"",		"*/*"}
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "TestFragment-----onCreateView");

//
//        Bundle args = getArguments();
//        hello = args != null ? args.getString("hello") : defaultHello;
        View view = inflater.inflate(R.layout.activity_result, container, false);

        FloatingActionButton m_fab = (FloatingActionButton) view.findViewById(R.id.fab);

        m_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshWebView();
                //   Intent itCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //  startActivityForResult(itCapture, RESULT_SHOT_IMAGE_FRONT);
            }
        });

        mWebView = (WebView) view.findViewById(R.id.webView01);
       // setupWebView();


        Button myButton=(Button)view.findViewById(R.id.btShowBodyModel);
        //设置按键监听事件
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String m_objFileUrl   = Setting.getInstance().m_ServerHome + "body.obj"; //模型文件
                LoadObjFileTask task = new LoadObjFileTask();

                File path = Environment.getExternalStorageDirectory();
                m_localObjFile = path.getPath() + "/body.obj";

                task.execute(m_objFileUrl,m_localObjFile);
                // Toast.makeText(SizesFragment.this.getActivity(), "Open Local File:"+ m_localObjFile, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupWebView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, " onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, " onStop: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, " onStart: ");
    }

    public  void refreshWebView()
    {
        Log.i(TAG, "refreshWebView: " + Setting.getInstance().m_ServerHome);
        mWebView.loadUrl(Setting.getInstance().m_ServerHome + "result.htm");
        mWebView.reload();
    }

    private  void setupWebView() {
        WebSettings webSettings = mWebView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置出现缩放工具
        //webSettings.setBuiltInZoomControls(true);
        //扩大比例的缩放
        webSettings.setUseWideViewPort(true);
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);

        //加载需要显示的网页
        mWebView.getSettings().setDefaultTextEncodingName("GB2312") ;
        mWebView.loadUrl(Setting.getInstance().m_ServerHome + "result.htm");
        // mWebView.loadData(content, "text/html", "UTF-8");
        //设置Web视图
        mWebView.setWebViewClient(new CMyWebViewClient ());
    }

    //Web视图
    private class CMyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
