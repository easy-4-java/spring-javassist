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
	 * Corresponds to the `schema` field of the response message object.
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
	
	public MvcApiResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public MvcApiResponse(int code, String message, Class<?> response) {
		this.code = code;
		this.message = message;
		this.response = response;
	}
	
	public MvcApiResponse(int code, String message, Class<?> response, String responseContainer) {
		this.code = code;
		this.message = message;
		this.response = response;
		this.responseContainer = responseContainer;
	}
	
	public MvcApiResponse(int code, String message, Class<?> response, String reference, String responseContainer) {
		this.code = code;
		this.message = message;
		this.response = response;
		this.reference = reference;
		this.responseContainer = responseContainer;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Class<?> getResponse() {
		return response;
	}

	public void setResponse(Class<?> response) {
		this.response = response;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getResponseContainer() {
		return responseContainer;
	}

	public void setResponseContainer(String responseContainer) {
		this.responseContainer = responseContainer;
	}

}
