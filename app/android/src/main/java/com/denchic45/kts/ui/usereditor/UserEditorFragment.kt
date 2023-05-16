package com.denchic45.kts.ui.usereditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.KeyboardManager
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentUserEditorBinding
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.util.toUUID
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UserEditorFragment(
    appBarInteractor: AppBarInteractor,
    component: (
        onFinish: () -> Unit,
        userId: UUID?,
        ComponentContext
    ) -> UserEditorComponent,
) : Fragment(R.layout.fragment_user_editor), HasNavArgs<UserEditorFragmentArgs> {

    override val navArgs: UserEditorFragmentArgs by navArgs()

        private val component: UserEditorComponent by lazy {
        component(
            findNavController()::navigateUp,
            navArgs.userId?.toUUID(),
            defaultComponentContext(requireActivity().onBackPressedDispatcher)
        )
    }
    val binding: FragmentUserEditorBinding by viewBinding(FragmentUserEditorBinding::bind)
    private lateinit var keyboardManager: KeyboardManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                UserEditorContent(component = component)
            }
        }
    }

//    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        keyboardManager = KeyboardManager()
//        val groupAdapter: ListPopupWindowAdapter
//        with(binding) {
//            groupAdapter = ListPopupWindowAdapter(requireContext(), emptyList())
//
//            etFirstName.textChanges()
//                .skip(1)
//                .compose(AsyncTransformer())
//                .map(CharSequence::toString)
//                .subscribe(component::onFirstNameType)
//
//            etSurname.textChanges()
//                .skip(1)
//                .compose(AsyncTransformer())
//                .map(CharSequence::toString)
//                .subscribe(component::onSurnameType)
//
//            etPatronymic.textChanges()
//                .skip(1)
//                .compose(AsyncTransformer())
//                .map(CharSequence::toString)
//                .subscribe(component::onPatronymicType)
//
//            etEmail.textChanges()
//                .skip(1)
//                .compose(AsyncTransformer())
//                .map { obj: CharSequence -> obj.toString() }
//                .subscribe(component::onEmailType)
//
//
//            component.firstNameField.collectWhenStarted(viewLifecycleOwner) { firstName ->
//                if (firstName != etFirstName.text.toString()) etFirstName.setText(firstName)
//            }
//
//            component.surnameField.collectWhenStarted(viewLifecycleOwner) { surname ->
//                if (surname != etSurname.text.toString()) etSurname.setText(surname)
//            }
//
//            component.patronymicField.collectWhenStarted(viewLifecycleOwner) { patronymic ->
//                if (patronymic != etPatronymic.text
//                        .toString()
//                ) etPatronymic.setText(patronymic)
//            }
//
//            component.emailField.collectWhenStarted(viewLifecycleOwner) { email ->
//                if (email != etEmail.text.toString()) etEmail.setText(email)
//            }
//
//            component.genderField.collectWhenStarted(viewLifecycleOwner) { gender: GenderAction ->
//                etGender.setText(gender.title)
//            }
//
//            component.accountFieldsVisibility.collectWhenStarted(viewLifecycleOwner) { visibility ->
//                llAccount.visibility = if (visibility) View.VISIBLE else View.GONE
//            }
//
//            component.avatarUrl.collectWhenStarted(viewLifecycleOwner) { photoUrl ->
//                Glide.with(requireContext())
//                    .load(photoUrl)
//                    .placeholder(ivAvatar.drawable)
//                    .transition(DrawableTransitionOptions.withCrossFade(100))
//                    .into(ivAvatar)
//            }
//
//            component.errorState.collectWhenStarted(viewLifecycleOwner) {
//                tilFirstName.error = it.firstNameMessage
//                tilSurname.error = it.surnameMessage
//                tilEmail.error = it.emailMessage
//            }
//
//
//            component.genders.let { genders ->
//                val adapter = ListPopupWindowAdapter(
//                    requireContext(),
//                    genders.map { ListItem(title = it.title) })
//                etGender.setAdapter(adapter)
//                etGender.onItemClickListener =
//                    AdapterView.OnItemClickListener { _, _, position, _ ->
//                        component.onGenderSelect(genders[position].action)
//                    }
//            }
//        }
//    }


//    @Deprecated("Deprecated in Java")
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            component.onBackPress()
//        } else {
//            component.onOptionClick(item.itemId)
//        }
//        return true
//    }

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