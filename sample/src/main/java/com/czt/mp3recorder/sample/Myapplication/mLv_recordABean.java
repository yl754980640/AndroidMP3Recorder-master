package com.czt.mp3recorder.sample.Myapplication;

import android.graphics.drawable.AnimationDrawable;

/**
 * @创建者 Administrator
 * @创建时间 2016/11/25  11:34
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class mLv_recordABean {
    String filePath;
    String length ;
    int id;
    int position;
    public AnimationDrawable anim;
    int invisible ;
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "mLv_recordABean{" +
                "filePath='" + filePath + '\'' +
                ", length='" + length + '\'' +
                ", id=" + id +
                '}';
    }
}
