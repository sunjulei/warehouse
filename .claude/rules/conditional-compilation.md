## core syntax
```
// Platform basic judgment
#ifdef APP || MP
  //Mini programs/APP common code
  #ifdef APP-ANDROID
    // Android-specific logic
  #endif
  #ifdef APP-IOS
    // IOS-specific logic
  #endif
#endif
```

## Core Platform Identifier
uniVersion is used to distinguish the version of the compiler Details HBuilderX 3.9.0 
APP App
APP-ANDROID App Android Platform Details
APP-IOS App iOS Platform Details
APP-HARMONY App HarmonyOS Next platform
WEB web (same as H5) HBuilderX 3.6.3 
MP-WEIXIN WeChat Mini Program
MP-ALIPAY APPLET
MP-BAIDU BAIDU MINI PROGRAM
MP-TUTIAO TIKTOK MINI PROGRAM
MP-KUAISHOU Kuaishou Mini Program
MP-JD JD Mini Program
MP-HARMONY Harmony Atom Service HBuilderX 4.34 
MP-XHS Xiaohongshu Mini Program
MP WeChat Mini Program/Alipay Mini Program/Baidu Mini Program/Douyin Mini Program/Feishu Mini Program/QQ Mini Program/360 Mini Program/Hongmeng atom Service