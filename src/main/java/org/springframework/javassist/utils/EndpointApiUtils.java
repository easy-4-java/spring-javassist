/*
 * Copyright (c) 2018-present, easy-4-java (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * Utility facade used by every {@code *CtClassBuilder} to materialise
 * Spring MVC annotations, generated methods, parameter bindings and
 * {@code @WebBound} payloads on a Javassist-generated class.
 *
 * <p>The methods in this class can be grouped into three categories:</p>
 * <ol>
 *     <li>Annotation factories (prefixed with {@code annot*}) that produce a
 *         {@link javassist.bytecode.annotation.Annotation} instance ready to be
 *         attached to a class, method or parameter.</li>
 *     <li>Method-body helpers ({@link #methodBody}, {@link #methodCatch},
 *         {@link #methodAnnotations}, {@link #methodBound}) that wire the
 *         generated method together with the right {@code @WebBound} and
 *         {@code @*Mapping} annotations.</li>
 *     <li>Class-loading helpers ({@link #makeClass}, {@link #makeInterface},
 *         {@link #makeConstructor}, {@link #defaultConstructor},
 *         {@link #setSuperclass}, {@link #makeParams}) that encapsulate the
 *         Javassist quirks (cache pruning, constructors, ...).</li>
 * </ol>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see SwaggerApiUtils
 * @see org.springframework.javassist.bytecode.CtClassBuilder
 */
public class EndpointApiUtils {

	/**
	 * Builds a {@link Configuration} annotation carrying a single string
	 * {@code value}.
	 *
	 * @param constPool the constant pool of the target class, never {@code null}
	 * @param name      the {@code value} to embed in the annotation
	 * @return the annotation ready to be added to a class
	 * @since 3.0.0
	 */
	public static Annotation annotConfiguration(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(Configuration.class, constPool).addStringMember("value", name).build();
	}

	/**
	 * Builds a {@link Bean} annotation carrying every Spring attribute.
	 *
	 * @param constPool      the constant pool of the target class, never {@code null}
	 * @param name           the bean name(s)
	 * @param autowire       the autowire mode
	 * @param initMethod     the init method name
	 * @param destroyMethod  the destroy method name
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotBean(ConstPool constPool, String[] name, Autowire autowire
			,String initMethod,String destroyMethod) {
		return CtAnnotationBuilder.create(Bean.class, constPool).addStringMember("name", name)
				.addEnumMember("autowire", autowire)
				.addStringMember("initMethod", initMethod)
				.addStringMember("destroyMethod", destroyMethod).build();
	}

	/**
	 * Builds a {@link Lazy} annotation.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param lazy      whether the bean should be lazily initialised
	 * @return the annotation ready to be added to a class or method
	 * @since 3.0.0
	 */
	public static Annotation annotLazy(ConstPool constPool, boolean lazy) {
		return CtAnnotationBuilder.create(Lazy.class, constPool).addBooleanMember("value", lazy).build();
	}

	/**
	 * Builds a {@link Scope} annotation with the given scope name and proxy
	 * mode.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param scopeName the name of the scope
	 * @param proxyMode the proxy mode
	 * @return the annotation ready to be added to a class or method
	 * @since 3.0.0
	 */
	public static Annotation annotScope(ConstPool constPool, String scopeName, ScopedProxyMode proxyMode) {
		return CtAnnotationBuilder.create(Scope.class, constPool).addStringMember("scopeName", scopeName)
				.addEnumMember("proxyMode", proxyMode).build();
	}

	/**
	 * Builds a {@link Controller} annotation.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the controller name
	 * @return the annotation ready to be added to a class
	 * @since 3.0.0
	 */
	public static Annotation annotController(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(Controller.class, constPool).addStringMember("value", name).build();
	}

	/**
	 * Builds a {@link RestController} annotation.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the controller name
	 * @return the annotation ready to be added to a class
	 * @since 3.0.0
	 */
	public static Annotation annotRestController(ConstPool constPool, String name) {
		return CtAnnotationBuilder.create(RestController.class, constPool).addStringMember("value", name).build();
	}

	/**
	 * Builds a {@link RequestMapping} annotation from a high-level
	 * {@link MvcMapping} descriptor.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param mapping   the mapping descriptor
	 * @return the annotation ready to be added to a class or method
	 * @since 3.0.0
	 */
	public static Annotation annotRequestMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, RequestMapping.class, mapping);
	}

	/**
	 * Builds a {@link GetMapping} annotation from a high-level
	 * {@link MvcMapping} descriptor.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param mapping   the mapping descriptor
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotGetMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, GetMapping.class, mapping);
	}

	/**
	 * Builds a {@link PostMapping} annotation from a high-level
	 * {@link MvcMapping} descriptor.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param mapping   the mapping descriptor
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotPostMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PostMapping.class, mapping);
	}

	/**
	 * Builds a {@link PutMapping} annotation from a high-level
	 * {@link MvcMapping} descriptor.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param mapping   the mapping descriptor
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotPutMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PutMapping.class, mapping);
	}

	/**
	 * Builds a {@link DeleteMapping} annotation from a high-level
	 * {@link MvcMapping} descriptor.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param mapping   the mapping descriptor
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotDeleteMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, DeleteMapping.class, mapping);
	}

	/**
	 * Builds a {@link PatchMapping} annotation from a high-level
	 * {@link MvcMapping} descriptor.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param mapping   the mapping descriptor
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotPatchMapping(ConstPool constPool, MvcMapping mapping) {
		return annotHttpMethod(constPool, PatchMapping.class, mapping);
	}

	/**
	 * Builds any of {@link RequestMapping}, {@link GetMapping},
	 * {@link PostMapping}, {@link PutMapping}, {@link DeleteMapping} or
	 * {@link PatchMapping} from a high-level descriptor.
	 *
	 * @param constPool  the constant pool, never {@code null}
	 * @param annotation the Spring annotation class to emit
	 * @param mapping    the mapping descriptor
	 * @return the annotation ready to be added to a class or method
	 * @since 3.0.0
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
	 * Builds a {@link RequestMapping} annotation from raw attribute values.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the name attribute
	 * @param path      the path attribute
	 * @param method    the HTTP method attribute
	 * @param params    the params attribute
	 * @param headers   the headers attribute
	 * @param consumes  the consumes attribute
	 * @param produces  the produces attribute
	 * @return the annotation ready to be added to a class or method
	 * @since 3.0.0
	 */
	public static Annotation annotRequestMapping(ConstPool constPool, String name, String[] path,
			RequestMethod[] method, String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, RequestMapping.class, name, path, method, params, headers, consumes, produces);
	}

	/**
	 * Builds a {@link GetMapping} annotation from raw attribute values.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the name attribute
	 * @param path      the path attribute
	 * @param params    the params attribute
	 * @param headers   the headers attribute
	 * @param consumes  the consumes attribute
	 * @param produces  the produces attribute
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotGetMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, GetMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Builds a {@link PostMapping} annotation from raw attribute values.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the name attribute
	 * @param path      the path attribute
	 * @param params    the params attribute
	 * @param headers   the headers attribute
	 * @param consumes  the consumes attribute
	 * @param produces  the produces attribute
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotPostMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PostMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Builds a {@link PutMapping} annotation from raw attribute values.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the name attribute
	 * @param path      the path attribute
	 * @param params    the params attribute
	 * @param headers   the headers attribute
	 * @param consumes  the consumes attribute
	 * @param produces  the produces attribute
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotPutMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PutMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Builds a {@link DeleteMapping} annotation from raw attribute values.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the name attribute
	 * @param path      the path attribute
	 * @param params    the params attribute
	 * @param headers   the headers attribute
	 * @param consumes  the consumes attribute
	 * @param produces  the produces attribute
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotDeleteMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, DeleteMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Builds a {@link PatchMapping} annotation from raw attribute values.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the name attribute
	 * @param path      the path attribute
	 * @param params    the params attribute
	 * @param headers   the headers attribute
	 * @param consumes  the consumes attribute
	 * @param produces  the produces attribute
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotPatchMapping(ConstPool constPool, String name, String[] path,
			String[] params, String[] headers, String[] consumes, String[] produces) {
		return annotHttpMethod(constPool, PatchMapping.class, name, path, null, params, headers, consumes, produces);
	}

	/**
	 * Builds any of the {@code @*Mapping} annotations from raw attribute
	 * values.
	 *
	 * @param constPool  the constant pool, never {@code null}
	 * @param annotation the Spring annotation class
	 * @param name       the name attribute
	 * @param path       the path attribute
	 * @param method     the HTTP method attribute
	 * @param params     the params attribute
	 * @param headers    the headers attribute
	 * @param consumes   the consumes attribute
	 * @param produces   the produces attribute
	 * @return the annotation ready to be added to a class or method
	 * @since 3.0.0
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

	/**
	 * Obtains (or creates) a {@link CtClass} for the given fully-qualified
	 * name inside the supplied pool.
	 *
	 * @param pool      the class pool, never {@code null}
	 * @param classname the fully-qualified class name
	 * @return the existing or newly created class
	 * @throws NotFoundException        if the pool cannot resolve a referenced
	 *                                  super-type
	 * @throws CannotCompileException  if a new class cannot be created
	 * @since 3.0.0
	 */
	public static CtClass makeClass(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {

		CtClass declaring = pool.getOrNull(classname);
		if (null == declaring) {
			declaring = pool.makeClass(classname);
		}

		/*
		 * When ClassPool.doPruning=true, Javassist will release the metadata
		 * stored against the CtClass when it is frozen. This reduces the
		 * memory footprint of long-running applications that generate many
		 * classes; we enable it explicitly so users do not have to remember to
		 * toggle it on themselves.
		 */
		declaring.stopPruning(true);

		return declaring;
	}

	/**
	 * Builds a no-argument constructor that simply calls the super-class
	 * default constructor.
	 *
	 * @param declaring the target {@link CtClass}, never {@code null}
	 * @return the new constructor
	 * @throws CannotCompileException if the body cannot be compiled
	 * @since 3.0.0
	 */
	public static CtConstructor defaultConstructor(CtClass declaring) throws CannotCompileException   {
		// Adds a default no-argument constructor
		CtConstructor cons = new CtConstructor(null, declaring);
		cons.setBody("{}");
    	return cons;
	}

	/**
	 * Builds a constructor that accepts an {@link InvocationHandler} and
	 * forwards it to the super-class constructor.
	 *
	 * @param pool      the class pool, never {@code null}
	 * @param declaring the target {@link CtClass}, never {@code null}
	 * @return the new constructor
	 * @throws NotFoundException        if the pool cannot resolve
	 *                                  {@link InvocationHandler}
	 * @throws CannotCompileException  if the body cannot be compiled
	 * @since 3.0.0
	 */
	public static CtConstructor makeConstructor(ClassPool pool, CtClass declaring) throws NotFoundException, CannotCompileException  {

		// Adds a constructor that injects the InvocationHandler callback.
    	CtClass[] parameters = new CtClass[] {pool.get(InvocationHandler.class.getName())};
    	CtClass[] exceptions = new CtClass[] { pool.get("java.lang.Exception") };
    	return CtNewConstructor.make(parameters, exceptions, "{super($1);}", declaring);

	}

	/**
	 * Obtains (or creates) a {@link CtClass} representing a Javassist
	 * interface for the given name.
	 *
	 * @param pool      the class pool, never {@code null}
	 * @param classname the fully-qualified interface name
	 * @return the existing or newly created interface
	 * @throws NotFoundException        if the pool cannot resolve a referenced
	 *                                  super-type
	 * @throws CannotCompileException  if a new interface cannot be created
	 * @since 3.0.0
	 */
	public static CtClass makeInterface(ClassPool pool, String classname)
			throws NotFoundException, CannotCompileException {

		CtClass declaring = pool.getOrNull(classname);
		if (null == declaring) {
			declaring = pool.makeInterface(classname);
		}

		// When ClassPool.doPruning=true, Javassist releases the metadata
		// stored against the CtClass when it is frozen. This reduces the
		// memory footprint of long-running applications that generate many
		// classes.
		declaring.stopPruning(true);

		return declaring;
	}

	/**
	 * Sets the super-class of a {@link CtClass} to the given Java class.
	 *
	 * @param pool      the class pool, never {@code null}
	 * @param declaring the target {@link CtClass}, never {@code null}
	 * @param clazz     the Java super-class
	 * @param <T>       the type of the super-class
	 * @throws Exception when the super-class cannot be resolved or assigned
	 * @since 3.0.0
	 */
	public static <T> void setSuperclass(ClassPool pool, CtClass declaring, Class<T> clazz)
			throws Exception {

		/* Resolves the given Java class to a CtClass and assigns it. */
		CtClass superclass = pool.get(clazz.getName());
		declaring.setSuperclass(superclass);

	}

	/**
	 * Translates an array of {@link MvcParam} descriptors into the matching
	 * {@link CtClass} array, suitable for handing to a Javassist
	 * {@code CtMethod} constructor.
	 *
	 * @param pool   the class pool, never {@code null}
	 * @param params the parameter descriptors
	 * @return the array of {@link CtClass}, or {@code null} when {@code params}
	 *         is null or empty
	 * @throws NotFoundException when one of the parameter types cannot be
	 *                           resolved
	 * @since 3.0.0
	 */
	public static CtClass[] makeParams(ClassPool pool, MvcParam<?>... params) throws NotFoundException {
		// No parameters
		if(params == null || params.length == 0) {
			return null;
		}
		// Method parameters
		CtClass[] parameters = new CtClass[params.length];
		for(int i = 0;i < params.length; i++) {
			parameters[i] = pool.get(params[i].getType().getName());
		}

		return parameters;
	}

	/**
	 * Attaches the {@code @GetMapping}, {@code @PostMapping},
	 * {@code @PutMapping}, {@code @DeleteMapping} or {@code @PatchMapping}
	 * annotation (depending on {@link RequestMethod}), the optional
	 * {@code @WebBound} annotation and the per-parameter
	 * {@code @CookieValue}/{@code @PathVariable}/... annotations to the
	 * given {@link CtMethod}.
	 *
	 * @param ctMethod    the target method, never {@code null}
	 * @param constPool   the constant pool, never {@code null}
	 * @param path        the URI path
	 * @param method      the HTTP method
	 * @param contentType the produced content type
	 * @param bound       the {@code @WebBound} payload, may be {@code null}
	 * @param params      the method parameter descriptors, may be {@code null}
	 * @since 3.0.0
	 */
	public static void methodAnnotations(CtMethod ctMethod, ConstPool constPool, String path, RequestMethod method,
			String contentType, MvcBound bound, MvcParam<?>[] params) {

		// Fetch the method-level annotations attribute.
        AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
        MethodInfo methodInfo = ctMethod.getMethodInfo();

        // Attach @WebBound if a binding is supplied.
        if (bound != null) {
        	methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }

        // Attach the matching @*Mapping annotation.
        methodAttr.addAnnotation(EndpointApiUtils.annotMethodMapping(constPool, path, method, contentType));

        methodInfo.addAttribute(methodAttr);

        // Attach the per-parameter annotations.
        if(params != null && params.length > 0) {

        	ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays = EndpointApiUtils.annotParams(constPool, params);
            parameterAtrribute.setAnnotations(paramArrays);
            methodInfo.addAttribute(parameterAtrribute);

        }

	}

	/**
	 * Attaches a {@code @WebBound} annotation to the given {@link CtMethod}.
	 *
	 * @param ctMethod  the target method, never {@code null}
	 * @param constPool the constant pool, never {@code null}
	 * @param bound     the {@code @WebBound} payload, may be {@code null}
	 * @since 3.0.0
	 */
	public static void methodBound(CtMethod ctMethod, ConstPool constPool, MvcBound bound) {

		// Fetch the method-level annotations attribute.
        AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        // Attach @WebBound if a binding is supplied.
        if (bound != null) {
        	methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }

        methodInfo.addAttribute(methodAttr);

	}

	/**
	 * Attaches the {@code @*Mapping} annotation (resolved from the supplied
	 * {@link MvcMethod}), the optional {@code @WebBound} annotation, the
	 * optional {@code @ResponseBody} annotation and the per-parameter
	 * annotations to the given {@link CtMethod}.
	 *
	 * @param ctMethod  the target method, never {@code null}
	 * @param constPool the constant pool, never {@code null}
	 * @param method    the method descriptor
	 * @param bound     the {@code @WebBound} payload, may be {@code null}
	 * @param params    the method parameter descriptors, may be {@code null}
	 * @since 3.0.0
	 */
	public static void methodAnnotations(CtMethod ctMethod, ConstPool constPool, MvcMethod method, MvcBound bound, MvcParam<?>... params) {

		// Fetch the method-level annotations attribute.
        AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        // Attach @WebBound if a binding is supplied.
        if (bound != null) {
        	methodAttr.addAnnotation(EndpointApiUtils.annotWebBound(constPool, bound));
        }

        // Attach the matching @*Mapping annotation.
        methodAttr.addAnnotation(EndpointApiUtils.annotMethodMapping(constPool, method));

        // Attach @ResponseBody when the descriptor asks for it.
        if(method.isResponseBody()) {
        	Annotation annot = new Annotation(ResponseBody.class.getName(), constPool);
        	methodAttr.addAnnotation(annot);
        }

        methodInfo.addAttribute(methodAttr);

        // Attach the per-parameter annotations.
        if(params != null && params.length > 0) {

        	ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
            Annotation[][] paramArrays = EndpointApiUtils.annotParams(constPool, params);
            parameterAtrribute.setAnnotations(paramArrays);
            methodInfo.addAttribute(parameterAtrribute);

        }

	}

	/**
	 * Sets the method body using the name stored on the given
	 * {@link MvcMethod}.
	 *
	 * @param ctMethod the target method, never {@code null}
	 * @param method   the method descriptor whose name will be used
	 * @throws CannotCompileException if the generated body cannot be compiled
	 * @since 3.0.0
	 */
	public static void methodBody(CtMethod ctMethod, MvcMethod method) throws CannotCompileException {
        methodBody(ctMethod, method.getName());
	}

	/**
	 * Sets the method body so that the call is forwarded to the
	 * {@link InvocationHandler} stored on the parent
	 * {@link org.springframework.javassist.bytecode.EndpointApi}.
	 *
	 * @param ctMethod   the target method, never {@code null}
	 * @param methodName the Java method name used in the reflection lookup
	 *                   inside the generated body
	 * @throws CannotCompileException if the generated body cannot be compiled
	 * @since 3.0.0
	 */
	public static void methodBody(CtMethod ctMethod, String methodName) throws CannotCompileException {

		// Build the body that delegates to getHandler().invoke(...).
		StringBuilder body = new StringBuilder();
        body.append("{\n");
        	body.append("if(getHandler() != null){\n");
        		body.append("Method method = this.getClass().getDeclaredMethod(\"" + methodName + "\", $sig);");
        		body.append("return ($r)getHandler().invoke($0, method, $args);");
        	body.append("}\n");
	        body.append("return null;\n");
        body.append("}");
        // When the method was abstract, this call removes the abstract modifier.
        ctMethod.setBody(body.toString());

	}

	/**
	 * Attaches a catch-all handler that prints and re-throws any exception
	 * the generated method throws.
	 *
	 * @param pool     the class pool, never {@code null}
	 * @param ctMethod the target method, never {@code null}
	 * @throws NotFoundException        if {@code java.lang.Exception} cannot
	 *                                  be resolved in the pool
	 * @throws CannotCompileException  if the catch block cannot be compiled
	 * @since 3.0.0
	 */
	public static void methodCatch(ClassPool pool, CtMethod ctMethod) throws NotFoundException, CannotCompileException {

		// Add the catch block that rethrows after printing.
        CtClass etype = pool.get("java.lang.Exception");
        ctMethod.addCatch("{ System.out.println($e); throw $e; }", etype);

	}

	/**
	 * Builds a {@link WebBound} annotation from the given binding descriptor.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param bound     the binding descriptor
	 * @return the annotation ready to be added to a class or method
	 * @since 3.0.0
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
	 * Resolves the {@link MvcMethod#getMethod()} list to the most specific
	 * Spring mapping annotation. When the method targets more than one HTTP
	 * verb a generic {@code @RequestMapping} is emitted; otherwise the
	 * matching {@code @GetMapping}/{@code @PostMapping}/... is emitted.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param method    the method descriptor
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotMethodMapping(ConstPool constPool, MvcMethod method) {

		Annotation annot = null;
		// Multiple HTTP methods fall back to @RequestMapping.
		if(method.getMethod().length > 1) {
			annot = annotRequestMapping(constPool, method.getName(), method.getPath(), method.getMethod(),
					method.getParams(), method.getHeaders(), method.getConsumes(), method.getProduces());
			return annot;
		}
		// Single HTTP method emits the matching @*Mapping.
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
	 * Builds the matching {@code @*Mapping} annotation from the given HTTP
	 * method and path/content-type. Used when the caller has not yet built a
	 * full {@link MvcMethod} descriptor.
	 *
	 * @param constPool   the constant pool, never {@code null}
	 * @param path        the URI path
	 * @param method      the HTTP method
	 * @param contentType the produced content type
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotMethodMapping(ConstPool constPool, String path, RequestMethod method,
			String contentType) {

		Annotation annot = null;
		// Single HTTP method only.
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
	 * Translates an array of {@link MvcParam} descriptors into a
	 * two-dimensional array of parameter annotations suitable for attaching
	 * to a Javassist method.
	 *
	 * <p>For every parameter a primary Spring annotation
	 * ({@link CookieValue}, {@link MatrixVariable}, ...) is added as the
	 * visible annotation; a secondary {@link ParamName} annotation carrying the
	 * parameter's original name is added as the invisible annotation.</p>
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param params    the parameter descriptors
	 * @return the two-dimensional annotation array, or {@code null} when
	 *         {@code params} is null or empty
	 * @since 3.0.0
	 */
	public static Annotation[][] annotParams(ConstPool constPool, MvcParam<?>... params) {

		// Translate the parameter descriptors.
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

				// Add the parameter-name annotation for downstream consumers.
				Annotation nameAnnot = new Annotation(ParamName.class.getName(), constPool);
				nameAnnot.addMemberValue("name", new StringMemberValue(params[i].getName(), constPool));

				paramArrays[i][1] = nameAnnot;

			}

			return paramArrays;

		}
		return null;
	}

}