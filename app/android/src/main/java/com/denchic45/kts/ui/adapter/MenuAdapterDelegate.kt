package com.denchic45.kts.ui.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.denchic45.avatarGenerator.AvatarGenerator
import com.denchic45.kts.databinding.ItemNavBinding
import com.denchic45.kts.databinding.ItemNavDividerBinding
import com.denchic45.kts.databinding.ItemNavDropdownBinding
import com.denchic45.kts.databinding.ItemNavSubHeaderBinding
import com.denchic45.kts.ui.UiText
import com.denchic45.kts.ui.model.UiModel
import com.denchic45.kts.ui.onResource
import com.denchic45.kts.ui.onString
import com.denchic45.kts.util.dpToPx
import com.denchic45.kts.util.viewBinding
import com.denchic45.widget.extendedAdapter.DelegationAdapterDsl
import com.denchic45.widget.extendedAdapter.DelegationAdapterExtended
import com.denchic45.widget.extendedAdapter.ListItemAdapterDelegate
import java.util.UUID

fun navAdapter(builderDelegation: DelegationAdapterDsl.DelegationAdapterBuilder.() -> Unit): DelegationAdapterExtended {
    val delegationAdapterBuilder = DelegationAdapterDsl.DelegationAdapterBuilder()
    delegationAdapterBuilder.delegates(
        NavItemAdapterDelegate(),
        NavSubHeaderItemAdapterDelegate(),
        DividerItemAdapterDelegate(),
        NavDropdownItemDelegate()
    )
    delegationAdapterBuilder.changePayload = { old, new ->
        when (old) {
            is NavDropdownItem -> {
                PAYLOAD.NAV_EXPANDED
            }

            else -> null
        }
    }
    return delegationAdapterBuilder.apply(builderDelegation).build()
}

object PAYLOAD {
    const val NAV_EXPANDED = "PAYLOAD_NAV_EXPANDED"
}

class NavItemAdapterDelegate : ListItemAdapterDelegate<NavTextItem, NavItemHolder>() {

    override fun isForViewType(item: Any): Boolean = item is NavTextItem

    override fun onBindViewHolder(item: NavTextItem, holder: NavItemHolder) = holder.onBind(item)

    override fun onCreateViewHolder(parent: ViewGroup): NavItemHolder {
        return NavItemHolder(parent.viewBinding(ItemNavBinding::inflate))
    }
}

class NavItemHolder(itemNavBinding: ItemNavBinding) :
    BaseViewHolder<NavTextItem, ItemNavBinding>(itemNavBinding) {

    override fun onBind(item: NavTextItem) {
        with(binding) {
            item.name
                .onString { tvName.text = it }
                .onResource { tvName.setText(it) }

            when (item.iconType) {
                NavTextItem.IconType.NONE -> {
                    ivIcon.roundPercent = 0F
                    ivIcon.updateLayoutParams {
                        height = 26.dpToPx
                        width = 26.dpToPx
                    }
                }

                NavTextItem.IconType.CIRCLE -> {
                    ivIcon.roundPercent = 100F
                    ivIcon.updateLayoutParams {
                        height = 32.dpToPx
                        width = 32.dpToPx
                    }
                }
            }

            item.icon.onResource {
                if (it != 0)
                    ivIcon.setImageResource(it)
                else
                    ivIcon.setImageDrawable(
                        AvatarGenerator.Builder(itemView.context).apply {
                            color(
                                item.color.fold({ colorId -> colorId },
                                    { colorName ->
                                        ContextCompat.getColor(
                                            context,
                                            context.resources.getIdentifier(
                                                colorName,
                                                "color",
                                                context.packageName
                                            )
                                        )
                                    }) as Int
                            )
                            item.name.onString { name -> name(name) }
                        }.generateBitmapDrawable()
                    )
            }



            root.isEnabled = item.enabled
            root.alpha = if (item.enabled) 1F else 0.5F
        }
    }
}

class NavDropdownItemHolder(itemNavDropdownBinding: ItemNavDropdownBinding) :
    BaseViewHolder<NavDropdownItem, ItemNavDropdownBinding>(itemNavDropdownBinding) {
    override fun onBind(item: NavDropdownItem) {
        bind(item)
    }

    private fun bind(item: NavDropdownItem) {
        with(binding) {
            item.name
                .onResource { tvName.setText(it) }
                .onString { tvName.text = it }
        }
    }

    override fun onBind(item: NavDropdownItem, payload: Any) {
        bind(item)
        if (payload == PAYLOAD.NAV_EXPANDED) {
            binding.ivIcon.animate()
                .rotation(if (item.expanded) 180f else 0f)
                .setDuration(300)
                .start()
        }
    }
}

class NavDropdownItemDelegate :
    ListItemAdapterDelegate<NavDropdownItem, NavDropdownItemHolder>() {

    override fun isForViewType(item: Any): Boolean = item is NavDropdownItem

    override fun onBindViewHolder(item: NavDropdownItem, holder: NavDropdownItemHolder) {
        holder.onBind(item)
    }

    override fun onBindViewHolder(
        item: NavDropdownItem,
        holder: NavDropdownItemHolder,
        payload: Any
    ) {
        holder.onBind(item, payload)
    }

    override fun onCreateViewHolder(parent: ViewGroup): NavDropdownItemHolder {
        return NavDropdownItemHolder(parent.viewBinding(ItemNavDropdownBinding::inflate))
    }
}



data class NavTextItem(
    val name: UiText,
    val icon: UiText = UiText.ResourceText(0),
    var checked: Boolean = false,
    val visible: Boolean = true,
    val checkable: Boolean = true,
    val enabled: Boolean = true,
    override var id: UUID = UUID.randomUUID(),
    val iconType: IconType = IconType.NONE,
    val color: UiText = UiText.ResourceText(0),
) : NavItem() {
    enum class IconType { NONE, CIRCLE }
}

data class NavDropdownItem(
    val name: UiText,
    val expanded: Boolean = false
) : NavItem()

class NavSubHeaderItemAdapterDelegate :
    ListItemAdapterDelegate<NavSubHeaderItem, NavSubHeaderItemHolder>() {

    override fun isForViewType(item: Any): Boolean = item is NavSubHeaderItem

    override fun onBindViewHolder(item: NavSubHeaderItem, holder: NavSubHeaderItemHolder) =
        holder.onBind(item)

    override fun onCreateViewHolder(parent: ViewGroup): NavSubHeaderItemHolder {
        return NavSubHeaderItemHolder(parent.viewBinding(ItemNavSubHeaderBinding::inflate))
    }
}

class NavSubHeaderItemHolder(itemNavSubHeaderBinding: ItemNavSubHeaderBinding) :
    BaseViewHolder<NavSubHeaderItem, ItemNavSubHeaderBinding>(itemNavSubHeaderBinding) {

    override fun onBind(item: NavSubHeaderItem) {
        with(binding) {
            item.name
                .onResource { tvHeader.setText(it) }
                .onString { tvHeader.text = it }
        }
    }
}

data class NavSubHeaderItem(
    val name: UiText
) : NavItem()

class DividerItemAdapterDelegate : ListItemAdapterDelegate<DividerItem, DividerItemHolder>() {

    override fun isForViewType(item: Any): Boolean = item is DividerItem

    override fun onBindViewHolder(item: DividerItem, holder: DividerItemHolder) =
        holder.onBind(item)

    override fun onCreateViewHolder(parent: ViewGroup): DividerItemHolder {
        return DividerItemHolder(parent.viewBinding(ItemNavDividerBinding::inflate))
    }
}

class DividerItemHolder(itemNavDividerBinding: ItemNavDividerBinding) :
    BaseViewHolder<DividerItem, ItemNavDividerBinding>(itemNavDividerBinding) {

    override fun onBind(item: DividerItem) {}
}

data class DividerItem(override var id: UUID = UUID.randomUUID()) : NavItem()

abstract class NavItem : UiModel