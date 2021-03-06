package com.zhangls.jsbridge.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebView
import com.zhangls.jsbridge.original.BridgeWebView
import com.zhangls.jsbridge.tencent.X5BridgeWebView
import com.zhangls.jsbridge.tencent.X5BridgeWebViewClient


/**
 * @author zhangls
 */
class MainActivity : AppCompatActivity() {
    private val matchParent = ViewGroup.LayoutParams.MATCH_PARENT

    private val originalWebView by lazy {
        BridgeWebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
        }
    }

    private val x5WebView by lazy {
        X5BridgeWebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
        }
    }

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
                if (isX5) {
                    initWebView()
                }
                x5WebView.settingsExtension.setDisplayCutoutEnable(true)
            }
        })
    }

    private fun initWebView() {
        x5WebView.run {
            setContentView(this)
            with(settings) {
                // ????????????????????????????????????
                useWideViewPort = true
                domStorageEnabled = true
                // ????????????????????????
                loadWithOverviewMode = true
                javaScriptEnabled = true
                // ???????????????????????????????????? false????????? WebView ????????????
                builtInZoomControls = true
                // ???????????????????????????
                displayZoomControls = false
                // ????????????????????????
                allowFileAccess = false
                // ???????????? JS ???????????????
                javaScriptCanOpenWindowsAutomatically = true
                // ????????????????????????
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                // ??????
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
                databaseEnabled = false
                // ??????????????????
                defaultTextEncodingName = "utf-8"
            }
            clearCache(true)

//            loadUrl("https://liulanmi.com/labs/core.html")
//            loadUrl("https://debugtbs.qq.com")

            loadUrl("file:///android_asset/demo.html")

            val client = object : X5BridgeWebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    Log.d(this@MainActivity::class.simpleName, "onPageFinished: $url")
                }
            }
            // ?????? WebViewClient ?????????????????? webViewClient = client ???????????????????????? setJSWebViewClient ??????
            setJSWebViewClient(client)

            callHandler("functionInJs", "hahahahah 123") {
                Log.d(this@MainActivity::class.simpleName, "functionInJs: $it")
            }
            registerHandler("submitFromWeb") { data, callback ->
                Log.d(this@MainActivity::class.simpleName, "submitFromWeb: $data")
            }
        }
    }
}