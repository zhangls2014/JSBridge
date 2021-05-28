package com.zhangls.jsbridge.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.zhangls.jsbridge.original.BridgeWebView


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

//    private val x5WebView by lazy {
//        X5BridgeWebView(this).apply {
//            layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
//        }
//    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                domStorageEnabled = false
                databaseEnabled = false
                // 设置编码格式
                defaultTextEncodingName = "utf-8"
            }
            clearCache(true)

            loadUrl("https://www.bing.com")
        }
    }
}