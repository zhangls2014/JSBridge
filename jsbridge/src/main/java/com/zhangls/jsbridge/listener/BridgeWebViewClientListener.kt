package com.zhangls.jsbridge.listener

/**
 * @author zhangls
 */
internal interface BridgeWebViewClientListener {

    fun handleReturnData(url: String)

    fun flushMessageQueue()

    fun dispatchMessages()

}