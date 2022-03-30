package com.milylg.editarea.ui;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;

import com.milylg.editarea.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class StyleActionModeCallback extends ActionMode.Callback2 {

    private final MenuStyleActionHandler styleHandler;

    public StyleActionModeCallback(MenuStyleActionHandler styleHandler) {
        this.styleHandler = styleHandler;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // 如果需要屏蔽系统的menu items，仅调用 menu.clear().
        mode.getMenuInflater().inflate(R.menu.menu_style, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bold_style:
                styleHandler.applyBoldStyle();
                mode.finish();
                break;

            case R.id.font_size_style:
                styleHandler.toggleFontSize();
                mode.finish();
                break;

            case R.id.font_color_style:
                styleHandler.applyTextColor();
                mode.finish();
                break;

            case R.id.quote_block_style:
                styleHandler.applyQuoteBlock();
                mode.finish();
                break;

            case R.id.image_get_way:
                styleHandler.showGetImageWay();
                mode.finish();
                break;
        }
        // 返回 true，系统的menu item失效
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

}
