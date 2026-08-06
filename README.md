# spring-javassist

![Java](https://img.shields.io/badge/Java-17-orange) ![License](https://img.shields.io/badge/License-Apache%202.0-blue)

[1. Project Overview](#1-project-overview) | [2. Features & Status](#2-features--status) | [3. Requirements & Compatibility](#3-requirements--compatibility) | [4. Architecture & Modules](#4-architecture--modules) | [5. Installation](#5-installation) | [6. Quick Start](#6-quick-start) | [7. Configuration](#7-configuration) | [8. Core Usage / API](#8-core-usage--api) | [9. Testing & Build](#9-testing--build) | [10. Versioning & Branches](#10-versioning--branches) | [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

`spring-javassist` generates Spring MVC controllers at runtime with Javassist (through the easy4j `javassist-plus` module). Instead of hand-writing a `@Controller` / `@RestController`, you describe the endpoint with a fluent builder (`EndpointApiCtClassBuilder`) — class annotations, request mappings, methods, fields and parameters — and the module produces a `CtClass` that can be turned into a loadable class wired for Spring MVC (servlet) or Spring WebFlux (reactive).

It is for dynamic/plugin-style applications that must expose new HTTP endpoints without recompiling — it is not a code generator tool and not a replacement for writing regular Spring controllers.

Typical scenarios:

| Scenario | What this module contributes |
|:---|:---|
| Runtime-generated `@RestController` | `EndpointApiCtClassBuilder` (annotations, mappings, methods, fields, parameters) |
| Reactive handler endpoints | `ReactiveHandlerCtClassBuilder` / `ReactiveHandler` |
| Swagger annotations on generated classes | `SwaggerApiUtils` |
| Typed parameter/response metadata | `MvcApiImplicitParam`, `MvcApiResponse`, `MvcParamFrom` |
| Base builder | `CtClassBuilder` (fields, setters/getters, method source snippets) |

## 2. Features & Status

Project status: pre-release development line (`1.0.x.*` snapshots); public API is still stabilizing until the first tagged release.

| Capability | Status | Notes |
|:---|:---|:---|
| Fluent controller builder | Stable | `EndpointApiCtClassBuilder` chains `controller`, `restController`, `api`, `requestMapping`, `autowired`, `newMethod`, `removeMethod` |
| Field/method source injection | Stable | `makeField(src)`, `makeMethod(src)`, `newField(type, name, value)`, `removeField`, `makeSetter` |
| Runtime annotation generation | Stable | `@Controller`, `@RestController`, `@RequestMapping` family, `@Autowired`, Swagger annotations |
| Method + parameter generation | Stable | `newMethod(name, path, method, contentType, ...)` and `newMethod(rtClass, MvcMethod, MvcBound, MvcParam...)` with `MvcApiImplicitParam` / `MvcApiResponse` metadata |
| Base class | Stable | `EndpointApi` carries an optional `InvocationHandler` for generated method dispatch |
| Reactive support | Stable | `ReactiveHandlerCtClassBuilder` with `monoMethod` for WebFlux-style handlers |
| Utils | Stable | `EndpointApiUtils`, `SwaggerApiUtils`, `RandomString` |

## 3. Requirements & Compatibility

| Requirement | Version |
|:---|:---|
| JDK | 17+ |
| Maven | 3.6+ |
| Javassist | via easy4j `javassist-plus` (same `1.0.x.*` line) |
| Spring Framework | 5.3.x (spring-webmvc, spring-webflux, spring-context) |
| Swagger annotations | springfox-core + swagger-annotations |

Version lines:

| Branch | JDK | Version pattern | Notes |
|:---|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` | Current line; Spring 5.x era |
| `feature/2.0.x` | 17 | `2.0.x.*` | Next line |
| `feature/3.0.x` | 21 | `3.0.x.*` | Future line |

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

The project is a single jar module. Packages under `org.springframework.javassist`:

| Package | Responsibility |
|:---|:---|
| `bytecode` | `EndpointApi`, `CtClassBuilder`, `EndpointApiCtClassBuilder`, `ReactiveHandler`, `ReactiveHandlerCtClassBuilder` |
| `bytecode.definition` | `MvcBound`, `MvcMapping`, `MvcMethod`, `MvcParam`, `MvcParamFrom`, `MvcApiImplicitParam`, `MvcApiResponse` |
| `utils` | `EndpointApiUtils`, `SwaggerApiUtils`, `RandomString` |
| `annotation` | `WebBound`, `ParamName` |

## 5. Installation

Artifacts are published to the easy4j private repository and GitHub Releases; the project is not yet on Maven Central.

Maven:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>spring-javassist</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:spring-javassist:2.0.x.x.20260630-SNAPSHOT'
```

## 6. Quick Start

Generate a controller `CtClass` and load it (based on the module's own test flow):

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

Expected result: a class named `com.example.DemoApi` is generated with a `uid` field and a `sayHello(String)` method, annotated as a Spring controller, loaded into the current class loader and instantiable.

## 7. Configuration

Programmatic library — there are no configuration files or property prefixes. Everything is configured through the builder DSL at runtime.

## 8. Core Usage / API

Define a typed request method with parameter metadata:

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

Remove a previously added method:

```java
ctClass = builder.removeMethod("sayHello", new MvcParam<>(String.class, "name")).build();
```

## 9. Testing & Build

Build and run tests:

```bash
./mvnw clean verify
```

- Test suite covers the endpoint builder (`EndpointApiCtClassBuilder_Test`), the reactive builder (`ReactiveHandlerCtClassBuilder_Test`) and the invocation handler (`EndpointApiInvocationHandler`).
- The build is configured with the JaCoCo Maven plugin: a coverage report is generated at `target/site/jacoco/index.html` and a rule checks the bundle line coverage against a 90% minimum (`haltOnFailure=false`, so the check reports but does not fail the build).
- The `central` Maven profile (`./mvnw -Pcentral deploy`) attaches GPG signatures, sources and Javadoc jars for publishing.

## 10. Versioning & Branches

Three parallel version lines are maintained:

| Branch | JDK | Version pattern |
|:---|:---|:---|
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

Maintenance policy: the `1.0.x` line is the actively developed line (current snapshot `2.0.x.x.20260630-SNAPSHOT`); `2.0.x` and `3.0.x` are forward porting lines targeting newer JDKs. Snapshots are built on demand; tagged releases are distributed via GitHub Releases.

## 11. Contributing & License

- Fork the repository and open a pull request; keep the `1.0.x` line compatible with JDK 8.
- Bug reports and feature requests are tracked via GitHub Issues.
- Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
