package com.milylg.editarea.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.appcompat.widget.AppCompatEditText;

public class NEditText extends AppCompatEditText {

    private SampleInputConnection inputConnection;

    public NEditText(Context context) {
        super(context);
        initInputConnection();
    }

    public NEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInputConnection();
    }


    public NEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInputConnection();
    }

    public void setKeyEventHandler(KeyEventHandler handler) {
        inputConnection.setKeyEventHandler(handler);
    }

    private void initInputConnection() {
        inputConnection = new SampleInputConnection(null,true);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        inputConnection.setTarget(super.onCreateInputConnection(outAttrs));
        return inputConnection;
    }

    public interface KeyEventHandler {
        /**
         * @return true表示消费了这个事件，不会交由EditableInputConnection处理
         */
        boolean onBackSpace();

        boolean onNextLine();
    }

    private static class SampleInputConnection extends InputConnectionWrapper {

        private static final String TAG = "SampleInputConnection";

        private KeyEventHandler keyEventHandler;

        /**
         * Initializes a wrapper.
         *
         * <p><b>Caveat:</b> Although the system can accept {@code (InputConnection) null} in some
         * places, you cannot emulate such a behavior by non-null {@link InputConnectionWrapper} that
         * has {@code null} in {@code target}.</p>
         *
         * @param target  the {@link InputConnection} to be proxied.
         * @param mutable set {@code true} to protect this object from being reconfigured to target
         *                another {@link InputConnection}.  Note that this is ignored while the target is {@code null}.
         */
        public SampleInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        public void setKeyEventHandler(KeyEventHandler keyEventHandler) {
            this.keyEventHandler = keyEventHandler;
        }

        /**
         * 软键盘删除该文本时，会调用该方法通知EditText
         * 在这里可以重写方法，判断是否拦截删除事件。
         * @param beforeLength
         * @param afterLength
         * @return
         */
        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {

            Log.i(TAG, "onBackSpace: true, bl = " + beforeLength + "  al = " + afterLength);

            if (keyEventHandler == null) {
                return super.deleteSurroundingText(beforeLength, afterLength);
            }

            if (keyEventHandler.onBackSpace()) {
                return true;
            }

            if (keyEventHandler.onNextLine()) {
                return true;
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }

        /**
         * 在软键盘上点击一些按钮（回车，退格，数字），当需要拦截这些事件时，可以在这里拦截并处理
         *
         * @param event
         * @return 如果返回true，EditText将不会接收到KeyEvent。
         */
        @Override
        public boolean sendKeyEvent(KeyEvent event) {

            Log.i(TAG, "sendKeyEvent: " + event.toString());

            if (keyEventHandler == null) {
                return super.sendKeyEvent(event);
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DEL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyEventHandler.onBackSpace()) {
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyEventHandler.onNextLine()) {
                    return true;
                }
            }
            return super.sendKeyEvent(event);
        }
    }

    public EditCtl editCtl() {
        return (EditCtl) getTag();
    }

}
