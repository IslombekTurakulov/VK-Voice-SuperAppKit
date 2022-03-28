package com.iuturakulov.vkvoicesuperappkit.controller;

import android.content.Context;
import android.view.ContextMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class ContextMenuRecyclerView extends RecyclerView {

    private RecyclerViewContextMenuInfo mContextMenuInfo;

    public ContextMenuRecyclerView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        final int longPressPosition = getChildPosition(originalView);
        if (longPressPosition >= 0) {
            final long longPressId = Objects.requireNonNull(getAdapter()).getItemId(longPressPosition);
            mContextMenuInfo = new RecyclerViewContextMenuInfo(longPressPosition, longPressId);
            return super.showContextMenuForChild(originalView);
        }
        return false;
    }

    public static class RecyclerViewContextMenuInfo implements ContextMenu.ContextMenuInfo {

        public RecyclerViewContextMenuInfo(int position, long id) {
            this.position = position;
            this.id = id;
        }

        final public int position;
        final public long id;
    }
}