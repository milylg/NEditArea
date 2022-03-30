package com.milylg.editarea.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.milylg.editarea.R;

import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageStyle extends EditStyle {

    private RequestOptions options;

    protected ImageStyle(EditArea editArea) {
        super(editArea);
        init();
    }

    @SuppressLint("CheckResult")
    @Override
    public void init() {
        options = RequestOptions.bitmapTransform(
                new RoundedCorners(20));
    }

    @Override
    public Node getNodeFrom(View view) {
        return new Node(view);
    }

    @Override
    public void buildParagraphBy(Element element) {

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    void insertImage(Bitmap bitmap) {
        final View imageLayout = LayoutInflater.from(mContext)
                .inflate(R.layout.tmpl_image_view, null);
        final ImageView ivImage = imageLayout.findViewById(R.id.image);
        final EditText imageDescText = imageLayout.findViewById(R.id.et_image_desc);

        // 初始化View Tag
        Glide.with(mContext).load(bitmap)
                .apply(options)
                .into(ivImage);
        // ivImage.setImageBitmap(bitmap);
        String imageName = generateImageId();
        imageLayout.setTag(newImageTag(imageName));
        imageDescText.setTag(new EditCtl(StyleTagEnum.IMG_SUB));

        // 判断图片布局插入位置
        int indexViewFocused = editArea.activeViewIndex();
        editArea.addView(imageLayout, indexViewFocused + 1);
        editArea.getInputTextStyle()
                .insertRowTextAt(indexViewFocused + 2, "");


        // 事件绑定，保存图片到磁盘
        if (configuration.editable()) {
            bindEvents(imageLayout, null);
            editArea.callSaveImage(bitmap, imageName, (path, uuid) -> {
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                EditCtl imageEditCtl = (EditCtl) ivImage.getTag();
                imageEditCtl.setMediaPath(path);
            });

        } else {
            imageDescText.setEnabled(false);
        }

    }


    private String generateImageId() {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String sdt = df.format(new Date());
        return MessageFormat.format("img_{0}.jpeg", sdt);
    }

    private EditCtl newImageTag(String path) {
        EditCtl control = new EditCtl(StyleTagEnum.IMG);
        control.setMediaPath(path);
        return control;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindEvents(final View layout, String url) {
        final ImageView imageView = layout.findViewById(R.id.image);
        final View btnRemoveImage = layout.findViewById(R.id.btn_remove);
        final View btnCutImage = layout.findViewById(R.id.btn_cut_image);
        final View btnAffixTimeOrLocation = layout.findViewById(R.id.btn_affix);
        final EditText etImageDesc = layout.findViewById(R.id.et_image_desc);

        etImageDesc.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editArea.markActiveView(etImageDesc);
            }
        });

        btnRemoveImage.setOnClickListener(v -> {
            editArea.removeView(layout);
            editArea.removeImageFileBy(url);
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 新特性 - 放大图片
                Log.i("TAG", "ImageView.onClick...");
            }
        });

        imageView.setOnLongClickListener(v -> {
            btnRemoveImage.setVisibility(View.VISIBLE);
            btnCutImage.setVisibility(View.VISIBLE);
            btnAffixTimeOrLocation.setVisibility(View.VISIBLE);
            Log.i("TAG", "ImageView.OnLongClick...");
            return true;
        });

        imageView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                btnRemoveImage.setVisibility(View.GONE);
                btnCutImage.setVisibility(View.GONE);
                btnAffixTimeOrLocation.setVisibility(View.GONE);
            }
        });
    }

}
