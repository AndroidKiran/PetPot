package com.droid47.petfriend.bookmark.presentation.di

import androidx.lifecycle.ViewModel
import com.droid47.petfriend.app.di.scopes.ViewModelKey
import com.droid47.petfriend.bookmark.presentation.viewmodel.BookmarkViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AbstractBookmarkModule {

    @Binds
    @IntoMap
    @ViewModelKey(BookmarkViewModel::class)
    fun bindBookmarkViewModel(bookmarkViewModel: BookmarkViewModel): ViewModel
}