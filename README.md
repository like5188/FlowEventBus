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
    ②、当 tag 相同时，可以用 requestCode 来区分。同一个@BusObserver注解的方法中，tag如果有重复，则会自动去重。
    ③、isSticky 只是针对`@BusObserver`注解的接收消息的方法。发送消息时不区分粘性或者非粘性消息。sticky 为 true 时表示会缓存最近的一条消息，让新注册的宿主也能收到这条消息。
    ④、发送消息时，必须要由相同的 tag、requestCode，以及匹配的参数类型（注意：可空类型的参数类型可以接收非空类型的数据），才能成功发送消息。
    ⑤、此方法必须用 public 修饰，且不能被 static 修饰，且参数最多只能是1个。

3、被`@BusObserver`注解的方法所在类称为宿主类。

    ①、此类必须用 public 修饰。
    ②、同一个宿主不会重复注册。

4、支持跨进程发送消息(仅支持Intent支持的数据类型)。使用了广播来实现，不支持粘性消息。

    注意：跨进程发送消息时，如果出现类型转换报错，比如：传递Parcelable数组或者集合时，报错 android.os.Parcelable[] cannot be cast to xxx[]，那么请使用Bundle来传递数据，然后自己解析。

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
        // 发送消息
        implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.0'
        implementation 'com.github.like5188.FlowEventBus:floweventbus:版本号'
        // 接收消息
        implementation 'com.github.like5188.FlowEventBus:floweventbus_annotations:版本号'
        kapt 'com.github.like5188.FlowEventBus:floweventbus_compiler:版本号'
    }
```

2、在Application中初始化。
```java
    FlowEventBus.init(Application)
```

3、发送消息。（注意：泛型为空时，本库没有做区分，比如Array<Int?>? 和 Array<Int>? 本库认为是一样的，发送其中一种数据，两种类型都能接收数据）

    ①不跨进程
```java
    FlowEventBus.post(tag: String)
    FlowEventBus.post(tag: String, t: T)
    FlowEventBus.post(tag: String, requestCode: String, t: T)
```
    ②跨进程(不支持粘性消息)(仅支持Intent支持的数据类型)
```java
    FlowEventBus.postAcrossProcess(tag: String)
    FlowEventBus.postAcrossProcess(tag: String, t: T)
    FlowEventBus.postAcrossProcess(tag: String, requestCode: String, t: T)
```

4、接收消息。

    ①在需要接收消息的类中调用`register`方法进行注册宿主。当在父类调用`register`方法后，在子类中无需再调用。
```java
    // 在需要接收消息的类中调用`register`方法进行注册宿主。当在父类调用`register`方法后，在子类中无需再调用。
    FlowEventBus.register(host: Any, owner: LifecycleOwner?)
    // 当注册时参数 owner 不是LifecycleOwner或者View类型，或者为null时，不会自动关联生命周期，必须显示调用下面的方法取消注册；不为null时会自动关联生命周期，不用调用取消注册的方法。
    FlowEventBus.unregister(host: Any)
```

    ②接收消息与发送消息一一对应。(注意：如果接收String类型的参数，可以使用String或者String?来接收)
```java
    发送消息:(主线程)
    FlowEventBus.post(tag1: String)
    FlowEventBus.post(tag2: String)
    
    接收消息:(主线程)
    @BusObserver(["tag1", "tag2"])
    fun test() {
    }
```
```java
    发送消息:(主线程)
    FlowEventBus.post(tag: String, t: T)

    接收消息:(主线程)
    @BusObserver(["tag"])
    fun test(t: T) {
    }
```
```java
    发送消息:(主线程)
    FlowEventBus.post(tag: String, requestCode: String, t: T)
    
    接收消息:(主线程)
    @BusObserver(["tag"], requestCode = "requestCode")
    fun test(t: T) {
    }
```

5、混淆。需要发送的数据不能被混淆。因为用到了反射获取其类型。
