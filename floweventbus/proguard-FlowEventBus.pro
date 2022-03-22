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

# floweventbus
# 需要发送的数据不能被混淆。因为用到了反射获取其类型。
-keep class com.like.floweventbus.NoArgs
# 不混淆包含被 BusObserver 注解的方法的宿主类，因为需要用它的类名来获取自动生成的类 FlowEventbusInitializer。
-keepclasseswithmembernames class **{
     @com.like.floweventbus_annotations.BusObserver <methods>;
}
# 不能混淆自动生成的类 FlowEventbusInitializer，因为需要反射调用它的方法进行初始化。
-keep class **.FlowEventbusInitializer{*;}