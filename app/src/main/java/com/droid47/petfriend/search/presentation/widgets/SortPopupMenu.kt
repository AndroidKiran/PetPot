package com.droid47.petfriend.search.presentation.widgets

import android.content.Context
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.PopupMenu.OnDismissListener
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener
import androidx.core.view.get
import androidx.core.view.size
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.droid47.petfriend.R
import com.droid47.petfriend.base.widgets.BaseStateModel
import com.droid47.petfriend.base.widgets.Empty
import com.droid47.petfriend.base.widgets.Failure
import com.droid47.petfriend.base.widgets.Success
import com.droid47.petfriend.search.data.models.FilterItemEntity
import com.droid47.petfriend.search.data.models.SORT
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel
import com.droid47.petfriend.search.presentation.viewmodel.FilterViewModel.Companion.EVENT_APPLY_SORT_FILTER
import java.util.*

class SortPopupMenu @JvmOverloads constructor(
    context: Context,
    anchor: View,
    private val filterViewModel: FilterViewModel,
    private val lifeCycleOwner: LifecycleOwner
) : PopupMenu(context, anchor) {

    private val onDismissListener = OnDismissListener {
        dismissPopup()
    }

    private val onMenuClickListener = OnMenuItemClickListener {
        val menuItem = it ?: return@OnMenuItemClickListener false
        val title = menuItem.title?.toString()?.toLowerCase(Locale.US) ?: ""
        if (title.isEmpty()) return@OnMenuItemClickListener false
        filterViewModel.refreshSortFilter(FilterItemEntity(title, SORT, true))
        return@OnMenuItemClickListener false
    }

    init {
        menuInflater.inflate(R.menu.sort_menu, menu)
        setOnDismissListener(onDismissListener)
    }

    fun showPopup() {
        dismissPopup()
        filterViewModel.sortFilterLiveDataEntity.run {
            removeObserver(sortStateObserver)
            observe(lifeCycleOwner, sortStateObserver)
        }

        filterViewModel.eventLiveData.run {
            removeObserver(applySortStateObserver)
            observe(lifeCycleOwner, applySortStateObserver)
        }
        setOnMenuItemClickListener(onMenuClickListener)
        filterViewModel.fetchSortMenuState()
    }

    private fun dismissPopup() {
        filterViewModel.sortFilterLiveDataEntity.removeObserver(sortStateObserver)
        filterViewModel.eventLiveData.removeObserver(applySortStateObserver)
        setOnMenuItemClickListener(null)
        dismiss()
    }

    private val sortStateObserver = Observer<BaseStateModel<FilterItemEntity>> {
        when (it) {
            is Success -> {
                updateCheckableState(it.data)
                show()
            }
            is Failure,
            is Empty -> dismissPopup()
            else -> dismissPopup()
        }
    }

    private val applySortStateObserver = Observer<Int> {
        if (it == EVENT_APPLY_SORT_FILTER) {
            dismissPopup()
        }
    }

    private fun updateCheckableState(filterItemEntity: FilterItemEntity) {
        for (item in 0 until menu.size) {
            val menuItem = menu[item]
            if (menuItem.title.toString().toLowerCase(Locale.US) == filterItemEntity.name) {
                menuItem.isChecked = true
                break
            }
        }
    }
}