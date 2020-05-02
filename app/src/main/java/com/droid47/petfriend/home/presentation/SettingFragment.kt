package com.droid47.petfriend.home.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.activityViewModelProvider
import com.droid47.petfriend.base.extensions.applyTheme
import com.droid47.petfriend.home.presentation.viewmodels.HomeViewModel
import com.google.android.material.bottomappbar.BottomAppBar

class SettingFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener {

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
        with(view.findViewById<BottomAppBar>(R.id.bottom_app_bar)) {
            setNavigationIcon(R.drawable.vc_nav_menu)
            setNavigationOnClickListener {
                homeViewModel.eventLiveData.postValue(HomeViewModel.EVENT_TOGGLE_NAVIGATION)
            }
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean =
        when (preference?.key ?: false) {
            else -> false
        }

    private fun listenToThemePreference() {
        findPreference<Preference>(getString(R.string.key_current_theme))?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val themeOption = newValue as String
                themeOption.applyTheme()
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