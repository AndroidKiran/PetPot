package com.droid47.petfriend.launcher.presentation.ui.models

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR


class WebViewModel : BaseObservable() {

    @Bindable
    var showProgress: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.showProgress)
        }

    @Bindable
    val url = "file:///android_asset/privacy_policy.html"

    inner class Client : WebViewClient() {
        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            showProgress = false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            showProgress = false
        }
    }

    @Bindable
    fun getWebViewClient(): WebViewClient = Client()

}