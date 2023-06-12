package com.denchic45.studiversity.widget.extendedAdapter

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import com.denchic45.studiversity.data.model.domain.ListItem
import com.denchic45.studiversity.ui.onVector
import com.denchic45.studiversity.databinding.ItemIconContentBinding
import com.denchic45.studiversity.ui.adapter.BaseViewHolder
import com.denchic45.studiversity.ui.onName
import com.denchic45.studiversity.util.viewBinding

class ItemAdapterDelegate : ListItemAdapterDelegate<ListItem, ItemAdapterDelegate.ItemHolder>() {

    class ItemHolder(
        itemIconContentBinding: ItemIconContentBinding,
    ) : BaseViewHolder<ListItem, ItemIconContentBinding>(
        itemIconContentBinding
    ) {
        override fun onBind(item: ListItem) {
            with(binding) {

                item.icon?.onVector {
                    ivIcon.setImageResource(it)
                }?.onName {
                    val iconResId = itemView.context.resources.getIdentifier(
                        it,
                        "drawable",
                        itemView.context.packageName
                    )
                    ivIcon.setImageResource(iconResId)
                }


                tvName.text = item.title

                item.color?.onVector {
                    ImageViewCompat.setImageTintList(
                        ivIcon,
                        ColorStateList.valueOf(it)
                    )
                }?.onName {
                    val identifier = itemView.resources
                        .getIdentifier(it, "color", itemView.context.packageName)
                    ColorStateList.valueOf(
                        identifier
                    )
                }
            }
        }
    }

    override fun isForViewType(item: Any): Boolean {
        return item is ListItem && item.type == 0
    }

    override fun onBindViewHolder(item: ListItem, holder: ItemHolder) {
        holder.onBind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): ItemHolder {
        return ItemHolder(parent.viewBinding(ItemIconContentBinding::inflate))
    }
}