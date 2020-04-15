package com.droid47.petfriend.base.widgets.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droid47.petfriend.databinding.DialogFragmentActionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionBottomSheetDialog constructor(
    private val title: String,
    private val primaryAction: String,
    private val secondaryAction: String,
    private val onActionClicked: (Boolean) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogFragmentActionBinding

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
//        (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
//            setOnShowListener {
//                window?.setDimAmount(3f)
//            }
//        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = DialogFragmentActionBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.also {
            it.title = title
            it.primaryAction = primaryAction
            it.secondaryAction = secondaryAction
        }.executePendingBindings()

        binding.btnPrimaryAction.setOnClickListener {
            onActionClicked(true)
            dismiss()
        }

        binding.btnSecondaryAction.setOnClickListener {
            onActionClicked(false)
            dismiss()
        }
    }

    companion object {
        const val TAG = "action_dialog_fragment"
        fun newInstanceShow(
            title: String,
            primaryAction: String,
            secondaryAction: String,
            onActionClicked: (Boolean) -> Unit
        ) = ActionBottomSheetDialog(
            title,
            primaryAction,
            secondaryAction,
            onActionClicked
        )
    }
}