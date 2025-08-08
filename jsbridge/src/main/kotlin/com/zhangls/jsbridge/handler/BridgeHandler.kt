package com.zhangls.jsbridge.handler


/**
 * @author zhangls
 */
fun interface BridgeHandler {
  fun handler(data: String?, callback: CallbackFunction?)
}