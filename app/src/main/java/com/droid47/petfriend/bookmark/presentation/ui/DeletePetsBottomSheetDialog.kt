package com.droid47.petfriend.bookmark.presentation.ui

import android.os.Bundle
import android.view.View
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.parentFragmentViewModelProvider
import com.droid47.petfriend.base.widgets.BaseBindingBottomSheetDialogFragment
import com.droid47.petfriend.bookmark.presentation.viewmodel.BookmarkViewModel
import com.droid47.petfriend.databinding.DialogFragmentDeletePetsBinding
import com.droid47.petfriend.home.presentation.HomeActivity
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel

class DeletePetsBottomSheetDialog : BaseBindingBottomSheetDialogFragment<DialogFragmentDeletePetsBinding, HomeViewModel, HomeViewModel>() {

    private val bookmarkViewModel: BookmarkViewModel by lazy {
        requireParentFragment().parentFragmentViewModelProvider<BookmarkViewModel>()
    }

    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    override fun getLayoutId(): Int = R.layout.dialog_fragment_delete_pets

    override fun getFragmentNavId(): Int = R.id.navigation_dialog_delete_favorite

    override fun executePendingVariablesBinding() {
        getViewDataBinding().also {
            it.lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun getViewModel(): HomeViewModel = homeViewModel

    override fun getParentViewModel(): HomeViewModel = homeViewModel

    override fun getSnackBarAnchorView(): View = getViewDataBinding().btnPrimaryAction

    override fun injectSubComponent() {
        (activity as HomeActivity).homeComponent.inject(this@DeletePetsBottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding().btnPrimaryAction.setOnClickListener {
//            getViewModel().deleteAllBookmark()
            dismiss()
        }

        getViewDataBinding().btnSecondaryAction.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "action_dialog_fragment"
    }

}