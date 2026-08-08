package org.springframework.javassist.bytecode;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.javassist.bytecode.definition.MvcApiImplicitParam;
import org.springframework.javassist.bytecode.definition.MvcApiResponse;
import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.bytecode.definition.MvcMapping;
import org.springframework.javassist.bytecode.definition.MvcMethod;
import org.springframework.javassist.bytecode.definition.MvcParam;
import org.springframework.javassist.utils.EndpointApiUtils;
import org.springframework.javassist.utils.SwaggerApiUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import io.github.easy4j.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;

/**
 * Specialised {@link CtClassBuilder} that materialises a Spring MVC
 * controller interface at runtime using Javassist bytecode generation.
 *
 * <p>The generated class extends {@link EndpointApi} and exposes fluent
 * builder methods for attaching {@code @Controller}/{@code @RestController},
 * {@code @RequestMapping}, Swagger documentation and individual handler
 * methods. Each handler method delegates to the {@link java.lang.reflect.InvocationHandler}
 * stored on the parent {@link EndpointApi}.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see CtClassBuilder
 * @see EndpointApi
 * @see ReactiveHandlerCtClassBuilder
 */
public class EndpointApiCtClassBuilder extends CtClassBuilder {

	/**
	 * Creates a builder for a controller class with the given fully-qualified
	 * name, using the default class pool.
	 *
	 * @param classname the fully-qualified name of the class to create
	 * @throws CannotCompileException if a new class cannot be created
	 * @throws NotFoundException      if a referenced class cannot be resolved
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		super(classname, EndpointApi.class);
	}

	/**
	 * Creates a builder for a controller class with the given name inside
	 * the supplied class pool.
	 *
	 * @param pool      the class pool, never {@code null}
	 * @param classname the fully-qualified name of the class to create
	 * @throws CannotCompileException if a new class cannot be created
	 * @throws NotFoundException      if a referenced class cannot be resolved
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		super(pool, classname, EndpointApi.class);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public <T> EndpointApiCtClassBuilder autowired(Class<T> type, String name, boolean required) throws CannotCompileException, NotFoundException {
		super.autowired(type, name, required);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public EndpointApiCtClassBuilder autowiredHandler(boolean required, String qualifier)
			throws CannotCompileException, NotFoundException {
		super.autowiredHandler(required, qualifier);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public <T> CtClassBuilder autowired(Class<T> type, String name, boolean required, String qualifier)
			throws CannotCompileException, NotFoundException {
		super.autowired(type, name, required, qualifier);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public EndpointApiCtClassBuilder bind(MvcBound bound) {
		super.bind(bound);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public EndpointApiCtClassBuilder bind(String uid, String json) {
		super.bind(uid, json);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public EndpointApiCtClassBuilder makeField(String src) throws CannotCompileException {
		super.makeField(src);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public EndpointApiCtClassBuilder makeMethod(String src) throws CannotCompileException {
		super.makeMethod(src);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public <T> EndpointApiCtClassBuilder newField(Class<T> fieldClass, String fieldName, String fieldValue)
			throws CannotCompileException, NotFoundException {
		super.newField(fieldClass, fieldName, fieldValue);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public EndpointApiCtClassBuilder removeField(String fieldName) throws NotFoundException {
		super.removeField(fieldName);
		return this;
	}

	/**
	 * Adds the Swagger {@code @Api} annotation to the generated class with
	 * the given tags, and enables Swagger annotation emission on subsequent
	 * methods.
	 *
	 * @param tags the Swagger tag names
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder api(String... tags) {

		if(tags != null && tags.length > 0) {
			ConstPool constPool = this.classFile.getConstPool();
			JavassistUtils.addClassAnnotation(declaring, SwaggerApiUtils.annotApi(constPool, tags));
		}
		annotApi = true;
		return this;
	}

	/**
	 * Adds the Swagger {@code @ApiIgnore} annotation to the generated class
	 * and disables Swagger annotation emission on subsequent methods.
	 *
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder apiIgnore() {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, SwaggerApiUtils.annotApiIgnore(constPool, "Ignore"));
		annotApi = false;
		return this;
	}

	/**
	 * Adds a {@code @Controller} annotation with an empty name to the
	 * generated class.
	 *
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder controller() {
		return this.controller("");
	}

	/**
	 * Adds a {@code @Controller} annotation with the given name to the
	 * generated class.
	 *
	 * @param name the controller name; must be unique
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder controller(String name) {
		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotController(constPool, name));
		return this;
	}

	/**
	 * Adds a {@code @RestController} annotation with an empty name to the
	 * generated class.
	 *
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder restController() {
		return this.restController("");
	}

	/**
	 * Adds a {@code @RestController} annotation with the given name to the
	 * generated class.
	 *
	 * @param name the controller name; must be unique
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder restController(String name) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRestController(constPool, name));

		return this;
	}

	/**
	 * Adds a {@code @RequestMapping} annotation derived from the given
	 * {@link MvcMapping} descriptor to the generated class.
	 *
	 * @param mapping the mapping descriptor, never {@code null}
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder requestMapping(MvcMapping mapping) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRequestMapping(constPool, mapping));

		return this;
	}

	/**
	 * Adds a {@code @RequestMapping} annotation with only a path to the
	 * generated class.
	 *
	 * @param path the URI path
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder requestMapping(String path) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRequestMapping(constPool, null, new String[] { path }, null,
				null, null, null, null));

		return this;
	}

	/**
	 * Adds a fully-specified {@code @RequestMapping} annotation to the
	 * generated class.
	 *
	 * @param name      the mapping name
	 * @param path      the URI paths
	 * @param method    the HTTP methods
	 * @param params    the request-parameter preconditions
	 * @param headers   the request-header preconditions
	 * @param consumes  the consumable media types
	 * @param produces  the producible media types
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder requestMapping(String name, String[] path, RequestMethod[] method,
			String[] params, String[] headers, String[] consumes, String[] produces) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRequestMapping(constPool, name, path, method,
				params, headers, consumes, produces));

		return this;
	}

	/**
	 * Creates a new handler method on the generated controller using raw
	 * attribute values.
	 *
	 * @param methodName    the Java method name
	 * @param path          the URI path
	 * @param method        the HTTP method (GET, POST, ...)
	 * @param contentType   the produced content type
	 * @param bound         the {@code @WebBound} binding descriptor
	 * @param params        the method parameter descriptors
	 * @return {@code this} for fluent chaining
	 * @throws CannotCompileException if the method cannot be compiled
	 * @throws NotFoundException      if a referenced type cannot be resolved
	 * @since 3.0.0
	 */
	public EndpointApiCtClassBuilder newMethod(String methodName, String path, RequestMethod method, String contentType,
			MvcBound bound, MvcParam<?>... params) throws CannotCompileException, NotFoundException {

		//ResponseEntity.class

		ConstPool constPool = this.classFile.getConstPool();
		// Create method
		CtClass returnType = pool.get(Object.class.getName());
		CtMethod ctMethod = null;
		// Method parameters
		CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);
		// Parameterised method
		if(parameters != null && parameters.length > 0) {
			ctMethod = new CtMethod(returnType, methodName, parameters, declaring);
		}
		// No-arg method
		else {
			ctMethod = new CtMethod(returnType, methodName , null, declaring);
		}
        // Set method body
        EndpointApiUtils.methodBody(ctMethod, methodName);
        // Set exception catch logic
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // Add @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping
        EndpointApiUtils.methodAnnotations(ctMethod, constPool, path, method, contentType, bound, params);

        // Add @ApiOperation | @ApiImplicitParams | @ApiResponses
        if(annotApi) {

        	// Get method annotations attribute
            AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
            MethodInfo methodInfo = ctMethod.getMethodInfo();

            // Add @ApiOperation
        	methodAttr.addAnnotation(SwaggerApiUtils.annotApiOperation(constPool, String.format("Method : %s", methodName), bound.getNotes()));
            // Add @ApiImplicitParams
            if(params != null && params.length > 0) {

				MvcApiImplicitParam[] apiImplicitParams = Arrays.stream(params).map(param -> {
					MvcApiImplicitParam implicitParam = new MvcApiImplicitParam(param.getName(), "", param.isRequired(),
							param.getType().getName());
					implicitParam.setDefaultValue(param.getDef());
					return implicitParam;
				}).collect(Collectors.toList()).toArray(new MvcApiImplicitParam[params.length]);

				methodAttr.addAnnotation(SwaggerApiUtils.annotApiImplicitParams(constPool, apiImplicitParams));
            }
            // Add @ApiResponses
        	methodAttr.addAnnotation(SwaggerApiUtils.annotApiResponses(constPool, new MvcApiResponse(0, "Invoke Success", Object.class)));

            methodInfo.addAttribute(methodAttr);
        }

        // Add the method to the class
        declaring.addMethod(ctMethod);

        return this;

	}


	/**
	 * Creates a new handler method on the generated controller using a
	 * {@link MvcMethod} descriptor.
	 *
	 * @param rtClass  the return type class
	 * @param method   the method descriptor
	 * @param bound    the {@code @WebBound} binding descriptor
	 * @param params   the method parameter descriptors
	 * @param <T>      the return type
	 * @return {@code this} for fluent chaining
	 * @throws CannotCompileException if the method cannot be compiled
	 * @throws NotFoundException      if a referenced type cannot be resolved
	 * @since 3.0.0
	 */
	public <T> EndpointApiCtClassBuilder newMethod(final Class<T> rtClass, final MvcMethod method, final MvcBound bound, MvcParam<?>... params) throws CannotCompileException, NotFoundException {

		ConstPool constPool = this.classFile.getConstPool();

		// Create method
		CtClass returnType = rtClass != null ? pool.get(rtClass.getName()) : CtClass.voidType;
		CtMethod ctMethod = null;
		// Method parameters
		CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);
		// Parameterised method
		if(parameters != null && parameters.length > 0) {
			ctMethod = new CtMethod(returnType, method.getName(), parameters, declaring);
		}
		// No-arg method
		else {
			ctMethod = new CtMethod(returnType, method.getName() , null, declaring);
		}
        // Set method body
        EndpointApiUtils.methodBody(ctMethod, method);
        // Set exception catch logic
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping
        EndpointApiUtils.methodAnnotations(ctMethod, constPool, method, bound, params);

        // Add @ApiOperation | @ApiImplicitParams | @ApiResponses
        if(annotApi) {

        	// Get method annotations attribute
            AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
            MethodInfo methodInfo = ctMethod.getMethodInfo();

            // Add @ApiOperation
        	methodAttr.addAnnotation(SwaggerApiUtils.annotApiOperation(constPool, String.format("Method : %s", method.getName()), bound.getNotes()));
            // Add @ApiImplicitParams
            if(params != null && params.length > 0) {

				MvcApiImplicitParam[] apiImplicitParams = Arrays.stream(params).map(param -> {
					MvcApiImplicitParam implicitParam = new MvcApiImplicitParam(param.getName(), "", param.isRequired(),
							param.getType().getName());
					implicitParam.setDefaultValue(param.getDef());
					return implicitParam;
				}).collect(Collectors.toList()).toArray(new MvcApiImplicitParam[params.length]);

				methodAttr.addAnnotation(SwaggerApiUtils.annotApiImplicitParams(constPool, apiImplicitParams));
            }
            // Add @ApiResponses
            if(!rtClass.isAssignableFrom(Void.class)) {
            	methodAttr.addAnnotation(SwaggerApiUtils.annotApiResponses(constPool, new MvcApiResponse(0, "Invoke Success", rtClass)));
            }

            methodInfo.addAttribute(methodAttr);
        }

        // Add the method to the class
        declaring.addMethod(ctMethod);

        return this;
	}

	/**
	 * Removes a previously declared handler method from the generated
	 * controller. When the method does not exist the call is a silent no-op.
	 *
	 * @param methodName the Java method name
	 * @param params     the method parameter descriptors used to locate the
	 *                   method
	 * @return {@code this} for fluent chaining
	 * @throws NotFoundException if the method is declared but cannot be
	 *                           resolved
	 * @since 3.0.0
	 */
	public <T> EndpointApiCtClassBuilder removeMethod(final String methodName, MvcParam<?>... params) throws NotFoundException {

		// Parameterised method
		if(params != null && params.length > 0) {

			// Method parameters
			CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);

			// Check whether the method is already defined
			if(!JavassistUtils.hasMethod(declaring, methodName, parameters)) {
				return this;
			}

			declaring.removeMethod(declaring.getDeclaredMethod(methodName, parameters));

		}
		else {

			// Check whether the method is already defined
			if(!JavassistUtils.hasMethod(declaring, methodName)) {
				return this;
			}

			declaring.removeMethod(declaring.getDeclaredMethod(methodName));

		}

		return this;
	}

}
