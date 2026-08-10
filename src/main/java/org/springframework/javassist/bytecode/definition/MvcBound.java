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
 * Mutable POJO that captures the data a generated controller method is bound
 * to through the {@code @WebBound} annotation.
 *
 * <p>The instance is consumed by
 * {@code EndpointApiUtils#annotWebBound(javassist.bytecode.ConstPool, MvcBound)}
 * when it builds the annotation that will be emitted on the generated
 * {@link org.springframework.javassist.annotation.WebBound}-decorated method.
 * Three values are carried:</p>
 * <ul>
 *     <li>{@link #uid} &mdash; the primary key the framework uses to look up
 *         additional context (typically the entity id of the bound record);</li>
 *     <li>{@link #json} &mdash; a fallback JSON payload that the handler
 *         should expose when no record can be resolved by {@code uid};</li>
 *     <li>{@link #notes} &mdash; an optional human-readable note surfaced
 *         through the generated Swagger documentation.</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.springframework.javassist.annotation.WebBound
 * @see org.springframework.javassist.utils.EndpointApiUtils#annotWebBound(javassist.bytecode.ConstPool, MvcBound)
 */
public class MvcBound {

    /**
     * Builds a binding that only carries the primary key.
     *
     * @param uid the primary key the framework should use to look up bound
     *            data, may be {@code null} in which case {@link #setUid(String)}
     *            will store the empty default
     * @since 3.0.0
     */
    public MvcBound(String uid) {
    	this.uid = uid;
	}

    /**
     * Builds a binding carrying both the primary key and a fallback JSON
     * payload.
     *
     * @param uid  the primary key, may be {@code null}
     * @param json the fallback JSON payload, may be {@code null}
     * @since 3.0.0
     */
	public MvcBound(String uid, String json) {
		this.uid = uid;
		this.json = json;
	}

	/**
	 * The primary key of the bound data record. Used by the framework to
	 * fetch additional context for the method.
	 */
	private String uid = "";

	/**
	 * The fallback JSON payload to expose when no bound record can be
	 * resolved through {@link #uid}.
	 */
	private String json = "";

	/**
	 * Free-form human-readable notes surfaced through the generated Swagger
	 * documentation of the bound method.
	 */
	private String notes = "";

	/**
	 * Returns the bound record's primary key.
	 *
	 * @return the primary key, never {@code null} (defaults to empty string)
	 * @since 3.0.0
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Overrides the bound record's primary key.
	 *
	 * @param uid the new primary key, may be {@code null} which is stored as
	 *            an empty string
	 * @since 3.0.0
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Returns the fallback JSON payload.
	 *
	 * @return the JSON payload, never {@code null} (defaults to empty string)
	 * @since 3.0.0
	 */
	public String getJson() {
		return json;
	}

	/**
	 * Overrides the fallback JSON payload.
	 *
	 * @param json the new JSON payload, may be {@code null}
	 * @since 3.0.0
	 */
	public void setJson(String json) {
		this.json = json;
	}

	/**
	 * Returns the human-readable notes for the binding.
	 *
	 * @return the notes, never {@code null} (defaults to empty string)
	 * @since 3.0.0
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Overrides the human-readable notes surfaced through the generated
	 * Swagger documentation.
	 *
	 * @param notes the new notes, may be {@code null}
	 * @since 3.0.0
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

}