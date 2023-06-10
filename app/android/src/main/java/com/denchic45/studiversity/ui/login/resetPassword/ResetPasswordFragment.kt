package com.denchic45.studiversity.ui.login.resetPassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.studiversity.R
import com.denchic45.studiversity.databinding.FragmentResetPasswordBinding
import com.denchic45.studiversity.ui.base.BaseFragment

class ResetPasswordFragment :
    BaseFragment<ResetPasswordViewModel, FragmentResetPasswordBinding>(R.layout.fragment_reset_password) {

    override val viewModel: ResetPasswordViewModel by viewModels { viewModelFactory }
    override val binding: FragmentResetPasswordBinding by viewBinding(FragmentResetPasswordBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnSend.setOnClickListener { v: View? ->
                viewModel.onSendClick(
                    etEmail.text.toString()
                )
            }
            viewModel.showErrorFieldEmail.observe(viewLifecycleOwner) { error: Boolean ->
                if (error) {
                    tilEmail.error = "Некоректная почта"
                } else {
                    tilEmail.error = null
                }
            }
        }
    }
}