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
package org.springframework.javassist.bytecode.definition;

/**
 * Mutable POJO describing a single Swagger {@code @ApiImplicitParam} entry
 * that will be emitted by the dynamic controller generators.
 *
 * <p>The values stored here are consumed verbatim by
 * {@code SwaggerApiUtils#annotApiImplicitParams} when it builds the
 * {@code @ApiImplicitParams} annotation on a generated method. Every
 * field maps directly to one of the standard Swagger {@code @ApiImplicitParam}
 * attributes, so changing a field on this object is enough to influence the
 * produced documentation.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see io.swagger.annotations.ApiImplicitParam
 * @see org.springframework.javassist.utils.SwaggerApiUtils#annotApiImplicitParams(javassist.bytecode.ConstPool, MvcApiImplicitParam...)
 */
public class MvcApiImplicitParam {

	/**
	 * Name of the parameter.
	 * <p>
	 * For proper Swagger functionality, follow these rules when naming your
	 * parameters based on {@link #paramType}:
	 * <ol>
	 * <li>If {@code paramType} is "path", the name should be the associated section
	 * in the path.</li>
	 * <li>For all other cases, the name should be the parameter name as your
	 * application expects to accept.</li>
	 * </ol>
	 *
	 * @see #paramType
	 */
	String name = "";

	/**
	 * A brief description of the parameter.
	 */
	String value = "";

	/**
	 * Describes the default value for the parameter.
	 */
	String defaultValue = "";

	/**
	 * Limits the acceptable values for this parameter.
	 * <p>
	 * There are three ways to describe the allowable values:
	 * <ol>
	 * <li>To set a list of values, provide a comma-separated list. For example:
	 * {@code first, second, third}.</li>
	 * <li>To set a range of values, start the value with "range", and surrounding
	 * by square brackets include the minimum and maximum values, or round brackets
	 * for exclusive minimum and maximum values. For example: {@code range[1, 5]},
	 * {@code range(1, 5)}, {@code range[1, 5)}.</li>
	 * <li>To set a minimum/maximum value, use the same format for range but use
	 * "infinity" or "-infinity" as the second value. For example,
	 * {@code range[1, infinity]} means the minimum allowable value of this
	 * parameter is 1.</li>
	 * </ol>
	 */
	String allowableValues = "";

	/**
	 * Specifies if the parameter is required or not.
	 * <p>
	 * Path parameters should always be set as required.
	 */
	boolean required = false;

	/**
	 * Allows for filtering a parameter from the API documentation.
	 * <p>
	 * See io.swagger.core.filter.SwaggerSpecFilter for further details.
	 */
	String access = "";

	/**
	 * Specifies whether the parameter can accept multiple values by having multiple
	 * occurrences.
	 */
	boolean allowMultiple = false;

	/**
	 * The data type of the parameter.
	 * <p>
	 * This can be the class name or a primitive.
	 */
	String dataType = "";

	/**
	 * The class of the parameter.
	 * <p>
	 * Overrides {@code dataType} if provided.
	 */
	Class<?> dataTypeClass = Void.class;

	/**
	 * The parameter type of the parameter.
	 * <p>
	 * Valid values are {@code path}, {@code query}, {@code body}, {@code header} or
	 * {@code form}.
	 */
	String paramType = "";

	/**
	 * A single example for non-body type parameters.
	 */
	String example = "";

	/**
	 * Adds the ability to override the detected type.
	 */
	String type = "";

	/**
	 * Adds the ability to provide a custom format.
	 */
	String format = "";

	/**
	 * Adds the ability to set a format as empty.
	 */
	boolean allowEmptyValue = false;

	/**
	 * Adds ability to be designated as read only.
	 */
	boolean readOnly = false;

	/**
	 * Adds ability to override collectionFormat with `array` types.
	 */
	String collectionFormat = "";

	/**
	 * Creates an empty instance with all attributes defaulted to empty strings
	 * or {@code false}/{@code Void.class} as appropriate.
	 *
	 * @since 3.0.0
	 */
	public MvcApiImplicitParam() {
	}

	/**
	 * Convenience constructor for the most common four-attribute combination.
	 *
	 * @param name     the parameter name
	 * @param value    a brief description of the parameter
	 * @param required whether the parameter is required
	 * @param dataType the data type expressed as a string
	 * @since 3.0.0
	 */
	public MvcApiImplicitParam(String name, String value, boolean required, String dataType) {
		this.name = name;
		this.value = value;
		this.required = required;
		this.dataType = dataType;
	}

	/**
	 * Convenience constructor that supplies the data type as a {@link Class}.
	 *
	 * @param name          the parameter name
	 * @param value         a brief description of the parameter
	 * @param required      whether the parameter is required
	 * @param dataTypeClass the data type as a class reference
	 * @since 3.0.0
	 */
	public MvcApiImplicitParam(String name, String value, boolean required, Class<?> dataTypeClass) {
		this.name = name;
		this.value = value;
		this.required = required;
		this.dataTypeClass = dataTypeClass;
	}

	/**
	 * Full constructor covering every attribute supported by
	 * {@code @ApiImplicitParam}.
	 *
	 * @param name             parameter name
	 * @param value            brief description
	 * @param defaultValue     default value
	 * @param allowableValues  list or range of allowed values
	 * @param required         whether the parameter is required
	 * @param access           filter access string
	 * @param allowMultiple    whether the parameter can appear multiple times
	 * @param dataType         textual data type
	 * @param dataTypeClass    data type as a class
	 * @param paramType        the parameter location (path, query, body, ...)
	 * @param example          example value
	 * @param type             overriding type description
	 * @param format           custom format
	 * @param allowEmptyValue  whether empty values are allowed
	 * @param readOnly         whether the value is read-only
	 * @param collectionFormat the collection format override
	 * @since 3.0.0
	 */
	public MvcApiImplicitParam(String name, String value, String defaultValue, String allowableValues, boolean required,
			String access, boolean allowMultiple, String dataType, Class<?> dataTypeClass, String paramType,
			String example, String type, String format, boolean allowEmptyValue, boolean readOnly,
			String collectionFormat) {
		this.name = name;
		this.value = value;
		this.defaultValue = defaultValue;
		this.allowableValues = allowableValues;
		this.required = required;
		this.access = access;
		this.allowMultiple = allowMultiple;
		this.dataType = dataType;
		this.dataTypeClass = dataTypeClass;
		this.paramType = paramType;
		this.example = example;
		this.type = type;
		this.format = format;
		this.allowEmptyValue = allowEmptyValue;
		this.readOnly = readOnly;
		this.collectionFormat = collectionFormat;
	}



	/**
	 * Returns the parameter name.
	 *
	 * @return the parameter name, never {@code null}
	 * @since 3.0.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * Overrides the parameter name.
	 *
	 * @param name the new parameter name
	 * @since 3.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the brief description of the parameter.
	 *
	 * @return the value, never {@code null}
	 * @since 3.0.0
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Overrides the brief description of the parameter.
	 *
	 * @param value the new value
	 * @since 3.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the default value advertised in the documentation.
	 *
	 * @return the default value, never {@code null}
	 * @since 3.0.0
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Overrides the default value.
	 *
	 * @param defaultValue the new default value
	 * @since 3.0.0
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Returns the list or range of allowed values.
	 *
	 * @return the allowable values string, never {@code null}
	 * @since 3.0.0
	 */
	public String getAllowableValues() {
		return allowableValues;
	}

	/**
	 * Overrides the allowable values.
	 *
	 * @param allowableValues the new allowable values string
	 * @since 3.0.0
	 */
	public void setAllowableValues(String allowableValues) {
		this.allowableValues = allowableValues;
	}

	/**
	 * Indicates whether the parameter is required.
	 *
	 * @return {@code true} when the parameter is required
	 * @since 3.0.0
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Marks the parameter as required or optional.
	 *
	 * @param required the new required flag
	 * @since 3.0.0
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Returns the filter access string used to hide the parameter from the
	 * generated documentation.
	 *
	 * @return the access string, never {@code null}
	 * @since 3.0.0
	 */
	public String getAccess() {
		return access;
	}

	/**
	 * Overrides the filter access string.
	 *
	 * @param access the new access string
	 * @since 3.0.0
	 */
	public void setAccess(String access) {
		this.access = access;
	}

	/**
	 * Indicates whether the parameter may appear multiple times.
	 *
	 * @return {@code true} when the parameter is repeatable
	 * @since 3.0.0
	 */
	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	/**
	 * Marks the parameter as repeatable or single-value.
	 *
	 * @param allowMultiple the new flag
	 * @since 3.0.0
	 */
	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	/**
	 * Returns the textual data type.
	 *
	 * @return the data type string, never {@code null}
	 * @since 3.0.0
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Overrides the textual data type.
	 *
	 * @param dataType the new data type
	 * @since 3.0.0
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * Returns the data type as a {@link Class} reference.
	 *
	 * @return the data type class, defaults to {@link Void}
	 * @since 3.0.0
	 */
	public Class<?> getDataTypeClass() {
		return dataTypeClass;
	}

	/**
	 * Overrides the data type class.
	 *
	 * @param dataTypeClass the new data type class
	 * @since 3.0.0
	 */
	public void setDataTypeClass(Class<?> dataTypeClass) {
		this.dataTypeClass = dataTypeClass;
	}

	/**
	 * Returns the parameter location ({@code path}, {@code query}, ...).
	 *
	 * @return the parameter type, never {@code null}
	 * @since 3.0.0
	 */
	public String getParamType() {
		return paramType;
	}

	/**
	 * Overrides the parameter location.
	 *
	 * @param paramType the new parameter type
	 * @since 3.0.0
	 */
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	/**
	 * Returns the example value used in the documentation.
	 *
	 * @return the example string, never {@code null}
	 * @since 3.0.0
	 */
	public String getExample() {
		return example;
	}

	/**
	 * Overrides the example value.
	 *
	 * @param example the new example
	 * @since 3.0.0
	 */
	public void setExample(String example) {
		this.example = example;
	}

	/**
	 * Returns the override type description.
	 *
	 * @return the type string, never {@code null}
	 * @since 3.0.0
	 */
	public String getType() {
		return type;
	}

	/**
	 * Overrides the type description.
	 *
	 * @param type the new type string
	 * @since 3.0.0
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the custom format string.
	 *
	 * @return the format, never {@code null}
	 * @since 3.0.0
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Overrides the custom format.
	 *
	 * @param format the new format
	 * @since 3.0.0
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Indicates whether empty values are allowed.
	 *
	 * @return {@code true} when empty values are accepted
	 * @since 3.0.0
	 */
	public boolean isAllowEmptyValue() {
		return allowEmptyValue;
	}

	/**
	 * Marks the parameter as accepting or rejecting empty values.
	 *
	 * @param allowEmptyValue the new flag
	 * @since 3.0.0
	 */
	public void setAllowEmptyValue(boolean allowEmptyValue) {
		this.allowEmptyValue = allowEmptyValue;
	}

	/**
	 * Indicates whether the parameter is read-only.
	 *
	 * @return {@code true} when the parameter is read-only
	 * @since 3.0.0
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Marks the parameter as read-only or writable.
	 *
	 * @param readOnly the new flag
	 * @since 3.0.0
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Returns the override for the {@code array} collection format.
	 *
	 * @return the collection format, never {@code null}
	 * @since 3.0.0
	 */
	public String getCollectionFormat() {
		return collectionFormat;
	}

	/**
	 * Overrides the {@code array} collection format.
	 *
	 * @param collectionFormat the new collection format
	 * @since 3.0.0
	 */
	public void setCollectionFormat(String collectionFormat) {
		this.collectionFormat = collectionFormat;
	}

}