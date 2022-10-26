### JSBridge

由于项目中使用的 [JSBridge](https://github.com/hjhrq1991/JsBridge) 库长期不维护，出现了未解决的 bug，所以决定使用使用 kotlin 重写一次。



#### Android 上的使用方法

1. Android 调用 JS 方法

   ```kotlin
   BridgeWebView.callHandler("functionName", "data") {
     // 回调内容
   }
   ```

2. 注册 JS 调用 Android 方法

   ```kotlin
   BridgeWebView.registerHandler("functionName") { "data", callback ->
     // 回调内容
   }
   ```

#### X5BridgeWebView

X5BridgeWebView 基于 Tbs (腾讯浏览服务) X5 内核，如需使用需要引入 Tbs 的依赖。

#### WebViewClient

`setWebViewClient` 方法被标记过时，需要使用 `setJSWebViewClient` 替代

#### 集成方法

[![](https://jitpack.io/v/zhangls2014/JSBridge.svg)](https://jitpack.io/#zhangls2014/JSBridge)

```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
  implementation 'com.github.zhangls2014:JSBridge:Tag'
}
```

