package cn.edu.dhu.figures;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by X on 2016/5/6.
 */
public class Setting {

    //Singleton
    private static Setting ourInstance = new Setting();
    public static Setting getInstance() {
        return ourInstance;
    }
    private Setting() {
    }

    public    String     m_ServerHome       = "http://10.199.15.197/";

    public  static final String m_dataPath  = Environment.getExternalStorageDirectory() +"/Figures/";
    private static final String TAG         = "Setting";
    private static final String TAG_SERVER  = "serverurl";


    public  void loadSettings(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Setting.getInstance().m_ServerHome= sharedPref.getString("server_url", null);
        Log.i(TAG, "loadSettings : m_ServerHome=" + Setting.getInstance().m_ServerHome);
    }
    public  void saveSettings(Context context)
    {
        //保存设置数据；
        SharedPreferences.Editor sharedata = context.getSharedPreferences("data", 0).edit();
        sharedata.putString(TAG_SERVER, Setting.getInstance().m_ServerHome);
        sharedata.commit();
    }
}
