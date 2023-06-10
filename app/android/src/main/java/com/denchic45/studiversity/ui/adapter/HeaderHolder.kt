package com.denchic45.studiversity.ui.adapter

import com.denchic45.studiversity.data.model.domain.ListItem
import com.denchic45.studiversity.databinding.ItemHeaderBinding

class HeaderHolder(itemHeaderBinding: ItemHeaderBinding) :
    BaseViewHolder<ListItem, ItemHeaderBinding>(itemHeaderBinding) {
    override fun onBind(item: ListItem) {
        binding.tvHeader.text = item.title
    }
}