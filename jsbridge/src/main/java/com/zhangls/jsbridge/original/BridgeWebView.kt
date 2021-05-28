package com.zhangls.jsbridge.original

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.zhangls.jsbridge.Bridge
import com.zhangls.jsbridge.handler.BridgeHandler
import com.zhangls.jsbridge.handler.CallbackFunction
import com.zhangls.jsbridge.listener.BridgeWebViewClientListener

/**
 * @author zhangls
 */
class BridgeWebView : WebView {
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


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        @SuppressLint("SetJavaScriptEnabled")
        settings.javaScriptEnabled = true
        webViewClient = BridgeWebViewClient().also {
            it.setBridgeWebViewClientListener(listener)
        }
        bridge.loadUrlCallback = { loadUrl(it) }
    }


    fun setWebViewClient(client: BridgeWebViewClient) {
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