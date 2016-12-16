package com.czt.mp3recorder.sample.Myapplication;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.czt.mp3recorder.sample.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;



/**
 * Created by pengyq on 5/16/2016.
 */
public class mLv_recordAdapter extends BaseAdapter {
    Activity activity;
    List<mLv_recordABean> list;
    IUdeleteLisedata IdeleteLisedata;
    List<AnimationDrawable> animList = new ArrayList<>();
    private AnimationDrawable mAnim4;

    public mLv_recordAdapter(Activity activity ,  List<mLv_recordABean> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    public void setList( List list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setIUdeleteLisedata(IUdeleteLisedata IdeleteLisedata) {
        this.IdeleteLisedata = IdeleteLisedata;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        HotHolder holder = new HotHolder();
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.item_list_person, null);
          /*  holder.sound_delete_layout = (RelativeLayout) view.findViewById(R.id.sound_delete_layout);*/
            holder.sound_delete = (TextView) view.findViewById(R.id.sound_delete_new);
            holder.sound_upload = (TextView) view.findViewById(R.id.sound_upload_new);
            holder.sound_show = (Button) view.findViewById(R.id.sound_show_new);
            holder.iv_red_point = (ImageView) view.findViewById(R.id.iv_red_point);

            view.setTag(holder);
        } else {
            holder = (HotHolder) view.getTag();
        }

        list.get(position).anim=(AnimationDrawable) holder.sound_show.getCompoundDrawables()[0];
        holder.sound_show.setText(list.get(position).length+":"+position);
        holder.iv_red_point.setVisibility(list.get(position).invisible);
        final View finalView =view;

        holder.sound_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(position).invisible = View.INVISIBLE;
                ImageView iv_red_point = (ImageView) finalView.findViewById(R.id.iv_red_point);
                iv_red_point.setVisibility(View.INVISIBLE);
                mLv_recordABean item = (mLv_recordABean) getItem(position);
                String  filePath = item.getFilePath();
                for (int i = 0; i < list.size(); i++) {
                    //第一帧
                    list.get(i).anim.selectDrawable(0);
                    list.get(i).anim.stop();
                    if (TextUtils.equals(list.get(i).getFilePath(),filePath)){
                        IdeleteLisedata.showSound(list ,i, mAnim4);
                    }
                }

                if( list.get(position).anim!=null){
                    list.get(position).anim.start();
                    String filePath1 = list.get(position).filePath;
                    System.out.println("这是一个新世界 holder.sound_show : "+"position==" +position+", mlist.size()=="+list.size()+", filePath1=="+filePath1);
                     //MediaPlayerManager.getInstance().setSoundWaveAnimationList(list,position);
                    IdeleteLisedata.play_finish( list, position);
                }

            }
        });
        holder.sound_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* System.out.println("这是一个新世界holder.sound_delete.position=="+ position);*/
                mLv_recordABean item = (mLv_recordABean) getItem(position);
                String  filePath = item.getFilePath();
                for (int i = 0; i < list.size(); i++) {
                    if (TextUtils.equals(list.get(i).getFilePath(),filePath)){
                        IdeleteLisedata.deleteLisedata(list ,i);
                    }
                }
            }
        });

        holder.sound_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //上传文件

            }
        });

        SwipeLayout sl = (SwipeLayout) view;
        sl.setOnSwipeListener(onSwipeListener);

        return view;
    }

    HashSet<SwipeLayout> openedItems = new HashSet<>();

    SwipeLayout.OnSwipeListener onSwipeListener = new SwipeLayout.OnSwipeListener() {
        @Override
        public void onClose(SwipeLayout layout) {
            System.out.println("---onClose");
            openedItems.remove(layout);
        }

        @Override
        public void onOpen(SwipeLayout layout) {
            System.out.println("---onOpen");
            openedItems.add(layout);
        }

        @Override
        public void onStartOpen(SwipeLayout layout) {
            closeAllItems();
        }
    };

    public void closeAllItems() {
        System.out.println("---onStartOpen");
        for (SwipeLayout openedItem : openedItems) {
            openedItem.close();
        }
        openedItems.clear();
    }


    class HotHolder {
      /*  RelativeLayout sound_delete_layout;*/
        TextView sound_delete;
        TextView sound_upload;
        Button sound_show;
        ImageView iv_red_point ;
    }

}
