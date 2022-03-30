/*
 * Copyright (C) 2016 Muhammed Irshad
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milylg.editarea.ui;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.milylg.editarea.R;

import org.jsoup.nodes.Element;

public class InputTextStyle extends EditStyle {

    private ContextThemeWrapper themeWrapper;
    private StyleActionModeCallback styleActionModeCallback;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public InputTextStyle(EditArea editArea) {
        super(editArea);
        init();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void init() {
        themeWrapper = new ContextThemeWrapper(
                mContext, R.style.edit_text_input_style);
        styleActionModeCallback = new StyleActionModeCallback(
                newStyleActionHandler()
        );
        // 当且仅当编辑器处于编辑状态时，需要自动插入标题段落
        if (configuration.editable()) {
            insertRowTextAt(0, "");
        }
    }

    private MenuStyleActionHandler newStyleActionHandler() {
        return new MenuStyleActionHandler() {
            @Override
            public void applyBoldStyle() {
                NEditText editText = (NEditText) editArea.activeView();
                boolean isTitleParagraph = editArea.viewIndex(editText) == 0;
                if (isTitleParagraph) {
                    return;
                }

                EditCtl editControl = editText.editCtl();
                if (editControl.contains(ApplyStyleEnum.BOLD)) {
                    editControl.removeApplyStyle(ApplyStyleEnum.BOLD);
                    editText.setTypeface(configuration.getTypeface(Typeface.NORMAL));
                } else {
                    editControl.addApplyStyle(ApplyStyleEnum.BOLD);
                    editText.setTypeface(configuration.getTypeface(Typeface.BOLD));
                }
            }

            @Override
            public void toggleFontSize() {
                NEditText editText = (NEditText) editArea.activeView();
                boolean isTitleParagraph = editArea.viewIndex(editText) == 0;
                if (isTitleParagraph) {
                    return;
                }
                EditCtl editControl = editText.editCtl();

                if (editControl.contains(ApplyStyleEnum.BOLD)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, configuration.getPlainTextSize());
                    editText.setTypeface(configuration.getTypeface(Typeface.NORMAL));
                    editControl.addApplyStyle(ApplyStyleEnum.NORMAL);
                    editControl.removeApplyStyle(ApplyStyleEnum.BOLD);
                } else {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, configuration.getSubTitleSize());
                    editText.setTypeface(configuration.getTypeface(Typeface.BOLD));
                    editControl.addApplyStyle(ApplyStyleEnum.BOLD);
                    editControl.removeApplyStyle(ApplyStyleEnum.H2);
                }
            }

            @Override
            public void applyTextColor() {
                NEditText editText = (NEditText) editArea.activeView();
                EditCtl editControl = editText.editCtl();
                String newColor = editControl.rotationApplyColor();
                editText.setTextColor(Color.parseColor(newColor));
            }

            @Override
            public void applyQuoteBlock() {
                NEditText editText = (NEditText) editArea.activeView();
                boolean isTitleParagraph = editArea.viewIndex(editText) == 0;
                if (isTitleParagraph) {
                    return;
                }
                EditCtl editControl = editText.editCtl();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) editText.getLayoutParams();
                int pTop = editText.getPaddingTop();
                int pBottom = editText.getPaddingBottom();
                int pRight = editText.getPaddingRight();

                if (editControl.contains(ApplyStyleEnum.BLOCKQUOTE)) {
                    editControl.removeApplyStyle(ApplyStyleEnum.BLOCKQUOTE);
                    editControl.restoreTextColor();
                    editText.setTextColor(Color.parseColor(editControl.currentTextColor()));
                    editText.setTextSize(configuration.getPlainTextSize());
                    editText.setPadding(0, pTop, pRight, pBottom);
                    editText.setBackground(ContextCompat.getDrawable(
                            mContext, R.drawable.bg_edit_text_empty));
                    editText.setPadding(5, 5, 5, 8);
                    params.setMargins(0, 0, 0, 0);
                } else {
                    editControl.addApplyStyle(ApplyStyleEnum.BLOCKQUOTE);
                    editControl.modifiedTextColor(TextStyle.TextColor.BLACK_LIGHT);
                    editText.setTextColor(Color.parseColor(editControl.currentTextColor()));
                    editText.setTextSize(configuration.getQuoteTextSize());
                    editText.setBackground(ContextCompat.getDrawable(
                            mContext, R.drawable.block_quote_background));
                    editText.setPadding(15, pTop, 5, pBottom);
                    params.setMargins(5, 5, 5, 10);
                }
            }

            @Override
            public void showGetImageWay() {
                editArea.showImageGetWay();
            }
        };
    }


    @Override
    public Node getNodeFrom(View view) {
        return new Node(view);
    }

    @Override
    public void buildParagraphBy(Element element) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void insertRowTextAt(int position, @NonNull CharSequence hyperText) {

        EditCtl editCtl = new EditCtl(StyleTagEnum.INPUT);
        // 编辑模式：当处于编辑状态时，才能进行下列操作
        NEditText paragraph = newEditText(hyperText);
        if (isTitleParagraph(position)) {
            int titleFontSize = configuration.getTitleSize();
            paragraph.setTextSize(titleFontSize);
            paragraph.setHint(configuration.titleHintInfo());
            paragraph.setBackground(
                    ContextCompat.getDrawable(
                            mContext, R.drawable.bg_edit_text));
            paragraph.setTypeface(configuration.getTypeface(Typeface.BOLD));
            TextPaint paint = paragraph.getPaint();
            paint.setFakeBoldText(true);
            editCtl.addApplyStyle(ApplyStyleEnum.H1);
            editCtl.addApplyStyle(ApplyStyleEnum.BOLD);
        } else {
            paragraph.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    configuration.getPlainTextSize());
            paragraph.setBackground(
                    ContextCompat.getDrawable(
                            mContext, R.drawable.bg_edit_text_empty));
            paragraph.setTypeface(configuration.getTypeface(Typeface.NORMAL));
            paragraph.setCustomSelectionActionModeCallback(styleActionModeCallback);
            editCtl.addApplyStyle(ApplyStyleEnum.NORMAL);
        }
        paragraph.setCustomInsertionActionModeCallback(styleActionModeCallback);
        String textColor = editCtl.currentTextColor();
        paragraph.setTextColor(Color.parseColor(textColor));
        paragraph.setTag(editCtl);
        editArea.addTextView(paragraph, position);
        // 渲染模式......
    }

    private boolean isTitleParagraph(int position) {
        return position == 0;
    }


    private NEditText newEditText(CharSequence hyperText) {

        final NEditText editTextCreated = new NEditText(themeWrapper);
        styleFormat(editTextCreated);
        registerActionHandler(editTextCreated);
        // 这里需要对hyperText进行过滤，变成普通文本。
        editTextCreated.setText(hyperText);
        editTextCreated.setSelection(hyperText.length());
        editTextCreated.requestFocus();
        return editTextCreated;
    }

    private void styleFormat(TextView editText) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(params);
        editText.setFocusableInTouchMode(true);
        editText.setPadding(5, 5, 5, 8);
        editText.setGravity(Gravity.CENTER_VERTICAL);
    }

    // 标记当前EditText对象为激活视图，用于表明光标位置
    private final View.OnFocusChangeListener activeViewFocusHandler = (v, hasFocus) -> {
        if (hasFocus) {
            editArea.markActiveView(v);
        }
    };

    // 为避免重复创建监听器对象，将事件处理器变成InputTextStyle的成员变量。
    private void registerActionHandler(NEditText editText) {

        // 对EditText的换行符进行监听处理
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void afterTextChanged(Editable s) {
                // 处理换行逻辑
                // 设置当前文本编辑框纯文本内容[0，i）
                int nextLineSymbolIndex = -1;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '\n') {
                        editText.setText(s.subSequence(0, i));
                        nextLineSymbolIndex = i;
                        break;
                    }
                }
                if (nextLineSymbolIndex == -1) {
                    return;
                }

                // 处理[i, s.len)文本内容
                SpannableStringBuilder editable = new SpannableStringBuilder();
                int lastIndex = s.length();
                int nextIndex = nextLineSymbolIndex + 1;
                if (nextIndex < lastIndex) {
                    editable.append(s.subSequence(nextIndex, lastIndex));
                }
                // 在layout中添加下一段文字（editable, position);
                int nextParagraphIndex = editArea.viewIndex(editText) + 1;
                insertRowTextAt(nextParagraphIndex, editable);
            }
        });

        // 保留监听处理：若需要其它的文本输入定制时，实现接口。
        editText.setKeyEventHandler(new NEditText.KeyEventHandler() {
            @Override
            public boolean onBackSpace() {
                return false;
            }

            @Override
            public boolean onNextLine() {
                return false;
            }
        });

        editText.setOnKeyListener((v, keyCode, event) -> {

            if (keyCode != KeyEvent.KEYCODE_DEL) {
                return false;
            }

            // case 0:当光标在段落首列，不作处理
            int viewIndexFromParent = editArea.viewIndex(editText);
            if (viewIndexFromParent == 0) {
                return false;
            }

            Editable text = editText.getText();

            if (text != null && text.length() == 0) {
                // case 1:文本为空
                NEditText prevEditText = previousInputTextOf(editText);
                editArea.removeView(viewIndexFromParent);
                // NOTE:这里需要使用NeastView嵌套EditArea，
                // 并设置focusable=true and touchmode = true
                // 否则编辑器会失去软键盘
                prevEditText.requestFocus();
                prevEditText.setSelection(prevEditText.getText().length());
                return false;
            } else {
                // case 2:文本不为空
                int selectionStart = editText.getSelectionStart();
                if (selectionStart != 0) {
                    return false; // 光标没有移动到边界，交由下一个事件处理器，当前不处理。
                }
                // 仅当光标处于段落首列时
                NEditText lastestInput = previousInputTextOf(editText);

                if (text.length() > 0) {
                    // 合并文本，删除光标所在段落
                    if (lastestInput != null) {
                        Editable latestInputText = lastestInput.getText();
                        latestInputText.append(editText.getText().toString());
                        editArea.removeView(viewIndexFromParent);
                        lastestInput.requestFocus();
                        lastestInput.setSelection(latestInputText.length());
                    }
                } else {
                    // 仅删除光标所在段落
                    editArea.removeView(viewIndexFromParent);
                    lastestInput.setSelection(lastestInput.length());
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(activeViewFocusHandler);
    }

    private NEditText previousInputTextOf(NEditText editText) {
        int index = editArea.viewIndex(editText);
        NEditText latestInput = null;
        for (int i = index - 1; i >= 0;  i--) {
            View view = editArea.viewAt(i);
            EditCtl editCtl = (EditCtl) view.getTag();
            if (editCtl.isParagraphStyleType(StyleTagEnum.INPUT)) {
                latestInput = (NEditText) view;
                break;
            }
        }
        return latestInput;
    }


}
