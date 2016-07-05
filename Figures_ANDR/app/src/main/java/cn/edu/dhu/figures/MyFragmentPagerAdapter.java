package cn.edu.dhu.figures;

        import java.util.ArrayList;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentPagerAdapter;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

    ArrayList<Fragment> list;
    public static final String[] TITLES = new String[] { "业界", "移动", "研发", "程序员杂志", "云计算" };


    public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return TITLES[position % TITLES.length];
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }
}
