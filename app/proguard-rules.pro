# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\adt-bundle-windows-x86_64-20131030\sdk/tools/proguard/proguard-android.txt
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

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
# 混淆前后的映射
-printmapping map.txt
# 不混淆jars中的 非public classes   默认选项
-dontskipnonpubliclibraryclassmembers
# 忽略警告
-ignorewarnings
# 指定代码的压缩级别
-optimizationpasses 5
# 不使用大小写混合类名
-dontusemixedcaseclassnames
# 不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
# 不启用优化  不优化输入的类文件
-dontoptimize
# 不预校验
-dontpreverify
# 混淆时记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保护注解
-renamesourcefileattribute SourceFile
# 保持源文件和行号的信息,用于混淆后定位错误位置
-keepattributes SourceFile,LineNumberTable
# 保持含有Annotation字符串的 attributes
-keepattributes *Annotation*
-keepattributes Signature
# Exceptions InnerClasses
-keepattributes Exceptions,InnerClasses

# 基础配置
# 保持哪些类不被混淆
# 系统组件
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
# 如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment


# 自定义View
-keep public class * extends android.view.View

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#support.v4/v7包不混淆
-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**
-keep interface android.support.v7.app.** { *; }
-dontwarn android.support.**    # 忽略警告


# 保持native方法及其类声明
-keepclasseswithmembers class * {
    native <methods>;
}


# 保持view的子类成员： getter setter
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}


# 保持Activity的子类成员：参数为一个View类型的方法   如setContentView(View v)
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}


# 保持枚举类的成员：values方法和valueOf  (每个enum 类都默认有这两个方法)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 任意包名.R类的类成员属性。  即保护R文件中的属性名不变
-keepclassmembers class **.R$* {
    public static <fields>;
}

# JSONObject
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

# GSON 2.2.4 specific rules ##
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }


# Serializable Parcelable
-keepnames class * implements java.io.Serializable
-keep public class * implements android.os.Parcelable {*;}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**


# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**


#BuildConfig
-keep class com.dgzrdz.mobile.cocobee.BuildConfig { *; }


## Google Play Services 4.3.23 specific rules ##
## https://developer.android.com/google/play-services/setup.html#Proguard ##
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Updated as of Stetho 1.1.1
# Note: Doesn't include Javascript console lines. See https://github.com/facebook/stetho/tree/master/stetho-js-rhino#proguard
-keep class com.facebook.stetho.** { *; }

## New rules for EventBus 3.0.x ##
# http://greenrobot.org/eventbus/documentation/proguard/
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


# Square's wire configuration.
-keep class com.squareup.**{*;}
-keep public class * extends com.squareup.**{*;}
-dontwarn com.squareup.**
# Keep methods with Wire annotations (e.g. @ProtoField)
-keepclassmembers class ** {
    @com.squareup.wire.ProtoField public *;
    @com.squareup.wire.ProtoEnum public *;
}


# ButterKnife 7
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


#bean
-keepclassmembers class com.dgzrdz.mobile.cocobee.model.** {* ; }



# com.nineoldandroids uses reflection
-keep class com.nineoldandroids.**
-keepclassmembers class com.nineoldandroids.** { *; }
-keep enum com.nineoldandroids.**
-keepclassmembers enum com.nineoldandroids.** { *; }
-keep interface com.nineoldandroids.**
-keepclassmembers interface com.nineoldandroids.** { *; }



# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn java.lang.invoke.*

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}



# 百度地图
#-keep class com.baidu.**{*;}
#-keep class vi.com.gdi.bgl.**{*;}
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

-dontwarn com.guo.duoduo.library.**
-keep class com.guo.duoduo.library.**{*;}


-dontwarn com.aiseminar.**
-keep class com.aiseminar.**{*;}

-keep class org.xmlpull.v1.** { *; }
-keep class * { *; }

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#光大银行支付
-dontwarn com.switfpass.pay.**
-keep class com.switfpass.pay.** { *;}

 #微信支付
 -keep class com.tencent.mm.opensdk.** {*;}
 -keep class com.tencent.wxop.** {*;}
 -keep class com.tencent.mm.sdk.** {*;}


 #galleryfinal
 -keep class cn.finalteam.galleryfinal.widget.*{*;}
 -keep class cn.finalteam.galleryfinal.widget.crop.*{*;}
 -keep class cn.finalteam.galleryfinal.widget.zoonview.*{*;}