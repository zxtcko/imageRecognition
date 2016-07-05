package cn.edu.dhu.figures;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.*;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

//http://blog.csdn.net/liguojin1230/article/details/45568185
public class MainActivity extends FragmentActivity {

    /**
     * 功能：主页引导栏的三个Fragment页面设置适配器
     */
    public class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public FragmentAdapter(FragmentManager fm,List<Fragment> fragments) {
            super(fm);
            this.fragments=fragments;
        }

        public Fragment getItem(int fragment) {
            return fragments.get(fragment);
        }

        public int getCount() {
            return fragments.size();
        }

    }
    /**
     * 顶部三个LinearLayout
     */
    private List<LinearLayout> m_TabLayouts = new ArrayList<LinearLayout>();
    private List<TextView>     m_TabTVs     = new ArrayList<TextView>();
    private List<Fragment>     m_fragments  = new ArrayList<Fragment>();

    /**
     * 顶部的三个TextView
     */
    private int mCrActive;
    private int mCrInactive;
    /**
     * Tab的那个引导线
     */
    private ImageView mTabLine;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    private ViewPager       mViewPager;
    private FragmentAdapter mAdapter;
   // private Setting  m_settings = new Setting();

    private static final String TAG = "MainActivity";

    final int REQUEST_CODE_SETTING=101;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            //设置透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.crToolBar));
        }

        setContentView(R.layout.main_frame);

        //设置
        ImageView btSetting=(ImageView)findViewById(R.id.btSetting);
        btSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动设置Activity
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingsActivity.class);
                //intent.setData(m_sideImgUri);
                // intent.putExtra("picUri", m_frontImgUri);
                startActivityForResult(intent, REQUEST_CODE_SETTING);
                return;
            }
        });

        //重置
        ImageView btReset=(ImageView)findViewById(R.id.btReset);
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasureFragment fg=(MeasureFragment)m_fragments.get(0);
                if (fg!=null) {
                    fg.resetPictures();
                }
                return;
            }
        });


        Log.i(TAG, "onCreate: loadSettings");
        //加载设置数据
        Setting.getInstance().loadSettings(getApplicationContext());

        //res=getResources();
        mCrActive   = getResources().getColor(R.color.white);
        mCrInactive = getResources().getColor(R.color.gray);

        initView();

        mViewPager=(ViewPager) findViewById(R.id.id_viewpager);

        /**
         * 初始化Adapter
         */
        mAdapter=new FragmentAdapter(getSupportFragmentManager(), m_fragments);

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new TabOnPageChangeListener());

        mViewPager.setOffscreenPageLimit(0);

        initTabLine();
      //  mViewPager.setCurrentItem(1);//选择某一页
        //mViewPager.setCurrentItem(0);//选择某一页
      //  updateTabUIs();
        updateTabUIs();
         }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==REQUEST_CODE_SETTING) {
            //保存设置
            Setting.getInstance().loadSettings(getApplicationContext());
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void ShowSizes()
    {
        mViewPager.setCurrentItem(1);
    }

    /**
     * 根据屏幕的宽度，初始化引导线的宽度
     */
    private void initTabLine() {
        mTabLine=(ImageView) findViewById(R.id.id_tab_line);

        //获取屏幕的宽度
        DisplayMetrics outMetrics=new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth=outMetrics.widthPixels;

        //获取控件的LayoutParams参数(注意：一定要用父控件的LayoutParams写LinearLayout.LayoutParams)
        LinearLayout.LayoutParams lp=(android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
        lp.width=screenWidth/m_TabLayouts.size();//设置该控件的layoutParams参数
        mTabLine.setLayoutParams(lp);//将修改好的layoutParams设置为该控件的layoutParams
    }

    /**
     * 初始化控件，初始化Fragment
     */
    private void initView() {
        m_fragments.add(new MeasureFragment());
        m_fragments.add(new SizesFragment());
        m_fragments.add(new ResultFragment());

        m_TabTVs.add((TextView) findViewById(R.id.id_chat));
        m_TabTVs.add((TextView) findViewById(R.id.id_found));
        m_TabTVs.add((TextView) findViewById(R.id.id_contact));

        m_TabLayouts.add((LinearLayout) findViewById(R.id.id_tab1_chat));
        m_TabLayouts.add((LinearLayout) findViewById(R.id.id_tab2_found));
        m_TabLayouts.add((LinearLayout) findViewById(R.id.id_tab3_contact));
      //  m_TabLayouts.add((LinearLayout) findViewById(R.id.id_tab4_shopping));

        for (int i=0;i<m_TabLayouts.size();++i) {
            m_TabLayouts.get(i).setOnClickListener(new TabOnClickListener(i));
        }
    }

    /**
     * 功能：点击主页TAB事件
     */
    public class TabOnClickListener implements View.OnClickListener{
        private int index=0;

        public TabOnClickListener(int i){
            index=i;
        }

        public void onClick(View v) {
            //m_TabS.get(index).setPressed(true);
            mViewPager.setCurrentItem(index);//选择某一页
        }

    }

    /**
     * 功能：Fragment页面改变事件
     */
    public class TabOnPageChangeListener implements OnPageChangeListener{
        //当滑动状态改变时调用
        public void onPageScrollStateChanged(int state) {

        }

        //当前页面被滑动时调用
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
            LinearLayout.LayoutParams lp=(android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
            //返回组件距离左侧组件的距离
            lp.leftMargin= (int) ((positionOffset+position)*screenWidth/m_TabLayouts.size());
            mTabLine.setLayoutParams(lp);
        }

        //当新的页面被选中时调用
        public void onPageSelected(int position) {
            //重置所有TextView的字体颜色
            updateTabUIs();
        }
    }

    protected void updateTabUIs()
    {
        //重置所有TextView的字体颜色
        for (int i=0;i<m_TabTVs.size();++i)
          m_TabTVs.get(i).setTextColor(mCrInactive);

        for (int i=0;i<m_TabLayouts.size();++i)
            m_TabLayouts.get(i).setBackgroundResource(R.color.crMainTabBTN);

        int  position=mViewPager.getCurrentItem();
        m_TabTVs.get(position).setTextColor(mCrActive);
        m_TabLayouts.get(position).setBackgroundResource(R.color.crMainTabPressed);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showTips();
            return false;
        }
        //return false;

        return super.onKeyDown(keyCode, event);
    }

    protected void showTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //AccoutList.this.finish();
                //System.exit(1);
                android.os.Process.killProcess(Process.myPid());
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




}


/*

public class MainActivity extends FragmentActivity {
    private ViewPager mPager;

    TabLayout mTabLayout;
    //private TabPageIndicator mIndicator;
    private ArrayList<Fragment> fragmentList;
    private ImageView image;
    private TextView view1, view2, view3, view4;
    private int currIndex;//当前页卡编号
    private int bmpW;//横线图片宽度
    private int offset;//图片移动的偏移量



    MeasureFragment m_MeasureFragment;
    ResultFragment  m_resultFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);
////        toolbar.setTitle(R.string.title_buttom_tab);
////        setSupportActionBar(toolbar);
////        getSupportActionBar().setHomeButtonEnabled(true);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

        InitTextView();
      //  InitImage();
        InitViewPager();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showTips();
            return false;
        }
        //return false;

        return super.onKeyDown(keyCode, event);
    }

    protected void showTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //AccoutList.this.finish();
                //System.exit(1);
                android.os.Process.killProcess(Process.myPid());
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

    */
/*
     * 初始化标签名
     *//*

    public void InitTextView(){
        view1 = (TextView)findViewById(R.id.tv_guid1);
        view2 = (TextView)findViewById(R.id.tv_guid2);
        view3 = (TextView)findViewById(R.id.tv_guid3);
        view4 = (TextView)findViewById(R.id.tv_guid4);

        view1.setOnClickListener(new txListener(0));
        view2.setOnClickListener(new txListener(1));
        view3.setOnClickListener(new txListener(2));
        view4.setOnClickListener(new txListener(3));
    }


    public class txListener implements View.OnClickListener{
        private int index=0;

        public txListener(int i) {
            index =i;
        }
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mPager.setCurrentItem(index);
            v.setActivated(true);
        }
    }


    */
/*
     * 初始化图片的位移像素
     *//*

*/
/*    public void InitImage(){
        image = (ImageView)findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.outline_front).getWidth();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW/4 - bmpW)/2;

        //imgageview设置平移，使下划线平移到初始位置（平移一个offset）
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        image.setImageMatrix(matrix);
    }*//*


    */
/*
     * 初始化ViewPager
     *//*

    public void InitViewPager(){
        mPager = (ViewPager)findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();

        m_MeasureFragment   =  new MeasureFragment();
        m_resultFragment    =  new  ResultFragment();

        //Fragment thirdFragment =new Fragment2();
        Fragment thirdFragment = TestFragment.newInstance("this is third fragment");
        Fragment fourthFragment = TestFragment.newInstance("this is fourth fragment");

        fragmentList.add(m_MeasureFragment);
        fragmentList.add(m_resultFragment);
        fragmentList.add(thirdFragment);
        fragmentList.add(fourthFragment);

        //给ViewPager设置适配器
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.setCurrentItem(0);//设置当前显示标签页为第一页
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器


       // mIndicator = (TabPageIndicator) findViewById(R.id.id_indicator);
      //  mIndicator.setViewPager(mPager, 0);
    }


    public class MyOnPageChangeListener implements OnPageChangeListener{
        private int one = offset *2 +bmpW;//两个相邻页面的偏移量

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
//            Animation animation = new TranslateAnimation(currIndex*one,arg0*one,0,0);//平移动画
              currIndex = arg0;
//            animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
//            animation.setDuration(200);//动画持续时间0.2秒
//            image.startAnimation(animation);//是用ImageView来显示动画的
            int i = currIndex + 1;
            Toast.makeText(MainActivity.this, "您选择了第"+i+"个页卡", Toast.LENGTH_SHORT).show();
           // findViewById(R.layout.tv_guid1)
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}*/
