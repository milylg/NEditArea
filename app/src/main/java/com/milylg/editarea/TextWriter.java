package com.milylg.editarea;

import android.app.Activity;
import android.view.View;

import com.milylg.editarea.ui.Configuration;

public interface TextWriter {

    Activity getActivity();

    void addTextView(View view, int position);

    void addTextView(View view);

    void removeView(int position);

    View viewAt(int position);

    int viewIndex(View view);

    void markActiveView(View view);

    Configuration configuration();

    String html();

    void render(String html);

}
