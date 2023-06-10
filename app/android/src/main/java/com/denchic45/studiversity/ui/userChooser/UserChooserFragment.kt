package com.denchic45.studiversity.ui.userChooser

import androidx.fragment.app.viewModels
import com.denchic45.studiversity.ui.adapter.UserAdapterDelegate
import com.denchic45.studiversity.ui.base.chooser.ChooserFragment
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.studiversity.widget.extendedAdapter.AdapterDelegate


class UserChooserFragment : ChooserFragment<UserChooserViewModel, UserResponse>() {
    override val viewModel: UserChooserViewModel by viewModels { viewModelFactory }
    override fun adapterDelegates(): List<AdapterDelegate> = listOf(UserAdapterDelegate())
}