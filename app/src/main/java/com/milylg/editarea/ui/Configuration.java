package com.milylg.editarea.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.milylg.editarea.R;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private static final String TITLE_SIZE_KEY = "title_size";
    private static final String SUBTITLE_SIZE_KEY = "subtitle_size";
    private static final String NORMAL_SIZE_KEY = "normal_size";
    private static final String QUOTE_TEXT_SIZE = "quote_size";
    private static final String TITLE_HINT_INFO = "title_hint_info";

    private final Context mContext;
    private final SharedPreferences preferences;

    private final Map<Integer, String> fontTypeface;

    private final int titleSize;
    private final int subTitleSize;
    private final int plainTextSize;
    private final int quoteTextSize;

    private String titleEmptyHintText;
    private final int defaultFontTypefaceResId;

    private RenderType editAreaStatus;

    public Configuration(Context context) {
        this.mContext = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String titleSizeStr = preferences.getString(TITLE_SIZE_KEY, "24");
        titleSize = Integer.parseInt(titleSizeStr);
        String subTitleSizeStr = preferences.getString(SUBTITLE_SIZE_KEY, "20");
        subTitleSize = Integer.parseInt(subTitleSizeStr);
        String normalTextSizeStr = preferences.getString(NORMAL_SIZE_KEY, "18");
        plainTextSize = Integer.parseInt(normalTextSizeStr);
        String quoteTextSizeStr = preferences.getString(QUOTE_TEXT_SIZE, "16");
        quoteTextSize = Integer.parseInt(quoteTextSizeStr);
        titleEmptyHintText = preferences.getString(TITLE_HINT_INFO, "备忘录标题");
        editAreaStatus = RenderType.EDITOR;
        fontTypeface = new HashMap<>();
        defaultFontTypefaceResId = R.string.font_family_sans_serif;
    }

    void initStatus(RenderType type) {
        editAreaStatus = type;
    }

    void setTitleHint(String hintText) {
        this.titleEmptyHintText = hintText;
    }

    boolean editable() {
        return editAreaStatus == RenderType.EDITOR;
    }

    int getTitleSize() {
        return titleSize;
    }

    int getSubTitleSize() {
        return subTitleSize;
    }

    int getPlainTextSize() {
        return plainTextSize;
    }

    int getQuoteTextSize() {
        return quoteTextSize;
    }

    String titleHintInfo() {
        return titleEmptyHintText;
    }

    private String defaultFontFace() {
        return mContext.getResources().getString(defaultFontTypefaceResId);
    }

    Typeface getTypeface(int style) {
        if (fontTypeface.isEmpty()) {
            return Typeface.create(defaultFontFace(), style);
        } else {
            if (fontTypeface.containsKey(style)) {
                return FontCache.get(fontTypeface.get(style), mContext);
            } else {
                throw new IllegalArgumentException(
                        "系统没有发现字体文件，请检查项目文件目录assert/font和自定义字体文件路径");
            }
        }
    }

    void setTypeface(int fontKey, @NonNull String fontResPath) {
        fontTypeface.put(fontKey, fontResPath);
    }
}
