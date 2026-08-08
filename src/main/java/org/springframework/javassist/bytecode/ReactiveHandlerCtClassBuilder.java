package org.springframework.javassist.bytecode;

import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.utils.EndpointApiUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import io.github.easy4j.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Specialised {@link CtClassBuilder} that materialises a Spring WebFlux
 * functional-style handler at runtime using Javassist bytecode generation.
 *
 * <p>The generated class extends {@link ReactiveHandler} and exposes fluent
 * builder methods for attaching {@code mono} and {@code flux} handler
 * methods. Each handler method delegates to the
 * {@link java.lang.reflect.InvocationHandler} stored on the parent
 * {@link ReactiveHandler}.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see CtClassBuilder
 * @see ReactiveHandler
 * @see EndpointApiCtClassBuilder
 */
public class ReactiveHandlerCtClassBuilder extends CtClassBuilder  {

	/**
	 * The method name used for the single-value reactive handler.
	 */
	public static final String METHOD_MONO_NAME = "mono";

	/**
	 * The method name used for the streaming reactive handler.
	 */
	public static final String METHOD_FLUX_NAME = "flux";

	/**
	 * Creates a builder for a reactive handler class with the given
	 * fully-qualified name, using the default class pool.
	 *
	 * @param classname the fully-qualified name of the class to create
	 * @throws CannotCompileException if a new class cannot be created
	 * @throws NotFoundException      if a referenced class cannot be resolved
	 * @since 3.0.0
	 */
	public ReactiveHandlerCtClassBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		super(classname, ReactiveHandler.class);
	}

	/**
	 * Creates a builder for a reactive handler class with the given name
	 * inside the supplied class pool.
	 *
	 * @param pool      the class pool, never {@code null}
	 * @param classname the fully-qualified name of the class to create
	 * @throws CannotCompileException if a new class cannot be created
	 * @throws NotFoundException      if a referenced class cannot be resolved
	 * @since 3.0.0
	 */
	public ReactiveHandlerCtClassBuilder(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		super(pool, classname, ReactiveHandler.class);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public <T> ReactiveHandlerCtClassBuilder autowired(Class<T> type, String name, boolean required) throws CannotCompileException, NotFoundException {
		super.autowired(type, name, required);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public ReactiveHandlerCtClassBuilder autowiredHandler(boolean required, String qualifier)
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
	public ReactiveHandlerCtClassBuilder bind(MvcBound bound) {
		super.bind(bound);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public ReactiveHandlerCtClassBuilder bind(String uid, String json) {
		super.bind(uid, json);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public ReactiveHandlerCtClassBuilder makeField(String src) throws CannotCompileException {
		super.makeField(src);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public ReactiveHandlerCtClassBuilder makeMethod(String src) throws CannotCompileException {
		super.makeMethod(src);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code this} for fluent chaining
	 */
	@Override
	public <T> ReactiveHandlerCtClassBuilder newField(Class<T> fieldClass, String fieldName, String fieldValue)
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
	public ReactiveHandlerCtClassBuilder removeField(String fieldName) throws NotFoundException {
		super.removeField(fieldName);
		return this;
	}

	/**
	 * Creates a {@code mono} handler method on the generated reactive
	 * controller. The method accepts a {@link ServerRequest} and returns
	 * a {@link Mono} of {@code ServerResponse}.
	 *
	 * @param bound the {@code @WebBound} binding descriptor
	 * @return {@code this} for fluent chaining
	 * @throws CannotCompileException if the method cannot be compiled
	 * @throws NotFoundException      if a referenced type cannot be resolved
	 * @since 3.0.0
	 */
	public ReactiveHandlerCtClassBuilder monoMethod(final MvcBound bound) throws CannotCompileException, NotFoundException {

		ConstPool constPool = this.classFile.getConstPool();

		// Method parameters
		CtClass[] parameters = new CtClass[1];
				  parameters[0] = pool.get(ServerRequest.class.getName());
		// Create method
		CtClass returnType = pool.get(Mono.class.getName());
		CtMethod ctMethod = new CtMethod(returnType, METHOD_MONO_NAME, parameters, declaring);

        // Set method body
        EndpointApiUtils.methodBody(ctMethod, METHOD_MONO_NAME);
        // Set exception catch logic
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // @WebBound annotation
        EndpointApiUtils.methodBound(ctMethod, constPool, bound);

        // Add the method to the class
        declaring.addMethod(ctMethod);

        return this;

	}

	/**
	 * Creates a {@code flux} handler method on the generated reactive
	 * controller. The method accepts a {@link ServerRequest} and returns
	 * a {@link Flux} of {@code ServerResponse}.
	 *
	 * @param bound the {@code @WebBound} binding descriptor
	 * @return {@code this} for fluent chaining
	 * @throws CannotCompileException if the method cannot be compiled
	 * @throws NotFoundException      if a referenced type cannot be resolved
	 * @since 3.0.0
	 */
	public ReactiveHandlerCtClassBuilder fluxMethod(final MvcBound bound) throws CannotCompileException, NotFoundException {

		ConstPool constPool = this.classFile.getConstPool();

		// Method parameters
		CtClass[] parameters = new CtClass[1];
				  parameters[0] = pool.get(ServerRequest.class.getName());
		// Create method
		CtClass returnType = pool.get(Flux.class.getName());
		CtMethod ctMethod = new CtMethod(returnType, METHOD_FLUX_NAME, parameters, declaring);

        // Set method body
        EndpointApiUtils.methodBody(ctMethod, METHOD_FLUX_NAME);
        // Set exception catch logic
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // @WebBound annotation
        EndpointApiUtils.methodBound(ctMethod, constPool, bound);

        // Add the method to the class
        declaring.addMethod(ctMethod);

        return this;

	}

	/**
	 * Creates a custom handler method on the generated reactive controller
	 * with the given return type and name. The method accepts a
	 * {@link ServerRequest} and returns a publisher of the given type.
	 *
	 * @param rtClass    the return type class
	 * @param methodName the Java method name
	 * @param bound      the {@code @WebBound} binding descriptor
	 * @param <T>        the return type
	 * @return {@code this} for fluent chaining
	 * @throws CannotCompileException if the method cannot be compiled
	 * @throws NotFoundException      if a referenced type cannot be resolved
	 * @since 3.0.0
	 */
	public <T> ReactiveHandlerCtClassBuilder newMethod(final Class<T> rtClass, final String methodName, final MvcBound bound) throws CannotCompileException, NotFoundException {

		ConstPool constPool = this.classFile.getConstPool();

		// Create method
		CtClass returnType = rtClass != null ? pool.get(rtClass.getName()) : CtClass.voidType;
		// Method parameters
		CtClass[] parameters = new CtClass[1];
				  parameters[0] = pool.get(ServerRequest.class.getName());
		// Create method
		CtMethod ctMethod = new CtMethod(returnType, methodName, parameters, declaring);

        // Set method body
        EndpointApiUtils.methodBody(ctMethod, methodName);
        // Set exception catch logic
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // @WebBound annotation
        EndpointApiUtils.methodBound(ctMethod, constPool, bound);

        // Add the method to the class
        declaring.addMethod(ctMethod);

        return this;
	}

	/**
	 * Removes the {@code mono} handler method from the generated reactive
	 * controller. When the method does not exist the call is a silent no-op.
	 *
	 * @return {@code this} for fluent chaining
	 * @throws NotFoundException if the method is declared but cannot be
	 *                           resolved
	 * @since 3.0.0
	 */
	public <T> ReactiveHandlerCtClassBuilder removeMono() throws NotFoundException {
		return this.removeMethod(METHOD_MONO_NAME);
	}

	/**
	 * Removes the {@code flux} handler method from the generated reactive
	 * controller. When the method does not exist the call is a silent no-op.
	 *
	 * @return {@code this} for fluent chaining
	 * @throws NotFoundException if the method is declared but cannot be
	 *                           resolved
	 * @since 3.0.0
	 */
	public <T> ReactiveHandlerCtClassBuilder removeFlux() throws NotFoundException {
		return this.removeMethod(METHOD_FLUX_NAME);
	}

	/**
	 * Removes a previously declared handler method from the generated
	 * reactive controller. When the method does not exist the call is a
	 * silent no-op.
	 *
	 * @param methodName the Java method name
	 * @return {@code this} for fluent chaining
	 * @throws NotFoundException if the method is declared but cannot be
	 *                           resolved
	 * @since 3.0.0
	 */
	public <T> ReactiveHandlerCtClassBuilder removeMethod(final String methodName) throws NotFoundException {

		// Method parameters
		CtClass[] parameters = new CtClass[1];
				  parameters[0] = pool.get(ServerRequest.class.getName());

		// Check whether the method is already defined
		if(!JavassistUtils.hasMethod(declaring, methodName, parameters)) {
			return this;
		}

		declaring.removeMethod(declaring.getDeclaredMethod(methodName, parameters));

		return this;
	}

}
