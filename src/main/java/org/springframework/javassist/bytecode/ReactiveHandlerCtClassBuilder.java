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
 * 动态构建Controller接口
 */
public class ReactiveHandlerCtClassBuilder extends CtClassBuilder  {
	
	public static final String METHOD_MONO_NAME = "mono";
	public static final String METHOD_FLUX_NAME = "flux";
	
	public ReactiveHandlerCtClassBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		super(classname, ReactiveHandler.class);
	}
	
	public ReactiveHandlerCtClassBuilder(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		super(pool, classname, ReactiveHandler.class);
	}
	
	public <T> ReactiveHandlerCtClassBuilder autowired(Class<T> type, String name, boolean required) throws CannotCompileException, NotFoundException {
		super.autowired(type, name, required);
		return this;
	}
	
	@Override
	public ReactiveHandlerCtClassBuilder autowiredHandler(boolean required, String qualifier)
			throws CannotCompileException, NotFoundException {
		super.autowiredHandler(required, qualifier);
		return this;
	}
	
	@Override
	public <T> CtClassBuilder autowired(Class<T> type, String name, boolean required, String qualifier)
			throws CannotCompileException, NotFoundException {
		super.autowired(type, name, required, qualifier);
		return this;
	}
	
	@Override
	public ReactiveHandlerCtClassBuilder bind(MvcBound bound) {
		super.bind(bound);
		return this;
	}
	
	@Override
	public ReactiveHandlerCtClassBuilder bind(String uid, String json) {
		super.bind(uid, json);
		return this;
	}
	
	@Override
	public ReactiveHandlerCtClassBuilder makeField(String src) throws CannotCompileException {
		super.makeField(src);
		return this;
	}
	
	@Override
	public ReactiveHandlerCtClassBuilder makeMethod(String src) throws CannotCompileException {
		super.makeMethod(src);
		return this;
	}
	
	@Override
	public <T> ReactiveHandlerCtClassBuilder newField(Class<T> fieldClass, String fieldName, String fieldValue)
			throws CannotCompileException, NotFoundException {
		super.newField(fieldClass, fieldName, fieldValue);
		return this;
	}
	
	@Override
	public ReactiveHandlerCtClassBuilder removeField(String fieldName) throws NotFoundException {
		super.removeField(fieldName);
		return this;
	}
	
	/**
	 * @param bound  		：方法绑定数据信息
	 * @return {@link ReactiveHandlerCtClassBuilder} instance
	 * @throws CannotCompileException if can't compile
	 * @throws NotFoundException  if not found
	 */
	public ReactiveHandlerCtClassBuilder monoMethod(final MvcBound bound) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.classFile.getConstPool();
		
		// 方法参数
		CtClass[] parameters = new CtClass[1];
				  parameters[0] = pool.get(ServerRequest.class.getName());
		// 创建方法
		CtClass returnType = pool.get(Mono.class.getName());
		CtMethod ctMethod = new CtMethod(returnType, METHOD_MONO_NAME, parameters, declaring);
		
        // 设置方法体
        EndpointApiUtils.methodBody(ctMethod, METHOD_MONO_NAME);
        // 设置方法异常捕获逻辑
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // @WebBound 注解
        EndpointApiUtils.methodBound(ctMethod, constPool, bound);
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;

	}
	
	/**
	 * @param bound  		：方法绑定数据信息
	 * @return {@link ReactiveHandlerCtClassBuilder} instance
	 * @throws CannotCompileException if can't compile
	 * @throws NotFoundException  if not found
	 */
	public ReactiveHandlerCtClassBuilder fluxMethod(final MvcBound bound) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.classFile.getConstPool();
		
		// 方法参数
		CtClass[] parameters = new CtClass[1];
				  parameters[0] = pool.get(ServerRequest.class.getName());
		// 创建方法
		CtClass returnType = pool.get(Flux.class.getName());
		CtMethod ctMethod = new CtMethod(returnType, METHOD_FLUX_NAME, parameters, declaring);
		
        // 设置方法体
        EndpointApiUtils.methodBody(ctMethod, METHOD_FLUX_NAME);
        // 设置方法异常捕获逻辑
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // @WebBound 注解
        EndpointApiUtils.methodBound(ctMethod, constPool, bound);
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;

	}
	
	/**
	 * 
	 * 根据参数构造一个新的方法
	 * @param rtClass 		：返回对象类型
	 * @param methodName 	：方法名称
	 * @param bound  		：方法绑定数据信息
	 * @param <T> 	   ： 参数泛型
	 * @return {@link ReactiveHandlerCtClassBuilder} instance
	 * @throws CannotCompileException if can't compile
	 * @throws NotFoundException  if not found
	 */ 
	public <T> ReactiveHandlerCtClassBuilder newMethod(final Class<T> rtClass, final String methodName, final MvcBound bound) throws CannotCompileException, NotFoundException {
	       
		ConstPool constPool = this.classFile.getConstPool();
		
		// 创建抽象方法
		CtClass returnType = rtClass != null ? pool.get(rtClass.getName()) : CtClass.voidType;
		// 方法参数
		CtClass[] parameters = new CtClass[1];
				  parameters[0] = pool.get(ServerRequest.class.getName());
		// 创建方法
		CtMethod ctMethod = new CtMethod(returnType, methodName, parameters, declaring);
		
        // 设置方法体
        EndpointApiUtils.methodBody(ctMethod, methodName);
        // 设置方法异常捕获逻辑
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // @WebBound 注解
        EndpointApiUtils.methodBound(ctMethod, constPool, bound);
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;
	}
	
	public <T> ReactiveHandlerCtClassBuilder removeMono() throws NotFoundException {
		return this.removeMethod(METHOD_MONO_NAME);
	}
	
	public <T> ReactiveHandlerCtClassBuilder removeFlux() throws NotFoundException {
		return this.removeMethod(METHOD_FLUX_NAME);
	}
	
	public <T> ReactiveHandlerCtClassBuilder removeMethod(final String methodName) throws NotFoundException {
		
		// 方法参数
		CtClass[] parameters = new CtClass[1];
				  parameters[0] = pool.get(ServerRequest.class.getName());
			
		// 检查方法是否已经定义
		if(!JavassistUtils.hasMethod(declaring, methodName, parameters)) {
			return this;
		}
		
		declaring.removeMethod(declaring.getDeclaredMethod(methodName, parameters));
		
		return this;
	}

}