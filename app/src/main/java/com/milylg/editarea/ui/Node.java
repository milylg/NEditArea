package com.milylg.editarea.ui;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;

import com.milylg.editarea.R;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private static final String HTML_TAG_CONTENT = "{{$content}}";
    private static final String HTML_TAG_CSS = "{{$style}}";
    private static final String HTML_TAG_IMAGE_URL = "{{$url}}";
    private static final String HTML_TAG_SUB_IMAGE = "{{$img-sub}}";

    private final StyleTagEnum htmlTagType;
    private final List<String> textArray;
    private List<ApplyStyleEnum> applyStyles;
    private TextStyle textStyle;
    private ArrayList<Node> childes;

    public Node(StyleTagEnum htmlTagType) {
        this.htmlTagType = htmlTagType;
        textArray = new ArrayList<>();
        applyStyles = new ArrayList<>();
    }

    public Node(View view) {
        EditCtl editCtl = (EditCtl) view.getTag();
        this.htmlTagType = editCtl.getParagraphType();
        textArray = new ArrayList<>();
        applyStyles = new ArrayList<>();
        childes = new ArrayList<>();

        if (htmlTagType == StyleTagEnum.INPUT) {
            EditText text = (EditText) view;
            applyStyles.addAll(editCtl.applyStyles());
            textArray.add(Html.toHtml(text.getText()));
            textStyle = editCtl.textStyle();
        } else if (htmlTagType == StyleTagEnum.IMG) {
            String imagePath = editCtl.getMediaPath();
            if (!TextUtils.isEmpty(imagePath)) {
                textArray.add(imagePath);
                EditText textView = view.findViewById(R.id.et_image_desc);
                Node imageDesc = new Node(textView);
                childes.add(imageDesc);
            }
        } else if (htmlTagType == StyleTagEnum.TABLE_NOORDER_LIST
                || htmlTagType == StyleTagEnum.TABLE_ORDER_LIST) {

            TableLayout table = (TableLayout) view;
            int rowCount = table.getChildCount();
            for (int j = 0; j < rowCount; j++) {
                View rowItem = table.getChildAt(j);
                EditText li = rowItem.findViewById(R.id.et_item_text);
                Node item = new Node(li);
                childes.add(item);
            }
        }
    }

    @Deprecated
    public void updateApplyStyle(List<ApplyStyleEnum> applyStyles) {
        this.applyStyles = applyStyles;
    }

    public void updateTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public void append(String text) {
        this.textArray.add(text);
    }

    private String elementText() {
        String html = textArray.get(0);
        return Jsoup.parse(html)
                .body()
                .select("p")
                .html();
    }

    public String html() {
        if (htmlTagType == StyleTagEnum.INPUT) {
            return inputHtml();
        } else if (htmlTagType == StyleTagEnum.IMG) {
            return imgHtml();
        } else if (htmlTagType == StyleTagEnum.TABLE_ORDER_LIST
                || htmlTagType == StyleTagEnum.TABLE_NOORDER_LIST) {
            return listHtml();
        } else {
            return "";
        }
    }

    private String inputHtml() {
        String htmlTemplate = htmlTagType.template();

        if (applyStyles.isEmpty()) {
            htmlTemplate =  ApplyStyleEnum.NORMAL.apply(htmlTemplate);
        } else {
            for (ApplyStyleEnum style: applyStyles) {
               htmlTemplate = style.apply(htmlTemplate);
            }
        }
        htmlTemplate = replace(HTML_TAG_CSS, htmlTemplate, textStyle.htmlValue());
        return replace(HTML_TAG_CONTENT, htmlTemplate, elementText());
    }

    private String imgHtml() {
        StringBuilder subImgHtml = new StringBuilder();
        for (Node node: childes) {
            subImgHtml.append(node.inputHtml());
        }
        String imageHtml = htmlTagType.template();
        imageHtml = replace(HTML_TAG_IMAGE_URL, imageHtml, textArray.get(0));
        return replace(HTML_TAG_SUB_IMAGE, imageHtml, subImgHtml.toString());
    }

    private String listHtml() {
        String parentTemplate = htmlTagType.template();
        StringBuilder childBlock = new StringBuilder();
        for (Node child: childes) {
            String itemHtml = child.inputHtml();
            childBlock.append(itemHtml);
        }
        return replace(HTML_TAG_CONTENT, parentTemplate, childBlock.toString());
    }

    private String replace(String tag, String templ, String text) {
        return templ.replace(tag, text);
    }
}
