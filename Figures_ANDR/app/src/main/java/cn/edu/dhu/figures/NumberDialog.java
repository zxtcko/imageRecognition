package cn.edu.dhu.figures;

/**
 * Created by X on 2016/6/7.
 */

        import java.util.Calendar;
        import android.app.Dialog;
        import android.content.Context;
        import android.os.Bundle;
        import android.view.Gravity;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.NumberPicker;
        import android.widget.NumberPicker.Formatter;
        import android.widget.NumberPicker.OnScrollListener;
        import android.widget.NumberPicker.OnValueChangeListener;
        import android.widget.TextView;


public class NumberDialog extends Dialog implements OnValueChangeListener,
        OnScrollListener, Formatter, android.view.View.OnClickListener {
    private NumberPicker m_npHeight;// 年
    private NumberPicker m_npWeight;// 年

   // private NumberPicker hour_numberpicker;// 时
   // private NumberPicker min_numberpicker;// 分
    private TextView year_tv;
  //  private TextView hour_tv;
  //  private TextView min_tv;

    public static int YEAR = 1;// 年
    public static int MONTH = 2;// 月
    public static int DATE = 3;// 日
    public static int YEAR_DATE = 4;// 月日
    public static int YEAR_MONTH_DATE = 5;// 年月日
    public static int HOUR_MIN_SECOND = 6;// 时分秒
    private int dateFormat = YEAR_MONTH_DATE;// 月日，时分秒

    private Context context;
    private Button sure_btn, cacel_btn;
    private OnclickLinsterDialog linsterDialog;

    public NumberDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pick_number_dialog);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = getWidth() * 9 / 10; // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);

        setTitle("选择时间");
        initView();
        super.onCreate(savedInstanceState);
    }

    public void setDateFormat(int format) {
        this.dateFormat = format;

    }

    /**
     * 获取手机屏幕宽
     *
     * @return
     */
    public int getWidth() {
        int width = 1;
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    private void initView() {
        sure_btn = (Button) findViewById(R.id.sure_btn);
        cacel_btn = (Button) findViewById(R.id.cacel_btn);
        sure_btn.setOnClickListener(this);
        cacel_btn.setOnClickListener(this);


        m_npHeight = (NumberPicker) findViewById(R.id.npHeight);
        m_npHeight.setFormatter(this);
        m_npHeight.setOnValueChangedListener(this);// 值改变监听
        m_npHeight.setOnScrollListener(this);// 活动监听
        m_npHeight.setEnabled(true);// 内容不可编辑

        m_npHeight.setMaxValue(220);// 最大值
        m_npHeight.setMinValue(140);// 最小值
        m_npHeight.setValue(160);// 获取当前年

        m_npWeight = (NumberPicker) findViewById(R.id.npWeight);
        m_npWeight.setFormatter(this);
        m_npWeight.setOnValueChangedListener(this);// 值改变监听
        m_npWeight.setOnScrollListener(this);// 活动监听
        m_npWeight.setEnabled(true);// 内容不可编辑

        m_npWeight.setMaxValue(120);// 最大值
        m_npWeight.setMinValue(40);// 最小值
        m_npWeight.setValue(52);// 获取当前年



        /*else if (dateFormat == HOUR_MIN_SECOND) {
            // 时
            m_npHeight.setMaxValue(24);// 最大值
            m_npHeight.setMinValue(0);// 最小值
            m_npHeight.setValue(calendar.get(Calendar.HOUR_OF_DAY));// 获取当前时
            // 分
            hour_numberpicker.setMaxValue(60);// 最大值
            hour_numberpicker.setMinValue(0);// 最小值
            hour_numberpicker.setValue(calendar.get(Calendar.MINUTE));// 获取当前时
            // 秒
            min_numberpicker.setMaxValue(60);
            min_numberpicker.setMinValue(0);
            min_numberpicker.setValue(calendar.get(Calendar.SECOND));

            m_npHeight.setVisibility(View.VISIBLE);
            hour_numberpicker.setVisibility(View.VISIBLE);
            min_numberpicker.setVisibility(View.VISIBLE);
            year_tv.setVisibility(View.VISIBLE);
            year_tv.setText("时");
            hour_tv.setVisibility(View.VISIBLE);
            hour_tv.setText("分");
            min_tv.setVisibility(View.VISIBLE);
            min_tv.setText("秒");
        }*/

    }

    /***
     * 值改变监听 oldVal 原来值 newVal 改变值
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {

            case R.id.npHeight:

                if (newVal < 140) {
                    m_npHeight.setValue(newVal + 1);
                }
                break;
            case R.id.npWeight:

                if (newVal < 40) {
                    m_npWeight.setValue(newVal + 1);
                }
                break;

//            case R.id.hour:// 中间的控件，1，显示月，2，显示分
//
//                // 如果显示年月日
//                if (dateFormat == YEAR_MONTH_DATE) {
//                    if (oldVal == 12 && newVal == 01) {
//                        m_npHeight
//                                .setValue(m_npHeight.getValue() + 1);
//                    } else if (oldVal == 01 && newVal == 12) {
//                        m_npHeight
//                                .setValue(m_npHeight.getValue() - 1);
//                    }
//
//                    // 如果显示时分秒
//                } else if (dateFormat == HOUR_MIN_SECOND) {
//                    if (oldVal == 60 && newVal == 01) {
//                        m_npHeight
//                                .setValue(m_npHeight.getValue() + 1);
//                    } else if (oldVal == 01 && newVal == 60) {
//                        m_npHeight
//                                .setValue(m_npHeight.getValue() - 1);
//                    }
//                }
//
//                break;

            default:
                break;
        }
    }

    /***
     * value的值格式，当值小于10，前面加一个0
     */
    @Override
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    /**
     * 滑动监听事件
     */
    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {

        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_FLING:// 后续滑动，停不下来
                break;
            case OnScrollListener.SCROLL_STATE_IDLE:// 不滑动
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滑动中
                break;
        }

    }

    public void setOnclickLinsterDialog(OnclickLinsterDialog click) {
        this.linsterDialog = click;
    }

    interface OnclickLinsterDialog {

        void choseDate(String height,String weight);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.sure_btn:
                String height = String.valueOf(m_npHeight.getValue());//
                String weight = String.valueOf(m_npWeight.getValue());//

                linsterDialog.choseDate(height,weight);
                dismiss();

                break;
            case R.id.cacel_btn:
                dismiss();
                break;

            default:
                break;
        }

    }

}
