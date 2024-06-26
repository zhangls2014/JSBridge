package com.zhangls.jsbridge.demo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.export.external.interfaces.PermissionRequest
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.zhangls.jsbridge.tencent.X5BridgeWebView
import com.zhangls.jsbridge.tencent.X5BridgeWebViewClient


/**
 * @author zhangls
 */
class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initX5SDK()
    }

    private fun initX5SDK() {
        QbSdk.setDownloadWithoutWifi(true)
        QbSdk.setCoreMinVersion(45900)
        val map = mapOf(
            TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
            TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true,
        )
        QbSdk.initTbsSettings(map)
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
            }

            override fun onViewInitFinished(isX5: Boolean) {
                Log.d(
                    this@MainActivity::class.simpleName,
                    "onViewInitFinished = $isX5, x5Version = ${QbSdk.getTbsVersion(this@MainActivity)}"
                )
                initWebView()
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        setContentView(R.layout.app_activity_main)
        val web = findViewById<X5BridgeWebView>(R.id.webView)

        findViewById<MaterialButton>(R.id.mbSendMessage).setOnClickListener {
            web.callHandler("functionInJs", "random ==> ${(0..100).random()}") {
                Log.d(this@MainActivity::class.simpleName, "functionInJs: $it")
            }
        }

        findViewById<MaterialButton>(R.id.mbReloading).setOnClickListener {
            web.reload()
        }

        web.run {
            if (this is X5BridgeWebView) {
                settingsExtension?.setDisplayCutoutEnable(true)
            }

            with(settings) {
                // 设置自适应屏幕，两者合用
                useWideViewPort = true
                domStorageEnabled = true
                // 缩放至屏幕的大小
                loadWithOverviewMode = true
                javaScriptEnabled = true
                // 设置内置的缩放控件。若为 false，则该 WebView 不可缩放
                builtInZoomControls = true
                // 隐藏原生的缩放控件
                displayZoomControls = false
                // 设置可以访问文件
                allowFileAccess = false
                // 支持通过 JS 打开新窗口
                javaScriptCanOpenWindowsAutomatically = true
                // 支持自动加载图片
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                // 缓存
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
                databaseEnabled = false
                // 设置编码格式
                defaultTextEncodingName = "utf-8"
            }

            loadUrl("file:///android_asset/demo.html")
//            loadUrl("https://debugtbs.qq.com")

            registerHandler("submitFromWeb") { data, _ ->
                Log.d(this@MainActivity::class.simpleName, "submitFromWeb: $data")
                Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
            }

            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest?) {
                    request?.let {
                        val permissions = arrayListOf<String>()
                        if (it.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                            permissions.add(Permission.RECORD_AUDIO)
                        }
                        if (it.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                            permissions.add(Permission.CAMERA)
                        }
                        // 权限申请框架，需要手动调用申请权限获取系统对应用的授权，然后授予 WebView
                        XXPermissions.with(this@MainActivity)
                            .permission(permissions)
                            .request { _, all ->
                                if (all) {
                                    Log.d(this@MainActivity::class.simpleName, "onPermissionRequest: granted")
                                    request.grant(it.resources)
                                } else {
                                    Log.d(this@MainActivity::class.simpleName, "onPermissionRequest: denied")
                                }
                            }

                        it.resources.forEach { resource ->
                            Log.d(this@MainActivity::class.simpleName, "onPermissionRequest: $resource")
                        }
                    }
                }
            }

            setJSWebViewClient(object : X5BridgeWebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.d(this@MainActivity::class.simpleName, "onPageStarted: url = $url")
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    Log.d(this@MainActivity::class.simpleName, "onPageFinished: url = $url")
                }
            })
        }
    }

}