/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.javassist.utils;

import java.lang.reflect.InvocationHandler;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.javassist.annotation.ParamName;
import org.springframework.javassist.annotation.WebBound;
import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.bytecode.definition.MvcMapping;
import org.springframework.javassist.bytecode.definition.MvcMethod;
import org.springframework.javassist.bytecode.definition.MvcParam;
import org.springframework.javassist.bytecode.definition.MvcParamFrom;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.easy4j.javassist.bytecode.CtAnnotationBuilder;
import io.github.easy4j.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * https://www.cnblogs.com/sunfie/p/5154246.html
 * @author [@Loong Wan](https://github.com/loong10k)
 */
public class EndpointApiUtils {
	
	/**
	 * 构造 @Configuration 注解
	 * @param constPool {@link ConstPool} instance
	 * @param name bean 名称
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotConfiguration(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(Configuration.class, constPool).addStringMember("value", name).build();
	}
	
	/**
	 * 构造 @Bean 注解
	 * @param constPool {@link ConstPool} instance
	 * @param name 				: name of bean
	 * @param autowire 			: autowire type
	 * @param initMethod 		: method name of init
	 * @param destroyMethod 	: method name of destroy
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotBean(ConstPool constPool, String[] name, Autowire autowire
			,String initMethod,String destroyMethod) {
		return CtAnnotationBuilder.create(Bean.class, constPool).addStringMember("name", name)
				.addEnumMember("autowire", autowire)
				.addStringMember("initMethod", initMethod)
				.addStringMember("destroyMethod", destroyMethod).build();
	}

	/**
	 * 构造 @Lazy 注解
	 * @param constPool {@link ConstPool} instance
	 * @param lazy 				: whither lazy load class
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotLazy(ConstPool constPool, boolean lazy) {
		return CtAnnotationBuilder.create(Lazy.class, constPool).addBooleanMember("value", lazy).build();
	}

	/**
	 * 构造 @Scope 注解
	 * @param constPool {@link ConstPool} instance
	 * @param scopeName 			: the name of scope
	 * @param proxyMode 			: {@link ScopedProxyMode} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotScope(ConstPool constPool, String scopeName, ScopedProxyMode proxyMode) {
		return CtAnnotationBuilder.create(Scope.class, constPool).addStringMember("scopeName", scopeName)
				.addEnumMember("proxyMode", proxyMode).build();
	}
	
	/**
	 * 构造 @Controller 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute values of @Controller
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotController(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(Controller.class, constPool).addStringMember("value", name).build();
	}
	
	/**
	 * 构造 @RestController 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute values of @RestController
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotRestController(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(RestController.class, constPool).addStringMember("value", name).build();
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param mapping 	: {@link MvcMapping} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotRequestMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, RequestMapping.class, mapping);
	}

	/**
	 * 构造 @GetMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param mapping 	: {@link MvcMapping} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotGetMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, GetMapping.class, mapping);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param mapping 	: {@link MvcMapping} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotPostMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PostMapping.class, mapping);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param mapping 	: {@link MvcMapping} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotPutMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PutMapping.class, mapping);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param mapping 	: {@link MvcMapping} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotDeleteMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, DeleteMapping.class, mapping);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param mapping 	: {@link MvcMapping} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotPatchMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PatchMapping.class, mapping);
	}
	
	/**
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
	 * @param constPool 	: {@link ConstPool} instance
	 * @param annotation	: The class of {@link java.lang.annotation.Annotation}
	 * @param mapping 		: {@link MvcMapping} instance
	 * @return {@link Annotation} instance
	 */
	private static Annotation annotHttpMethod(ConstPool constPool, 
			Class<? extends java.lang.annotation.Annotation> annotation,
			MvcMapping mapping) {

		String name = StringUtils.hasText(mapping.getName()) ? mapping.getName() : "";
		String[] path = ArrayUtils.isNotEmpty(mapping.getPath()) ? mapping.getPath() : new String[] {};
		String[] params = ArrayUtils.isNotEmpty(mapping.getParams()) ? mapping.getParams() : new String[] {};
		String[] headers = ArrayUtils.isNotEmpty(mapping.getHeaders()) ? mapping.getHeaders() : new String[] {};
		String[] consumes = ArrayUtils.isNotEmpty(mapping.getConsumes()) ? mapping.getConsumes() : new String[] {};
		String[] produces = ArrayUtils.isNotEmpty(mapping.getProduces()) ? mapping.getProduces() : new String[] {};
		
		CtAnnotationBuilder builder = CtAnnotationBuilder.create(annotation, constPool)
				.addStringMember("name", name)
				.addStringMember("value", path)
				.addStringMember("path", path)
				.addStringMember("params", params)
				.addStringMember("headers", headers)
				.addStringMember("consumes", consumes)
				.addStringMember("produces", produces);
		if(ArrayUtils.isNotEmpty(mapping.getMethod())) {
			builder = builder.addEnumMember("method", mapping.getMethod());
		}
		return builder.build();
	}
	
	/**
	 * 构造 @RequestMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute value of @RequestMapping
	 * @param path 		: The path attribute values of @RequestMapping
	 * @param method 	: The method attribute values of @RequestMapping
	 * @param params	: The param attribute values of @RequestMapping
	 * @param headers	: The header attribute values of @RequestMapping
	 * @param consumes	: The consume attribute values of @RequestMapping
	 * @param produces	: The produce attribute values of @RequestMapping
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotRequestMapping(ConstPool constPool, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, RequestMapping.class, name, path, method, params, headers, consumes, produces);
	}

	/**
	 * 构造 @GetMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute value of @GetMapping
	 * @param path 		: The path attribute values of @GetMapping
	 * @param params	: The param attribute values of @GetMapping
	 * @param headers	: The header attribute values of @GetMapping
	 * @param consumes	: The consume attribute values of @GetMapping
	 * @param produces	: The produce attribute values of @GetMapping
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotGetMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, GetMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PostMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute value of @PostMapping
	 * @param path 		: The path attribute values of @PostMapping
	 * @param params	: The param attribute values of @PostMapping
	 * @param headers	: The header attribute values of @PostMapping
	 * @param consumes	: The consume attribute values of @PostMapping
	 * @param produces	: The produce attribute values of @PostMapping
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotPostMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PostMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PutMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute value of @PutMapping
	 * @param path 		: The path attribute values of @PutMapping
	 * @param params	: The param attribute values of @PutMapping
	 * @param headers	: The header attribute values of @PutMapping
	 * @param consumes	: The consume attribute values of @PutMapping
	 * @param produces	: The produce attribute values of @PutMapping
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotPutMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PutMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @DeleteMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute value of @DeleteMapping
	 * @param path 		: The path attribute values of @DeleteMapping
	 * @param params	: The param attribute values of @DeleteMapping
	 * @param headers	: The header attribute values of @DeleteMapping
	 * @param consumes	: The consume attribute values of @DeleteMapping
	 * @param produces	: The produce attribute values of @DeleteMapping
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotDeleteMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, DeleteMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @PatchMapping 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute value of @PatchMapping
	 * @param path 		: The path attribute values of @PatchMapping
	 * @param params	: The param attribute values of @PatchMapping
	 * @param headers	: The header attribute values of @PatchMapping
	 * @param consumes	: The consume attribute values of @PatchMapping
	 * @param produces	: The produce attribute values of @PatchMapping
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotPatchMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PatchMapping.class, name, path, null, params, headers, consumes, produces);
	}
	
	/**
	 * 构造 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping | 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param name 		: The name attribute value of annotation
	 * @param path 		: The path attribute values of annotation
	 * @param method 	: The method attribute values of annotation
	 * @param params	: The param attribute values of annotation
	 * @param headers	: The header attribute values of annotation
	 * @param consumes	: The consume attribute values of annotation
	 * @param produces	: The produce attribute values of annotation
	 * @return {@link Annotation} instance
	 */
	private static Annotation annotHttpMethod(ConstPool constPool, Class<? extends java.lang.annotation.Annotation> annotation,
			String name, String[] path,	RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {

		name = StringUtils.hasText(name) ? name : "";
		path = ArrayUtils.isNotEmpty(path) ? path : new String[] {};
		params = ArrayUtils.isNotEmpty(params) ? params : new String[] {};
		headers = ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
		consumes = ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
		produces = ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
		CtAnnotationBuilder builder = CtAnnotationBuilder.create(annotation, constPool)
				.addStringMember("name", name)
				.addStringMember("path", path)
				.addStringMember("params", params)
				.addStringMember("headers", headers)
				.addStringMember("consumes", consumes)
				.addStringMember("produces", produces);
		if(ArrayUtils.isNotEmpty(method)) {
			builder = builder.addEnumMember("method", method);
		}
		return builder.build();

	}
	
	public static CtClass makeClass(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {

		CtClass declaring = pool.getOrNull(classname);
		if (null == declaring) {
			declaring = pool.makeClass(classname);
		}
		
		/*
		 * 当 ClassPool.doPruning=true的时候，Javassist 在CtClass object被冻结时，会释放存储在ClassPool对应的数据。
		 * 这样做可以减少javassist的内存消耗。默认情况ClassPool.doPruning=false。
		 */
		declaring.stopPruning(true);

		return declaring;
	}
	
	public static CtConstructor defaultConstructor(CtClass declaring) throws CannotCompileException   {
		// 默认添加无参构造器  
		CtConstructor cons = new CtConstructor(null, declaring);  
		cons.setBody("{}");  
    	return cons;
	}
	
	public static CtConstructor makeConstructor(ClassPool pool, CtClass declaring) throws NotFoundException, CannotCompileException  {

		// 添加有参构造器，注入回调接口
    	CtClass[] parameters = new CtClass[] {pool.get(InvocationHandler.class.getName())};
    	CtClass[] exceptions = new CtClass[] { pool.get("java.lang.Exception") };
    	return CtNewConstructor.make(parameters, exceptions, "{super($1);}", declaring);
    	
	}

	public static CtClass makeInterface(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {

		CtClass declaring = pool.getOrNull(classname);
		if (null == declaring) {
			declaring = pool.makeInterface(classname);
		}

		// 当 ClassPool.doPruning=true的时候，Javassist 在CtClass
		// object被冻结时，会释放存储在ClassPool对应的数据。这样做可以减少javassist的内存消耗。默认情况ClassPool.doPruning=false。
		declaring.stopPruning(true);

		return declaring;
	}
	
	public static <T> void setSuperclass(ClassPool pool, CtClass declaring, Class<T> clazz)
			throws Exception {

		/* 获得 JaxwsHandler 类作为动态类的父类 */
		CtClass superclass = pool.get(clazz.getName());
		declaring.setSuperclass(superclass);

	}
	
	public static CtClass[] makeParams(ClassPool pool, MvcParam<?>... params) throws NotFoundException {
		// 无参
		if(params == null || params.length == 0) {
			return null;
		}
		// 方法参数
		CtClass[] parameters = new CtClass[params.length];
		for(int i = 0;i < params.length; i++) {
			parameters[i] = pool.get(params[i].getType().getName());
		}

		return parameters;
	}
	
	/**
	 * 为方法添加  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 * @param ctMethod		: The {@link CtMethod ctMethod} instance
	 * @param constPool		: The {@link ConstPool constPool} instance
	 * @param path			: The path attribute value of annotation
	 * @param method		: The {@link RequestMethod method} instance
	 * @param contentType	: The contentType attribute value of annotation
	 * @param bound			: The {@link MvcBound bound} instance
	 * @param params		: The {@link MvcParam param} values
	 */
	public static void methodAnnotations(CtMethod ctMethod, ConstPool constPool, String path, RequestMethod method,
			String contentType, MvcBound bound, MvcParam<?>[] params) {

		// 获取方法属性对象
        AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        
        // 添加 @WebBound 注解
        if (bound != null) {
        	methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }
        
        // 添加 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
        methodAttr.addAnnotation(EndpointApiUtils.annotMethodMapping(constPool, path, method, contentType));
        
        methodInfo.addAttribute(methodAttr);
        
        // 添加 @WebParam 参数注解
        if(params != null && params.length > 0) {
        	
        	ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays = EndpointApiUtils.annotParams(constPool, params);
            parameterAtrribute.setAnnotations(paramArrays);
            methodInfo.addAttribute(parameterAtrribute);
            
        }
        
	}
	
	/**
	 * 为方法添加   @WebBound 注解
	 * @param ctMethod		: The {@link CtMethod ctMethod} instance
	 * @param constPool		: The {@link ConstPool constPool} instance
	 * @param bound			: The {@link MvcBound bound} instance
	 */
	public static void methodBound(CtMethod ctMethod, ConstPool constPool, MvcBound bound) {
		
		// 获取方法属性对象
        AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        // 添加 @WebBound 注解
        if (bound != null) {
        	methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }
         
        methodInfo.addAttribute(methodAttr);
        
	}
	
	/**
	 * 为方法添加  @WebBound | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 * @param ctMethod		: The {@link CtMethod ctMethod} instance
	 * @param constPool		: The {@link ConstPool constPool} instance
	 * @param method		: The {@link RequestMethod method} instance
	 * @param bound			: The {@link MvcBound bound} instance
	 * @param params		: The {@link MvcParam param} values
	 */
	public static void methodAnnotations(CtMethod ctMethod, ConstPool constPool, MvcMethod method, MvcBound bound, MvcParam<?>... params) {
		
		// 获取方法属性对象
        AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        // 添加 @WebBound 注解
        if (bound != null) {
        	methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }
        
        // 添加 @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
        methodAttr.addAnnotation(EndpointApiUtils.annotMethodMapping(constPool, method));
        
        // 添加 @ResponseBody 注解
        if(method.isResponseBody()) {
        	Annotation annot = new Annotation(ResponseBody.class.getName(), constPool);
        	methodAttr.addAnnotation(annot);
        }
        
        methodInfo.addAttribute(methodAttr);
        
        // 添加 @WebParam 参数注解
        if(params != null && params.length > 0) {
        	
        	ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays = EndpointApiUtils.annotParams(constPool, params);
            parameterAtrribute.setAnnotations(paramArrays);
            methodInfo.addAttribute(parameterAtrribute);
            
        }
        
	}
	
	/**
	 * 设置方法体
	 * @param ctMethod		: The {@link CtMethod ctMethod} instance
	 * @param method		: The {@link RequestMethod method} instance
	 * @throws CannotCompileException if can’t conplile
	 */
	public static void methodBody(CtMethod ctMethod, MvcMethod method) throws CannotCompileException {
        methodBody(ctMethod, method.getName());
	}
	
	/**
	 * 设置方法体
	 * @param ctMethod		: The {@link CtMethod ctMethod} instance
	 * @param methodName	: The name of method
	 * @throws CannotCompileException if can’t conplile 
	 */
	public static void methodBody(CtMethod ctMethod, String methodName) throws CannotCompileException {
		
		// 构造方法体
		StringBuilder body = new StringBuilder(); 
        body.append("{\n");
            //body.append("System.out.println(123456);");
            //body.append("System.out.println(getHandler());");
        	body.append("if(getHandler() != null){\n");
        		body.append("Method method = this.getClass().getDeclaredMethod(\"" + methodName + "\", $sig);");
        		body.append("return ($r)getHandler().invoke($0, method, $args);");
        	body.append("}\n"); 
	        body.append("return null;\n");
        body.append("}"); 
        // 将方法的内容设置为要写入的代码，当方法被 abstract修饰时，该修饰符被移除。
        ctMethod.setBody(body.toString());
        
	}
	
	/**
	 * 设置方法异常捕获逻辑
	 * @param pool			: The {@link ClassPool pool} instance
	 * @param ctMethod		: The {@link CtMethod ctMethod} instance
	 * @throws NotFoundException  if not found
	 * @throws CannotCompileException if can't compile
	 */
	public static void methodCatch(ClassPool pool, CtMethod ctMethod) throws NotFoundException, CannotCompileException {
		
		// 构造异常处理逻辑
        CtClass etype = pool.get("java.lang.Exception");
        ctMethod.addCatch("{ System.out.println($e); throw $e; }", etype);
        
	}
	
	/**
	 * 构造 @WebBound 注解
	 * @param constPool		: The {@link ConstPool constPool} instance
	 * @param bound			: The {@link MvcBound bound} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotWebBound(ConstPool constPool, MvcBound bound) {

		CtAnnotationBuilder builder = CtAnnotationBuilder.create(WebBound.class, constPool).
			addStringMember("uid", bound.getUid());
		if (StringUtils.hasText(bound.getJson())) {
			builder.addStringMember("json", bound.getJson());
        }
		return builder.build();
		
	}
	
	/**
	 * 根据参数 构造   @RequestMapping | @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 * @param constPool		: The {@link ConstPool constPool} instance
	 * @param method		: The {@link MvcMethod method} instance
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotMethodMapping(ConstPool constPool, MvcMethod method) {
		
		Annotation annot = null;
		// 多种支持请求方法
		if(method.getMethod().length > 1) {
			annot = annotRequestMapping(constPool, method.getName(), method.getPath(), method.getMethod(), 
					method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			return annot;
		}
		// 仅支持一种请求方式
		switch (method.getMethod()[0]) {
			case GET:{
				annot = annotGetMapping(constPool, method.getName(), method.getPath(),  
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case POST:{
				annot = annotPostMapping(constPool, method.getName(), method.getPath(),  
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case PUT:{
				annot = annotPutMapping(constPool, method.getName(), method.getPath(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case DELETE:{
				annot = annotDeleteMapping(constPool, method.getName(), method.getPath(), 
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			case PATCH:{
				annot = annotPatchMapping(constPool, method.getName(), method.getPath(),  
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
			default:{
				annot = annotGetMapping(constPool, method.getName(), method.getPath(),  
						method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			};break;
		}
		
		return annot;
	}
	
	/**
	 * 根据参数 构造  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
	 * @param constPool		: The {@link ConstPool constPool} instance
	 * @param path			: The path attribute value of annotation
	 * @param method		: The {@link RequestMethod method} instance
	 * @param contentType	: The contentType attribute value of annotation
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotMethodMapping(ConstPool constPool, String path, RequestMethod method,
			String contentType) {

		Annotation annot = null;
		// 仅支持一种请求方式
		switch (method) {
			case GET: {
				annot = CtAnnotationBuilder.create(GetMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			case POST: {
				annot = CtAnnotationBuilder.create(PostMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			case PUT: {
				annot = CtAnnotationBuilder.create(PutMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			case DELETE: {
				annot = CtAnnotationBuilder.create(DeleteMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			case PATCH: {
				annot = CtAnnotationBuilder.create(PatchMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
			default: {
				annot = CtAnnotationBuilder.create(GetMapping.class, constPool).addStringMember("path", path)
						.addEnumMember("method", method).addStringMember("produces", new String[] { contentType }).build();
			};break;
		}

		return annot;
	}
	
	/**
	 * 构造 @CookieValue | @MatrixVariable | @PathVariable | @RequestAttribute | @RequestBody | @RequestHeader
	 *  | @RequestParam | @RequestPart 参数注解
	 * @param constPool		: The {@link ConstPool constPool} instance
	 * @param params		: The params
	 * @return {@link Annotation} Array
	 */
	public static Annotation[][] annotParams(ConstPool constPool, MvcParam<?>... params) {

		// 添加参数注解
		if (params != null && params.length > 0) {
			
			Annotation[][] paramArrays = new Annotation[params.length][2];
			
			Annotation paramAnnot = null;
			boolean defAnnot = false;
			for (int i = 0; i < params.length; i++) {
				paramAnnot = null;
				defAnnot = false;
				switch (params[i].getFrom()) {
					case COOKIE:{
						paramAnnot = new Annotation(CookieValue.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
					case MATRIX:{
						paramAnnot = new Annotation(MatrixVariable.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
					case PATH:{
						paramAnnot = new Annotation(PathVariable.class.getName(), constPool);
					};break;
					case ATTR:{
						paramAnnot = new Annotation(RequestAttribute.class.getName(), constPool);
					};break;
					case BODY:{
						paramAnnot = new Annotation(RequestBody.class.getName(), constPool);
					};break;
					case HEADER:{
						paramAnnot = new Annotation(RequestHeader.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
					case PARAM:{
						paramAnnot = new Annotation(RequestParam.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
					case PART:{
						paramAnnot = new Annotation(RequestPart.class.getName(), constPool);
					};break;
					default:{
						paramAnnot = new Annotation(RequestParam.class.getName(), constPool);
						defAnnot = StringUtils.hasText(params[i].getDef());
					};break;
				}
				
				if(MvcParamFrom.BODY.compareTo(params[i].getFrom()) != 0){
					paramAnnot.addMemberValue("name", new StringMemberValue(params[i].getName(), constPool));
					if(defAnnot) {
						paramAnnot.addMemberValue("defaultValue", new StringMemberValue(StringUtils.trimWhitespace(params[i].getDef()), constPool));
					}
				}
				paramAnnot.addMemberValue("name", new StringMemberValue(params[i].getName(), constPool));
				
				
				
				paramArrays[i][0] = paramAnnot;
				
				// 增加参数名称注解,方便业务使用
				Annotation nameAnnot = new Annotation(ParamName.class.getName(), constPool);
				nameAnnot.addMemberValue("name", new StringMemberValue(params[i].getName(), constPool));
				
				paramArrays[i][1] = nameAnnot;
				
			}
			
			return paramArrays;

		}
		return null;
	}
	
}