package com.droid47.petfriend.base.extensions

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory
): VM = ViewModelProvider(this, provider).get(VM::class.java)

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory
): VM = ViewModelProvider(this, provider).get(VM::class.java)

inline fun <reified VM : ViewModel> activityViewModelProvider(activity: FragmentActivity) =
    ViewModelProvider(activity).get(VM::class.java)

inline fun <reified VM : ViewModel> parentFragmentViewModelProvider(parentFragment: Fragment) =
    ViewModelProvider(parentFragment).get(VM::class.java)

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?) -> Unit) =
    observe(owner, Observer<T> { v -> observer.invoke(v) })

fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>) = Transformations.switchMap(this, func)

fun <X, Y> LiveData<X>.map(func: (X) -> LiveData<Y>) = Transformations.map(this, func)

fun View.findNavController(): NavController = Navigation.findNavController(this)

fun <T : CoordinatorLayout.Behavior<*>> View.findBehavior(): T = layoutParams.run {
    if (this !is CoordinatorLayout.LayoutParams) throw IllegalArgumentException("View's layout params should be CoordinatorLayout.LayoutParams")

    (layoutParams as CoordinatorLayout.LayoutParams).behavior as? T
        ?: throw IllegalArgumentException("Layout's behavior is not current behavior")
}

inline fun <T> dependantLiveData(
    vararg dependencies: LiveData<out Any>,
    defaultValue: T? = null,
    crossinline mapper: () -> T?
): LiveData<T> =
    MediatorLiveData<T>().also { mediatorLiveData ->
        val observer = Observer<Any> { mediatorLiveData.value = mapper() }
        dependencies.forEach { dependencyLiveData ->
            mediatorLiveData.addSource(dependencyLiveData, observer)
        }
    }.apply { value = defaultValue }

val <T> T.exhaustive get() = this