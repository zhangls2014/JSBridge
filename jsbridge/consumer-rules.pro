-keep public class com.zhangls.jsbridge.tencent.X5BridgeWebView { *; }
-keep public class com.zhangls.jsbridge.tencent.X5BridgeWebViewClient

-keep public class com.zhangls.jsbridge.original.BridgeWebView { *; }
-keep public class com.zhangls.jsbridge.original.BridgeWebViewClient

-keep public class com.zhangls.jsbridge.handler.BridgeHandler { *; }
-keep public class com.zhangls.jsbridge.handler.CallbackFunction { *; }

# Tencent X5
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**
-keep class com.tencent.smtt.** { *; }
-keep class com.tencent.tbs.** { *; }