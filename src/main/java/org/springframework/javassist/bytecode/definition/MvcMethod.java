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

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Mutable POJO describing a single dynamically generated handler method.
 *
 * <p>The instance is consumed by
 * {@code EndpointApiCtClassBuilder#newMethod(Class, MvcMethod, MvcBound, MvcParam...)}
 * where the {@link #name} and {@link #path} attributes seed the Javassist
 * method declaration, while {@link #method}, {@link #params}, {@link #headers},
 * {@link #consumes}, {@link #produces} and {@link #responseBody} populate the
 * matching {@code @*Mapping} annotation.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.springframework.javassist.bytecode.EndpointApiCtClassBuilder#newMethod(Class, MvcMethod, MvcBound, MvcParam...)
 * @see org.springframework.web.bind.annotation.RequestMapping
 */
public class MvcMethod {

	/**
	 * Java method name.
	 */
	private final String name;

	/**
	 * In a Servlet environment only: the path mapping URIs (e.g. "/myPath.do").
	 * Ant-style path patterns are also supported (e.g. "/myPath/*.do"). At the
	 * method level, relative paths (e.g. "edit.do") are supported within the
	 * primary mapping expressed at the type level. Path mapping URIs may contain
	 * placeholders (e.g. "/${connect}")
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings inherit this primary mapping,
	 * narrowing it for a specific handler method.
	 *
	 * @see org.springframework.web.bind.annotation.ValueConstants#DEFAULT_NONE
	 * @since 4.2
	 */
	private final String[] path;

	/**
	 * The HTTP request methods to map to, narrowing the primary mapping: GET, POST,
	 * HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings inherit this HTTP method
	 * restriction (i.e. the type-level restriction gets checked before the handler
	 * method is even resolved).
	 */
	private RequestMethod[] method = RequestMethod.values();

	/**
	 * The parameters of the mapped request, narrowing the primary mapping.
	 * <p>
	 * Same format for any environment: a sequence of "myParam=myValue" style
	 * expressions, with a request only mapped if each such parameter is found to
	 * have the given value. Expressions can be negated by using the "!=" operator,
	 * as in "myParam!=myValue". "myParam" style expressions are also supported,
	 * with such parameters having to be present in the request (allowed to have any
	 * value). Finally, "!myParam" style expressions indicate that the specified
	 * parameter is <i>not</i> supposed to be present in the request.
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings inherit this parameter
	 * restriction (i.e. the type-level restriction gets checked before the handler
	 * method is even resolved).
	 * <p>
	 * Parameter mappings are considered as restrictions that are enforced at the
	 * type level. The primary path mapping (i.e. the specified URI value) still has
	 * to uniquely identify the target handler, with parameter mappings simply
	 * expressing preconditions for invoking the handler.
	 */
	private String[] params = new String[] {};

	/**
	 * The headers of the mapped request, narrowing the primary mapping.
	 * <p>
	 * Same format for any environment: a sequence of "My-Header=myValue" style
	 * expressions, with a request only mapped if each such header is found to have
	 * the given value. Expressions can be negated by using the "!=" operator, as in
	 * "My-Header!=myValue". "My-Header" style expressions are also supported, with
	 * such headers having to be present in the request (allowed to have any value).
	 * Finally, "!My-Header" style expressions indicate that the specified header is
	 * <i>not</i> supposed to be present in the request.
	 * <p>
	 * Also supports media type wildcards (*), for headers such as Accept and
	 * Content-Type. For instance,
	 *
	 * <pre class="code">
	 * &#064;RequestMapping(value = "/something", headers = "content-type=text/*")
	 * </pre>
	 *
	 * will match requests with a Content-Type of "text/html", "text/plain", etc.
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings inherit this header restriction
	 * (i.e. the type-level restriction gets checked before the handler method is
	 * even resolved).
	 * @see org.springframework.http.MediaType
	 */
	private String[] headers = new String[] {};

	/**
	 * The consumable media types of the mapped request, narrowing the primary
	 * mapping.
	 * <p>
	 * The format is a single media type or a sequence of media types, with a
	 * request only mapped if the {@code Content-Type} matches one of these media
	 * types. Examples:
	 *
	 * <pre class="code">
	 * consumes = "text/plain"
	 * consumes = {"text/plain", "application/*"}
	 * </pre>
	 *
	 * Expressions can be negated by using the "!" operator, as in "!text/plain",
	 * which matches all requests with a {@code Content-Type} other than
	 * "text/plain".
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings override this consumes
	 * restriction.
	 *
	 * @see org.springframework.http.MediaType
	 * @see javax.servlet.http.HttpServletRequest#getContentType()
	 */
	private String[] consumes = new String[] {};

	/**
	 * The producible media types of the mapped request, narrowing the primary
	 * mapping.
	 * <p>
	 * The format is a single media type or a sequence of media types, with a
	 * request only mapped if the {@code Accept} matches one of these media types.
	 * Examples:
	 *
	 * <pre class="code">
	 * produces = "text/plain"
	 * produces = {"text/plain", "application/*"}
	 * produces = "application/json; charset=UTF-8"
	 * </pre>
	 * <p>
	 * It affects the actual content type written, for example to produce a JSON
	 * response with UTF-8 encoding, {@code "application/json; charset=UTF-8"}
	 * should be used.
	 * <p>
	 * Expressions can be negated by using the "!" operator, as in "!text/plain",
	 * which matches all requests with an {@code Accept} other than "text/plain".
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b> When used
	 * at the type level, all method-level mappings override this produces
	 * restriction.
	 * @see org.springframework.http.MediaType
	 */
	private String[] produces = new String[] {};

	/**
	 * Annotation that indicates a method return value should be bound to the web
	 * response body. Supported for annotated handler methods in Servlet
	 * environments.
	 */
	private boolean responseBody = true;

	/**
	 * Builds a method descriptor for a single HTTP method without explicit
	 * {@code @ResponseBody} (defaults to {@code true}).
	 *
	 * @param name   the Java method name
	 * @param path   the URI paths
	 * @param method the HTTP method
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, RequestMethod method) {
		this(name, path, true, method, null, null, null, null);
	}

	/**
	 * Builds a method descriptor that may target multiple HTTP methods.
	 *
	 * @param name    the Java method name
	 * @param path    the URI paths
	 * @param methods the HTTP methods
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, RequestMethod[] methods) {
		this(name, path, true, methods, null, null, null, null);
	}

	/**
	 * Builds a method descriptor with explicit {@code @ResponseBody} toggle and
	 * a single HTTP method.
	 *
	 * @param name         the Java method name
	 * @param path         the URI paths
	 * @param responseBody whether the method should be annotated with
	 *                     {@code @ResponseBody}
	 * @param method       the HTTP method
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method) {
		this(name, path, responseBody, method, null, null, null, null);
	}

	/**
	 * Builds a method descriptor with explicit {@code @ResponseBody} toggle and
	 * a list of HTTP methods.
	 *
	 * @param name         the Java method name
	 * @param path         the URI paths
	 * @param responseBody whether the method should be annotated with
	 *                     {@code @ResponseBody}
	 * @param methods      the HTTP methods
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods) {
		this(name, path, responseBody, methods, null, null, null, null);
	}

	/**
	 * Builds a method descriptor that also carries the producible media types.
	 *
	 * @param name         the Java method name
	 * @param path         the URI paths
	 * @param responseBody whether the method should be annotated with
	 *                     {@code @ResponseBody}
	 * @param method       the HTTP method
	 * @param produces     producible media types
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] produces) {
		this(name, path, responseBody, method, null, null, produces, null);
	}

	/**
	 * Builds a method descriptor with multiple HTTP methods and producible media
	 * types.
	 *
	 * @param name         the Java method name
	 * @param path         the URI paths
	 * @param responseBody whether the method should be annotated with
	 *                     {@code @ResponseBody}
	 * @param methods      the HTTP methods
	 * @param produces     producible media types
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods, String[] produces) {
		this(name, path, responseBody, methods, null, null, produces, null);
	}

	/**
	 * Builds a method descriptor with producible and consumable media types.
	 *
	 * @param name         the Java method name
	 * @param path         the URI paths
	 * @param responseBody whether the method should be annotated with
	 *                     {@code @ResponseBody}
	 * @param method       the HTTP method
	 * @param produces     producible media types
	 * @param consumes     consumable media types
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] produces, String[] consumes) {
		this(name, path, responseBody, method, null, null, produces, consumes);
	}

	/**
	 * Builds a method descriptor with multiple HTTP methods and producible and
	 * consumable media types.
	 *
	 * @param name         the Java method name
	 * @param path         the URI paths
	 * @param responseBody whether the method should be annotated with
	 *                     {@code @ResponseBody}
	 * @param methods      the HTTP methods
	 * @param produces     producible media types
	 * @param consumes     consumable media types
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods, String[] produces, String[] consumes) {
		this(name, path, responseBody, methods, null, null, produces, consumes);
	}

	/**
	 * Builds a method descriptor that also captures parameter and header
	 * preconditions for a single HTTP method.
	 *
	 * @param name         the Java method name
	 * @param path         the URI paths
	 * @param responseBody whether the method should be annotated with
	 *                     {@code @ResponseBody}
	 * @param method       the HTTP method
	 * @param params       request-parameter preconditions
	 * @param headers      request-header preconditions
	 * @param produces     producible media types
	 * @param consumes     consumable media types
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod method, String[] params, String[] headers,
			String[] produces, String[] consumes) {
		this(name, path, responseBody, new RequestMethod[] { method }, params, headers, produces, consumes);
	}

	/**
	 * Full constructor covering every attribute supported by the method
	 * descriptor.
	 *
	 * @param name         the Java method name
	 * @param path         the URI paths
	 * @param responseBody whether the method should be annotated with
	 *                     {@code @ResponseBody}
	 * @param methods      the HTTP methods
	 * @param params       request-parameter preconditions
	 * @param headers      request-header preconditions
	 * @param produces     producible media types
	 * @param consumes     consumable media types
	 * @since 3.0.0
	 */
	public MvcMethod(String name, String[] path, boolean responseBody, RequestMethod[] methods, String[] params, String[] headers,
			String[] produces, String[] consumes) {
		this.name = name;
		this.path = path;
		this.responseBody = responseBody;
		this.method = ArrayUtils.isNotEmpty(methods) ? methods : RequestMethod.values();
		this.params = ArrayUtils.isNotEmpty(params) ? params : new String[] {};
		this.headers = ArrayUtils.isNotEmpty(headers) ? headers : new String[] {};
		this.produces = ArrayUtils.isNotEmpty(produces) ? produces : new String[] {};
		this.consumes = ArrayUtils.isNotEmpty(consumes) ? consumes : new String[] {};
	}

	/**
	 * Returns the HTTP methods the method narrows to.
	 *
	 * @return the HTTP methods
	 * @since 3.0.0
	 */
	public RequestMethod[] getMethod() {
		return method;
	}

	/**
	 * Sets the HTTP methods.
	 *
	 * @param method the new HTTP methods
	 * @since 3.0.0
	 */
	public void setMethod(RequestMethod[] method) {
		this.method = method;
	}

	/**
	 * Returns the request-parameter preconditions.
	 *
	 * @return the parameter expressions
	 * @since 3.0.0
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * Sets the request-parameter preconditions.
	 *
	 * @param params the new parameter expressions
	 * @since 3.0.0
	 */
	public void setParams(String[] params) {
		this.params = params;
	}

	/**
	 * Returns the request-header preconditions.
	 *
	 * @return the header expressions
	 * @since 3.0.0
	 */
	public String[] getHeaders() {
		return headers;
	}

	/**
	 * Sets the request-header preconditions.
	 *
	 * @param headers the new header expressions
	 * @since 3.0.0
	 */
	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	/**
	 * Returns the consumable media types.
	 *
	 * @return the consumable media types
	 * @since 3.0.0
	 */
	public String[] getConsumes() {
		return consumes;
	}

	/**
	 * Sets the consumable media types.
	 *
	 * @param consumes the new consumable media types
	 * @since 3.0.0
	 */
	public void setConsumes(String[] consumes) {
		this.consumes = consumes;
	}

	/**
	 * Returns the producible media types.
	 *
	 * @return the producible media types
	 * @since 3.0.0
	 */
	public String[] getProduces() {
		return produces;
	}

	/**
	 * Sets the producible media types.
	 *
	 * @param produces the new producible media types
	 * @since 3.0.0
	 */
	public void setProduces(String[] produces) {
		this.produces = produces;
	}

	/**
	 * Indicates whether the method should be annotated with
	 * {@code @ResponseBody}.
	 *
	 * @return {@code true} when {@code @ResponseBody} will be emitted
	 * @since 3.0.0
	 */
	public boolean isResponseBody() {
		return responseBody;
	}

	/**
	 * Toggles whether the method should be annotated with
	 * {@code @ResponseBody}.
	 *
	 * @param responseBody the new flag
	 * @since 3.0.0
	 */
	public void setResponseBody(boolean responseBody) {
		this.responseBody = responseBody;
	}

	/**
	 * Returns the Java method name.
	 *
	 * @return the method name
	 * @since 3.0.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the URI paths.
	 *
	 * @return the URI paths (immutable)
	 * @since 3.0.0
	 */
	public String[] getPath() {
		return path;
	}

}