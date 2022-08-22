/*
 * Copyright (c) 2016 Nikita Obrekht
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.obrekht.onlinecameras.ui.common;

import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Adds offsets to a RecyclerView with a GridLayoutManager.
 */
public class GridOffsetItemDecoration extends RecyclerView.ItemDecoration {

    private final int offset;

    public GridOffsetItemDecoration(int offset) {
        this.offset = offset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int numColumns = layoutManager.getSpanCount();

        if (numColumns > 1) {
            boolean childIsInLeftmostColumn =
                    (parent.getChildAdapterPosition(view) % numColumns) == 0;
            boolean childIsInRightmostColumn =
                    (parent.getChildAdapterPosition(view) % numColumns) == (numColumns - 1);

            if (childIsInLeftmostColumn) {
                outRect.right = offset / 2;
            } else if (childIsInRightmostColumn) {
                outRect.left = offset / 2;
            } else {
                outRect.right = offset / 2;
                outRect.left = offset / 2;
            }
        }

        boolean childIsInFirstRow = (parent.getChildAdapterPosition(view)) < numColumns;
        if (!childIsInFirstRow) {
            outRect.top = offset;
        }
    }
}