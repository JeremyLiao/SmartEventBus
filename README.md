# SmartEventBus
![license](https://img.shields.io/github/license/JeremyLiao/SmartEventBus.svg) [![version](https://img.shields.io/badge/JCenter-v0.0.1-blue.svg)](https://mvnrepository.com/artifact/com.jeremyliao/)

SmartEventBus是一个Android平台的消息总线框架，这是一款非常smart的消息总线框架，能让你定制自己的消息总线。

![logo](/images/logo.png)

## 常用消息总线对比

消息总线 | 延迟发送 | 有序接收消息 | Sticky | 生命周期感知 | 跨进程/APP | Customize（定制能力） | 线程分发
---|---|---|---|---|---|----|---
EventBus | :x: | :white_check_mark: | :white_check_mark: | :x: | :x: | :x: | :white_check_mark:
RxBus | :x: | :x: | :white_check_mark: | :x: | :x: | :x: | :white_check_mark:
LiveEventBus | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x: | :x:
SmartEventBus | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :x:

#### 想了解更多？请点击：[全面了解Android消息总线](https://github.com/JeremyLiao/SmartEventBus/blob/master/docs/bus_all.md)

## SmartEventBus的使用步骤
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

#### 2. 配置你的消息总线（可选）

```
@SmartEventConfig(packageName = "yourPackageName", busName = "YourClassName")
public class BaseEventConfig {
}
```
- 配置之后，会生成定制消息总线：yourPackageName.YourClassName
- 也可以不配置，生成默认命名DefaultSmartEventBus的消息总线

#### 3. 订阅和发送消息

```
MySmartEventBus
        .event1()
        .observe(this, new Observer<MessageEvent>() {
            @Override
            public void onChanged(@Nullable MessageEvent event) {
            }
        });
```

```
MySmartEventBus
        .event1()
        .post(new MessageEvent("msg from smarteventbus"));
```

#### 4. 添加依赖和注解处理器

```
implementation 'com.jeremyliao:smart-event-bus-base:0.0.1'
annotationProcessor 'com.jeremyliao:smart-event-bus-compiler:0.0.2'
```

## 混淆规则

```
-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
```

## SmartEventBus的前身——[invoking-message](/docs/IM_README.md)

## 其他
