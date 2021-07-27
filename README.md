#### 最新版本

模块|floweventbus|floweventbus_annotations|floweventbus_compiler
---|---|---|---
最新版本|[![Download](https://jitpack.io/v/like5188/FlowEventBus.svg)](https://jitpack.io/#like5188/FlowEventBus)|[![Download](https://jitpack.io/v/like5188/FlowEventBus.svg)](https://jitpack.io/#like5188/FlowEventBus)|[![Download](https://jitpack.io/v/like5188/FlowEventBus.svg)](https://jitpack.io/#like5188/FlowEventBus)

## 功能介绍
1、该项目基于 Flow 开发的，优点如下：

    ①、避免内存泄漏。当被生命周期结束时，会立刻取消搜集数据的任务并销毁相关事件，使用者不用显示调用反注册方法。并且绑定生命周期后，组件在不活跃状态时，不会收到数据。当组件处于活跃状态或者从不活跃状态到活跃状态时总是能收到最新的数据。
    ②、解决Configuration Change问题。在屏幕发生旋转或者被回收再次启动，立刻就能收到最新的数据。

2、通过`@BusObserver`注解方法来接收消息。

    ①、此注解中可以设置 tag、requestCode、isSticky 三个参数。
    ②、当 tag 相同时，可以用 requestCode 来区分。
    ③、isSticky 只是针对`@BusObserver`注解的接收消息的方法。发送消息时不区分粘性或者非粘性消息。sticky 为 true 时表示会收到注册之前发送过的最新一条消息。
    ④、发送消息时，必须要由相同的 tag、requestCode、参数类型，才能成功发送消息。
    ⑤、此方法必须用 public 修饰，且不能被 static 修饰，且参数最多只能是1个。

3、被`@BusObserver`注解的方法所在类称为宿主类。

    ①、此类必须用 public 修饰。
    ②、同一个宿主不会重复注册。

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
        implementation 'com.github.like5188.FlowEventBus:floweventbus:版本号'
        implementation 'com.github.like5188.FlowEventBus:floweventbus_annotations:版本号'
        kapt 'com.github.like5188.FlowEventBus:floweventbus_compiler:版本号'
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.20")
    }
```

2、在需要接收消息的类的初始化方法（通常为构造函数）中调用`register`方法进行注册宿主。当在父类调用`register`方法后，在子类中无需再调用。
```java
    FlowEventBus.register(host: Any, owner: LifecycleOwner?)//
    // 当注册时参数 owner 不是LifecycleOwner或者View类型，或者为null时，不会自动关联生命周期，必须显示调用下面的方法取消注册；不为null时会自动关联生命周期，不用调用取消注册的方法。
    FlowEventBus.unregister(host: Any)
```

3、发送消息。
```java
    FlowEventBus.post(tag: String)
    FlowEventBus.post(tag: String, t: T)
    FlowEventBus.post(tag: String, requestCode: String, t: T)
```

4、接收消息与发送消息一一对应。
```java
    发送消息:
    FlowEventBus.post(tag: String)
    
    接收消息:
    @BusObserver(["tag"])
    fun test() {
    }
```
```java
    发送消息:
    FlowEventBus.post(tag: String, t: T)

    接收消息:
    @BusObserver(["tag"])
    fun test(t: T) {
    }
```
```java
    发送消息:
    FlowEventBus.post(tag: String, requestCode: String, t: T)
    
    接收消息:
    @BusObserver(["tag"], requestCode = "requestCode")
    fun test(t: T) {
    }
```

5、混淆。需要发送的数据不能被混淆。因为用到了反射获取其类型。