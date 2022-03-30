package com.milylg.editarea.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.milylg.editarea.R;

public class EditArea extends LinearLayout {

    private static final String TAG = "EditArea";


    public interface ImageGetWayCallback {
        void imageGetWay();
    }

    public interface  SaveImageCallback {
        void saveImageBitmap(
                Bitmap imageBitmap,
                String imageName,
                ImageInsertedPostCallback postCallback);
    }

    public interface DeleteImageCallback {
        void delete(String imageUrl);
    }

    public interface ImageInsertedPostCallback {
        void updateImageMetadata(String url, String imageName);
    }

    private Configuration configuration;
    private View active;
    private InputTextStyle inputTextStyle;
    private ImageStyle imageStyle;

    public EditArea(Context context) {
        super(context);
        initialize(null);
    }

    public EditArea(Context context,
                    @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public EditArea(Context context,
                    @Nullable AttributeSet attrs,
                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    @SuppressLint("CustomViewStyleable")
    private void loadAttributes(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = null;
        try {
            typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.config);
            String editStatus = typedArray.getString(R.styleable.config_edit_status);
            String titleHintInfo = typedArray.getString(R.styleable.config_title_hint);
            if (titleHintInfo!=null && !"".equals(titleHintInfo)) {
                configuration.setTitleHint(titleHintInfo);
            }
            if (!TextUtils.isEmpty(editStatus)) {
                try {
                    RenderType type = RenderType.valueOf(editStatus.toUpperCase());
                    configuration.initStatus(type);
                } catch (IllegalArgumentException ex) {
                    Log.w(TAG, "EditArea组件参数配置错误：" + ex.getMessage());
                }
            }
        } finally {
            if (typedArray != null) {
                typedArray.recycle(); // ensure this is always called
            }
        }
    }

    private void initialize(AttributeSet attrs) {
        configuration = new Configuration(getContext());
        loadAttributes(attrs);
    }

    InputTextStyle getInputTextStyle() {
        return inputTextStyle;
    }

    ImageStyle getImageStyle() {
        return imageStyle;
    }

    void addTextView(View view, int position) {
        addView(view, position);
        active = view;
    }

    void removeView(int position) {
        removeViewAt(position);
    }

    View viewAt(int position) {
        return getChildAt(position);
    }

    int viewIndex(View view) {
        return indexOfChild(view);
    }

    int activeViewIndex() {
        return indexOfChild(active);
    }

    void markActiveView(View view) {
        active = view;
    }

    View activeView() {
        return active;
    }

    public void applyNormalTypeface(String typefaceFilePath) {
        if (typefaceFilePath == null || "".equals(typefaceFilePath)) {
            throw new IllegalArgumentException("字体文件路径配置错误:" +
                    "[typefaceFilePath]:" + typefaceFilePath);
        }
        configuration.setTypeface(Typeface.NORMAL, typefaceFilePath);
    }

    public void applyBoldTypeface(String typefaceFilePath) {
        if (typefaceFilePath == null || "".equals(typefaceFilePath)) {
            throw new IllegalArgumentException("字体文件路径配置错误:" +
                    "[typefaceFilePath]:" + typefaceFilePath);
        }
        configuration.setTypeface(Typeface.BOLD, typefaceFilePath);
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void start() {
        inputTextStyle = new InputTextStyle(this);
        imageStyle = new ImageStyle(this);

        setOnTouchListener((v, event) -> {
            // positon = (0, n-1)   child count = n
            View lastView = viewAt(getChildCount() - 1);
            EditCtl editCtl = (EditCtl) lastView.getTag();
            StyleTagEnum styleTag = editCtl.getParagraphType();
            if (styleTag != StyleTagEnum.INPUT) {
                inputTextStyle.insertRowTextAt(getChildCount(), "");
            }
            return true;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void insertImage(Bitmap imageBitmap) {
        imageStyle.insertImage(imageBitmap);
    }

    public String html() {

        // 当编辑器处于渲染状态时，显示文字的组件时TEXTVIEW.
        if (!configuration.editable()) {
            throw new IllegalStateException("Not allowed output html string.");
        }

        int childCount = getChildCount();
        StringBuilder htmlBlock = new StringBuilder();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            Node node = new Node(view);
            htmlBlock.append(node.html());
        }
        return htmlBlock.toString();
    }


    public void render(String html) {
        // TODO:
    }

    Configuration configuration() {
        return configuration;
    }

    private DeleteImageCallback deleteImageCallback;
    private SaveImageCallback saveImageCallback;
    private ImageGetWayCallback imageGetWayCallback;

    public void setImageGetWayCallback(ImageGetWayCallback imageGetWayCallback) {
        this.imageGetWayCallback = imageGetWayCallback;
    }

    void showImageGetWay() {
        imageGetWayCallback.imageGetWay();;
    }

    public void setDeleteImageCallback(DeleteImageCallback deleteImageCallback) {
        this.deleteImageCallback = deleteImageCallback;
    }

    public void setSaveImageCallback(SaveImageCallback saveImageCallback) {
        this.saveImageCallback = saveImageCallback;
    }

    void removeImageFileBy(String imageUrl) {
        deleteImageCallback.delete(imageUrl);
    }

    void callSaveImage(Bitmap bitmap, String imageName, ImageInsertedPostCallback postCallback) {
        saveImageCallback.saveImageBitmap(
                bitmap, imageName, postCallback);
    }
}
