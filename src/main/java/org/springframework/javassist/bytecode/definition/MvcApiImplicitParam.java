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
package org.springframework.javassist.bytecode.definition;

public class MvcApiImplicitParam {

	/**
	 * Name of the parameter.
	 * <p>
	 * For proper Swagger functionality, follow these rules when naming your
	 * parameters based on {@link #paramType()}:
	 * <ol>
	 * <li>If {@code paramType} is "path", the name should be the associated section
	 * in the path.</li>
	 * <li>For all other cases, the name should be the parameter name as your
	 * application expects to accept.</li>
	 * </ol>
	 *
	 * @see #paramType()
	 */
	String name = "";

	/**
	 * A brief description of the parameter.
	 */
	String value = "";

	/**
	 * Describes the= value for the parameter.
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
	 * a single example for non-body type parameters
	 *
	 * @return
	 */
	String example = "";

	/**
	 * Adds the ability to override the detected type
	 *
	 *
	 * @return
	 */
	String type = "";

	/**
	 * Adds the ability to provide a custom format
	 *
	 * @return
	 */
	String format = "";

	/**
	 * Adds the ability to set a format as empty
	 *
	 * @return
	 */
	boolean allowEmptyValue = false;

	/**
	 * adds ability to be designated as read only.
	 *
	 */
	boolean readOnly = false;

	/**
	 * adds ability to override collectionFormat with `array` types
	 *
	 */
	String collectionFormat = "";

	public MvcApiImplicitParam() {
	}
	
	public MvcApiImplicitParam(String name, String value, boolean required, String dataType) {
		this.name = name;
		this.value = value;
		this.required = required;
		this.dataType = dataType;
	}
	
	public MvcApiImplicitParam(String name, String value, boolean required, Class<?> dataTypeClass) {
		this.name = name;
		this.value = value;
		this.required = required;
		this.dataTypeClass = dataTypeClass;
	}
	
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



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getAllowableValues() {
		return allowableValues;
	}

	public void setAllowableValues(String allowableValues) {
		this.allowableValues = allowableValues;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Class<?> getDataTypeClass() {
		return dataTypeClass;
	}

	public void setDataTypeClass(Class<?> dataTypeClass) {
		this.dataTypeClass = dataTypeClass;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isAllowEmptyValue() {
		return allowEmptyValue;
	}

	public void setAllowEmptyValue(boolean allowEmptyValue) {
		this.allowEmptyValue = allowEmptyValue;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getCollectionFormat() {
		return collectionFormat;
	}

	public void setCollectionFormat(String collectionFormat) {
		this.collectionFormat = collectionFormat;
	}

}
