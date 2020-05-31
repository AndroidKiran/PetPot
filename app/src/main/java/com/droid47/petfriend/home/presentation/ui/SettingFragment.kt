package com.droid47.petfriend.home.presentation.ui

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.applyTheme
import com.droid47.petfriend.base.widgets.bindingadapters.setFullScreenBottomPadding
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_setting.*
import javax.inject.Inject

class SettingFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener {

    @Inject
    lateinit var application: Application

    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as HomeActivity).homeComponent.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        listenToThemePreference()
        listenToSearchLimitPreference()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean =
        when (preference?.key ?: false) {
            else -> false
        }

    private fun setUpViews() {
        with(bottom_app_bar) {
            setFullScreenBottomPadding(true)
//            setNavigationIcon(R.drawable.vc_nav_menu)
            setNavigationOnClickListener {
                homeViewModel.eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
            }
        }

        btn_nav_search.setOnClickListener {
            homeViewModel.eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
        }
    }

    private fun listenToThemePreference() {
        findPreference<Preference>(getString(R.string.key_current_theme))?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val themeOption = newValue as String
                applyTheme(themeOption)
                true
            }
    }

    private fun listenToSearchLimitPreference() {
        findPreference<Preference>(getString(R.string.key_search_limit))?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                true
            }
    }
}