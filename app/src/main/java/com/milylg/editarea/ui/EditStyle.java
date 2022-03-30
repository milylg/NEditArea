package com.milylg.editarea.ui;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import org.jsoup.nodes.Element;

public abstract class EditStyle {

    protected final EditArea editArea;
    protected Configuration configuration;
    protected Context mContext;

    protected EditStyle(EditArea editArea) {
        this.editArea = editArea;
        mContext = editArea.getContext();
        configuration = editArea.configuration();
    }

    public abstract void init();

    public abstract Node getNodeFrom(View view);

    public abstract void buildParagraphBy(Element element);

    protected Node createNodeBy(@NonNull View view){
        EditCtl metadata = (EditCtl) view.getTag();
        return new Node(metadata.getParagraphType());
    }

}
