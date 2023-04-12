package com.denchic45.kts.ui.userEditor

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RestrictTo
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.denchic45.kts.KeyboardManager
import com.denchic45.kts.R
import com.denchic45.kts.customPopup.ListPopupWindowAdapter
import com.denchic45.kts.data.model.domain.ListItem
import com.denchic45.kts.databinding.FragmentUserEditorBinding
import com.denchic45.kts.rx.AsyncTransformer
import com.denchic45.kts.ui.BaseFragment2
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.usereditor.GenderAction
import com.denchic45.kts.util.collectWhenStarted
import com.jakewharton.rxbinding4.widget.textChanges
import me.tatarka.inject.annotations.Inject

@Inject
class UserEditorFragment(component: (String?) -> UserEditorViewModel) :
    BaseFragment2<UserEditorViewModel, FragmentUserEditorBinding>(
        R.layout.fragment_user_editor,
        R.menu.options_user_editor
    ), HasNavArgs<UserEditorFragmentArgs> {

    override val navArgs: UserEditorFragmentArgs by navArgs()
    override val component: UserEditorViewModel by lazy { component(navArgs.userId) }
    override val binding: FragmentUserEditorBinding by viewBinding(FragmentUserEditorBinding::bind)
    private lateinit var keyboardManager: KeyboardManager

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardManager = KeyboardManager()
        val groupAdapter: ListPopupWindowAdapter
        with(binding) {
            groupAdapter = ListPopupWindowAdapter(requireContext(), emptyList())

            etFirstName.textChanges()
                .skip(1)
                .compose(AsyncTransformer())
                .map(CharSequence::toString)
                .subscribe(component::onFirstNameType)

            etSurname.textChanges()
                .skip(1)
                .compose(AsyncTransformer())
                .map(CharSequence::toString)
                .subscribe(component::onSurnameType)

            etPatronymic.textChanges()
                .skip(1)
                .compose(AsyncTransformer())
                .map(CharSequence::toString)
                .subscribe(component::onPatronymicType)

            etEmail.textChanges()
                .skip(1)
                .compose(AsyncTransformer())
                .map { obj: CharSequence -> obj.toString() }
                .subscribe(component::onEmailType)


            component.firstNameField.collectWhenStarted(viewLifecycleOwner) { firstName ->
                if (firstName != etFirstName.text.toString()) etFirstName.setText(firstName)
            }

            component.surnameField.collectWhenStarted(viewLifecycleOwner) { surname ->
                if (surname != etSurname.text.toString()) etSurname.setText(surname)
            }

            component.patronymicField.collectWhenStarted(viewLifecycleOwner) { patronymic ->
                if (patronymic != etPatronymic.text
                        .toString()
                ) etPatronymic.setText(patronymic)
            }

            component.emailField.collectWhenStarted(viewLifecycleOwner) { email ->
                if (email != etEmail.text.toString()) etEmail.setText(email)
            }

            component.genderField.collectWhenStarted(viewLifecycleOwner) { gender: GenderAction ->
                etGender.setText(gender.title)
            }

            component.accountFieldsVisibility.collectWhenStarted(viewLifecycleOwner) { visibility ->
                llAccount.visibility = if (visibility) View.VISIBLE else View.GONE
            }

            component.avatarUrl.collectWhenStarted(viewLifecycleOwner) { photoUrl ->
                Glide.with(requireContext())
                    .load(photoUrl)
                    .placeholder(ivAvatar.drawable)
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .into(ivAvatar)
            }

            component.errorState.collectWhenStarted(viewLifecycleOwner) {
                tilFirstName.error = it.firstNameMessage
                tilSurname.error = it.surnameMessage
                tilEmail.error = it.emailMessage
            }


            component.genders.let { genders ->
                val adapter = ListPopupWindowAdapter(
                    requireContext(),
                    genders.map { ListItem(title = it.title) })
                etGender.setAdapter(adapter)
                etGender.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        component.onGenderSelect(genders[position].action)
                    }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            component.onBackPress()
        } else {
            component.onOptionClick(item.itemId)
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        keyboardManager.unregisterKeyboardListener()
    }

    companion object {
        //        const val USER_ROLE = "USER_ROLE"
        const val USER_ID = "USER_ID"
//        const val USER_GROUP_ID = "USER_GROUP_ID"
    }
}