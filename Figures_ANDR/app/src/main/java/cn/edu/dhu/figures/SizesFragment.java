package cn.edu.dhu.figures;

        import android.app.Activity;
        import android.content.ActivityNotFoundException;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.content.pm.ResolveInfo;
        import android.content.res.Resources;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.SimpleAdapter;
        import android.widget.Toast;

        import org.apache.http.util.ByteArrayBuffer;
        import org.apache.http.util.EncodingUtils;

        import java.io.BufferedInputStream;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.net.URL;
        import java.net.URLConnection;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.regex.Pattern;

public class SizesFragment extends Fragment{
    Button myButton;
    MyRefreshListView mListView;
    MySimpleAdapter listAdapter;

    List<Map<String, Object>> m_listData = new ArrayList<Map<String, Object>>();

    private List<Map<String, Object>> list = null;
    private PackageManager mPackageManager;
    private List<ResolveInfo> mAllApps;
    private Context mContext;


    //后台下载OBJ文件
    class LoadSizesFileTask extends AsyncTask<String, Integer, String> {
        private static final String TAG = "LoadSizesFileTask";
        private  boolean m_bLoaded =false;
        // 可变长的输入参数，与AsyncTask.exucute()对应
        public LoadSizesFileTask() {
        }
        @Override
        protected String doInBackground(String... params) {


            String urlStr = params[0];
            Log.i(TAG, "doInBackground: 开始加载数据："+urlStr);
            // 在这里进行 http request.网络请求相关操作
            Bundle data = new Bundle();
            String fileStr = "请求结果";
            try {
                // 定义获取文件内容的URL
                URL myURL = new URL(urlStr);
                // 打开URL链接
                URLConnection ucon = myURL.openConnection();
                // 使用InputStream，从URLConnection读取数据
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                // 用ByteArrayBuffer缓存
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }
                // 将缓存的内容转化为String,用UTF-8编码
                fileStr = EncodingUtils.getString(baf.toByteArray(), "GB2312");
                m_bLoaded=true;
                Log.i(TAG, "doInBackground: 数据已加载："+urlStr);
                return fileStr;
            } catch (Exception e) {
                // Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                m_bLoaded=false;

                Log.i(TAG, "doInBackground: 数据加载异常："+urlStr + e.toString());
                //return e.getMessage();
                return e.toString();
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
            Log.i(TAG, "onPostExecute: 加载" + result +Boolean.toString(m_bLoaded));
            //
            if (m_bLoaded)
            {
                parseData2List(result);
                updateListView();
                //showMessage("数据加载成功！");
            }
            else
            {
                m_listData.clear();
                updateListView();
                showMessage(result);
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
            System.out.println("" + values[0]);
            //  message.setText(""+values[0]);
            //pdialog.setProgress(values[0]);
        }
    }

    private void showMessage(String mes)
    {
        Toast.makeText(getActivity().getApplicationContext(),mes,Toast.LENGTH_SHORT).show();
    }


    private void openObjFile(String filePath)
    {
        File file = new File(filePath);
        if (file.exists()) {
            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/obj");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "No Application Available to View obj",Toast.LENGTH_SHORT).show();
            }
        }

    }


    public boolean  saveUrl2File(String urlStr,String filePath)
    {
        File file = new File(filePath);
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
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            OutputStream os = new FileOutputStream(filePath);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }


//    class DownObjModel extends AsyncTask {
//
//        String m_FilePath;
//        private ImageView imageView;
//
//        public DownObjModel() {
//            this.imageView = imageView;
//        }
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            return null;
//        }
//
//        @Override
//        protected boolean doInBackground(String... params) {
//            String url = params[0];
//            m_FilePath = params[1];
//            return saveUrl2File(url,m_FilePath);
//        }
//
//        @Override
//        protected void onPostExecute(boolean result) {
//            openObjFile(m_FilePath);
//        }
//    }

    /**
     * 检查系统应用程序，并打开
     */
    private void openApp(){
        mContext=getActivity().getApplicationContext();
        //应用过滤条件
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mPackageManager = mContext.getPackageManager();
        mAllApps = mPackageManager.queryIntentActivities(mainIntent, 0);
        //按报名排序
        Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));

        for(ResolveInfo res : mAllApps){
            //该应用的包名和主Activity
            String pkg = res.activityInfo.packageName;
            String cls = res.activityInfo.name;

            // 打开QQ
            if(pkg.contains("objviewer")){
                ComponentName componet = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                intent.setComponent(componet);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }

    private String getSizeFileUrl()  {
        return Setting.getInstance().m_ServerHome + "sizes.txt"; //尺寸文件
    }

    private void startLoadSizesTask()
    {
        LoadSizesFileTask task = new LoadSizesFileTask();
        task.execute(getSizeFileUrl());
    }

//    private  void init()
//    {
//        Activity act=getActivity();
//        //设置图片文件路径
//        String  m_imgCachePath = act.getApplicationContext().getFilesDir().getAbsolutePath() + "/imgCache/";
//        File path = Environment.getExternalStorageDirectory();
//        m_localObjFile = path.getPath() + "/body.obj";
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sizes, container, false);//关联布局文件

        myButton = (Button)rootView.findViewById(R.id.mybutton);//根据rootView找到button
        mListView=(MyRefreshListView)rootView.findViewById(R.id.listView);


        mListView.setonRefreshListener(new MyRefreshListView.OnRefreshListener() {
            private static final String TAG = "LV.OnRefreshListener";
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            //Thread.sleep(200);
                            //new Thread(m_loadRemoteData).start();
                            Log.i(TAG, "doInBackground: startLoadSizesTask");
                            startLoadSizesTask();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        list.add("刷新后添加的内容");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                       // listAdapter.notifyDataSetChanged();
                         mListView.onRefreshComplete();
                       // showMessage("数据加载完成！");
                        Log.i(TAG, "onPostExecute: onRefreshComplete" );
                    }
                }.execute(null, null, null);
            }
        });

        //设置按键监听事件
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
                //new Thread(m_loadRemoteData).start();
                //openApp();

               // Toast.makeText(SizesFragment.this.getActivity(), "Open Local File:"+ m_localObjFile, Toast.LENGTH_SHORT).show();
               // saveUrl2File(m_objFileUrl,filePath);
                //openObjFile(m_localObjFile);
             }
        });

        // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
       // new Thread(m_loadRemoteData).start();
        startLoadSizesTask();

        //数据适配器
        listAdapter = new MySimpleAdapter(getActivity(),m_listData,R.layout.fragment_sizes_vlist,
                new String[]{"id","name","value"},
                new int[]{R.id.sizeId,R.id.sizeName ,R.id.sizeValue});
        mListView.setAdapter(listAdapter);

        return rootView;
    }

 /*  // protected  String m_fileStr = "请求结果";
    // 处理相应消息
    Handler m_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
            parseData2List(val);
            updateListView();
        }
    };
    *//**
     * 网络操作相关的子线程
     *//*
    Runnable m_loadRemoteData = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Message msg = new Message();
            Bundle data = new Bundle();

            String fileStr = "请求结果";

            try {
                // 定义获取文件内容的URL
                URL myURL = new URL(m_sizesFileUrl);
                // 打开URL链接
                URLConnection ucon = myURL.openConnection();
                // 使用InputStream，从URLConnection读取数据
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                // 用ByteArrayBuffer缓存
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }
                // 将缓存的内容转化为String,用UTF-8编码
                fileStr = EncodingUtils.getString(baf.toByteArray(), "GB2312");

            } catch (Exception e) {
                fileStr = e.getMessage();
//              Toast.makeText(null, m_fileStr, Toast.LENGTH_SHORT).show();
            }
            data.putString("value", fileStr);
            msg.setData(data);
            m_handler.sendMessage(msg);
        }
    };*/

    private void parseData2List(String fileStr) {
            m_listData.clear();
            //分割字符串
            String pattern="\r\n";
            Pattern pat=Pattern.compile(pattern);
            String[] strLines=pat.split(fileStr);
            pattern=",";  pat=Pattern.compile(pattern);
            String[] tags={"id","name","value"};
            for (int i = 0; strLines.length > i; ++i)
            {
                String[] values=pat.split(strLines[i]);
                Map<String, Object> map = new HashMap<String, Object>();

                if (values.length>0) {
                    for (int j = 0; j < values.length; ++j) {
                        map.put(tags[j], values[j]);
                    }
                    m_listData.add(map);
                }
            }
        }

    protected void updateListView()
    {
        listAdapter.notifyDataSetChanged();
    }

    class MySimpleAdapter extends SimpleAdapter
    {
        int mCrListView1;
        int mCrListView2;

        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data,
                             int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
            Resources r = getActivity().getResources();
            //
            mCrListView1=r.getColor(R.color.colorListView1);
            mCrListView2=r.getColor(R.color.colorListView2);
        };

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v!=null){
                if (position % 2 == 0) {
                    //v.setBackgroundColor(Color.parseColor("#b3FFFFFF"));
                    v.setBackgroundColor(mCrListView1);
                } else {
                   // v.setBackgroundColor(Color.parseColor("#b3FAFAFA"));
                    v.setBackgroundColor(mCrListView2);
                }
            }
            return  v;
        }
    }



}
