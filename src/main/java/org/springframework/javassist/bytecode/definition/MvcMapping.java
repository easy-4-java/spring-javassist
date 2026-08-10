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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Mutable POJO that mirrors the configuration supported by Spring's
 * {@code @RequestMapping} family of annotations.
 *
 * <p>The values stored here are passed verbatim to
 * {@code EndpointApiUtils#annotRequestMapping(javassist.bytecode.ConstPool, MvcMapping)}
 * which emits the actual {@code @RequestMapping}/{@code @GetMapping}/...
 * annotation on the dynamically generated controller.</p>
 *
 * <p>The semantics of every attribute are intentionally identical to those of
 * the Spring annotation &mdash; see the {@code @see} links below for the
 * canonical Spring documentation of each field.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @see org.springframework.javassist.utils.EndpointApiUtils#annotRequestMapping(javassist.bytecode.ConstPool, MvcMapping)
 */
public class MvcMapping {


	/**
	 * Assign a name to this mapping.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used on both levels, a combined name is derived by concatenation
	 * with "#" as separator.
	 * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
	 * @see org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
	 */
	private String name = "";

	/**
	 * In a Servlet environment only: the path mapping URIs (e.g. "/myPath.do").
	 * Ant-style path patterns are also supported (e.g. "/myPath/*.do").
	 * At the method level, relative paths (e.g. "edit.do") are supported within
	 * the primary mapping expressed at the type level. Path mapping URIs may
	 * contain placeholders (e.g. "/${connect}")
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this primary mapping, narrowing it for a specific handler method.
	 * @see org.springframework.web.bind.annotation.ValueConstants#DEFAULT_NONE
	 * @since 4.2
	 */
	private final String[] path;

	/**
	 * The HTTP request methods to map to, narrowing the primary mapping:
	 * GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this HTTP method restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 */
	private RequestMethod[] method = RequestMethod.values();


	/**
	 * The parameters of the mapped request, narrowing the primary mapping.
	 * <p>Same format for any environment: a sequence of "myParam=myValue" style
	 * expressions, with a request only mapped if each such parameter is found
	 * to have the given value. Expressions can be negated by using the "!=" operator,
	 * as in "myParam!=myValue". "myParam" style expressions are also supported,
	 * with such parameters having to be present in the request (allowed to have
	 * any value). Finally, "!myParam" style expressions indicate that the
	 * specified parameter is <i>not</i> supposed to be present in the request.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this parameter restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 * <p>Parameter mappings are considered as restrictions that are enforced at
	 * the type level. The primary path mapping (i.e. the specified URI value)
	 * still has to uniquely identify the target handler, with parameter mappings
	 * simply expressing preconditions for invoking the handler.
	 */
	private String[] params = new String[]{};


	/**
	 * The headers of the mapped request, narrowing the primary mapping.
	 * <p>Same format for any environment: a sequence of "My-Header=myValue" style
	 * expressions, with a request only mapped if each such header is found
	 * to have the given value. Expressions can be negated by using the "!=" operator,
	 * as in "My-Header!=myValue". "My-Header" style expressions are also supported,
	 * with such headers having to be present in the request (allowed to have
	 * any value). Finally, "!My-Header" style expressions indicate that the
	 * specified header is <i>not</i> supposed to be present in the request.
	 * <p>Also supports media type wildcards (*), for headers such as Accept
	 * and Content-Type. For instance,
	 * <pre class="code">
	 * &#064;RequestMapping(value = "/something", headers = "content-type=text/*")
	 * </pre>
	 * will match requests with a Content-Type of "text/html", "text/plain", etc.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this header restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 * @see org.springframework.http.MediaType
	 */
	private String[] headers = new String[]{};

	/**
	 * The consumable media types of the mapped request, narrowing the primary mapping.
	 * <p>The format is a single media type or a sequence of media types,
	 * with a request only mapped if the {@code Content-Type} matches one of these media types.
	 * Examples:
	 * <pre class="code">
	 * consumes = "text/plain"
	 * consumes = {"text/plain", "application/*"}
	 * </pre>
	 * Expressions can be negated by using the "!" operator, as in "!text/plain", which matches
	 * all requests with a {@code Content-Type} other than "text/plain".
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings override
	 * this consumes restriction.
	 * @see org.springframework.http.MediaType
	 * @see javax.servlet.http.HttpServletRequest#getContentType()
	 */
	private String[] consumes = new String[]{};

	/**
	 * The producible media types of the mapped request, narrowing the primary mapping.
	 * <p>The format is a single media type or a sequence of media types,
	 * with a request only mapped if the {@code Accept} matches one of these media types.
	 * Examples:
	 * <pre class="code">
	 * produces = "text/plain"
	 * produces = {"text/plain", "application/*"}
	 * produces = "application/json; charset=UTF-8"
	 * </pre>
	 * <p>It affects the actual content type written, for example to produce a JSON response
	 * with UTF-8 encoding, {@code "application/json; charset=UTF-8"} should be used.
	 * <p>Expressions can be negated by using the "!" operator, as in "!text/plain", which matches
	 * all requests with an {@code Accept} other than "text/plain".
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings override
	 * this produces restriction.
	 * @see org.springframework.http.MediaType
	 */
	private String[] produces = new String[] { MediaType.ALL_VALUE };

	/**
	 * Builds a mapping with only the path and HTTP methods specified. The
	 * remaining narrowing attributes are left at their defaults.
	 *
	 * @param path   the URI paths to match, may be {@code null}
	 * @param method the HTTP methods to match; when empty, defaults to
	 *               {@link RequestMethod#values()} (all methods)
	 * @since 3.0.0
	 */
	public MvcMapping(String[] path, RequestMethod... method) {
		this.path = path;
		this.method = method;
	}

	/**
	 * Builds a mapping with every attribute populated.
	 *
	 * @param name     a logical name for the mapping
	 * @param path     the URI paths to match
	 * @param method   the HTTP methods to match
	 * @param params   request parameters that must match
	 * @param headers  request headers that must match
	 * @param consumes consumable media types
	 * @param produces producible media types
	 * @since 3.0.0
	 */
	public MvcMapping(String name, String[] path, RequestMethod[] method, String[] params, String[] headers,
			String[] consumes, String[] produces) {
		this.name = name;
		this.path = path;
		this.method = method;
		this.params = params;
		this.headers = headers;
		this.consumes = consumes;
		this.produces = produces;
	}


	/**
	 * Returns the logical name of the mapping.
	 *
	 * @return the name, never {@code null} (defaults to empty string)
	 * @since 3.0.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * Overrides the logical name.
	 *
	 * @param name the new name
	 * @since 3.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the HTTP methods the mapping narrows to.
	 *
	 * @return the HTTP methods
	 * @since 3.0.0
	 */
	public RequestMethod[] getMethod() {
		return method;
	}

	/**
	 * Overrides the HTTP methods the mapping narrows to.
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
	 * Overrides the request-parameter preconditions.
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
	 * Overrides the request-header preconditions.
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
	 * @return the {@code Content-Type} expressions
	 * @since 3.0.0
	 */
	public String[] getConsumes() {
		return consumes;
	}

	/**
	 * Overrides the consumable media types.
	 *
	 * @param consumes the new {@code Content-Type} expressions
	 * @since 3.0.0
	 */
	public void setConsumes(String[] consumes) {
		this.consumes = consumes;
	}

	/**
	 * Returns the producible media types.
	 *
	 * @return the {@code Accept} expressions
	 * @since 3.0.0
	 */
	public String[] getProduces() {
		return produces;
	}

	/**
	 * Overrides the producible media types.
	 *
	 * @param produces the new {@code Accept} expressions
	 * @since 3.0.0
	 */
	public void setProduces(String[] produces) {
		this.produces = produces;
	}

	/**
	 * Returns the URI paths of the mapping.
	 *
	 * @return the path array (immutable &mdash; the only way to change it is
	 *         to construct a new {@link MvcMapping})
	 * @since 3.0.0
	 */
	public String[] getPath() {
		return path;
	}

}