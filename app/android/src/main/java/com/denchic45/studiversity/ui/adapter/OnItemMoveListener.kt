package com.denchic45.studiversity.ui.adapter

interface OnItemMoveListener {
    fun onMove(oldPosition: Int, targetPosition: Int, dayOfWeek: Int)
}