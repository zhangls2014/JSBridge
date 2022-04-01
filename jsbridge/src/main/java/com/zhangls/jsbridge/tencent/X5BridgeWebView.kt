package com.zhangls.jsbridge.tencent

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.zhangls.jsbridge.Bridge
import com.zhangls.jsbridge.handler.BridgeHandler
import com.zhangls.jsbridge.handler.CallbackFunction
import com.zhangls.jsbridge.listener.BridgeWebViewClientListener

/**
 * @author zhangls
 */
class X5BridgeWebView : WebView {
    private val bridge = Bridge()
    private val listener = object : BridgeWebViewClientListener {
        override fun handleReturnData(url: String) {
            bridge.handleReturnData(url)
        }

        override fun flushMessageQueue() {
            bridge.flushMessageQueue()
        }

        override fun dispatchMessages() {
            bridge.dispatchMessages()
        }
    }


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        @Suppress("DEPRECATION")
        @SuppressLint("SetJavaScriptEnabled")
        settings.javaScriptEnabled = true
        super.setWebViewClient(X5BridgeWebViewClient().also {
            it.setBridgeWebViewClientListener(listener)
        })
        bridge.loadUrlCallback = { loadUrl(it) }
    }

    fun setJSWebViewClient(client: X5BridgeWebViewClient) {
        super.setWebViewClient(client.also {
            it.setBridgeWebViewClientListener(listener)
        })
    }

    @Deprecated(
        message = "JSBridge 已经实现了该方法，请调用 setJSWebViewClient 方法，否则会出现异常",
        replaceWith = ReplaceWith("setJSWebViewClient(client)"),
        level = DeprecationLevel.ERROR
    )
    override fun setWebViewClient(client: WebViewClient?) {
        super.setWebViewClient(client)
    }

    fun registerHandler(handlerName: String, handler: BridgeHandler) {
        bridge.registerHandler(handlerName, handler)
    }

    fun callHandler(handlerName: String, data: String?, callback: CallbackFunction?) {
        bridge.doSend(handlerName, data, callback)
    }

    override fun destroy() {
        super.destroy()
        bridge.destroy()
    }

}