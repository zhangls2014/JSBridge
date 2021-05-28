package com.zhangls.jsbridge.original

import android.graphics.Bitmap
import android.webkit.*
import androidx.annotation.CallSuper
import com.zhangls.jsbridge.Bridge
import com.zhangls.jsbridge.listener.BridgeWebViewClientListener
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

/**
 * @author zhangls
 */
open class BridgeWebViewClient : WebViewClient() {
    /**
     * 是否重定向，避免 web 未渲染即跳转导致系统未调用 onPageStarted 就调用 onPageFinished 方法引起的 JSBridge 初始化失败
     */
    private var isRedirected = false

    /**
     * onPageStarted 连续调用次数,避免渲染立马跳转可能连续调用 onPageStarted 多次
     * 并且调用 shouldOverrideUrlLoading 后不调用 onPageStarted 引起的 JSBridge 未初始化问题
     */
    private var onPageStartedCount = 0

    private var listener: BridgeWebViewClientListener? = null


    internal fun setBridgeWebViewClientListener(listener: BridgeWebViewClientListener) {
        this.listener = listener
    }


    @CallSuper
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return urlLoading(url)
    }

    @CallSuper
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return urlLoading(request.url.toString())
    }

    private fun urlLoading(url: String): Boolean {
        if (onPageStartedCount < 2) isRedirected = true

        onPageStartedCount = 0

        val newUrl = try {
            URLDecoder.decode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }

        return when {
            newUrl.startsWith(Bridge.YY_RETURN_DATA) -> {
                listener?.handleReturnData(newUrl)
                true
            }
            newUrl.startsWith(Bridge.YY_OVERRIDE_SCHEMA) -> {
                listener?.flushMessageQueue()
                true
            }
            else -> {
                false
            }
        }
    }

    @CallSuper
    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        isRedirected = false
        onPageStartedCount++
    }

    @CallSuper
    override fun onPageFinished(view: WebView, url: String) {
        if (url.contains("about:blank").not() && isRedirected.not()) {
            val jsUrl = Bridge.webViewLoadLocalJs(view.context.assets)
            view.loadUrl(jsUrl)
        }
        listener?.dispatchMessages()
    }
}