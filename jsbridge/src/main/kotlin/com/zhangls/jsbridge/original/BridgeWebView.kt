package com.zhangls.jsbridge.original

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient
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
    super.setWebViewClient(BridgeWebViewClient().also {
      it.setBridgeWebViewClientListener(listener)
    })
    bridge.loadUrlCallback = { loadUrl(it) }
  }


  fun setJSWebViewClient(client: BridgeWebViewClient) {
    super.setWebViewClient(client.also {
      it.setBridgeWebViewClientListener(listener)
    })
  }

  @Deprecated(
    message = "JSBridge 已经实现了该方法，请调用 setJSWebViewClient 方法，否则会出现异常",
    replaceWith = ReplaceWith("setJSWebViewClient(client)"),
    level = DeprecationLevel.ERROR
  )
  override fun setWebViewClient(client: WebViewClient) {
    super.setWebViewClient(client)
  }

  fun registerHandler(handlerName: String, handler: BridgeHandler) {
    bridge.registerHandler(handlerName, handler)
  }

  fun callHandler(handlerName: String, data: String? = null, callback: CallbackFunction = CallbackFunction { }) {
    bridge.doSend(handlerName, data, callback)
  }

  override fun destroy() {
    super.destroy()
    bridge.destroy()
  }

}