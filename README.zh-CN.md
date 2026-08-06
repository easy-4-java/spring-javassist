# spring-javassist

![Java](https://img.shields.io/badge/Java-17-orange) ![License](https://img.shields.io/badge/License-Apache%202.0-blue)

[English](./README.md) | [简体中文](./README.zh-CN.md)

[1. Project Overview](#1-project-overview) | [2. Features & Status](#2-features--status) | [3. Requirements & Compatibility](#3-requirements--compatibility) | [4. Architecture & Modules](#4-architecture--modules) | [5. Installation](#5-installation) | [6. Quick Start](#6-quick-start) | [7. Configuration](#7-configuration) | [8. Core Usage / API](#8-core-usage--api) | [9. Testing & Build](#9-testing--build) | [10. Versioning & Branches](#10-versioning--branches) | [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`spring-javassist` 使用 Javassist（经 easy4j `javassist-plus` 模块）在运行时生成 Spring MVC Controller。无需手写 `@Controller` / `@RestController`，只需用流式构建器（`EndpointApiCtClassBuilder`）描述端点——类注解、请求映射、方法、字段与参数——模块即可产出可转为可加载类（面向 Spring MVC Servlet 或 Spring WebFlux 响应式）的 `CtClass`。

它面向需要在不重新编译的情况下动态暴露 HTTP 端点的插件化应用——不是代码生成工具，也不能替代手写常规 Spring Controller。

典型场景：

| 场景 | 本模块提供的组件 |
|:---|:---|
| 运行时生成 `@RestController` | `EndpointApiCtClassBuilder`（注解、映射、方法、字段、参数） |
| 响应式处理器端点 | `ReactiveHandlerCtClassBuilder` / `ReactiveHandler` |
| 生成类上的 Swagger 注解 | `SwaggerApiUtils` |
| 类型化参数/响应元数据 | `MvcApiImplicitParam`、`MvcApiResponse`、`MvcParamFrom` |
| 基础构建器 | `CtClassBuilder`（字段、setter/getter、方法源码片段） |

## 2. Features & Status

项目状态：`1.0.x.*` 预发布开发线（快照版本）；在首个正式 Release 标签之前，公开 API 仍在稳定过程中。

| 能力 | 状态 | 说明 |
|:---|:---|:---|
| 流式 Controller 构建器 | 稳定 | `EndpointApiCtClassBuilder` 串联 `controller`、`restController`、`api`、`requestMapping`、`autowired`、`newMethod`、`removeMethod` |
| 字段/方法源码注入 | 稳定 | `makeField(src)`、`makeMethod(src)`、`newField(type, name, value)`、`removeField`、`makeSetter` |
| 运行时注解生成 | 稳定 | `@Controller`、`@RestController`、`@RequestMapping` 系列、`@Autowired`、Swagger 注解 |
| 方法与参数生成 | 稳定 | `newMethod(name, path, method, contentType, ...)` 与 `newMethod(rtClass, MvcMethod, MvcBound, MvcParam...)`，支持 `MvcApiImplicitParam` / `MvcApiResponse` 元数据 |
| 基类 | 稳定 | `EndpointApi` 可携带可选 `InvocationHandler` 用于生成方法的统一分发 |
| 响应式支持 | 稳定 | `ReactiveHandlerCtClassBuilder` 提供 `monoMethod` 支持 WebFlux 风格处理器 |
| 工具类 | 稳定 | `EndpointApiUtils`、`SwaggerApiUtils`、`RandomString` |

## 3. Requirements & Compatibility

| 要求 | 版本 |
|:---|:---|
| JDK | 17+ |
| Maven | 3.6+ |
| Javassist | 经 easy4j `javassist-plus`（同一 `1.0.x.*` 版本线） |
| Spring Framework | 5.3.x（spring-webmvc、spring-webflux、spring-context） |
| Swagger 注解 | springfox-core + swagger-annotations |

版本线：

| 分支 | JDK | 版本模式 | 说明 |
|:---|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` | 当前开发线；Spring 5.x 时代 |
| `feature/2.0.x` | 17 | `2.0.x.*` | 下一条版本线 |
| `feature/3.0.x` | 21 | `3.0.x.*` | 未来版本线 |

## 4. Architecture & Modules

```
Endpoint description (fluent DSL)
        |
        v
EndpointApiCtClassBuilder (Javassist)
  controller / requestMapping / newMethod / makeField
        |
        v
CtClass -> toClass() -> load(ClassLoader)
        |
        v
Generated Spring MVC Controller class
  (@Controller, @RequestMapping, Swagger @Api)
```

本工程为单 jar 模块，包位于 `org.springframework.javassist`：

| 包 | 职责 |
|:---|:---|
| `bytecode` | `EndpointApi`、`CtClassBuilder`、`EndpointApiCtClassBuilder`、`ReactiveHandler`、`ReactiveHandlerCtClassBuilder` |
| `bytecode.definition` | `MvcBound`、`MvcMapping`、`MvcMethod`、`MvcParam`、`MvcParamFrom`、`MvcApiImplicitParam`、`MvcApiResponse` |
| `utils` | `EndpointApiUtils`、`SwaggerApiUtils`、`RandomString` |
| `annotation` | `WebBound`、`ParamName` |

## 5. Installation

制品发布到 easy4j 私有仓库与 GitHub Releases，暂未发布 Maven Central。

Maven：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>spring-javassist</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:spring-javassist:2.0.x.x.20260630-SNAPSHOT'
```

## 6. Quick Start

生成 Controller 的 `CtClass` 并加载（基于模块自身测试流程）：

```java
import javassist.CtClass;
import org.springframework.javassist.bytecode.EndpointApiCtClassBuilder;

CtClass ctClass = new EndpointApiCtClassBuilder("com.example.DemoApi")
        .controller()
        .newField(String.class, "uid", "demo-uid")
        .makeMethod("public String sayHello(String name) { return \"hello \" + name; }")
        .build();

Class<?> clazz = ctClass.toClass();
Object api = clazz.newInstance();
```

预期结果：生成名为 `com.example.DemoApi` 的类，含 `uid` 字段与 `sayHello(String)` 方法，带 Spring Controller 注解，已加载到当前类加载器并可直接实例化。

## 7. Configuration

编程式库——无配置文件与属性前缀，所有配置均在运行时通过构建器 DSL 完成。

## 8. Core Usage / API

带参数元数据的类型化请求方法：

```java
import org.springframework.javassist.bytecode.EndpointApiCtClassBuilder;
import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.bytecode.definition.MvcParam;
import org.springframework.web.bind.annotation.RequestMethod;

CtClass ctClass = new EndpointApiCtClassBuilder("com.example.DemoApi2")
        .restController()
        .bind(new MvcBound("uid", "{\"name\":\"demo\"}"))
        .newMethod("hello", "/hello/{name}", RequestMethod.POST, "application/json",
                new MvcParam<>(String.class, "name"))
        .build();
```

移除已添加的方法：

```java
ctClass = builder.removeMethod("sayHello", new MvcParam<>(String.class, "name")).build();
```

## 9. Testing & Build

构建与测试：

```bash
./mvnw clean verify
```

- 测试套件覆盖端点构建器（`EndpointApiCtClassBuilder_Test`）、响应式构建器（`ReactiveHandlerCtClassBuilder_Test`）与调用处理器（`EndpointApiInvocationHandler`）；
- 构建配置了 JaCoCo Maven 插件：覆盖率报告生成于 `target/site/jacoco/index.html`，并配置了 BUNDLE 行覆盖率 90% 的校验规则（`haltOnFailure=false`，即只报告不阻断构建）；
- `central` Maven Profile（`./mvnw -Pcentral deploy`）附加 GPG 签名、源码包与 Javadoc 包用于发布。

## 10. Versioning & Branches

维护三条并行版本线：

| 分支 | JDK | 版本模式 |
|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

维护策略：`1.0.x` 为当前活跃开发线（当前快照 `2.0.x.x.20260630-SNAPSHOT`）；`2.0.x` 与 `3.0.x` 为面向更新 JDK 的前向移植线。快照按需构建，正式 Release 通过 GitHub Releases 分发。

## 11. Contributing & License

- Fork 仓库并提交 Pull Request；`1.0.x` 版本线保持 JDK 8 兼容；
- Bug 反馈与功能建议通过 GitHub Issues 跟踪；
- 基于 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源。
