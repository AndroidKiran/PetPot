package com.droid47.petgoogle.home.presentation.di

import com.droid47.petgoogle.aboutUs.presentation.AboutUsFragment
import com.droid47.petgoogle.aboutUs.presentation.di.AbstractAboutUsModule
import com.droid47.petgoogle.app.di.scopes.ActivityScope
import com.droid47.petgoogle.bookmark.presentation.BookmarkFragment
import com.droid47.petgoogle.bookmark.presentation.di.AbstractBookmarkModule
import com.droid47.petgoogle.home.presentation.BottomNavDrawerFragment
import com.droid47.petgoogle.home.presentation.HomeActivity
import com.droid47.petgoogle.home.presentation.SettingFragment
import com.droid47.petgoogle.petDetails.presentation.PetDetailsFragment
import com.droid47.petgoogle.petDetails.presentation.SimilarPetsFragment
import com.droid47.petgoogle.petDetails.presentation.di.AbstractPetDetailsModule
import com.droid47.petgoogle.search.presentation.FilterFragment
import com.droid47.petgoogle.search.presentation.SearchFragment
import com.droid47.petgoogle.search.presentation.di.AbstractSearchModule
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = [AbstractHomeModule::class, AbstractBookmarkModule::class,
        AbstractPetDetailsModule::class, AbstractSearchModule::class, AbstractAboutUsModule::class]
)
interface HomeSubComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeSubComponent
    }

    fun inject(homeActivity: HomeActivity)
    fun inject(bottomNavDrawerFragment: BottomNavDrawerFragment)
    fun inject(searchFragment: SearchFragment)
    fun inject(filterFragment: FilterFragment)
    fun inject(bookmarkFragment: BookmarkFragment)
    fun inject(aboutUsFragment: AboutUsFragment)
    fun inject(settingFragment: SettingFragment)
    fun inject(petDetailsFragment: PetDetailsFragment)
    fun inject(similarPetsFragment: SimilarPetsFragment)
}