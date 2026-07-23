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
 * 动态构建Controller接口
 */
public class EndpointApiCtClassBuilder extends CtClassBuilder {
	
	public EndpointApiCtClassBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		super(classname, EndpointApi.class);
	}
	
	public EndpointApiCtClassBuilder(final ClassPool pool, final String classname) throws CannotCompileException, NotFoundException {
		super(pool, classname, EndpointApi.class);
	}
	
	public <T> EndpointApiCtClassBuilder autowired(Class<T> type, String name, boolean required) throws CannotCompileException, NotFoundException {
		super.autowired(type, name, required);
		return this;
	}
	
	@Override
	public EndpointApiCtClassBuilder autowiredHandler(boolean required, String qualifier)
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
	public EndpointApiCtClassBuilder bind(MvcBound bound) {
		super.bind(bound);
		return this;
	}
	
	@Override
	public EndpointApiCtClassBuilder bind(String uid, String json) {
		super.bind(uid, json);
		return this;
	}
	
	@Override
	public EndpointApiCtClassBuilder makeField(String src) throws CannotCompileException {
		super.makeField(src);
		return this;
	}
	
	@Override
	public EndpointApiCtClassBuilder makeMethod(String src) throws CannotCompileException {
		super.makeMethod(src);
		return this;
	}
	
	@Override
	public <T> EndpointApiCtClassBuilder newField(Class<T> fieldClass, String fieldName, String fieldValue)
			throws CannotCompileException, NotFoundException {
		super.newField(fieldClass, fieldName, fieldValue);
		return this;
	}
	
	@Override
	public EndpointApiCtClassBuilder removeField(String fieldName) throws NotFoundException {
		super.removeField(fieldName);
		return this;
	}
	
	/**
	 * 添加类注解 @Api
	 * @param tags 标签名称
	 * @return {@link EndpointApiCtClassBuilder} instance
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
	 * 添加类注解  @ApiIgnore
	 * @return {@link EndpointApiCtClassBuilder} instance
	 */
	public EndpointApiCtClassBuilder apiIgnore() {
		
		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, SwaggerApiUtils.annotApiIgnore(constPool, "Ignore"));
		annotApi = false;
		return this;
	}
	
	/**
	 * 添加类注解 @Controller
	 * @return {@link EndpointApiCtClassBuilder} instance
	 */
	public EndpointApiCtClassBuilder controller() {
		return this.controller("");
	}
	
	/**
	 * 添加类注解 @Controller
	 * @param name Controller名称：必须唯一
	 * @return {@link EndpointApiCtClassBuilder} instance
	 */
	public EndpointApiCtClassBuilder controller(String name) {
		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotController(constPool, name));
		return this;
	}
	
	/**
	 * 添加类注解 @RestController
	 * @return {@link EndpointApiCtClassBuilder} instance
	 */
	public EndpointApiCtClassBuilder restController() {
		return this.restController("");
	}
	
	/**
	 * 添加类注解 @RestController
	 * @param name Controller名称：必须唯一
	 * @return {@link EndpointApiCtClassBuilder} instance
	 */
	public EndpointApiCtClassBuilder restController(String name) {
		
		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRestController(constPool, name));
		
		return this;
	}
	
	/**
	 * 添加类注解 @RequestMapping
	 * @param mapping			: The {@link MvcMapping} instance
	 * @return {@link EndpointApiCtClassBuilder} instance
	 */
	public EndpointApiCtClassBuilder requestMapping(MvcMapping mapping) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRequestMapping(constPool, mapping));

		return this;
	}

	/**
	 * 添加类注解 @RequestMapping
	 * @param path			： The path attribute values of @RequestMapping 
	 * @return {@link EndpointApiCtClassBuilder} instance
	 */
	public EndpointApiCtClassBuilder requestMapping(String path) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRequestMapping(constPool, null, new String[] { path }, null,
				null, null, null, null));

		return this;
	}
	
	/**
	 * 添加类注解 @RequestMapping
	 * @param name 			： The name attribute value of @RequestMapping 
	 * @param path			： The path attribute values of @RequestMapping 
	 * @param method		： The method attribute values of @RequestMapping 
	 * @param params		： The params attribute values of @RequestMapping 
	 * @param headers		： The headers attribute values of @RequestMapping 
	 * @param consumes		： The consumes attribute values of @RequestMapping 
	 * @param produces		： The produces attribute values of @RequestMapping 
	 * @return {@link EndpointApiCtClassBuilder} instance
	 */
	public EndpointApiCtClassBuilder requestMapping(String name, String[] path, RequestMethod[] method,
			String[] params, String[] headers, String[] consumes, String[] produces) {

		ConstPool constPool = this.classFile.getConstPool();
		JavassistUtils.addClassAnnotation(declaring, EndpointApiUtils.annotRequestMapping(constPool, name, path, method,
				params, headers, consumes, produces));

		return this;
	} 
	
	/**
	 * @param methodName   	： 方法名称
	 * @param path   		： 发布地址
	 * @param method 		： 请求方式(GET/POST)
	 * @param contentType 	： 响应类型及编码
	 * @param bound			：数据绑定对象
	 * @param params		：参数信息
	 * @return {@link EndpointApiCtClassBuilder} instance
	 * @throws CannotCompileException if can't compile
	 * @throws NotFoundException  if not found
	 */
	public EndpointApiCtClassBuilder newMethod(String methodName, String path, RequestMethod method, String contentType,
			MvcBound bound, MvcParam<?>... params) throws CannotCompileException, NotFoundException {
		
		//ResponseEntity.class
		
		ConstPool constPool = this.classFile.getConstPool();
		// 创建方法
		CtClass returnType = pool.get(Object.class.getName());
		CtMethod ctMethod = null;
		// 方法参数
		CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);
		// 有参方法
		if(parameters != null && parameters.length > 0) {
			ctMethod = new CtMethod(returnType, methodName, parameters, declaring);
		} 
		// 无参方法 
		else {
			ctMethod = new CtMethod(returnType, methodName , null, declaring);
		}
        // 设置方法体
        EndpointApiUtils.methodBody(ctMethod, methodName);
        // 设置方法异常捕获逻辑
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // 为方法添加  @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
        EndpointApiUtils.methodAnnotations(ctMethod, constPool, path, method, contentType, bound, params);
        
        // 为方法添加  @ApiOperation | @ApiImplicitParams | @ApiResponses 注解
        if(annotApi) {
        	
        	// 获取方法属性对象
            AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            
            // 添加 @ApiOperation 注解
        	methodAttr.addAnnotation(SwaggerApiUtils.annotApiOperation(constPool, String.format("Method : %s", methodName), bound.getNotes()));
            // 添加 @ApiImplicitParams 注解
            if(params != null && params.length > 0) {

				MvcApiImplicitParam[] apiImplicitParams = Arrays.stream(params).map(param -> {
					MvcApiImplicitParam implicitParam = new MvcApiImplicitParam(param.getName(), "", param.isRequired(),
							param.getType().getName());
					implicitParam.setDefaultValue(param.getDef());
					return implicitParam;
				}).collect(Collectors.toList()).toArray(new MvcApiImplicitParam[params.length]);

				methodAttr.addAnnotation(SwaggerApiUtils.annotApiImplicitParams(constPool, apiImplicitParams));
            }
            // 添加@ApiResponses 注解
        	methodAttr.addAnnotation(SwaggerApiUtils.annotApiResponses(constPool, new MvcApiResponse(0, "Invoke Success", Object.class)));
            
            methodInfo.addAttribute(methodAttr);
        }
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;

	}
		
	
	/**
	 * 
	 * 根据参数构造一个新的方法
	 * @param rtClass ：返回对象类型
	 * @param method ：方法注释信息
	 * @param bound  ：方法绑定数据信息
	 * @param params ： 参数信息
	 * @param <T> 	   ： 参数泛型
	 * @return {@link EndpointApiCtClassBuilder} instance
	 * @throws CannotCompileException if can't compile
	 * @throws NotFoundException  if not found
	 */ 
	public <T> EndpointApiCtClassBuilder newMethod(final Class<T> rtClass, final MvcMethod method, final MvcBound bound, MvcParam<?>... params) throws CannotCompileException, NotFoundException {
	       
		ConstPool constPool = this.classFile.getConstPool();
		
		// 创建抽象方法
		CtClass returnType = rtClass != null ? pool.get(rtClass.getName()) : CtClass.voidType;
		CtMethod ctMethod = null;
		// 方法参数
		CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);
		// 有参方法
		if(parameters != null && parameters.length > 0) {
			ctMethod = new CtMethod(returnType, method.getName(), parameters, declaring);
		} 
		// 无参方法 
		else {
			ctMethod = new CtMethod(returnType, method.getName() , null, declaring);
		}
        // 设置方法体
        EndpointApiUtils.methodBody(ctMethod, method);
        // 设置方法异常捕获逻辑
        EndpointApiUtils.methodCatch(pool, ctMethod);
        // @GetMapping | @PostMapping | @PutMapping | @DeleteMapping | @PatchMapping 注解
        EndpointApiUtils.methodAnnotations(ctMethod, constPool, method, bound, params);
        
        // 为方法添加  @ApiOperation | @ApiImplicitParams | @ApiResponses 注解
        if(annotApi) {
        	
        	// 获取方法属性对象
            AnnotationsAttribute methodAttr = JavassistUtils.getAnnotationsAttribute(ctMethod);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            
            // 添加 @ApiOperation 注解
        	methodAttr.addAnnotation(SwaggerApiUtils.annotApiOperation(constPool, String.format("Method : %s", method.getName()), bound.getNotes()));
            // 添加 @ApiImplicitParams 注解
            if(params != null && params.length > 0) {

				MvcApiImplicitParam[] apiImplicitParams = Arrays.stream(params).map(param -> {
					MvcApiImplicitParam implicitParam = new MvcApiImplicitParam(param.getName(), "", param.isRequired(),
							param.getType().getName());
					implicitParam.setDefaultValue(param.getDef());
					return implicitParam;
				}).collect(Collectors.toList()).toArray(new MvcApiImplicitParam[params.length]);

				methodAttr.addAnnotation(SwaggerApiUtils.annotApiImplicitParams(constPool, apiImplicitParams));
            }
            // 添加@ApiResponses 注解
            if(!rtClass.isAssignableFrom(Void.class)) {
            	methodAttr.addAnnotation(SwaggerApiUtils.annotApiResponses(constPool, new MvcApiResponse(0, "Invoke Success", rtClass)));
            }
            
            methodInfo.addAttribute(methodAttr);
        }
        
        //新增方法
        declaring.addMethod(ctMethod);
        
        return this;
	}
	 
	public <T> EndpointApiCtClassBuilder removeMethod(final String methodName, MvcParam<?>... params) throws NotFoundException {
		
		// 有参方法
		if(params != null && params.length > 0) {
			
			// 方法参数
			CtClass[] parameters = EndpointApiUtils.makeParams(pool, params);
			
			// 检查方法是否已经定义
			if(!JavassistUtils.hasMethod(declaring, methodName, parameters)) {
				return this;
			}
			
			declaring.removeMethod(declaring.getDeclaredMethod(methodName, parameters));
			
		}
		else {
			
			// 检查方法是否已经定义
			if(!JavassistUtils.hasMethod(declaring, methodName)) {
				return this;
			}
			
			declaring.removeMethod(declaring.getDeclaredMethod(methodName));
			
		}
		
		return this;
	}

}