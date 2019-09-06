# invoking-message【该项目不再维护】
![license](https://img.shields.io/github/license/JeremyLiao/invoking-message.svg) [![version](https://img.shields.io/badge/JCenter-v1.1.0-blue.svg)](https://mvnrepository.com/artifact/com.jeremyliao/live-event-bus)

invoking-message是一款Android平台消息总线框架，基于[LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)实现。它颠覆了传统消息总线定义和使用的方式，通过链式的方法调用发送和接收消息，使用更简单。

## invoking-message的特点
1. 基于[LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)实现，具有LiveEventBus所有功能。
2. 通过链式的方法调用发送和接收消息，使用起来更加简单。
3. 只能使用预定义的消息，加强约束。
4. 定义的消息更容易查找和溯源。
5. 适合在组件化的架构中用来实现组件间的通信。

**所以，无论你之前使用哪种消息总线框架，如果你曾经遇到如下问题，请尝试invoking-message，它能解决你的痛点：**
1. 随意定义消息，不易管理，一个消息想知道谁是发送者，谁又是观察者，只能通过Ctrl+F，非常麻烦，如果消息分布在不同组件中，查找起来就更加麻烦了。
2. 消息定义越多，就越不清楚到底有哪些消息，写代码的时候是不是经常问自己：“哎，我该发送个什么消息来着？”。
3. 编写代码的时候一不小心把消息写错了，又不会报错，怎么收不到消息了，查找问题好麻烦。

## invoking-message的使用步骤
#### 1. 定义消息

```
@InvokingEventsDefine()
public class DemoEvents {

    public static final String EVENT1 = "event1";

    @EventType(String.class)
    public static final String EVENT2 = "event2";

    @EventType(TestEventBean.class)
    public static final String EVENT3 = "event3";
}
```
**关键点：**
1. 定义消息的类需加上注解@InvokingEventsDefine
2. 消息的名字需要定义成**public static final String**类型
3. 用注解@EventType定义消息体的类型，如不使用，默认为Object，@EventType支持自定义类型

#### 2. 使用compiler
定义了消息的Module，需要在build.gradle中使用compiler处理注解：

```
annotationProcessor 'com.jeremyliao:invoking-message-compiler:1.1.0'
```
**关键点：**
1. 注解处理器会生成以“EventsDefineAs”开头的接口文件
2. 编写代码的时候，如果记不得定义了哪些消息，请查找以“EventsDefineAs”开头的类

#### 3. 定义subscribers

```
InvokingMessage
        .get()
        .as(EventsDefineAsDemoEvents.class)
        .EVENT2()
        .observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
```
**关键点：**
1. 定义subscribers的整个过程为链式调用
2. 支持更多定义subscribers的方式，与[LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)一致

#### 4. 发送消息

```
InvokingMessage
        .get()
        .as(EventsDefineAsDemoEvents.class)
        .EVENT2()
        .post("test");
```
**关键点：**
1. 发送消息的整个过程为链式调用
2. 支持更多发送消息的方式，与[LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)一致

## 添加依赖
Via Gradle:

```
implementation 'com.jeremyliao:invoking-message-core:1.1.0'
```

## 混淆规则

```
-dontwarn com.jeremyliao.im.**
-keep class com.jeremyliao.im.** { *; }
-keep class com.jeremyliao.liveeventbus.** { *; }
```
