package com.droid47.petfriend.base.widgets.bindingadapters

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.droid47.petfriend.R
import com.droid47.petfriend.base.extensions.*
import com.droid47.petfriend.base.firebase.CrashlyticsExt
import com.droid47.petfriend.base.widgets.anim.BlurTransformation
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.google.android.material.circularreveal.CircularRevealCompat
import com.google.android.material.circularreveal.CircularRevealWidget
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.sqrt


@BindingAdapter("popupElevationOverlay")
fun Spinner.bindPopupElevationOverlay(popupElevationOverlay: Float) {
    setPopupBackgroundDrawable(
        ColorDrawable(
            ElevationOverlayProvider(context)
                .compositeOverlayWithThemeSurfaceColorIfNeeded(popupElevationOverlay)
        )
    )
}

@BindingAdapter("elevationOverlay")
fun View.bindElevationOverlay(previousElevation: Float, elevation: Float) {
    if (previousElevation == elevation) return
    val color = ElevationOverlayProvider(context)
        .compositeOverlayWithThemeSurfaceColorIfNeeded(elevation)
    setBackgroundColor(color)
}


@BindingAdapter(value = ["txtStartDrawable"])
fun CheckedTextView.bindStartDrawableResource(_imageResource: Int?) {
    val imageResource = _imageResource ?: return
    setCompoundDrawablesWithIntrinsicBounds(imageResource, 0, 0, 0)
}


@BindingAdapter(value = ["visible"])
fun View.bindViewVisibility(visible: Boolean?) {
    if (visible == true) {
        visible()
    } else {
        gone()
    }
}

@BindingAdapter(value = ["invisible"])
fun View.bindVisibility(visible: Boolean?) {
    if (visible == true) {
        visible()
    } else {
        invisible()
    }
}

@BindingAdapter("circularReveal")
fun View.bindCircularReveal(state: Boolean) {
    if (this !is CircularRevealWidget || !state) return
    try {
        val color = ColorStateList.valueOf(
            context.themeColor(R.attr.colorOnPrimary)
        ).defaultColor

        val viewWidth = width
        val viewHeight = height

        val viewDiagonal =
            sqrt((viewWidth * viewWidth + viewHeight * viewHeight).toDouble())
                .toInt()

        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofInt(
            this,
            CircularRevealWidget.CircularRevealScrimColorProperty.CIRCULAR_REVEAL_SCRIM_COLOR,
            color,
            Color.TRANSPARENT
        )
        animator.setEvaluator(ArgbEvaluatorCompat.getInstance())
        animatorSet.duration = 600
        val startRadius = 10f
        val endRadius = viewDiagonal / 2f
        val centerX = viewWidth / 2f
        val centerY = viewHeight / 2f

        post {
            animatorSet.playTogether(
                CircularRevealCompat.createCircularReveal(
                    this,
                    centerX,
                    centerY,
                    startRadius,
                    endRadius
                ),
                animator
            )
            animatorSet.start()
        }
    } catch (exception: Exception) {
        CrashlyticsExt.logHandledException(exception)
    }
}

@BindingAdapter("circularRevealShow")
fun View.bindRevealVisibility(state: Boolean?) {
    if (this !is CircularRevealWidget) return
    try {
        val color = ColorStateList.valueOf(
            context.themeColor(R.attr.colorOnPrimary)
        ).defaultColor

        val viewWidth = width
        val viewHeight = height

        val viewDiagonal =
            sqrt((viewWidth * viewWidth + viewHeight * viewHeight).toDouble())
                .toInt()

        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofInt(
            this,
            CircularRevealWidget.CircularRevealScrimColorProperty.CIRCULAR_REVEAL_SCRIM_COLOR,
            color,
            Color.TRANSPARENT
        )
        animator.setEvaluator(ArgbEvaluatorCompat.getInstance())
        animatorSet.duration = 600
        val startRadius = 10f
        val endRadius = viewDiagonal / 2f
        val centerX = viewWidth / 2f
        val centerY = viewHeight / 2f

        if (state == true) {
            visible()
            post {
                animatorSet.playTogether(
                    CircularRevealCompat.createCircularReveal(
                        this,
                        centerX,
                        centerY,
                        startRadius,
                        endRadius
                    ),
                    animator
                )
                animatorSet.start()

            }
        } else {
            gone()
        }
    } catch (exception: Exception) {
        CrashlyticsExt.logHandledException(exception)
    }
}

@BindingAdapter(
    "srcUrl",
    "circleCrop",
    "placeholder",
    "loadListener",
    "transform",
    requireAll = false
)
fun ImageView.bindSrcUrl(
    url: String?,
    circleCrop: Boolean,
    placeholder: Drawable?,
    loadListener: GlideDrawableLoadListener?,
    transform: Boolean = false
) {

    val request = Glide.with(this).load(url ?: "")
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    if (circleCrop) request.circleCrop()
    if (placeholder != null) request.placeholder(placeholder)
    if (loadListener != null) request.listener(loadListener)
    if (transform) request.transform(BlurTransformation(context))
    request.into(this)
}

@BindingAdapter(value = ["fabIcon"])
fun FloatingActionButton.bindImageResource(status: Boolean) {
    this.setImageResource(
        if (status) {
            R.drawable.vc_favorite_fill
        } else {
            R.drawable.vc_favorite
        }
    )
}

@BindingAdapter("shimmer")
fun ShimmerFrameLayout.bindAnim(state: Boolean) {
    if (state) {
        initHmeShimmer()
        showAndStartShimmer()
    } else {
        stopAndHideShimmer()
    }
}

@BindingAdapter("lottieAnimState")
fun LottieAnimationView.bindAnim(state: Boolean) {
    if (state) {
        playLottie()
    } else {
        pauseLottie()
    }
}

@BindingAdapter("setWebViewClient")
fun WebView.setWebClient(client: WebViewClient?) {
    webViewClient = client
}

@BindingAdapter("loadUrl")
fun WebView.loadWebUrl(url: String?) {
    setBackgroundColor(Color.parseColor("#eef0f2"))
    setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
    loadUrl(url)
}

@BindingAdapter("fromHtml")
fun TextView.fromHtml(input: String?) {
    text = input?.fromHtml() ?: ""
}

@BindingAdapter("app:srcCompat")
fun ImageView.setImageUri(imageUri: String?) {
    if (imageUri == null) {
        setImageURI(null)
    } else {
        setImageURI(Uri.parse(imageUri))
    }
}

@BindingAdapter("app:srcCompat")
fun ImageView.setImageUri(imageUri: Uri) {
    setImageURI(imageUri)
}

@BindingAdapter("app:srcCompat")
fun ImageView.setImageDrawable(drawable: Drawable) {
    setImageDrawable(drawable)
}

@BindingAdapter("app:srcCompat")
fun ImageView.setImageResource(resource: Int) {
    setImageResource(resource)
}