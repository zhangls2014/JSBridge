package com.zhangls.jsbridge.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.zhangls.jsbridge.demo.databinding.AppActivityMainBinding


/**
 * @author zhangls
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = AppActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val web = binding.webView
    binding.mbSendMessage.setOnClickListener {
      web.callHandler("functionInJs", "random ==> ${(0..100).random()}") {
        LogUtils.d(this@MainActivity::class.simpleName, "functionInJs: $it")
      }
    }

    binding.mbReloading.setOnClickListener { web.reload() }

    web.config()
    web.loadUrl("file:///android_asset/demo.html")
    web.registerHandler("submitFromWeb") { data, _ ->
      LogUtils.d("submitFromWeb: $data")
      Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
    }
  }

}