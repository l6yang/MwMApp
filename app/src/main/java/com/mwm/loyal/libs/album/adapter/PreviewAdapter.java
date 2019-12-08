package com.mwm.loyal.libs.album.adapter;

import android.graphics.Bitmap;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.List;

import com.mwm.loyal.libs.album.entity.AlbumImage;
import com.mwm.loyal.libs.album.task.ImageLocalLoader;
import com.mwm.loyal.libs.album.util.DisplayUtils;
import com.mwm.loyal.libs.album.widget.PhotoViewAttacher;

public class PreviewAdapter extends PagerAdapter {

    private List<AlbumImage> mAlbumImages;
    private int contentHeight;

    public PreviewAdapter(List<AlbumImage> mAlbumImages, int contentHeight) {
        this.mAlbumImages = mAlbumImages;
        this.contentHeight = contentHeight;
    }

    @Override
    public int getCount() {
        return mAlbumImages == null ? 0 : mAlbumImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        container.addView(imageView);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);

        ImageLocalLoader.getInstance().loadImage(imageView, mAlbumImages.get(position).getPath(), DisplayUtils.screenWidth, contentHeight, new ImageLocalLoader.LoadListener() {
            @Override
            public void onLoadFinish(Bitmap bitmap, ImageView imageView, String imagePath) {
                imageView.setImageBitmap(bitmap);
                attacher.update();

                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                int bitmapSize = height / width;
                int contentSize = contentHeight / DisplayUtils.screenWidth;

                if (height > width && bitmapSize >= contentSize) {
                    attacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    attacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((View) object));
    }
}
