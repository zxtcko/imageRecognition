package cn.edu.dhu.figures;

import android.view.MotionEvent;

/**
 * Created by X on 2015/11/10.
 */
abstract public class CmdObject {

    public   DrawView m_drawView;

    abstract public boolean onTouchEvent(MotionEvent event);
}
