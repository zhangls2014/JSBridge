package com.zhangls.jsbridge

import android.content.res.AssetManager
import android.os.Looper
import android.os.SystemClock
import com.zhangls.jsbridge.handler.BridgeHandler
import com.zhangls.jsbridge.handler.CallbackFunction


/**
 * @author zhangls
 */
internal class Bridge {
  private val responseCallbacks: MutableMap<String, CallbackFunction> = HashMap<String, CallbackFunction>()
  private val messageHandlers: MutableMap<String, BridgeHandler> = HashMap<String, BridgeHandler>()
  private var startupMessage: MutableList<Message>? = ArrayList()
  private var uniqueId: Long = 0

  // loadUrl 回调
  var loadUrlCallback: ((String) -> Unit)? = null

  companion object {
    const val YY_OVERRIDE_SCHEMA = "yy://"
    const val YY_RETURN_DATA = YY_OVERRIDE_SCHEMA + "return/"

    private const val CALLBACK_ID_FORMAT = "JAVA_CB_%s"
    private const val JS_HANDLE_MESSAGE_FROM_JAVA = "javascript:WebViewJavascriptBridge._handleMessageFromNative('%s');"
    private const val JS_FETCH_QUEUE_FROM_JAVA = "javascript:WebViewJavascriptBridge._fetchQueue();"

    private const val jsFileName = "WebViewJavascriptBridge.js"
    private const val YY_FETCH_QUEUE = YY_RETURN_DATA + "_fetchQueue/"
    private const val SPLIT_MARK = "/"

    fun webViewLoadLocalJs(assets: AssetManager): String {
      return readJS(assets).let { "javascript:$it" }
    }

    private fun readJS(assets: AssetManager): String {
      return assets.open(jsFileName)
        .bufferedReader()
        .use {
          val sb = StringBuilder()
          var line: String?
          do {
            line = it.readLine()
            if (line != null && !line.matches("^\\s*//.*".toRegex())) {
              sb.append(line)
            }
          } while (line != null)

          sb.toString()
        }
    }
  }


  private fun getDataFromReturnUrl(url: String): String? {
    if (url.startsWith(YY_FETCH_QUEUE)) {
      return url.replace(YY_FETCH_QUEUE, "")
    }

    val temp = url.replace(YY_RETURN_DATA, "")
    val functionAndData = temp.split(SPLIT_MARK).toTypedArray()
    if (functionAndData.size >= 2) {
      val sb = StringBuilder()
      for (item in 1 until functionAndData.size) {
        sb.append(functionAndData[item])
      }
      return sb.toString()
    }
    return null
  }

  private fun getFunctionFromReturnUrl(url: String): String? {
    val temp = url.replace(YY_RETURN_DATA, "")
    val functionAndData = temp.split(SPLIT_MARK).toTypedArray()
    return if (functionAndData.isNotEmpty()) {
      functionAndData[0]
    } else {
      null
    }
  }

  fun dispatchMessages() {
    startupMessage?.forEach { dispatchMessage(it) }
    startupMessage = null
  }

  fun handleReturnData(url: String) {
    val functionName = getFunctionFromReturnUrl(url)
    val data = getDataFromReturnUrl(url)
    responseCallbacks[functionName]?.let {
      it.onCallBack(data)
      responseCallbacks.remove(functionName)
    }
  }

  fun doSend(handlerName: String, data: String?, responseCallback: CallbackFunction?) {
    val callbackId = if (responseCallback != null) {
      val args = "${++uniqueId}_${SystemClock.currentThreadTimeMillis()}"
      val callbackStr = String.format(CALLBACK_ID_FORMAT, args)
      responseCallbacks[callbackStr] = responseCallback

      callbackStr
    } else {
      null
    }

    val message = Message(
      callbackId = callbackId,
      responseId = null,
      responseData = null,
      data = data,
      handlerName = handlerName
    )
    addMessage(message)
  }

  private fun addMessage(message: Message) {
    if (startupMessage == null) {
      // 表明已经完成网页加载，可以直接注入
      dispatchMessage(message)
    } else {
      startupMessage?.add(message)
    }
  }

  private fun dispatchMessage(message: Message) {
    message.toJson()?.let {
      val json = it.replace("(\\\\)([^utrn])".toRegex(), "\\\\\\\\$1$2")
        .replace("(?<=[^\\\\])(\")".toRegex(), "\\\\\"")
      val format = JS_HANDLE_MESSAGE_FROM_JAVA
      val javascriptCommand = String.format(format, json)
      if (Thread.currentThread() === Looper.getMainLooper().thread) {
        loadUrlCallback?.invoke(javascriptCommand)
      }
    }
  }

  fun flushMessageQueue() {
    if (Thread.currentThread() != Looper.getMainLooper().thread) {
      return
    }

    loadUrl(JS_FETCH_QUEUE_FROM_JAVA) { data ->
      val messages = arrayListOf<Message>()
      data?.toMessages()?.run { messages.addAll(this) }
      if (messages.isEmpty()) return@loadUrl

      messages.forEach {
        if (it.responseId?.isNotEmpty() == true) {
          responseCallbacks[it.responseId]?.onCallBack(it.responseData)
          responseCallbacks.remove(it.responseId)
        } else {
          val responseFunction = if (it.callbackId != null) {
            CallbackFunction { data ->
              val responseMsg = Message(
                responseId = it.callbackId,
                responseData = data,
                callbackId = null,
                data = null,
                handlerName = it.handlerName
              )
              addMessage(responseMsg)
            }
          } else {
            CallbackFunction {}
          }
          messageHandlers[it.handlerName]?.handler(it.data, responseFunction)
        }
      }
    }
  }

  private fun loadUrl(jsUrl: String, returnCallback: CallbackFunction) {
    loadUrlCallback?.invoke(jsUrl)
    val responseCallId = jsUrl.replace("javascript:WebViewJavascriptBridge.", "")
      .replace("\\(.*\\);".toRegex(), "")
    responseCallbacks[responseCallId] = returnCallback
  }

  fun registerHandler(handlerName: String, handler: BridgeHandler) {
    messageHandlers[handlerName] = handler
  }

  fun destroy() {
    responseCallbacks.clear()
    messageHandlers.clear()
    startupMessage?.clear()
    loadUrlCallback = null
  }
}