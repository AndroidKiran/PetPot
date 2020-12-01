package com.droid47.petpot.launcher.presentation.ui.widgets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.droid47.petpot.base.widgets.BaseViewHolder
import com.droid47.petpot.databinding.OnboardingPageItemBinding
import com.droid47.petpot.launcher.presentation.ui.models.OnBoardingPage

class OnBoardingPagerAdapter constructor(private val onBoardingPageList: Array<OnBoardingPage> = OnBoardingPage.values()) :
    RecyclerView.Adapter<BaseViewHolder<OnBoardingPage>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<OnBoardingPage> =
        OnBoardingViewHolder(
            OnboardingPageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onViewRecycled(holder: BaseViewHolder<OnBoardingPage>) {
        holder.onUnbind()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = onBoardingPageList.size

    override fun onBindViewHolder(holder: BaseViewHolder<OnBoardingPage>, position: Int) {
        holder.onBind(onBoardingPageList[position])
    }

    private inner class OnBoardingViewHolder(private val itemBinding: OnboardingPageItemBinding) :
        BaseViewHolder<OnBoardingPage>(itemBinding.root) {

        override fun onBind(item: OnBoardingPage) {
            itemBinding.run {
                onBoardingPage = item
                executePendingBindings()
            }
        }

        override fun onUnbind() {
            itemBinding.run {
                onBoardingPage = null
                executePendingBindings()
            }
        }
    }
}