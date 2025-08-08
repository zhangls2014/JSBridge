package com.zhangls.jsbridge.demo

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import com.zhangls.jsbridge.original.BridgeWebView

/**
 * @author zhangls
 */
@SuppressLint("SetJavaScriptEnabled")
fun WebView.config(
  javaScriptEnabled: Boolean = true,
  domStorageEnabled: Boolean = true,
  allowFileAccess: Boolean = false
) {
  with(settings) {
    this.javaScriptEnabled = javaScriptEnabled
    this.domStorageEnabled = domStorageEnabled
    this.allowFileAccess = allowFileAccess

    databaseEnabled = true
    useWideViewPort = true
    loadWithOverviewMode = true

    setSupportZoom(false)
    builtInZoomControls = false
    displayZoomControls = false

    cacheMode = WebSettings.LOAD_DEFAULT
    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    defaultTextEncodingName = Charsets.UTF_8.name()
    javaScriptCanOpenWindowsAutomatically = true
    loadsImagesAutomatically = true
  }

  webChromeClient = BaseWebChromeClient()
  setWebChromeClient(BaseWebChromeClient())
}