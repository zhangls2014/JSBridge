package com.zhangls.jsbridge.tencent

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.tencent.smtt.sdk.WebView
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
        webViewClient = X5BridgeWebViewClient().also {
            it.setBridgeWebViewClientListener(listener)
        }
        bridge.loadUrlCallback = { loadUrl(it) }
    }

    fun setWebViewClient(client: X5BridgeWebViewClient) {
        webViewClient = client.also {
            it.setBridgeWebViewClientListener(listener)
        }
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