package com.droid47.petpot.launcher.presentation.ui.models

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class WebViewModel : BaseObservable() {

    private val client = Client()

    sealed class ViewStateModel {
        object Loading : ViewStateModel()
        object Success : ViewStateModel()
        object Failure : ViewStateModel()
    }

    @Bindable
    var viewStateModel: ViewStateModel = ViewStateModel.Loading
        set(value) {
            field = value
            notifyPropertyChanged(BR.viewStateModel)
        }

    @Bindable
    val url = "file:///android_asset/privacy_policy.html"

    inner class Client : WebViewClient() {
        private var webView: WebView? = null

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            viewStateModel = ViewStateModel.Loading
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            this.webView = webView
            viewStateModel = ViewStateModel.Failure
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            this.webView = webView
            viewStateModel = ViewStateModel.Success
        }

        fun retry() {
            webView?.loadUrl(url)
        }
    }

    @Bindable
    fun getWebViewClient(): Client = client

}