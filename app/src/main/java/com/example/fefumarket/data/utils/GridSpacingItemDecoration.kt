package com.example.fefumarket.data.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// ItemDecoration для RecyclerView, добавляет равномерные отступы между элементами сетки
// spanCount — количество колонок, spacing — величина отступа в пикселях,
// includeEdge — учитывать ли отступы по краям
class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    // Вычисляет отступы для каждого элемента сетки
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}