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
 * Mutable POJO describing a single parameter of a dynamically generated
 * controller method.
 *
 * <p>The descriptor is consumed by
 * {@code EndpointApiUtils#annotParams(javassist.bytecode.ConstPool, MvcParam...)}
 * which translates the {@link #from} value into the matching Spring MVC
 * annotation ({@code @CookieValue}, {@code @PathVariable}, ...) when emitting
 * the generated method signature.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @param <T> the declared Java type of the parameter
 * @since 3.0.0
 * @see MvcParamFrom
 * @see org.springframework.javassist.utils.EndpointApiUtils#annotParams(javassist.bytecode.ConstPool, MvcParam...)
 */
public class MvcParam<T> {

	/**
	 * Parameter Java type.
	 */
	private Class<T> type;

	/**
	 * Parameter name.
	 */
	private String name;

	/**
	 * Source of the parameter; controls which Spring annotation is emitted.
	 *
	 * @see MvcParamFrom
	 */
	private MvcParamFrom from = MvcParamFrom.PARAM;

	/**
	 * Whether the parameter is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown
	 * if the parameter is missing in the request. Switch this to
	 * {@code false} if you prefer a {@code null} value if the parameter is
	 * not present in the request.
	 * <p>Alternatively, provide a {@link #def}, which implicitly
	 * sets this flag to {@code false}.
	 */
	private boolean required = true;

	/**
	 * Defines the default value of request meta-data that is bound using one of the
	 * following annotations:
	 * The default value is used if the corresponding meta-data is not present in the request.
	 */
	private String def;

	/**
	 * Builds a parameter descriptor with just a type and name. The source
	 * defaults to {@link MvcParamFrom#PARAM}.
	 *
	 * @param type the parameter type
	 * @param name the parameter name
	 * @since 3.0.0
	 */
	public MvcParam(Class<T> type, String name) {
		this.type = type;
		this.name = name;
	}

	/**
	 * Builds a parameter descriptor with an explicit source.
	 *
	 * @param type the parameter type
	 * @param name the parameter name
	 * @param from the parameter source
	 * @since 3.0.0
	 */
	public MvcParam(Class<T> type, String name, MvcParamFrom from) {
		this.type = type;
		this.name = name;
		this.from = from;
	}

	/**
	 * Builds a parameter descriptor that carries an explicit default value.
	 *
	 * @param type the parameter type
	 * @param name the parameter name
	 * @param from the parameter source
	 * @param def  the default value
	 * @since 3.0.0
	 */
	public MvcParam(Class<T> type, String name, MvcParamFrom from, String def ) {
		this.type = type;
		this.name = name;
		this.from = from;
		this.def = def;
	}

	/**
	 * Builds a parameter descriptor with a default value and the implicit
	 * {@link MvcParamFrom#PARAM} source.
	 *
	 * @param type the parameter type
	 * @param name the parameter name
	 * @param def  the default value
	 * @since 3.0.0
	 */
	public MvcParam(Class<T> type, String name, String def ) {
		this.type = type;
		this.name = name;
		this.def = def;
	}

	/**
	 * Returns the parameter Java type.
	 *
	 * @return the parameter type
	 * @since 3.0.0
	 */
	public Class<T> getType() {
		return type;
	}

	/**
	 * Overrides the parameter Java type.
	 *
	 * @param type the new parameter type
	 * @since 3.0.0
	 */
	public void setType(Class<T> type) {
		this.type = type;
	}

	/**
	 * Returns the parameter name.
	 *
	 * @return the parameter name
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
	 * Returns the parameter source.
	 *
	 * @return the {@link MvcParamFrom} value
	 * @since 3.0.0
	 */
	public MvcParamFrom getFrom() {
		return from;
	}

	/**
	 * Overrides the parameter source.
	 *
	 * @param from the new parameter source
	 * @since 3.0.0
	 */
	public void setFrom(MvcParamFrom from) {
		this.from = from;
	}

	/**
	 * Indicates whether the parameter is required.
	 *
	 * @return {@code true} when the parameter must be present in the request
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
	 * Returns the parameter default value.
	 *
	 * @return the default value, may be {@code null}
	 * @since 3.0.0
	 */
	public String getDef() {
		return def;
	}

	/**
	 * Overrides the parameter default value.
	 *
	 * @param def the new default value
	 * @since 3.0.0
	 */
	public void setDef(String def) {
		this.def = def;
	}

}