package com.yongf.smartguard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yongf.smartguard.R;

/**
 * Created by yongf-new on 2016/2/3.
 * 自定义的组合控件，它里面有两个TextView，一个CheckBox，还有一个View
 */
public class SettingItemView extends RelativeLayout {

    private CheckBox cb_status;

    private TextView tv_auto_update_title;

    private TextView tv_auto_update_desc;

    /**
     * 初始化布局文件
     * @param context
     */
    private void initView(Context context) {
        View.inflate(context, R.layout.setting_item_view, this);

        cb_status = (CheckBox) this.findViewById(R.id.cb_status);
        tv_auto_update_title = (TextView) this.findViewById(R.id.tv_auto_update_title);
        tv_auto_update_desc = (TextView) this.findViewById(R.id.tv_auto_update_desc);
    }

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     *校验组合控件是否有焦点
     * @return
     */
    public boolean isChecked() {
        return cb_status.isChecked();
    }

    /**
     * 设置组合控件的状态
     * @param checked
     */
    public void setChecked(boolean checked) {
        cb_status.setChecked(checked);
    }

    /**
     * 设置组合控件的描述信息
     * @param text
     */
    public void setDesc(String text) {
        tv_auto_update_desc.setText(text);
    }
}
