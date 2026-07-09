package org.springframework.javassist.bytecode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.utils.EndpointApiUtils;

import io.github.hiwepy.javassist.bytecode.CtAnnotationBuilder;
import io.github.hiwepy.javassist.bytecode.CtFieldBuilder;
import io.github.hiwepy.javassist.utils.ClassPoolFactory;
import io.github.hiwepy.javassist.utils.JavassistUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

/**
 * 动态构建对象
 */
public class CtClassBuilder implements Builder<CtClass> {
	
	protected final static String SETTER_STR    = "set";  
	protected final static String GETTER_STR    = "get";  
    // type/fieldName  
    protected final static String fieldTemplate = "private %s %s;";  
    
	// 构建动态类
	protected ClassPool pool = null;
	protected CtClass declaring  = null;
	protected ClassFile classFile = null;
	//private Loader loader = new Loader(pool);
	protected boolean annotApi = false;
	
	public CtClassBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		this(ClassPoolFactory.getDefaultPool(), classname, Object.class);
	}
	
	public CtClassBuilder(final String classname, final Class<?> superclass) throws CannotCompileException, NotFoundException  {
		this(ClassPoolFactory.getDefaultPool(), classname, superclass);
	}
	
	public CtClassBuilder(final ClassPool pool, final String classname, final Class<?> superclass) throws CannotCompileException, NotFoundException {
		
		this.pool = pool;
		this.declaring = EndpointApiUtils.makeClass(pool, classname);
		
		/* 获得 JaxwsHandler 类作为动态类的父类 */
		CtClass superCtClass = pool.get(superclass.getName());
		declaring.setSuperclass(superCtClass);
		
		// 默认添加无参构造器  
		declaring.addConstructor(CtNewConstructor.defaultConstructor(declaring));
		
		this.classFile = this.declaring.getClassFile();
		
	}
	
	/**
	 * 添加字段注解 @Autowired 实现对象注入
	 * @param type		： The type attribute value of @Autowired 
	 * @param name		： The name attribute value of @Autowired 
	 * @param required 	： Declares whether the annotated dependency is required.
	 * @param <T> 	  	： 参数泛型 
	 * @return {@link CtClassBuilder} instance
	 * @throws CannotCompileException if can't compile
	 * @throws NotFoundException  if not found
	 */
	public <T> CtClassBuilder autowired(Class<T> type, String name, boolean required) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.classFile.getConstPool();
		
		// 属性字段
        CtField field = new CtField(pool.get(type.getName()), name, declaring);
        field.setModifiers(Modifier.PRIVATE);

        // 在属性上添加注解(Autowired)
        CtAnnotationBuilder.create(Autowired.class, constPool).addBooleanMember("required", required).markField(field);

        //新增Field
        declaring.addField(field);

		return this;
	}
	
	/**
	 * 添加字段注解 @Autowired 实现对象注入
	 * @param required 	： Declares whether the annotated dependency is required.
	 * @param qualifier ： The qualifier attribute value of @Autowired 
	 * @return {@link CtClassBuilder} instance
	 * @throws CannotCompileException if can't compile
	 * @throws NotFoundException  if not found
	 */
	public CtClassBuilder autowiredHandler(boolean required, String qualifier) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.classFile.getConstPool();
		
		// 属性字段
        CtField field = declaring.getField("handler");
       
        AnnotationsAttribute attribute = JavassistUtils.getFieldAnnotationsAttribute(field);
        
        // 在属性上添加注解(Qualifier)
        if(StringUtils.isNotBlank(qualifier)) {
        	Annotation annot = CtAnnotationBuilder.create(Qualifier.class, constPool).addStringMember("value", qualifier).build();
        	attribute.addAnnotation(annot);
        }
        
        // 在属性上添加注解(Autowired)
        attribute.addAnnotation(CtAnnotationBuilder.create(Autowired.class, constPool).addBooleanMember("required", required).build());
        

       // field.getFieldInfo().addAttribute(attribute);

        //新增Field
        // declaring.addField(field);

		return this;
	}
	
	/**
	 * 添加字段注解 @Autowired @Qualifier 实现对象注入
	 * @param type		： The type attribute value of @Autowired 
	 * @param name		： The name attribute value of @Autowired 
	 * @param required 	： Declares whether the annotated dependency is required.
	 * @param qualifier ： The qualifier attribute value of @Autowired 
	 * @param <T> 	   ： 参数泛型
	 * @return {@link CtClassBuilder} instance
	 * @throws CannotCompileException if can't compile
	 * @throws NotFoundException  if not found
	 */
	public <T> CtClassBuilder autowired(Class<T> type, String name, boolean required, String qualifier) throws CannotCompileException, NotFoundException {
		
		ConstPool constPool = this.classFile.getConstPool();
		
		// 属性字段
        CtField field = new CtField(pool.get(type.getName()), name, declaring);
        field.setModifiers(Modifier.PRIVATE);

        // 在属性上添加注解(Qualifier)
        if(StringUtils.isNotBlank(qualifier)) {
            CtAnnotationBuilder.create(Qualifier.class, constPool).addStringMember("value", qualifier).markField(field);
        }
        
        // 在属性上添加注解(Autowired)
        CtAnnotationBuilder.create(Autowired.class, constPool).addBooleanMember("required", required).markField(field);
        
        //新增Field
        declaring.addField(field);

		return this;
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 * @param uid			: The value of uid
	 * @param json			: The value of json
	 * @return {@link CtClassBuilder} instance
	 */
	public CtClassBuilder bind(final String uid, final String json) {
		return bind(new MvcBound(uid, json));
	}
	
	/**
	 * 通过给动态类增加 <code>@WebBound</code>注解实现，数据的绑定
	 * @param bound			: The {@link MvcBound} instance
	 * @return {@link CtClassBuilder} instance
	 */
	public CtClassBuilder bind(final MvcBound bound) {

		ConstPool constPool = this.classFile.getConstPool();
		Annotation annot = EndpointApiUtils.annotWebBound(constPool, bound);
		JavassistUtils.addClassAnnotation(declaring, annot);
		
		return this;
	}
	
	/**
     * Compiles the given source code and creates a field.
     * Examples of the source code are:
     * 
     * <pre>
     * "public String name;"
     * "public int k = 3;"</pre>
     *
     * <p>Note that the source code ends with <code>';'</code>
     * (semicolon).
     *
     * @param src               the source text.
     * @return {@link CtClassBuilder} instance
     * @throws CannotCompileException if can't compile
     */
	public CtClassBuilder makeField(final String src) throws CannotCompileException {
		//创建属性
        declaring.addField(CtField.make(src, declaring));
		return this;
	}
	
	public <T> CtClassBuilder makeField(final Class<T> fieldClass, final String fieldName) throws CannotCompileException {
		//创建属性
		CtField newField = CtField.make(String.format(fieldTemplate, fieldClass.getName(), fieldName), declaring);  
        declaring.addField(newField);
		return this;
	}
	
	public <T> CtClassBuilder newField(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		CtFieldBuilder.create(declaring, this.pool.get(fieldClass.getName()), fieldName, fieldValue);
		return this;	
	}
	
	public CtClassBuilder removeField(final String fieldName) throws NotFoundException {
		
		// 检查字段是否已经定义
		if(!JavassistUtils.hasField(declaring, fieldName)) {
			return this;
		}
		
		declaring.removeField(declaring.getDeclaredField(fieldName));
		
		return this;
	}
	
	/**
     * Compiles the given source code and creates a method.
     * The source code must include not only the method body
     * but the whole declaration, for example,
     *
     * <pre>"public Object id(Object obj) { return obj; }"</pre>
     *
     * @param src               the source text. 
     * @return {@link CtClassBuilder} instance
     * @throws CannotCompileException if can't compile
     */
	public CtClassBuilder makeMethod(final String src) throws CannotCompileException {
		//创建方法 
		declaring.addMethod(CtMethod.make(src, declaring));
		return this;
	}
	
	
	public CtClassBuilder makeSetter(final String fieldName) throws CannotCompileException, NotFoundException {
		//创建方法 
		CtField newField = declaring.getDeclaredField(fieldName);
		String setMethodName = SETTER_STR + StringUtils.capitalize(fieldName);  
		CtMethod setter = CtNewMethod.setter(setMethodName, newField);  
		declaring.addMethod(setter);
		return this;
	}
	
	public CtClassBuilder makeGetter(final String fieldName) throws CannotCompileException, NotFoundException {
		//创建方法 
		CtField newField = declaring.getDeclaredField(fieldName);
		String getMethodName = GETTER_STR + StringUtils.capitalize(fieldName);  
		CtMethod getter = CtNewMethod.getter(getMethodName, newField);  
		declaring.addMethod(getter);
		return this;
	}
	
	@Override
	public CtClass build() {
        return declaring;
	}
	
	/**
	 * 
	 * javassist在加载类时会用Hashtable将类信息缓存到内存中，这样随着类的加载，内存会越来越大，甚至导致内存溢出。
	 * 如果应用中要加载的类比较多，建议在使用完CtClass之后删除缓存
	 * @return The Class 
	 * @throws CannotCompileException if can't compile
	 */
	public Class<?> toClass() throws CannotCompileException {
        try {
        	// 通过类加载器加载该CtClass
			return declaring.toClass();
		} finally {
			// 将该class从ClassPool中删除
			declaring.detach();
		} 
	}
	
	public Object toInstance(final InvocationHandler handler) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        try {
        	// 设置InvocationHandler参数构造器
			declaring.addConstructor(EndpointApiUtils.makeConstructor(pool, declaring));
			// 通过类加载器加载该CtClass，并通过构造器初始化对象
			return declaring.toClass().getConstructor(InvocationHandler.class).newInstance(handler);
		} finally {
			// 将该class从ClassPool中删除
			declaring.detach();
		} 
	}

}