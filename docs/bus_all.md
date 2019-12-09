# Android消息总线详解
作为Android开发者，一定会用到消息总线。我用过的消息总线包括EventBus、RxBus，以及我后面开发的LiveEventBus、SmartEventBus。本文旨在向阅读者以一个最全面的视角讨论这些消息总线在设计思想和使用方式上的异同。

## 设计思想
这四个消息总线基本设计思想都是观察者模式。
### EventBus
本文使用的EventBus版本是当前最新版本3.1.1

[greenrobot/EventBus](https://github.com/greenrobot/EventBus)

### RxBus
我实现了一个简单的RxBus，其实所有的RxBus在实现上都差不多，都是基于RxJava实现，Demo中用到的RxBus源码以及两个GitHub上找到的RxBus：
1. [Demo/RxBus](https://github.com/JeremyLiao/SmartEventBus/blob/master/eventbus-demo/rxbus/src/main/java/com/jeremyliao/android/eventbus/rxbus/RxBus.java)
2. [AndroidKnife/RxBus](https://github.com/AndroidKnife/RxBus)
3. [Blankj/RxBus](https://github.com/Blankj/RxBus)

### LiveEventBus
LiveEventBus基于Android arch库的LiveData实现，具有生命周期感知、跨进程/跨APP消息的能力：

[LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)

### SmartEventBus
SmartEventBus基于LiveEventBus实现，能让你定制自己的消息总线。

[SmartEventBus](https://github.com/JeremyLiao/SmartEventBus)

### 常用消息总线对比
用一张图表可以看出这几个消息总线的特性：

消息总线 | 延迟发送 | 有序接收消息 | Sticky | 生命周期感知 | 跨进程/APP | Customize（定制能力） | 线程分发
---|---|---|---|---|---|----|---
EventBus | :x: | :white_check_mark: | :white_check_mark: | :x: | :x: | :x: | :white_check_mark:
RxBus | :x: | :x: | :white_check_mark: | :x: | :x: | :x: | :white_check_mark:
LiveEventBus | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x: | :x:
SmartEventBus | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x:

## 普通用法
### EventBus
#### 1. 定义消息
EventBus需要定义一个Java Object（POJO）作为消息事件，如：

```
public class MessageEvent {

    public String msg;

    public MessageEvent(String msg) {
        this.msg = msg;
    }
}
```
#### 2. 订阅和取消订阅消息
EventBus需要在一个生命周期中订阅和取消订阅消息，然后用注解定义接收消息的方法：

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_eventbus_demo);
    EventBus.getDefault().register(this);
}

@Override
protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
}

@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(MessageEvent event) {
    Toast.makeText(this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
}
```

#### 3. 发送消息

```
public void sendMsg(View v) {
    EventBus.getDefault().post(new MessageEvent("msg from event bus"));
}
```

### RxBus
#### 1. 定义消息
RxBus定义消息的方法同EventBus一致，不再累述
#### 2. 订阅和取消订阅消息

```
private final CompositeDisposable disposable = new CompositeDisposable();

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_eventbus_demo);
    disposable.add(
            RxBus.getInstance()
                    .toObservable(MessageEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MessageEvent>() {
                        @Override
                        public void accept(MessageEvent event) throws Exception {
                            Toast.makeText(RxBusDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
                        }
                    }));
}

@Override
protected void onDestroy() {
    super.onDestroy();
    disposable.dispose();
}
```
利用Rxjava2实现的RxBus需要利用CompositeDisposable实现取消订阅

#### 3. 发送消息

```
public void sendMsg(View v) {
    RxBus.getInstance().post(new MessageEvent("msg from rxbus"));
}
```

### LiveEventBus
LiveEventBus不需要为每一个事件都定义一个Java Object（POJO），只需要维护一个String类型的消息名字即可，每一个消息可以携带任何类型的Java Object作为消息的payload，十分灵活。

并且如果是在Activity这种LifecycleOwner中使用LiveEventBus，只需要订阅消息，不需要手动取消订阅，消息会在观察者生命周期结束的时候自动取消订阅。
#### 1. 订阅消息

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_eventbus_demo);
    LiveEventBus
            .get(TEST_KEY, String.class)
            .observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    Toast.makeText(LiveEventBusDemo.this, "receive massage: " + s, Toast.LENGTH_SHORT).show();
                }
            });
}
```
#### 2. 发送消息

```
public void sendMsg(View v) {
    LiveEventBus
            .get(TEST_KEY)
            .post("msg from liveeventbus");
}
```

### SmartEventBus
SmartEventBus在LiveEventBus的基础上，增加了可定制化和可约束的特性。可以根据用户的需求定制消息总线。
#### 1. 定义消息

```
@SmartEvent(keys = {"event1", "event2", "event3"})
public class MessageEvent {

    public String msg;

    public MessageEvent(String msg) {
        this.msg = msg;
    }
}
```
SmartEventBus在消息定义上与EventBus、RxBus类似。SmartEventBus可以通过注解@SmartEvent定义多个同类消息，这样与EventBus、RxBus相比，可以大大简化消息定义。比如我们定义的多个消息，消息体都是String类型的，在EventBus、RxBus中，我们需要定义多个消息类，而在SmartEventBus中，我们只需要定义一个消息类，指定多个keys就可以了。
#### 2. 配置消息总线

```
@SmartEventConfig(packageName = "com.jeremyliao.android.eventbus.demo.smarteventbus", busName = "MySmartEventBus")
public class BaseEventConfig {
}
```
主要就是配置消息总线的包名和类名，也可以不配置，会生成一个默认类名为DefaultSmartEventBus的消息总线：

```
/**
 * Auto generate code, do not modify!!! */
public class MySmartEventBus {
  public static Observable<com.jeremyliao.android.eventbus.demo.smarteventbus.event.MessageEvent> event1(
      ) {
    return LiveEventBus.get("event1", com.jeremyliao.android.eventbus.demo.smarteventbus.event.MessageEvent.class);
  }

  public static Observable<com.jeremyliao.android.eventbus.demo.smarteventbus.event.MessageEvent> event2(
      ) {
    return LiveEventBus.get("event2", com.jeremyliao.android.eventbus.demo.smarteventbus.event.MessageEvent.class);
  }

  public static Observable<com.jeremyliao.android.eventbus.demo.smarteventbus.event.MessageEvent> event3(
      ) {
    return LiveEventBus.get("event3", com.jeremyliao.android.eventbus.demo.smarteventbus.event.MessageEvent.class);
  }
}
```

#### 2. 订阅消息
同LiveEventBus一致，如果是在Activity这种LifecycleOwner中使用LiveEventBus，只需要订阅消息，不需要手动取消订阅，消息会在观察者生命周期结束的时候自动取消订阅。
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_eventbus_demo);
    MySmartEventBus
            .event1()
            .observe(this, new Observer<MessageEvent>() {
                @Override
                public void onChanged(@Nullable MessageEvent event) {
                    Toast.makeText(SmartEventBusDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
                }
            });
}
```

#### 3. 发送消息

```
public void sendMsg(View v) {
    MySmartEventBus
            .event1()
            .post(new MessageEvent("msg from smarteventbus"));
}
```

SmartEventBus不仅可以定制你自己的消息总线，而且通过定制消息总线，实现了对发送消息的约束。

## 粘性消息
粘性消息使用的场景是接收方订阅消息在发送方发送消息之后，但是接收方期望能够接受到发送方之前发送的最后一条消息。这四种消息总线都支持粘性消息，我们来看看使用上的异同。
### EventBus
EventBus对粘性消息的支持需要发送发和接收方都设置成sticky模式，首先需要以sticky模式订阅消息：

```
@Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
public void onReceiveMessage(StickyMessageEvent event) {
    Toast.makeText(this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
}
```
其次发送消息也要以sticky模式发送消息：

```
public void sendStickyMsg(View v) {
    EventBus.getDefault().postSticky(new StickyMessageEvent("msg from event bus"));
    startActivity(new Intent(this, EventBusStickyDemo.class));
}
```
这样，在示例中，先发送一个StickyMessageEvent，然后再启动EventBusStickyDemo，EventBusStickyDemo启动之后能够收到消息：StickyMessageEvent。
### RxBus
在Demo提供的RxBus中，用一个HashMap来实现粘性消息，基本的原理就是发送粘性消息先把这个消息保存在HashMap中再发送。这样订阅者调用toObservableSticky订阅消息的时候先查找这个HashMap有没有消息，有的话就先接收这个消息。

RxBus一般要求订阅方和发送方都要以sticky模式处理订阅和发送。

订阅消息：

```
disposable.add(
        RxBus.getInstance()
                .toObservableSticky(StickyMessageEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StickyMessageEvent>() {
                    @Override
                    public void accept(StickyMessageEvent event) throws Exception {
                        Toast.makeText(RxBusStickyDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
                    }
                }));
```

发送消息：

```
public void sendStickyMsg(View v) {
    RxBus.getInstance().postSticky(new StickyMessageEvent("sticky msg from rxbus"));
    startActivity(new Intent(this, RxBusStickyDemo.class));
}
```

### LiveEventBus
LiveEventBus只需要在订阅的时候确定是以sticky模式还是普通模式订阅消息：

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LiveEventBus
            .get(TEST_STICKY_KEY, String.class)
            .observeSticky(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    Toast.makeText(LiveEventBusStickyDemo.this, "receive massage: " + s, Toast.LENGTH_SHORT).show();
                }
            });
}
```
发送消息的时候还是以通用的post方法发送：

```
public void sendStickyMsg(View v) {
    LiveEventBus
            .get(TEST_STICKY_KEY)
            .post("sticky msg from liveeventbus");
    startActivity(new Intent(this, LiveEventBusStickyDemo.class));
}
```

### SmartEventBus
SmartEventBus基于LiveEventBus实现，所以在支持sticky模式的方式上是一致的。
订阅消息：

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MySmartEventBus
            .event2()
            .observeSticky(this, new Observer<MessageEvent>() {
                @Override
                public void onChanged(@Nullable MessageEvent event) {
                    Toast.makeText(SmartEventBusStickyDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
                }
            });
}
```
发送消息：

```
public void sendStickyMsg(View v) {
    MySmartEventBus
            .event2()
            .post(new MessageEvent("sticky msg from smarteventbus"));
    startActivity(new Intent(this, SmartEventBusStickyDemo.class));
}
```

## 配置消息总线
### EventBus
EventBus通过EventBusBuilder来对消息总线进行配置，例如：

```
EventBus eventBus = EventBus
        .builder()
        .eventInheritance()
        .addIndex()
        .executorService()
        .ignoreGeneratedIndex()
        .logger()
        .logNoSubscriberMessages()
        .logSubscriberExceptions()
        .sendNoSubscriberEvent()
        .sendSubscriberExceptionEvent()
        .skipMethodVerificationFor()
        .strictMethodVerification()
        .throwSubscriberException()
        .build();
```
配置项很多，具体可以参考：[EventBus官方文档](http://greenrobot.org/eventbus/documentation/)

如果要配置一个默认的EventBus实例，则需要在配置完成后调用installDefaultEventBus：

```
EventBus.builder()
        .eventInheritance()
        .addIndex()
        .executorService()
        .ignoreGeneratedIndex()
        .logger()
        .logNoSubscriberMessages()
        .logSubscriberExceptions()
        .sendNoSubscriberEvent()
        .sendSubscriberExceptionEvent()
        .skipMethodVerificationFor()
        .strictMethodVerification()
        .throwSubscriberException()
        .installDefaultEventBus();
```
这个方法的调用需要在EventBus实例化之前，最好是在安卓的Application中去调用。

### RxBus
RxBus基本不需要什么配置。

### LiveEventBus
LiveEventBus有3个配置项，需要在Application.onCreate方法中配置：

```
LiveEventBus
        .config()
        .supportBroadcast(this)
        .lifecycleObserverAlwaysActive(true)
        .autoClear(false);
```
- **supportBroadcast**

配置支持跨进程、跨APP通信

- **lifecycleObserverAlwaysActive**

配置LifecycleObserver（如Activity）接收消息的模式（默认值true）：
1. true：整个生命周期（从onCreate到onDestroy）都可以实时收到消息
2. false：激活状态（Started）可以实时收到消息，非激活状态（Stoped）无法实时收到消息，需等到Activity重新变成激活状态，方可收到消息

- **autoClear**

配置在没有Observer关联的时候是否自动清除LiveEvent以释放内存（默认值false）

这3个配置项如果要支持跨进程/跨APP能力，是一定需要配置supportBroadcast，lifecycleObserverAlwaysActive和autoClear如果不配置，则会使用默认值。

### SmartEventBus
SmartEventBus基于LiveEventBus实现，按照LiveEventBus的配置方式即可。

## 线程分发和线程模型
### EventBus
EventBus可以为你处理线程：事件可以被发布到与发布线程不同的线程中。一般用法是处理UI，在安卓中UI的处理需要在主线程中完成。

设置接收消息的线程需要在订阅的时候用threadMode设置：

```
@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(MessageEvent event) {
    Toast.makeText(this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
}
```
EventBus支持的线程模式有：
1. POSTING

订阅者将会被调用在与发布线程同样的线程中。这是默认的，事件的分发意味着最小的开销，因为这种模式避免了线程切换。对于简单任务来说这是被推荐的用法。使用这个mode需要快速返回结果，避免锁住主线程，因为他可能在主线程执行。

2. MAIN

订阅者将被回调在安卓的主线程中。如果发布线程是主线程那事件的处理会马上被执行。

3. MAIN_ORDERED

同MAIN，订阅者将被回调在安卓的主线程中，并且接收的消息是有序的。

4. BACKGROUND

订阅者将会被回调在子线程中。如果发布线程不是主线程，事件处理会马上被执行在发布线程中。如果发布线程是主线程，EventBus会使用一个单独的子线程顺序处理事件。

5. ASYNC

事件处理方法会在一个单独的线程中调用。这个线程永远独立与发布线程和主线程。

### RxBus
RxBus以Rxjava的模式实现线程切换，例如：

```
RxBus.getInstance()
        .toObservable(MessageEvent.class)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<MessageEvent>() {
            @Override
            public void accept(MessageEvent event) throws Exception {
                Toast.makeText(RxBusDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
            }
        }));
```

### LiveEventBus和SmartEventBus
LiveEventBus和SmartEventBus都是基于LiveData实现，接收消息只能在主线程完成，发送消息可以在主线程或者后台线程发送。

## 生命周期感知能力
EventBus和RxBus没有生命周期感知能力，LiveEventBus和SmartEventBus基于LiveData实现，具有生命周期感知能力。

具体的说来，生命周期感知能力就是当在Android平台的LifecycleOwner（如Activity）中使用的时候，只需要订阅消息，而不需要取消订阅消息。LifecycleOwner的生命周期结束的时候，会自动取消订阅。这带来了两个好处：
1. 可以在任何位置订阅消息，而不是必须在onCreate方法中订阅
2. 避免了忘记取消订阅引起的内存泄漏

## 跨进程/跨APP发送消息
EventBus和RxBus没有跨进程/跨APP发送消息能力，LiveEventBus和SmartEventBus可以实现跨进程/跨APP发送消息。

在Application.onCreate方法中配置：

```
LiveEventBus
        .config()
        .supportBroadcast(this);
```

用broadcast发送跨进程/跨APP消息：

```
public void sendBroadcastMsg(View v) {
    LiveEventBus
            .get(TEST_BRC_KEY)
            .broadcast("msg from liveeventbus");
}
```

订阅消息的方式和普通方式一致：

```
LiveEventBus
        .get(TEST_BRC_KEY, String.class)
        .observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(LiveEventBusDemo.this, "receive massage: " + s, Toast.LENGTH_SHORT).show();
            }
        });
```

## 其他特性
### 订阅优先级
EventBus可以在订阅消息的时候设置订阅优先级，如：

```
@Subscribe(threadMode = ThreadMode.MAIN,priority = 1)
public void onMessageEvent(MessageEvent event) {
    Toast.makeText(this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
}
```
在同一个分发线程中，高优先级的订阅者会比低优先级的订阅者先得到事件的分发。在不同ThreadMode的订阅者中优先级是没有效果的。

Rxbus、LiveEventBus和SmartEventBus不支持设置订阅优先级。

### 取消事件分发
EventBus可以通过cancelEventDelivery方法去取消事件的分发,任何进一步的事件分发都会被取消，后续的订阅者不会再收到此类事件:

```
public void onEvent(MessageEvent event){
    EventBus.getDefault().cancelEventDelivery(event) ;
}
```
事件通常被高优先级的订阅者取消。取消只能在ThreadMode为PostThread时，也就是onEvent方法中取消。

LiveEventBus和SmartEventBus不支持取消事件分发。

### 延迟发送
LiveEventBus和SmartEventBus支持延迟发送消息：

```
LiveEventBus
    .get("key_name")
    .postDelay(value, 1000);
```

### 有序接收消息
EventBus可以通过订阅的时候设置成MAIN_ORDERED实现有序接收消息：

```
@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
public void onMessageEvent(MessageEvent event) {
    Toast.makeText(this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
}
```

LiveEventBus和SmartEventBus支持有序接收消息，需要在发送消息的时候调用postOrderly方法：

```
LiveEventBus
        .get(TEST_BRC_KEY)
        .postOrderly("msg from liveeventbus");
```

### 查看demo，请点击[Demo地址](https://github.com/JeremyLiao/SmartEventBus/tree/master/eventbus-demo)

### 参考文档
[EventBus详解](https://note.youdao.com/)

[Android消息总线的演进之路：用LiveDataBus替代RxBus、EventBus](https://tech.meituan.com/2018/07/26/android-livedatabus.html)
