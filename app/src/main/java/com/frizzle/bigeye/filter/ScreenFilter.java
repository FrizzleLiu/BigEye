package com.frizzle.bigeye.filter;

import android.content.Context;

import com.frizzle.bigeye.R;


/**
 * author: LWJ
 * date: 2020/8/6$
 * description
 * 摄像头滤镜作为显示滤镜,显示已经渲染好的特效
 */
public class ScreenFilter extends AbstractFilter{
    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }

    @Override
    protected void initCoordinate() {

    }
}
