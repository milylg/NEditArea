package com.milylg.editarea.ui;


import java.util.ArrayList;
import java.util.List;

public class EditCtl {

    private StyleTagEnum paragraphHtmlTagType;
    private String mediaPath;
    private TextStyle textStyle;
    private List<ApplyStyleEnum> applyStyles;

    public EditCtl(StyleTagEnum paragraphHtmlTagType) {
        this.paragraphHtmlTagType = paragraphHtmlTagType;
        applyStyles = new ArrayList<>();
        switch (paragraphHtmlTagType) {
            case INPUT:
                initTextColor();
                break;
            case ROW_ORDER:
                removeApplyStyle(ApplyStyleEnum.ORDER_ITEM);
                initTextColor();
                break;
            case ROW_NOORDER:
                addApplyStyle(ApplyStyleEnum.UNORDERED_ITEM);
                initTextColor();
                break;
            case IMG:
                break;
            case IMG_SUB:
                initTextColor();
                break;
        }
    }

    public boolean isParagraphStyleType(StyleTagEnum paragraphType) {
        if (paragraphType == null) {
            return false;
        }
        return paragraphHtmlTagType == paragraphType;
    }

    void initTextColor() {
        textStyle = new TextStyle(TextStyle.TextColor.PLAIN);
    }

    String rotationApplyColor() {
        return textStyle.rotation();
    }

    void restoreTextColor() {
        textStyle.restore(TextStyle.TextColor.PLAIN.color());
    }

    void modifiedTextColor(TextStyle.TextColor color) {
        textStyle.restore(color.color());
    }

    String currentTextColor() {
        return textStyle.getTextColor();
    }

    public boolean contains(ApplyStyleEnum editStyle) {
        return applyStyles.contains(editStyle);
    }

    public static final ApplyStyleEnum[] MUTEXES_EDIT_STYLES = {
            ApplyStyleEnum.NORMAL,
            ApplyStyleEnum.H2,
            ApplyStyleEnum.H1,
            ApplyStyleEnum.BLOCKQUOTE,
            ApplyStyleEnum.ORDER_ITEM,
            ApplyStyleEnum.UNORDERED_ITEM
    };

    public void removeApplyStyle(ApplyStyleEnum style) {
        int index = applyStyles.indexOf(style);
        if (index != -1) {
            applyStyles.remove(index);
        }
        if (applyStyles.isEmpty()) {
            applyStyles.add(ApplyStyleEnum.NORMAL);
        }
    }

    public void addApplyStyle(ApplyStyleEnum style) {
        int index = applyStyles.indexOf(style);
        if (index == -1) {
            clearMutexesIfAbsent(style);
            applyStyles.add(style);
        }
    }

    private void clearMutexesIfAbsent(ApplyStyleEnum style) {
        if (isMutexes(style)) {
            for (ApplyStyleEnum mutexesEditStyle : MUTEXES_EDIT_STYLES) {
                applyStyles.remove(mutexesEditStyle);
            }
        }
    }

    private boolean isMutexes(ApplyStyleEnum style) {
        for (ApplyStyleEnum mutexes : MUTEXES_EDIT_STYLES) {
            if (mutexes == style) {
                return true;
            }
        }
        return false;
    }

    public StyleTagEnum getParagraphType() {
        return paragraphHtmlTagType;
    }

    public List<ApplyStyleEnum> applyStyles() {
        return applyStyles;
    }

    public TextStyle textStyle() {
        return textStyle;
    }

    public void setMediaPath(String imagePath) {
        this.mediaPath = imagePath;
    }

    public String getMediaPath() {
        return mediaPath;
    }
}
