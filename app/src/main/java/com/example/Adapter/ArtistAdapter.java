package com.example.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mymediodemo.AppConstant;
import com.example.mymediodemo.MyApplication;
import com.example.mymediodemo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;

/**
 * Created by admin on 2016/12/26.
 */
public class ArtistAdapter extends BaseAdapter{

    private List<Artist> list = new ArrayList<Artist>();
    Handler mHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    public ArtistAdapter(Context context,final String artist){
        mContext = context;
        new Thread(){
            @Override
            public void run() {
                list.addAll(Artist.search(artist, AppConstant.LastFmAPI.api_key));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
                super.run();
            }
        }.start();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.music_item,null);
            ViewHolder holder = new ViewHolder();
            holder.artlist = (TextView) convertView.findViewById(R.id.artlist);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Artist model = (Artist) getItem(position);
        ImageLoader.getInstance().displayImage(model.getImageURL(ImageSize.SMALL), holder.icon, MyApplication.options);
        holder.name.setText(model.getName());
        holder.artlist.setText(model.getWikiSummary());
        return convertView;
    }

    class ViewHolder{
        public ImageView icon;
        public TextView name;
        public TextView artlist;
    }
}
