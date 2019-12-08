package com.mwm.loyal.libs.album.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;


import java.util.List;

import com.mwm.loyal.R;
import com.mwm.loyal.libs.album.entity.AlbumImage;
import com.mwm.loyal.libs.album.impl.OnCompatCompoundCheckListener;
import com.mwm.loyal.libs.album.impl.OnCompatItemClickListener;
import com.mwm.loyal.libs.album.task.ImageLocalLoader;
import com.mwm.loyal.libs.album.util.DisplayUtils;
import com.mwm.loyal.libs.album.util.SelectorUtils;

/**
 * <p>相册文件夹内容显示适配器。</p>
 * Created by Yan Zhenjie on 2016/10/18.
 */
public class AlbumContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BUTTON = 1;
    private static final int TYPE_IMAGE = 2;

    private LayoutInflater mLayoutInflater;

    private ColorStateList mColorStateList;

    private List<AlbumImage> mAlbumImages;

    private View.OnClickListener mAddPhotoClickListener;

    private OnCompatItemClickListener mItemClickListener;

    private OnCompatCompoundCheckListener mOnCompatCheckListener;

    private static int size = (DisplayUtils.screenWidth - 9) / 3;

    public AlbumContentAdapter(int normalColor, int checkColor) {
        this.mColorStateList = SelectorUtils.createColorStateList(normalColor, checkColor);
    }

    private void initLayoutInflater(Context context) {
        if (mLayoutInflater == null)
            mLayoutInflater = LayoutInflater.from(context);
    }

    public void notifyDataSetChanged(List<AlbumImage> albumImages) {
        this.mAlbumImages = albumImages;
        super.notifyDataSetChanged();
    }

    public void notifyItemChangedCompat(int position) {
        super.notifyItemChanged(position + 1);
    }

    public void setAddPhotoClickListener(View.OnClickListener addPhotoClickListener) {
        this.mAddPhotoClickListener = addPhotoClickListener;
    }

    public void setItemClickListener(OnCompatItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mAlbumImages == null ? 1 : mAlbumImages.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_BUTTON;
            default:
                return TYPE_IMAGE;
        }
    }

    public void setOnCheckListener(OnCompatCompoundCheckListener checkListener) {
        this.mOnCompatCheckListener = checkListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        initLayoutInflater(parent.getContext());
        switch (viewType) {
            case TYPE_BUTTON:
                return new GalleryContentButtonHolder(mLayoutInflater.inflate(R.layout.album_item_content_button, parent, false));
            default:
                return new GalleryContentImageHolder(mLayoutInflater.inflate(R.layout.album_item_content_image, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_BUTTON: {
                GalleryContentButtonHolder imageHolder = (GalleryContentButtonHolder) holder;
                imageHolder.setAddPhotoClickListener(mAddPhotoClickListener);
                break;
            }
            default: {
                int adapterPosition = holder.getAdapterPosition() - 1;
                AlbumImage albumImage = mAlbumImages.get(adapterPosition);
                GalleryContentImageHolder imageHolder = (GalleryContentImageHolder) holder;
                imageHolder.setButtonTint(mColorStateList);
                imageHolder.setData(albumImage);
                imageHolder.setItemClickListener(mItemClickListener);
                imageHolder.setOnCompatCheckListener(mOnCompatCheckListener);
                break;
            }
        }
    }

    private static class GalleryContentImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        private ImageView mIvImage;
        private AppCompatCheckBox mCompatCheckBox;

        private OnCompatItemClickListener mItemClickListener;
        private OnCompatCompoundCheckListener mOnCompatCheckListener;

        GalleryContentImageHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.getLayoutParams().width = size;
            itemView.getLayoutParams().height = size;
            itemView.requestLayout();

            mIvImage = (ImageView) itemView.findViewById(R.id.iv_album_content_image);
            mCompatCheckBox = (AppCompatCheckBox) itemView.findViewById(R.id.cb_album_check);
            mCompatCheckBox.setOnCheckedChangeListener(this);
        }

        public void setButtonTint(ColorStateList colorStateList) {
            mCompatCheckBox.setSupportButtonTintList(colorStateList);
        }

        public void setData(AlbumImage albumImage) {
            ImageLocalLoader.getInstance().loadImage(mIvImage, albumImage.getPath(), size, size);
            mCompatCheckBox.setChecked(albumImage.isChecked());
        }

        void setItemClickListener(OnCompatItemClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

        void setOnCompatCheckListener(OnCompatCompoundCheckListener mOnCompatCheckListener) {
            this.mOnCompatCheckListener = mOnCompatCheckListener;
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition() - 1);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mOnCompatCheckListener != null)
                mOnCompatCheckListener.onCheck(buttonView, getAdapterPosition() - 1, isChecked);
        }
    }

    private static class GalleryContentButtonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View.OnClickListener mClickListener;

        GalleryContentButtonHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.getLayoutParams().width = size;
            itemView.getLayoutParams().height = size;
            itemView.requestLayout();
        }

        void setAddPhotoClickListener(View.OnClickListener addPhotoClickListener) {
            this.mClickListener = addPhotoClickListener;
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null)
                mClickListener.onClick(v);
        }
    }
}
