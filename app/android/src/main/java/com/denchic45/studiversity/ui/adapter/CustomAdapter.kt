package com.denchic45.studiversity.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.denchic45.studiversity.data.domain.model.DomainModel

abstract class CustomAdapter<T : DomainModel, VH : BaseViewHolder<out T, *>>(
    diffCallback: DiffUtil.ItemCallback<T>,
    open val onItemClickListener: OnItemClickListener = OnItemClickListener {  },
    open val onItemLongClickListener: OnItemLongClickListener = OnItemLongClickListener {  }
) : ListAdapter<T, VH>(diffCallback)