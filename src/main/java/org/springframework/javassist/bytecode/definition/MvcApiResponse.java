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
 * Mutable POJO describing a single entry inside the Swagger
 * {@code @ApiResponses} annotation emitted by
 * {@code SwaggerApiUtils#annotApiResponses}.
 *
 * <p>Each instance corresponds to one HTTP status code that the documented
 * endpoint may return. The POJO carries the status code, a human-readable
 * message, an optional response payload class, an optional reference to a
 * remote schema, and an optional container hint ({@code List}, {@code Set} or
 * {@code Map}).</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see io.swagger.annotations.ApiResponse
 * @see org.springframework.javassist.utils.SwaggerApiUtils#annotApiResponses(javassist.bytecode.ConstPool, MvcApiResponse...)
 */
public class MvcApiResponse {

	/**
	 * The HTTP status code of the response.
	 * <p>
	 * The value should be one of the formal <a target="_blank" href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">HTTP Status Code Definitions</a>.
	 */
	int code;

	/**
	 * Human-readable message to accompany the response.
	 */
	String message;

	/**
	 * Optional response class to describe the payload of the message.
	 * <p>
	 * Corresponds to the {@code schema} field of the response message object.
	 */
	Class<?> response = Void.class;

	/**
	 * Specifies a reference to the response type. The specified reference can be
	 * either local or remote and will be used as-is, and will override any
	 * specified response() class.
	 */
	String reference = "";

	/**
	 * Declares a container wrapping the response.
	 * <p>
	 * Valid values are "List", "Set" or "Map". Any other value will be ignored.
	 */
	String responseContainer = "";

	/**
	 * Builds a response entry with just an HTTP status code and a human
	 * message.
	 *
	 * @param code    the HTTP status code
	 * @param message the human-readable message
	 * @since 3.0.0
	 */
	public MvcApiResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * Builds a response entry that also declares a response payload class.
	 *
	 * @param code     the HTTP status code
	 * @param message  the human-readable message
	 * @param response the response payload class
	 * @since 3.0.0
	 */
	public MvcApiResponse(int code, String message, Class<?> response) {
		this.code = code;
		this.message = message;
		this.response = response;
	}

	/**
	 * Builds a response entry that also declares a container hint.
	 *
	 * @param code             the HTTP status code
	 * @param message          the human-readable message
	 * @param response         the response payload class
	 * @param responseContainer container wrapping the response (List/Set/Map)
	 * @since 3.0.0
	 */
	public MvcApiResponse(int code, String message, Class<?> response, String responseContainer) {
		this.code = code;
		this.message = message;
		this.response = response;
		this.responseContainer = responseContainer;
	}

	/**
	 * Builds a response entry that includes every attribute: status code,
	 * message, payload class, reference and container.
	 *
	 * @param code             the HTTP status code
	 * @param message          the human-readable message
	 * @param response         the response payload class
	 * @param reference        external schema reference, overrides {@code response}
	 * @param responseContainer container wrapping the response
	 * @since 3.0.0
	 */
	public MvcApiResponse(int code, String message, Class<?> response, String reference, String responseContainer) {
		this.code = code;
		this.message = message;
		this.response = response;
		this.reference = reference;
		this.responseContainer = responseContainer;
	}

	/**
	 * Returns the HTTP status code.
	 *
	 * @return the HTTP status code
	 * @since 3.0.0
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Overrides the HTTP status code.
	 *
	 * @param code the new HTTP status code
	 * @since 3.0.0
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Returns the human-readable message.
	 *
	 * @return the message, may be {@code null}
	 * @since 3.0.0
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Overrides the human-readable message.
	 *
	 * @param message the new message
	 * @since 3.0.0
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the response payload class.
	 *
	 * @return the response class, defaults to {@link Void}
	 * @since 3.0.0
	 */
	public Class<?> getResponse() {
		return response;
	}

	/**
	 * Overrides the response payload class.
	 *
	 * @param response the new response class
	 * @since 3.0.0
	 */
	public void setResponse(Class<?> response) {
		this.response = response;
	}

	/**
	 * Returns the external schema reference.
	 *
	 * @return the reference, never {@code null} (defaults to empty string)
	 * @since 3.0.0
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Overrides the external schema reference. When non-empty it overrides
	 * the response class.
	 *
	 * @param reference the new reference
	 * @since 3.0.0
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Returns the container wrapping the response ({@code List}, {@code Set}
	 * or {@code Map}).
	 *
	 * @return the container name, never {@code null}
	 * @since 3.0.0
	 */
	public String getResponseContainer() {
		return responseContainer;
	}

	/**
	 * Overrides the container wrapping the response.
	 *
	 * @param responseContainer the new container name
	 * @since 3.0.0
	 */
	public void setResponseContainer(String responseContainer) {
		this.responseContainer = responseContainer;
	}

}