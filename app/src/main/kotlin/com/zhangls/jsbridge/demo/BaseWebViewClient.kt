package com.zhangls.jsbridge.demo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.annotation.CallSuper
import com.blankj.utilcode.util.LogUtils
import com.zhangls.jsbridge.original.BridgeWebViewClient


/**
 * @author zhangls
 */
open class BaseWebViewClient() : BridgeWebViewClient() {
  private var pageStartedTime = 0L


  @CallSuper
  override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
    super.onPageStarted(view, url, favicon)
    LogUtils.i("onPageStarted: $url")
    pageStartedTime = System.nanoTime()
  }

  @CallSuper
  override fun onPageFinished(view: WebView, url: String) {
    super.onPageFinished(view, url)

    val duration = (System.nanoTime() - pageStartedTime) / 1_000_000
    LogUtils.i("onPageFinished: duration = ${duration}ms, url = $url")
  }

  @CallSuper
  override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
    super.onReceivedError(view, request, error)
    LogUtils.e("onReceivedError: ${error.description}(${error.errorCode})")
  }

  @CallSuper
  @SuppressLint("WebViewClientOnReceivedSslError")
  override fun onReceivedSslError(webView: WebView, sslErrorHandler: SslErrorHandler, sslError: SslError) {
    LogUtils.e("onReceivedSslError: $sslError")
  }
}
