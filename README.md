#### 最新版本

模块|livedatabus-api|livedatabus_compiler
---|---|---
最新版本|[![Download](https://jitpack.io/v/like5188/LiveDataBus.svg)](https://jitpack.io/#like5188/LiveDataBus)|[![Download](https://jitpack.io/v/like5188/LiveDataBus.svg)](https://jitpack.io/#like5188/LiveDataBus)

## 功能介绍
1、该项目基于LiveData开发的，LiveData的有点如下：

    ①、避免内存泄漏。观察者被绑定到组件的生命周期上，当被绑定的组件销毁（destroy）时，观察者会立刻自动清理自身的数据，使用者不用显示调用反注册方法。并且绑定生命周期后，组件在不活跃状态时，不会收到数据。当组件处于活跃状态或者从不活跃状态到活跃状态时总是能收到最新的数据。
    ②、解决Configuration Change问题。在屏幕发生旋转或者被回收再次启动，立刻就能收到最新的数据。
    ③、UI和实时数据保持一致。因为LiveData采用的是观察者模式，这样一来就可以在数据发生改变时获得通知，更新UI。

2、通过`@BusObserver`注解方法来接收消息。

    ①、此注解中可以设置 tag、requestCode、Sticky 三个参数。
    ②、当 tag 相同时，可以用 requestCode 来区分。
    ③、sticky 只是针对`@BusObserver`注解的接收消息的方法。发送消息时不区分粘性或者非粘性消息。sticky 为 true 时表示会收到注册之前发送过的最新一条消息。
    ④、必须要 tag、requestCode、参数类型，这三项与发送的消息完全一致，才能接收到消息。
    ⑤、此方法必须用 public 修饰，且不能被 static 修饰，且参数最多只能是1个。

3、被`@BusObserver`注解的方法所在类称为宿主类。

    ①、此类必须用 public 修饰。
    ②、同一个宿主不会重复注册（重复了就只有第一个有效）。
    ③、同一个宿主中不能有相同的 tag+requestCode（重复了就只有第一个有效）。

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        implementation 'com.github.like5188.LiveDataBus:livedatabus:版本号'

        // gradle 3.2.1 不需要添加这个引用 ， 升级到 3.5.2 后必须添加 ， 否则会提示找不到livedatabus_annotations中的类 。
        implementation 'com.github.like5188.LiveDataBus:livedatabus_annotations:版本号'

        kapt 'com.github.like5188.LiveDataBus:livedatabus_compiler:版本号'
        compileOnly 'com.squareup:javapoet:1.13.0'// 自动生成源码的库
    }
```

2、在需要接收消息的类的初始化方法（通常为构造函数）中调用`register`方法进行注册宿主。当在父类调用`register`方法后，在子类中无需再调用。
```java
    LiveDataBus.register(host: Any, owner: LifecycleOwner?)//
    // 当注册时参数 owner 不是LifecycleOwner或者View类型，或者为null时，不会自动关联生命周期，必须显示调用下面的方法取消注册；不为null时会自动关联生命周期，不用调用取消注册的方法。
    LiveDataBus.unregister(host: Any)
```

3、发送消息。
```java
    LiveDataBus.post(tag: String)
    LiveDataBus.post(tag: String, t: T)
    LiveDataBus.post(tag: String, requestCode: String, t: T)
```

4、接收消息与发送消息一一对应。
```java
    发送消息:
    LiveDataBus.post(tag: String)
    
    接收消息:
    // java
    @BusObserver("tag")
    public void test() {
    }

    // kotlin
    @BusObserver(["tag"])
    fun test() {
    }
```
```java
    发送消息:
    LiveDataBus.post(tag: String, t: T)

    接收消息:
    // java
    @BusObserver("tag")
    public void test(T t) {
    }

    // kotlin
    @BusObserver(["tag"])
    fun test(t: T) {
    }
```
```java
    发送消息:
    LiveDataBus.post(tag: String, requestCode: String, t: T)
    
    接收消息:
    // java
    @BusObserver(value = "tag", requestCode = "requestCode")
    public void test(T t) {
    }

    // kotlin
    @BusObserver(["tag"], requestCode = "requestCode")
    fun test(t: T) {
    }
```