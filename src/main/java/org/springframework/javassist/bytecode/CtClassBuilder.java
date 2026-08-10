package org.springframework.javassist.bytecode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.utils.EndpointApiUtils;

import io.github.easy4j.javassist.bytecode.CtAnnotationBuilder;
import io.github.easy4j.javassist.bytecode.CtFieldBuilder;
import io.github.easy4j.javassist.utils.ClassPoolFactory;
import io.github.easy4j.javassist.utils.JavassistUtils;

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
 * Fluent builder that materialises a Javassist {@link CtClass} by adding
 * fields, methods and annotations one step at a time.
 *
 * <p>{@code CtClassBuilder} is the parent class of every domain-specific
 * builder (the {@code EndpointApiCtClassBuilder} and
 * {@code ReactiveHandlerCtClassBuilder} subclasses). It owns the underlying
 * {@link ClassPool}, the generated {@link CtClass} (stored in
 * {@link #declaring}), the matching {@link ClassFile} and a flag
 * ({@link #annotApi}) that lets downstream builders know whether Swagger
 * annotations should be emitted.</p>
 *
 * <p>Instances follow a fluent style &mdash; every mutator returns
 * {@code this} (or, in subclasses, the covariant return type) and the caller
 * can chain calls until {@link #build()} or {@link #toClass()} is invoked.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see EndpointApiCtClassBuilder
 * @see ReactiveHandlerCtClassBuilder
 */
public class CtClassBuilder implements Builder<CtClass> {

	/**
	 * The literal prefix used by {@link #makeSetter(String)} to derive the
	 * setter method name.
	 */
	protected final static String SETTER_STR    = "set";

	/**
	 * The literal prefix used by {@link #makeGetter(String)} to derive the
	 * getter method name.
	 */
	protected final static String GETTER_STR    = "get";

	/**
	 * Template used by {@link #makeField(Class, String)} to declare a
	 * private field whose name and type are supplied at runtime. Two
	 * {@code %s} placeholders are filled with the field type and the field
	 * name respectively.
	 */
	protected final static String fieldTemplate = "private %s %s;";

	/**
	 * The class pool used to resolve CtClasses.
	 */
	protected ClassPool pool = null;

	/**
	 * The CtClass being built.
	 */
	protected CtClass declaring  = null;

	/**
	 * The class file backing the generated CtClass.
	 */
	protected ClassFile classFile = null;
	//private Loader loader = new Loader(pool);

	/**
	 * Whether the API documentation annotations
	 * ({@code @ApiOperation}, {@code @ApiResponses}, ...) should be emitted
	 * on the methods added by this builder. The flag is toggled by
	 * {@code EndpointApiCtClassBuilder#api} / {@code #apiIgnore}.
	 */
	protected boolean annotApi = false;

	/**
	 * Builds a class named {@code classname} extending {@link Object}.
	 *
	 * @param classname the fully-qualified name of the class to create
	 * @throws CannotCompileException if a new class cannot be created
	 * @throws NotFoundException      if a referenced class cannot be resolved
	 * @since 3.0.0
	 */
	public CtClassBuilder(final String classname) throws CannotCompileException, NotFoundException  {
		this(ClassPoolFactory.getDefaultPool(), classname, Object.class);
	}

	/**
	 * Builds a class named {@code classname} extending the given super-class.
	 *
	 * @param classname  the fully-qualified name of the class to create
	 * @param superclass the super-class
	 * @throws CannotCompileException if a new class cannot be created
	 * @throws NotFoundException      if a referenced class cannot be resolved
	 * @since 3.0.0
	 */
	public CtClassBuilder(final String classname, final Class<?> superclass) throws CannotCompileException, NotFoundException  {
		this(ClassPoolFactory.getDefaultPool(), classname, superclass);
	}

	/**
	 * Builds a class named {@code classname} extending the given super-class
	 * inside the supplied pool.
	 *
	 * @param pool       the class pool, never {@code null}
	 * @param classname  the fully-qualified name of the class to create
	 * @param superclass the super-class
	 * @throws CannotCompileException if a new class cannot be created
	 * @throws NotFoundException      if a referenced class cannot be resolved
	 * @since 3.0.0
	 */
	public CtClassBuilder(final ClassPool pool, final String classname, final Class<?> superclass) throws CannotCompileException, NotFoundException {

		this.pool = pool;
		this.declaring = EndpointApiUtils.makeClass(pool, classname);

		/* Resolves the super-class through Javassist and assigns it. */
		CtClass superCtClass = pool.get(superclass.getName());
		declaring.setSuperclass(superCtClass);

		// Adds a default no-argument constructor
		declaring.addConstructor(CtNewConstructor.defaultConstructor(declaring));

		this.classFile = this.declaring.getClassFile();

	}

	/**
	 * Adds a new private field annotated with {@code @Autowired(required = ...)}
	 * so that Spring will inject a collaborator into the generated class.
	 *
	 * @param type     the type of the new field
	 * @param name     the name of the new field
	 * @param required whether the dependency is required
	 * @param <T>      the type of the field
	 * @return {@code this} for fluent chaining
	 * @throws CannotCompileException if the field cannot be compiled
	 * @throws NotFoundException      if {@code type} cannot be resolved
	 * @since 3.0.0
	 */
	public <T> CtClassBuilder autowired(Class<T> type, String name, boolean required) throws CannotCompileException, NotFoundException {

		ConstPool constPool = this.classFile.getConstPool();

		// The backing field
        CtField field = new CtField(pool.get(type.getName()), name, declaring);
        field.setModifiers(Modifier.PRIVATE);

        // Mark the field with @Autowired(required = ...)
        CtAnnotationBuilder.create(Autowired.class, constPool).addBooleanMember("required", required).markField(field);

        // Add the field to the generated class
        declaring.addField(field);

		return this;
	}

	/**
	 * Adds the {@code @Autowired(required = ...)} annotation (and optionally
	 * {@code @Qualifier(...)}) to the pre-existing {@code handler} field.
	 *
	 * @param required  whether the dependency is required
	 * @param qualifier the qualifier value; ignored when blank
	 * @return {@code this} for fluent chaining
	 * @throws CannotCompileException if the field cannot be compiled
	 * @throws NotFoundException      if the {@code handler} field cannot be
	 *                                resolved
	 * @since 3.0.0
	 */
	public CtClassBuilder autowiredHandler(boolean required, String qualifier) throws CannotCompileException, NotFoundException {

		ConstPool constPool = this.classFile.getConstPool();

		// The pre-existing handler field
        CtField field = declaring.getField("handler");

        AnnotationsAttribute attribute = JavassistUtils.getFieldAnnotationsAttribute(field);

        // Add @Qualifier when a non-blank qualifier is supplied
        if(StringUtils.isNotBlank(qualifier)) {
        	Annotation annot = CtAnnotationBuilder.create(Qualifier.class, constPool).addStringMember("value", qualifier).build();
        	attribute.addAnnotation(annot);
        }

        // Always add @Autowired(required = ...)
        attribute.addAnnotation(CtAnnotationBuilder.create(Autowired.class, constPool).addBooleanMember("required", required).build());


       // field.getFieldInfo().addAttribute(attribute);

        // Field was already added by the parent class.
        // declaring.addField(field);

		return this;
	}

	/**
	 * Adds a new private field annotated with both {@code @Autowired(required = ...)}
	 * and an optional {@code @Qualifier(...)}.
	 *
	 * @param type      the type of the new field
	 * @param name      the name of the new field
	 * @param required  whether the dependency is required
	 * @param qualifier the qualifier value; ignored when blank
	 * @param <T>       the type of the field
	 * @return {@code this} for fluent chaining
	 * @throws CannotCompileException if the field cannot be compiled
	 * @throws NotFoundException      if {@code type} cannot be resolved
	 * @since 3.0.0
	 */
	public <T> CtClassBuilder autowired(Class<T> type, String name, boolean required, String qualifier) throws CannotCompileException, NotFoundException {

		ConstPool constPool = this.classFile.getConstPool();

		// The backing field
        CtField field = new CtField(pool.get(type.getName()), name, declaring);
        field.setModifiers(Modifier.PRIVATE);

        // Add @Qualifier when a non-blank qualifier is supplied
        if(StringUtils.isNotBlank(qualifier)) {
            CtAnnotationBuilder.create(Qualifier.class, constPool).addStringMember("value", qualifier).markField(field);
        }

        // Always add @Autowired(required = ...)
        CtAnnotationBuilder.create(Autowired.class, constPool).addBooleanMember("required", required).markField(field);

        // Add the field to the generated class
        declaring.addField(field);

		return this;
	}

	/**
	 * Attaches a {@code @WebBound} annotation carrying the supplied binding
	 * values to the generated class.
	 *
	 * @param uid the bound record primary key
	 * @param json the fallback JSON payload
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public CtClassBuilder bind(final String uid, final String json) {
		return bind(new MvcBound(uid, json));
	}

	/**
	 * Attaches a {@code @WebBound} annotation derived from the supplied
	 * {@link MvcBound} descriptor.
	 *
	 * @param bound the binding descriptor
	 * @return {@code this} for fluent chaining
	 * @since 3.0.0
	 */
	public CtClassBuilder bind(final MvcBound bound) {

		ConstPool constPool = this.classFile.getConstPool();
		Annotation annot = EndpointApiUtils.annotWebBound(constPool, bound);
		JavassistUtils.addClassAnnotation(declaring, annot);

		return this;
	}

	/**
     * Compiles the given source code and creates a field. The source text
     * ends with a {@code ';'} and may include initialisers.
     *
     * @param src the source text.
     * @return {@code this} for fluent chaining
     * @throws CannotCompileException if the source text cannot be compiled
     * @since 3.0.0
     */
	public CtClassBuilder makeField(final String src) throws CannotCompileException {
		// Adds the field generated from the source text.
        declaring.addField(CtField.make(src, declaring));
		return this;
	}

	/**
     * Compiles the {@link #fieldTemplate} against the supplied type/name and
     * adds the resulting field to the generated class.
     *
     * @param fieldClass the field type
     * @param fieldName  the field name
     * @param <T>        the field type
     * @return {@code this} for fluent chaining
     * @throws CannotCompileException if the source text cannot be compiled
     * @since 3.0.0
     */
	public <T> CtClassBuilder makeField(final Class<T> fieldClass, final String fieldName) throws CannotCompileException {
		// Creates the field using the canonical template.
		CtField newField = CtField.make(String.format(fieldTemplate, fieldClass.getName(), fieldName), declaring);
        declaring.addField(newField);
		return this;
	}

	/**
     * Creates a typed field with an initial value through the
     * {@code CtFieldBuilder} helper.
     *
     * @param fieldClass the field type
     * @param fieldName  the field name
     * @param fieldValue the initial value, as a string
     * @param <T>        the field type
     * @return {@code this} for fluent chaining
     * @throws CannotCompileException if the field cannot be compiled
     * @throws NotFoundException      if {@code fieldClass} cannot be resolved
     * @since 3.0.0
     */
	public <T> CtClassBuilder newField(final Class<T> fieldClass, final String fieldName, final String fieldValue) throws CannotCompileException, NotFoundException {
		CtFieldBuilder.create(declaring, this.pool.get(fieldClass.getName()), fieldName, fieldValue);
		return this;
	}

	/**
     * Removes a previously declared field. When the field does not exist
     * the call is a silent no-op.
     *
     * @param fieldName the field name
     * @return {@code this} for fluent chaining
     * @throws NotFoundException if the field is declared but cannot be
     *                            resolved
     * @since 3.0.0
     */
	public CtClassBuilder removeField(final String fieldName) throws NotFoundException {

		// Skip silently when the field has not been declared.
		if(!JavassistUtils.hasField(declaring, fieldName)) {
			return this;
		}

		declaring.removeField(declaring.getDeclaredField(fieldName));

		return this;
	}

	/**
     * Compiles the given source code and creates a method. The source text
     * must include the entire declaration, for example
     * {@code "public Object id(Object obj) { return obj; }"}.
     *
     * @param src the source text.
     * @return {@code this} for fluent chaining
     * @throws CannotCompileException if the source text cannot be compiled
     * @since 3.0.0
     */
	public CtClassBuilder makeMethod(final String src) throws CannotCompileException {
		// Creates the method
		declaring.addMethod(CtMethod.make(src, declaring));
		return this;
	}

	/**
     * Generates a public setter for the supplied field using
     * {@link CtNewMethod#setter(String, CtField)}.
     *
     * @param fieldName the field name
     * @return {@code this} for fluent chaining
     * @throws CannotCompileException if the setter cannot be compiled
     * @throws NotFoundException      if the field cannot be resolved
     * @since 3.0.0
     */
	public CtClassBuilder makeSetter(final String fieldName) throws CannotCompileException, NotFoundException {
		// Creates the setter
		CtField newField = declaring.getDeclaredField(fieldName);
		String setMethodName = SETTER_STR + StringUtils.capitalize(fieldName);
		CtMethod setter = CtNewMethod.setter(setMethodName, newField);
		declaring.addMethod(setter);
		return this;
	}

	/**
     * Generates a public getter for the supplied field using
     * {@link CtNewMethod#getter(String, CtField)}.
     *
     * @param fieldName the field name
     * @return {@code this} for fluent chaining
     * @throws CannotCompileException if the getter cannot be compiled
     * @throws NotFoundException      if the field cannot be resolved
     * @since 3.0.0
     */
	public CtClassBuilder makeGetter(final String fieldName) throws CannotCompileException, NotFoundException {
		// Creates the getter
		CtField newField = declaring.getDeclaredField(fieldName);
		String getMethodName = GETTER_STR + StringUtils.capitalize(fieldName);
		CtMethod getter = CtNewMethod.getter(getMethodName, newField);
		declaring.addMethod(getter);
		return this;
	}

	/**
	 * Returns the generated {@link CtClass} without freezing it.
	 *
	 * @return the generated {@link CtClass}
	 * @since 3.0.0
	 */
	@Override
	public CtClass build() {
        return declaring;
	}

	/**
	 * Finalises the generated {@link CtClass} into a regular JVM class and
	 * detaches the {@link CtClass} from the class pool so its metadata can
	 * be reclaimed.
	 *
	 * <p>Javassist caches class metadata in an internal hash-table that grows
	 * unbounded; calling this helper ensures the metadata of the generated
	 * class is released as soon as the JVM class has been loaded.</p>
	 *
	 * @return the JVM class generated from the current {@link CtClass}
	 * @throws CannotCompileException if the class cannot be compiled
	 * @since 3.0.0
	 */
	public Class<?> toClass() throws CannotCompileException {
        try {
        	// Loads the generated class through the context class loader.
			return declaring.toClass();
		} finally {
			// Detaches the class from the pool so the cache is freed.
			declaring.detach();
		}
	}

	/**
	 * Adds an {@link InvocationHandler}-accepting constructor, loads the
	 * class through the JVM and instantiates it with the supplied handler.
	 *
	 * @param handler the {@link InvocationHandler} that should back the new
	 *                instance, never {@code null}
	 * @return the freshly instantiated proxy
	 * @throws CannotCompileException         if the class or constructor
	 *                                         cannot be compiled
	 * @throws NotFoundException              if a referenced type cannot be
	 *                                         resolved
	 * @throws InstantiationException         if the class cannot be
	 *                                         instantiated
	 * @throws IllegalAccessException         if the constructor is not
	 *                                         accessible
	 * @throws IllegalArgumentException       if the constructor arguments do
	 *                                         not match
	 * @throws InvocationTargetException      if the underlying constructor
	 *                                         throws
	 * @throws NoSuchMethodException          if the generated constructor
	 *                                         cannot be located
	 * @throws SecurityException              if the caller is not allowed to
	 *                                         access the constructor
	 * @since 3.0.0
	 */
	public Object toInstance(final InvocationHandler handler) throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        try {
        	// Adds the InvocationHandler-aware constructor.
			declaring.addConstructor(EndpointApiUtils.makeConstructor(pool, declaring));
			// Loads the class and uses the generated constructor to
			// instantiate the proxy with the supplied handler.
			return declaring.toClass().getConstructor(InvocationHandler.class).newInstance(handler);
		} finally {
			// Detaches the class from the pool so the cache is freed.
			declaring.detach();
		}
	}

}