package com.huangyu.mdfolder.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.huangyu.mdfolder.R;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by huangyu on 2017-6-20.
 */
public class ImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mImageList;

    public ImagePagerAdapter(Context context, List<String> imageList) {
        this.mContext = context;
        this.mImageList = imageList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image_pager, viewGroup, false);
        PhotoView photoView = ButterKnife.findById(view, R.id.photo_view);
        Glide.with(mContext).load(mImageList.get(position)).into(photoView);
        viewGroup.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
