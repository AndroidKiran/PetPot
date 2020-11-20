package com.droid47.petpot.home.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.activityViewModelProvider
import com.droid47.petpot.base.extensions.applyTheme
import com.droid47.petpot.base.extensions.viewModelProvider
import com.droid47.petpot.base.firebase.IFirebaseManager
import com.droid47.petpot.base.widgets.BaseBindingActivity
import com.droid47.petpot.base.widgets.bindingadapters.setFullScreenBottomPadding
import com.droid47.petpot.home.presentation.viewmodels.HomeViewModel
import com.droid47.petpot.search.presentation.viewmodel.SettingsViewModel
import com.droid47.petpot.workmanagers.notification.NotificationModel
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.android.synthetic.main.fragment_setting.*
import javax.inject.Inject

class SettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val settingsViewModel: SettingsViewModel by lazy {
        viewModelProvider<SettingsViewModel>(factory)
    }

    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().activityViewModelProvider<HomeViewModel>()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as HomeActivity).homeComponent.inject(this)
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        initTransition()
//    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        listenToThemePreference()
        listenToSearchLimitPreference()
        listenToNotificationStatePreference()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    override fun onResume() {
        super.onResume()
        trackFragment(settingsViewModel.fireBaseManager)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean =
        when (preference?.key) {
            else -> false
        }

    private fun initTransition() {
        enterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_small).toLong()
        }

        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.pet_motion_duration_medium).toLong()
        }
    }

    private fun trackFragment(firebaseManager: IFirebaseManager) {
        val baseActivity = requireActivity() as BaseBindingActivity<*, *>
        firebaseManager.sendScreenView(
            "SettingsScreen",
            SettingFragment::class.java.simpleName, baseActivity
        )
    }

    private fun setUpViews() {
        with(bottom_app_bar) {
            setFullScreenBottomPadding(true)
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
                settingsViewModel.trackThemeChange(themeOption)
                applyTheme(themeOption) {
                    toLauncher()
                }
                return@OnPreferenceChangeListener true
            }
    }

    private fun listenToNotificationStatePreference() {
        findPreference<Preference>(getString(R.string.key_notification_enabled))?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val enabled = newValue as Boolean
                settingsViewModel.trackNotificationState(enabled)
                return@OnPreferenceChangeListener true
            }
    }

    private fun listenToSearchLimitPreference() {
        findPreference<Preference>(getString(R.string.key_search_limit))?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                return@OnPreferenceChangeListener true
            }
    }

    private fun toLauncher() {
        val deepLinkBundle = Bundle().apply {
            putInt(NotificationModel.EXTRA_NAVIGATION_FRAGMENT_ID, R.id.navigation_settings)
        }
        val extras = FragmentNavigatorExtras(
            cdl_settings to getString(R.string.activity_transition)
        )
        homeViewModel.homeNavigator.toLauncherFromHome(deepLinkBundle, extras)
        requireActivity().finishAfterTransition()
    }
}