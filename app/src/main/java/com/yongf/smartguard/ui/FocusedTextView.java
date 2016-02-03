package com.yongf.smartguard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yongf-new on 2016/2/3.
 * 自定义一个TextView，天生有焦点
 */
public class FocusedTextView extends TextView {

    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 其实当前并没有焦点，只是欺骗了android系统
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
