package com.czt.mp3recorder.sample.Myapplication;

import android.graphics.drawable.AnimationDrawable;

import java.util.List;

/**
 * @创建者 Administrator
 * @创建时间 2016/11/25  16:18
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public interface IUdeleteLisedata {
    void deleteLisedata(List<mLv_recordABean> list, int position);
    void showSound(List<mLv_recordABean> list, int position, AnimationDrawable anim4);
    void play_finish(List<mLv_recordABean> list, int position);
}
