package cn.edu.dhu.figures;

/**
 * Created by X on 2015/11/5.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View
{
  //  public    DrawDoc m_doc  = new DrawDoc();

    public    Bitmap  m_bitmap=null;

    Paint m_paint =new Paint();
    //public    Bitmap        m_touchImg;         //显示的图像
    protected CmdObject     m_cmdObject;               //处理绘图的命令对象
    //protected Camera2d      m_camera =  new Camera2d();  //视图控制摄像机

    /// 放缩视图
    final public static int MODE_DRAG = 1;    // 一根手指在触碰
    final public static int MODE_ZOOM = 2;    // 多根手指在触碰

    // 当前触碰的手指模拟
    private int m_currentMode = 0;

    private PointF m_startPoint = new PointF();    // 单根手指触碰的开始点
    private PointF m_centerPoint = new PointF();    // 多根手指滑动的中心点

    // 多根手指滑动的前后距离
    private float m_startDist = 1f;

    private Matrix m_viewMatrix = new Matrix();
    private Matrix m_viewMatrix1 = new Matrix();
    private Matrix m_savedMatrix = new Matrix();


    private int screenWidth, screenHeight;
    private float[] x = new float[4];
    private float[] y = new float[4];
    private boolean flag = false;

    public void  init() {
        setBackgroundColor(Color.GRAY);
        //变量初始化
        //m_touchImg = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        //m_touchImg =Datas.m_bmFrontView;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        m_viewMatrix = new Matrix();
       // m_cmdObject=new CmdCameraCtrl();

        //setOnTouchListener();
    }

    //切换当前处理命令
    void switchCmd(CmdObject cmdobj)
    {
        if (cmdobj==null)
            return;



    }

    //构造函数
    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context) {
        super(context);
        init();
    }


 /*   @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas mCanvas)
    {
        super.onDraw(mCanvas);
        //创建笔刷
       Paint m_paint=new Paint();
         m_pic.draw(mCanvas,m_paint);
        m_curve.draw(mCanvas,m_paint);
    }*/


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        // 根据 matrix 来重绘新的view

        //Matrix matview = getMatrix();
        Matrix mat=canvas.getMatrix();

        mat.postConcat(m_viewMatrix);
        //canvas.getMatrix()
         canvas.setMatrix(mat);
        if (m_bitmap!=null)
            canvas.drawBitmap(m_bitmap, 0, 0, m_paint);
       // m_doc.draw(canvas, m_paint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        // 多点触摸的时候 必须加上MotionEvent.ACTION_MASK
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                m_savedMatrix.set(m_viewMatrix);
                // 得到单根手指一开始接触的坐标
                m_startPoint.set(event.getX(), event.getY());
                // 初始为drag模式
                m_currentMode = MODE_DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                m_savedMatrix.set(m_viewMatrix);
                // 初始的两个触摸点间的距离
                //initDis = spacing(event);
                // 设置为缩放模式
                //mode = MODE_ZOOM;
                // 多点触摸的时候 计算出中间点的坐标
                //midPoint(mid, event);
                //break;
                // 得到两根手指一开始触碰的距离
                m_savedMatrix.set(m_viewMatrix);
                m_startDist = getDistance(event);
                // 得到这两根手指的中心点
                getCenter(event);
                // 设置模式为多根手指触碰
                 m_currentMode = MODE_ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:

                // drag模式
                if (m_currentMode == MODE_DRAG) {
                    // 设置当前的 matrix
                    m_viewMatrix1.set(m_savedMatrix);
                    // 平移 当前坐标减去初始坐标 移动的距离
                    m_viewMatrix1.postTranslate(event.getX() - m_startPoint.x, event.getY()
                            - m_startPoint.y);// 平移
                    // 判断达到移动标准
                    flag = checkMatrix(m_viewMatrix1);
                    if (flag) {
                        // 设置matrix
                        m_viewMatrix.set(m_viewMatrix1);

                        // 调用ondraw重绘
                        invalidate();
                    }
                } else if (m_currentMode == MODE_ZOOM) {
                    m_viewMatrix1.set(m_savedMatrix);
                    float newDis = getDistance(event);
                    // 计算出缩放比例
                    float scale = newDis / m_startDist;

                    // 以mid为中心进行缩放
                    m_viewMatrix1.postScale(scale, scale, m_centerPoint.x, m_centerPoint.y);
                    flag = checkMatrix(m_viewMatrix1);
                    if (flag) {
                        m_viewMatrix.set(m_viewMatrix1);
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                m_currentMode = 0;
                break;
        }

        return true;

    }

    //取两点的距离
 /*   private float spacing(MotionEvent event) {
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);

            double dist=Math.sqrt(x * x + y * y);
            return   (float)dist;
        } catch (IllegalArgumentException ex) {
            Log.v("TAG", ex.getLocalizedMessage());
            return 0;
        }
    }*/

    /**
     * 得到两根手指之间的中心点
     *
     * @param event
     */
    private void getCenter(MotionEvent event) {
        try {
            float a = event.getX(1) + event.getX(0);
            float b = event.getY(1) + event.getY(0);
            m_centerPoint.set( a * 0.5f,  b * 0.5f);

        } catch (IllegalArgumentException ex) {
            //这个异常是android自带的，网上清一色的这么说。。。。
            Log.v("TAG", ex.getLocalizedMessage());
        }
    }

    /**
     * 得到两根手指之间的距离
     *
     * @param event
     * @return
     */
    private float getDistance(MotionEvent event) {
        float a = event.getX(1) - event.getX(0);
        float b = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(a * a + b * b);
    }

/*
    //取两点的中点
    private void midPoint(PointF point, MotionEvent event) {
        try {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        } catch (IllegalArgumentException ex) {

            //这个异常是android自带的，网上清一色的这么说。。。。
            Log.v("TAG", ex.getLocalizedMessage());
        }
    }*/

    private boolean checkMatrix(Matrix m) {

        return true;

    /*    GetFour(m);

        // 出界判断
        //view的右边缘x坐标小于屏幕宽度的1/3的时候，
        // view左边缘大于屏幕款短的2/3的时候
        //view的下边缘在屏幕1/3上的时候
        //view的上边缘在屏幕2/3下的时候
        if ((x[0] < screenWidth / 3 && x[1] < screenWidth / 3
                && x[2] < screenWidth / 3 && x[3] < screenWidth / 3)
                || (x[0] > screenWidth * 2 / 3 && x[1] > screenWidth * 2 / 3
                && x[2] > screenWidth * 2 / 3 && x[3] > screenWidth * 2 / 3)
                || (y[0] < screenHeight / 3 && y[1] < screenHeight / 3
                && y[2] < screenHeight / 3 && y[3] < screenHeight / 3)
                || (y[0] > screenHeight * 2 / 3 && y[1] > screenHeight * 2 / 3
                && y[2] > screenHeight * 2 / 3 && y[3] > screenHeight * 2 / 3)) {
            return true;
        }
        // 图片现宽度
        double width = Math.sqrt((x[0] - x[1]) * (x[0] - x[1]) + (y[0] - y[1])
                * (y[0] - y[1]));
        // 缩放比率判断 宽度打雨3倍屏宽，或者小于1/3屏宽
        if (width < screenWidth / 3 || width > screenWidth * 3) {
            return true;
        }
        return false;

*/
        // if ((x[0] >= 0 && x[1] >= 0 && x[2] >= 0 && x[3] >= 0)
        // && (x[0] <= screenWidth && x[1] <= screenWidth
        // && x[2] <= screenWidth && x[3] <= screenWidth)
        // && (y[0] >= 0 && y[1] >= 0 && y[2] >= 0 && y[3] >= 0) && (y[0] <=
        // screenHeight
        // && y[1] <= screenHeight && y[2] <= screenHeight && y[3] <=
        // screenHeight)) {
        //
        // return true;
        // }
        //
        // return false;
    }

 /*   private void GetFour(Matrix matrix) {
        float[] f = new float[9];
        matrix.getValues(f);
//		StringBuffer sb = new StringBuffer();
//		for(float ff : f)
//		{
//			sb.append(ff+"  ");
//		}
        // 图片4个顶点的坐标
        //矩阵  9     MSCALE_X 缩放的， MSKEW_X 倾斜的    。MTRANS_X 平移的
        x[0] = f[Matrix.MSCALE_X] * 0 + f[Matrix.MSKEW_X] * 0
                + f[Matrix.MTRANS_X];
        y[0] = f[Matrix.MSKEW_Y] * 0 + f[Matrix.MSCALE_Y] * 0
                + f[Matrix.MTRANS_Y];
        x[1] = f[Matrix.MSCALE_X] * m_touchImg.getWidth() + f[Matrix.MSKEW_X] * 0
                + f[Matrix.MTRANS_X];
        y[1] = f[Matrix.MSKEW_Y] * m_touchImg.getWidth() + f[Matrix.MSCALE_Y] * 0
                + f[Matrix.MTRANS_Y];
        x[2] = f[Matrix.MSCALE_X] * 0 + f[Matrix.MSKEW_X]
                * m_touchImg.getHeight() + f[Matrix.MTRANS_X];
        y[2] = f[Matrix.MSKEW_Y] * 0 + f[Matrix.MSCALE_Y]
                * m_touchImg.getHeight() + f[Matrix.MTRANS_Y];
        x[3] = f[Matrix.MSCALE_X] * m_touchImg.getWidth() + f[Matrix.MSKEW_X]
                * m_touchImg.getHeight() + f[Matrix.MTRANS_X];
        y[3] = f[Matrix.MSKEW_Y] * m_touchImg.getWidth() + f[Matrix.MSCALE_Y]
                * m_touchImg.getHeight() + f[Matrix.MTRANS_Y];
    }*/



}///


