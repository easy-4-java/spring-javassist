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

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

/**
 * Enumeration mapping every supported Spring MVC parameter-binding source to
 * the corresponding Spring annotation that the Javassist builder will emit.
 *
 * <p>Each enum value carries a short, stable {@code key} that is used by
 * {@link #valueOfIgnoreCase(String)} when reconstructing the enum from a
 * string serialisation, and a longer {@code desc} that documents the matching
 * Spring annotation. The enumeration also offers a {@link #toMap()} helper
 * used by the dynamic Web-bound layer and a {@link #fromList()} helper that
 * enumerates every value into a UI-friendly list.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.springframework.web.bind.annotation.CookieValue
 * @see org.springframework.web.bind.annotation.MatrixVariable
 * @see org.springframework.web.bind.annotation.PathVariable
 * @see org.springframework.web.bind.annotation.RequestAttribute
 * @see org.springframework.web.bind.annotation.RequestBody
 * @see org.springframework.web.bind.annotation.RequestHeader
 * @see org.springframework.web.bind.annotation.RequestParam
 * @see org.springframework.web.bind.annotation.RequestPart
 */
public enum MvcParamFrom {

	/**
	 * Annotation which indicates that a method parameter should be bound to an HTTP
	 * cookie.
	 *
	 * @see org.springframework.web.bind.annotation.CookieValue
	 */
	COOKIE("COOKIE", "HTTP Cookie"),

	/**
	 * Annotation which indicates that a method parameter should be bound to a
	 * name-value pair within a path segment. Supported for {@link RequestMapping}
	 * annotated handler methods in Servlet environments.
	 *
	 * @see org.springframework.web.bind.annotation.MatrixVariable
	 */
	MATRIX("MATRIX", "Name-value Pair Within A Path Segment"),

	/**
	 * Annotation which indicates that a method parameter should be bound to a URI
	 * template variable. Supported for {@link RequestMapping} annotated handler
	 * methods in Servlet environments.
	 *
	 * @see org.springframework.web.bind.annotation.PathVariable
	 */
	PATH("PATH", "URI Template Variable"),

	/**
	 * Annotation to bind a method parameter to a request attribute.
	 *
	 * @see org.springframework.web.bind.annotation.RequestAttribute
	 */
	ATTR("ATTR", "Request Attribute"),

	/**
	 * Annotation indicating a method parameter should be bound to the body of the
	 * web request. The body of the request is passed through an
	 * {@link HttpMessageConverter} to resolve the method argument depending on the
	 * content type of the request. Optionally, automatic validation can be applied
	 * by annotating the argument with {@code @Valid}.
	 *
	 * @see org.springframework.web.bind.annotation.RequestBody
	 */
	BODY("BODY", "Request Body"),

	/**
	 * Annotation which indicates that a method parameter should be bound to a web
	 * request header.
	 *
	 * <p>
	 * Supported for annotated handler methods in Servlet and Portlet environments.
	 * </p>
	 *
	 * <p>
	 * If the method parameter is {@link java.util.Map Map&lt;String, String&gt;},
	 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String,
	 * String&gt;}, or {@link org.springframework.http.HttpHeaders HttpHeaders} then
	 * the map is populated with all header names and values.
	 * </p>
	 *
	 * @see org.springframework.web.bind.annotation.RequestHeader
	 */
	HEADER("HEADER", "Request Header"),

	/**
	 * Annotation which indicates that a method parameter should be bound to a web
	 * request parameter.
	 *
	 * <p>
	 * Supported for annotated handler methods in Servlet and Portlet environments.
	 * </p>
	 *
	 * <p>
	 * If the method parameter type is {@link Map} and a request parameter name is
	 * specified, then the request parameter value is converted to a {@link Map}
	 * assuming an appropriate conversion strategy is available.
	 * </p>
	 * <p>
	 * If the method parameter is {@link java.util.Map Map&lt;String, String&gt;} or
	 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}
	 * and a parameter name is not specified, then the map parameter is
	 * populated with all request parameter names and values.
	 * </p>
	 *
	 * @see org.springframework.web.bind.annotation.RequestParam
	 */
	PARAM("PARAM", "Request Parameter"),

	/**
	 * Annotation that can be used to associate the part of a "multipart/form-data"
	 * request with a method argument.
	 *
	 * <p>
	 * Supported method argument types include {@link MultipartFile} in conjunction
	 * with Spring's {@link MultipartResolver} abstraction,
	 * {@code javax.servlet.http.Part} in conjunction with Servlet 3.0 multipart
	 * requests, or otherwise for any other method argument, the content of the part
	 * is passed through an {@link HttpMessageConverter} taking into consideration
	 * the 'Content-Type' header of the request part. This is analogous to
	 * what @{@link RequestBody} does to resolve an argument based on the content of
	 * a non-multipart regular request.
	 *
	 * <p>
	 * Note that @{@link RequestParam} annotation can also be used to associate the
	 * part of a "multipart/form-data" request with a method argument supporting the
	 * same method argument types. The main difference is that when the method
	 * argument is not a String, @{@link RequestParam} relies on type conversion via
	 * a registered {@link Converter} or {@link PropertyEditor}
	 * while @{@link RequestPart} relies on {@link HttpMessageConverter}s taking
	 * into consideration the 'Content-Type' header of the request
	 * part. @{@link RequestParam} is likely to be used with name-value form fields
	 * while @{@link RequestPart} is likely to be used with parts containing more
	 * complex content (e.g. JSON, XML).
	 *
	 * @see org.springframework.web.bind.annotation.RequestPart
	 */
	PART("PART", "\"multipart/form-data\" Request Part");

	/**
	 * The stable, machine-friendly key used when reconstructing the enum from a
	 * string serialisation through {@link #valueOfIgnoreCase(String)}.
	 */
	private String key;

	/**
	 * A human-readable description of the source. Suitable for surfacing in
	 * documentation or admin UIs.
	 */
	private String desc;

	/**
	 * Builds an enum value.
	 *
	 * @param key  the stable key
	 * @param desc the human-readable description
	 */
	private MvcParamFrom(String key, String desc) {
		this.key = key;
		this.desc = desc;

	}

	/**
	 * Returns the stable key associated with this value.
	 *
	 * @return the stable key
	 * @since 3.0.0
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns the human-readable description.
	 *
	 * @return the description
	 * @since 3.0.0
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Compares this enum value against another enum value for equality.
	 *
	 * @param from the value to compare against
	 * @return {@code true} when both values are the same enum constant
	 * @since 3.0.0
	 */
	public boolean equals(MvcParamFrom from) {
		return this.compareTo(from) == 0;
	}

	/**
	 * Resolves a string against {@link #valueOfIgnoreCase(String)} and compares
	 * it to this value.
	 *
	 * @param from the textual representation of an enum value
	 * @return {@code true} when the resolved enum matches this value
	 * @throws NoSuchElementException if the input string does not correspond to
	 *                                any enum value
	 * @since 3.0.0
	 */
	public boolean equals(String from) {
		return this.compareTo(MvcParamFrom.valueOfIgnoreCase(from)) == 0;
	}

	/**
	 * Resolves a {@link MvcParamFrom} from its textual {@code key}.
	 *
	 * @param from the textual representation (case-sensitive)
	 * @return the matching enum value
	 * @throws NoSuchElementException if no value carries the given key
	 * @since 3.0.0
	 */
	public static MvcParamFrom valueOfIgnoreCase(String from) {
		for (MvcParamFrom fromEnum : MvcParamFrom.values()) {
			if (fromEnum.getKey().equals(from)) {
				return fromEnum;
			}
		}
		throw new NoSuchElementException("Cannot found MvcParamFrom with from '" + from + "'.");
	}

	/**
	 * Renders this value as a small map suitable for serialisation or for
	 * direct use by view templates.
	 *
	 * @return a map with {@code key} and {@code desc} entries
	 * @since 3.0.0
	 */
	public Map<String, String> toMap() {
		Map<String, String> driverMap = new HashMap<String, String>();
		driverMap.put("key", this.getKey());
		driverMap.put("desc", this.getDesc());
		return driverMap;
	}

	/**
	 * Lists every enum value as a sequence of maps, typically used to seed a
	 * UI dropdown.
	 *
	 * @return a fresh list, never {@code null}
	 * @since 3.0.0
	 */
	public static List<Map<String, String>> fromList() {
		List<Map<String, String>> fromList = new LinkedList<Map<String, String>>();
		for (MvcParamFrom fromEnum : MvcParamFrom.values()) {
			fromList.add(fromEnum.toMap());
		}
		return fromList;
	}

}