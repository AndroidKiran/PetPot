package com.droid47.petpot.launcher.presentation.ui.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.droid47.petpot.R

enum class OnBoardingPage(
    @StringRes val titleResource: Int,
    @StringRes val subTitleResource: Int,
    @StringRes val descriptionResource: Int,
    @DrawableRes val logoResource: Int
) {

    ONE(R.string.onboarding_page_app_tite, R.string.app_name, R.string.onboarding_page_app_desc, R.drawable.ic_a_day_at_the_park),
    TWO(R.string.onboarding_page_search_tite, R.string.search, R.string.onboarding_page_search_desc, R.drawable.ic_directions),
    THREE(R.string.onboarding_page_fav_tite, R.string.favourite, R.string.onboarding_page_fav_desc, R.drawable.ic_hang_out)

}