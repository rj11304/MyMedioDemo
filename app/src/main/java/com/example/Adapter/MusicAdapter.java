package com.example.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.MusicModel;
import com.example.mymediodemo.MyApplication;
import com.example.mymediodemo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/26.
 */
public class MusicAdapter extends BaseAdapter{

    public int checkedIndex = -1;
    private List<MusicModel> musicModels = new ArrayList<MusicModel>();
    private Context mContext;

    public MusicAdapter(Context context,List<MusicModel> list) {
        mContext = context;
        musicModels.addAll(list);
    }

        @Override
        public int getCount() {
            return musicModels.size();
        }

        @Override
        public Object getItem(int position) {
            return musicModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(mContext, R.layout.music_item,null);
                ViewHolder holder = new ViewHolder();
                holder.artlist = (TextView) convertView.findViewById(R.id.artlist);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            MusicModel model = (MusicModel) getItem(position);
            ImageLoader.getInstance().displayImage(model.imgUrl, holder.icon, MyApplication.options);
            holder.name.setText(model.musicName);
            holder.artlist.setText(model.musicArtist);
            return convertView;
        }

        class ViewHolder{
            public ImageView icon;
            public TextView name;
            public TextView artlist;
        }
}
