package com.yongf.smartguard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yongf.smartguard.R;

/**
 * Created by yongf-new on 2016/2/3.
 * 自定义的组合控件，它里面有两个TextView，一个ImageView，还有一个View
 */
public class SettingClickView extends RelativeLayout {

    private TextView tv_title;

    private TextView tv_desc;

    private String desc_on;

    private String desc_off;

    /**
     * 初始化布局文件
     * @param context
     */
    private void initView(Context context) {
        View view = View.inflate(context, R.layout.setting_click_view, this);

        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
    }

    public SettingClickView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 带有两个参数的构造方法，布局文件使用的时候调用
     * @param context
     * @param attrs
     */
    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.yongf.smartguard", "title");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.yongf.smartguard", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.yongf.smartguard", "desc_off");
        tv_title.setText(title);
        //默认设置为关闭状态
        setDesc(desc_off);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 设置组合控件的状态
     */

    public void setChecked(boolean checked){
        if(checked){
            setDesc(desc_on);
        }else{
            setDesc(desc_off);
        }
    }

    /**
     * 设置组合控件的描述信息
     * @param text
     */
    public void setDesc(String text) {
        tv_desc.setText(text);
    }

    /**
     * 设置组合控件的标题
     * @param text
     */
    public void setTitle(String text) {
        tv_title.setText(text);
    }
}
