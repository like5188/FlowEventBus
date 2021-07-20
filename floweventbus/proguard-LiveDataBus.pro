# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#livedatabus
#不能混淆HostProxy的实现类，因为需要反射实例化它并调用其中的方法进行注册。
-keep class * extends com.like.livedatabus.HostProxy
#不混淆包含被BusObserver注解的方法的宿主类，因为注册时需要用到它的类名，然后根据此类名来获取其代理类。
-keepclasseswithmembernames class **{
     @com.like.livedatabus_annotations.BusObserver <methods>;
}