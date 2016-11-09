# [Heard](http://fir.im/qs4a)

已加入 [LeanCloud](https://leancloud.cn/) 开源生态系统聚合。 访问网站 [leancloud.sexy](http://leancloud.sexy/)。

[![Awesome LeanCloud](https://img.shields.io/badge/Awesome-LeanCloud-2c97e8.svg)](http://leancloud.sexy)

## 1.项目简介

---

写这个App主要是为了学习 [LeanCloud](https://leancloud.cn/) 平台和各种其他开发框架的使用。 [Heard](http://fir.im/qs4a) 就是基于 [LeanCloud](https://leancloud.cn/) 开发的一个图片分享应用，集转发、评论、点赞等功能于一体。同时还提供了即时通讯基本的功能的实现，包括注册、登录、注销，联系人列表，关注用户，取消关注，收发消息，消息提醒等功能。 

>[LeanCloud](https://leancloud.cn/) 提供一站式后端云服务，从数据存储、实时聊天、消息推送到移动统计，涵盖应用开发的多方面后端需求。提供 iOS / Android / Windows Phone / Javascript 等多平台的原生 SDK，几分钟简单集成，即可拥有一个成熟、稳定的后端系统。 一定 的免费额度，大幅降低开发和运维成本。

## 2.使用的开源项目

---

- [LeanCloud](https://github.com/leancloud)
- [PrettyTime](https://github.com/ocpsoft/prettytime)
- [Glide](https://github.com/bumptech/glide)
- [Gson](https://github.com/google/gson)
- [OkHttp](https://github.com/square/okhttp)
- [TencentBugly](http://bugly.qq.com/)
- [BGABanner-Android](https://github.com/bingoogolapple/BGABanner-Android)
- [Material-Dialogs](https://github.com/afollestad/material-dialogs)
- [Androrm](http://www.androrm.com/)
- [AutoLinkTextView](https://github.com/armcha/AutoLinkTextView)

### 2.1.LeanCloud数据存储和实时通信服务

![LeanCloud数据存储](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot023.jpg)

***LeanCloud数据存储**针对数据，不管结果多少，属性具体含义如何，它们都可以抽象成统一的对象来处理。LeanCloud 支持存储任意类型的对象，支持对象的增、删、改、查等多种操作，并且开发者无需担心数据规模的大小和访问流量的多少，可以简单将 LeanCloud 云端看成是一个面向对象的海量数据库来使用。*

![LeanCloud实时通信服务](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot024.jpg)

***LeanCloud实时通信服务**中的每一个终端称为一个 client。client 拥有一个在应用内唯一标识自己的 id。这个 id 由应用自己定义，必须是不多于 64 个字符的字符串。在大部分场合，client 都可以对应到应用中的某个用户。*

*默认情况下，LeanCloud 通信服务允许一个 clientId 在多个不同的设备上登录，也允许一个设备上有多个 clientId 同时登录。如果使用场景中需要限制用户只在一处登录，可以在登录时明确设置当前设备的 tag， 当 LeanCloud 检测到同一个 tag 的设备出现冲突时，会自动踢出已存在设备上的登录状态。开发者可以根据自己的应用场景选择合适的使用方式。*

*使用 LeanCloud 实时通信 SDK 完成登录后，开发者就不必关心网络连接等状态，SDK 会自动为开发者保持连接状态，并根据网络状态自动重连。Android 平台使用常驻后台的服务保持在线状态。*

### 2.2.PrettyTime时间格式

*当你希望能够将时间格式成易于用户阅读的格式，如"12分钟前"、"2天前"、"至今3个月"等，那么你可以用 PrettyTime 来处理。*

*PrettyTime 支持多语言，可以在构造 PrettyTime 的时候传递一个 Locale 参数即可。*

### 2.3.Glide图片加载

*Glide 是一个 android 平台上的快速和高效的开源的多媒体资源管理库,提供 多媒体文件的压缩,内存和磁盘缓存, 资源池的接口。*

*它可以最大性能地在 Android 设备上读取、解码、显示图片和视频。Glide 可以将远程的图片、视频、动画图片等缓存在设备本地便于提高用户浏览图片的流畅体验。*

### 2.3.Gson Json格式转换

*GSON是Google开发的Java API，用于转换Java对象和Json对象。*

### 2.4.OkHttp Http工具类

*OkHttp是 Square 开源的 http 工具类。一款优秀的HTTP框架，它支持get请求和post请求，支持基于Http的文件上传和下载，支持加载图片，支持下载文件透明的GZIP压缩，支持响应缓存避免重复的网络请求，支持使用连接池来降低响应延迟问题。*

### 2.5.Bugly异常监测

*腾讯Bugly，为移动开发者提供专业的异常上报，运营统计和内测分发解决方案，帮助开发者快速发现并解决异常，同时掌握产品运营动态，及时跟进用户反馈。专业、全面的异常监控和解决方案，可以让您及时发现应用的异常，并通过丰富的现场信息帮您快速定位和解决问题。*

### 2.6.BGABanner-Android ViewPager库

*由 [GitHub](https://github.com/) 用户 [bingoogolapple](https://github.com/bingoogolapple) 创建的多种功能ViewPager库。*

![BGABanner-Android](https://cloud.githubusercontent.com/assets/8949716/17474646/98ff0980-5d89-11e6-965e-fc5167b6f51f.gif)

*支持引导界面导航效果，支持根据服务端返回的数据动态设置广告条的总页数，支持选中特定页面，加载网络数据时支持占位图设置，避免出现整个广告条空白的情况等等。*

### 2.7.Material-Dialogs 对话框库

*由 [GitHub](https://github.com/) 用户 [afollestad](https://github.com/afollestad/) 创建的多种样式对话框库。*

![Material-Dialogs](https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/readmeshowcase.png)

*创建的Activities必须继承AppCompat themes，这样才能适应对话框风格. Material dialog会自动将按钮的 positiveColor保持和styles.xml文件中的主题colorAccent属性一致。*

### 2.8.Androrm ORM框架

*Androrm 是 Android 平台上的一个对象关系映射框架，也就是常说的 ORM 框架。用于帮助你快速开发使用数据库的应用，封装了常用的表创建、对象读写和查询，通过将 Model 类映射到数据库表，直接对对象进行操作便可读写数据库。*

### 2.9.AutoLinkTextView TextView控件

*由 [GitHub](https://github.com/) 用户 [armcha](https://github.com/armcha/) 创建的基于TextView的控件。*

![AutoLinkTextView](https://raw.githubusercontent.com/armcha/AutoLinkTextView/master/screens/gif1.gif)

![AutoLinkTextView](https://github.com/armcha/AutoLinkTextView/raw/master/screens/screen1.png)

*AutoLinkTextView是一个支持检测和点击Hashtags (#)，Mentions (@)，URLs (http://)，Phone以及Email文本格式的TextView控件。*

*默认支持Hashtag，Mention，Link，Phone number和Email格式，支持通过正则表达式regex创建自定义格式；支持设置文本颜色，支持设置点击样式。*


## 3.项目结构

---

- adapters 存放适配器
- base 存放全局类
- models 数据模型
- services 数据操作类
- ui 存放activity和fragment
 - activities
 - fragments
 - views 自定义控件
- utils 工具类

## 4.APP界面

---

### 4.1.Splash



### 4.2.Index

![Index](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-004746.png)

### 4.3.Login

![Login](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-004635.png)

### 4.4.Menu

![Menu](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-005152.png)

### 4.5.Home

![Home](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-005146.png)

### 4.6.Find

![Find](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-005309.png)

### 4.7.Topic

![Topic](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-005243.png)

### 4.8.Message

![Message](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-005211.png)

### 4.9.Me

![Me](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-005254.png)

### 4.10.Setting

![Setting](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/Screenshot_20161108-005301.png)

## 5.代码混肴

---

APK文件很容易被反编译出来，代码都会被别人看到，因此我们需要在编译过程中对代码进行一定程度的代码混淆，使得别人不能反编译不出你的代码。下面介绍下具体混淆过程：
新建一个项目，Android Studio 默认关闭代码混淆开关，在 build.gradle 文件中，如下图所示的 minifyEnabled 开关，因此如果需要混淆代码，需将false改为true，然后在文件 proguard-rules.pro 添加具体混淆规则。

	release {
    	minifyEnabled true
    	proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
    }

proguard-rules.pro 文件内容：

	# Add project specific ProGuard rules here.
	# By default, the flags in this file are appended to flags specified
	# in C:\Users\pc\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
	# You can edit the include path and order by changing the proguardFiles
	# directive in build.gradle.
	#
	# For more details, see
	#   http://developer.android.com/guide/developing/tools/proguard.html
	
	# Add any project specific keep options here:
	
	# If your project uses WebView with JS, uncomment the following
	# and specify the fully qualified class name to the JavaScript interface
	# class:
	#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
	#   public *;
	#}
	
	# GalleryFinal
	-keep class cn.finalteam.galleryfinal.widget.*{*;}
	-keep class cn.finalteam.galleryfinal.widget.crop.*{*;}
	-keep class cn.finalteam.galleryfinal.widget.zoonview.*{*;}
	-keep public class * implements com.bumptech.glide.module.GlideModule
	-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
	  **[] $VALUES;
	  public *;
	}
	
	# LeanCloud
	-keepattributes Signature
	-dontwarn com.jcraft.jzlib.**
	-keep class com.jcraft.jzlib.**  { *;}
	
	-dontwarn sun.misc.**
	-keep class sun.misc.** { *;}
	
	-dontwarn com.alibaba.fastjson.**
	-keep class com.alibaba.fastjson.** { *;}
	
	-dontwarn sun.security.**
	-keep class sun.security.** { *; }
	
	-dontwarn com.google.**
	-keep class com.google.** { *;}
	
	-dontwarn com.avos.**
	-keep class com.avos.** { *;}
	
	-keep public class android.net.http.SslError
	-keep public class android.webkit.WebViewClient
	
	-dontwarn android.webkit.WebView
	-dontwarn android.net.http.SslError
	-dontwarn android.webkit.WebViewClient
	
	-dontwarn android.support.**
	
	-dontwarn org.apache.**
	-keep class org.apache.** { *;}
	
	-dontwarn org.jivesoftware.smack.**
	-keep class org.jivesoftware.smack.** { *;}
	
	-dontwarn com.loopj.**
	-keep class com.loopj.** { *;}
	
	-dontwarn com.squareup.okhttp.**
	-keep class com.squareup.okhttp.** { *;}
	-keep interface com.squareup.okhttp.** { *; }
	
	-dontwarn okio.**
	
	-dontwarn org.xbill.**
	-keep class org.xbill.** { *;}
	
	-keepattributes *Annotation*
	
	# Bugly
	-dontwarn com.tencent.bugly.**
	-keep public class com.tencent.bugly.**{*;}
	
	# prettytime
	# -keep class org.ocpsoft.prettytime.i18n.**
	
	# QRCode
	-keep class net.sourceforge.zbar.** { *; }
	-keep interface net.sourceforge.zbar.** { *; }
	-dontwarn net.sourceforge.zbar.**

## 6.打包上传

---

各个平台的审核标准可能不尽相同，但是对于个人开发者，各个平台都不是很友好，很多限制。本项目作为开源项目，因此我将文件上传到[fir.im](http://fir.im/)内测托管平台，详细地址为[http://fir.im/qs4a](http://fir.im/qs4a)。

## 7.最后

---

作为第一个个人应用，都是花费下班或者周末时间完成，既是为了填充个人的空闲时间，也是为了学习新技术新框架，是自己保持不断更新的状态。但是其中肯定有许多不足之处，希望自己在将来的项目中可以有效避免。

![Sea](https://raw.githubusercontent.com/WaylanPunch/Heard/master/Screenshot/initpintu.jpg)