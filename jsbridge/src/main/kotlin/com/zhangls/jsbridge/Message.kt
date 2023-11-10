package com.zhangls.jsbridge

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * data of bridge
 *
 * @author zhangls
 */

internal const val CALLBACK_ID_STR = "callbackId"
internal const val RESPONSE_ID_STR = "responseId"
internal const val RESPONSE_DATA_STR = "responseData"
internal const val DATA_STR = "data"
internal const val HANDLER_NAME_STR = "handlerName"

internal data class Message(
    val callbackId: String?,
    val responseId: String?,
    val responseData: String?,
    val data: String?,
    val handlerName: String
) {
    fun toJson(): String? {
        val jsonObject = JSONObject()
        return try {
            jsonObject.put(CALLBACK_ID_STR, callbackId)
            jsonObject.put(DATA_STR, data)
            jsonObject.put(HANDLER_NAME_STR, handlerName)
            jsonObject.put(RESPONSE_DATA_STR, responseData)
            jsonObject.put(RESPONSE_ID_STR, responseId)
            jsonObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }
}

internal fun String.toMessages(): List<Message> {
    val messages: MutableList<Message> = ArrayList()
    try {
        val jsonArray = JSONArray(this)
        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(index).run {
                val handlerName = optString(HANDLER_NAME_STR)
                val callbackId = optString(CALLBACK_ID_STR)
                val responseData = optString(RESPONSE_DATA_STR)
                val responseId = optString(RESPONSE_ID_STR)
                val data = optString(DATA_STR)

                Message(callbackId, responseId, responseData, data, handlerName)
            }
            messages.add(item)
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    return messages
}