package com.zhangls.jsbridge.demo

import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.annotation.CallSuper
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions


/**
 * @author zhangls
 */
open class BaseWebChromeClient : WebChromeClient() {

  @CallSuper
  override fun onProgressChanged(view: WebView?, newProgress: Int) {
    super.onProgressChanged(view, newProgress)
    LogUtils.i("onProgressChanged: $newProgress")
  }

  @CallSuper
  override fun onPermissionRequest(request: PermissionRequest) {
    handlePermissionRequest(request)
  }

  private fun handlePermissionRequest(request: PermissionRequest) {
    val permissions = arrayListOf<String>()
    if (request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
      permissions.add(Permission.RECORD_AUDIO)
    }
    if (request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
      permissions.add(Permission.CAMERA)
    }

    // 权限申请框架，需要手动调用申请权限获取系统对应用的授权，然后授予 WebView
    XXPermissions.with(Utils.getApp())
      .permission(permissions)
      .request { _, all ->
        if (all) {
          LogUtils.d("onPermissionRequest: granted")
          request.grant(request.resources)
        } else {
          LogUtils.d("onPermissionRequest: denied")
        }
      }

    request.resources.forEach { LogUtils.d("onPermissionRequest: $it") }
  }

}