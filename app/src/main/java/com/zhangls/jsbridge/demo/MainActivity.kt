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
import com.zhangls.jsbridge.original.BridgeWebView
import com.zhangls.jsbridge.tencent.X5BridgeWebView


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

        initWebView()
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
        originalWebView.run {
            setContentView(this)
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
            clearCache(true)

//            loadUrl("https://liulanmi.com/labs/core.html")
//            loadUrl("https://debugtbs.qq.com")
        }
    }
}