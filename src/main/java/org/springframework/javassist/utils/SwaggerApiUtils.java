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
package org.springframework.javassist.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.javassist.bytecode.definition.MvcApiImplicitParam;
import org.springframework.javassist.bytecode.definition.MvcApiResponse;

import io.github.easy4j.javassist.bytecode.CtAnnotationBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

/**
 * Factory that emits Swagger annotations on Javassist-generated classes and
 * methods.
 *
 * <p>Every method on this class returns a ready-to-attach
 * {@link javassist.bytecode.annotation.Annotation}; the actual attachment is
 * performed by the higher-level
 * {@code EndpointApiCtClassBuilder#newMethod(...)} code. {@code null} values
 * supplied to the helpers are converted to safe defaults ({@code ""} for
 * strings, {@link Void} for response classes, empty arrays for tag lists)
 * so that downstream Swagger renderers never see a {@code null} member.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see EndpointApiUtils
 * @see io.swagger.annotations.ApiOperation
 */
public class SwaggerApiUtils {

	/**
	 * Builds an {@link Api} annotation carrying the supplied tags.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param tags      the Swagger tags
	 * @return the annotation ready to be added to a class
	 * @since 3.0.0
	 */
	public static Annotation annotApi(ConstPool constPool, String... tags) {

		tags = ArrayUtils.isEmpty(tags) ? new String[] { "" } : tags;
		return CtAnnotationBuilder.create(Api.class, constPool).addStringMember("tags", tags).build();

	}

	/**
	 * Builds a Springfox {@code @ApiIgnore} annotation.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param desc      the description that explains why the endpoint is
	 *                  hidden
	 * @return the annotation ready to be added to a class
	 * @since 3.0.0
	 */
	public static Annotation annotApiIgnore(ConstPool constPool, String desc) {
		return CtAnnotationBuilder.create(springfox.documentation.annotations.ApiIgnore.class, constPool).addStringMember("value", desc).build();
	}

	/**
	 * Builds an {@link ApiKeyAuthDefinition} annotation.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param name      the header or query parameter name
	 * @param key       the security-scheme key
	 * @param desc      a short description of the scheme
	 * @param in        the API key location
	 * @return the annotation ready to be added to a class
	 * @since 3.0.0
	 */
	public static Annotation annotApiKeyAuthDefinition(ConstPool constPool, String name, String key, String desc,
			ApiKeyLocation in) {
		return CtAnnotationBuilder.create(ApiKeyAuthDefinition.class, constPool)
				.addStringMember("name", StringUtils.defaultString(name, ""))
				.addStringMember("key", StringUtils.defaultString(key, ""))
				.addStringMember("desc", StringUtils.defaultString(desc, ""))
				.addEnumMember("in", ApiKeyLocation.QUERY).build();
	}

	/**
	 * Builds an {@link ApiOperation} annotation with a short value and
	 * verbose notes.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param value     brief description of the operation (max 120 chars)
	 * @param notes     verbose description of the operation
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, "")).build();
	}

	/**
	 * Builds an {@link ApiOperation} annotation that also declares the
	 * response payload type.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param value     brief description of the operation
	 * @param notes     verbose description of the operation
	 * @param response  the response payload class
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes,
			Class<?> response) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, ""))
				.addClassMember("response", response != null ? response.getName() : Void.class.getName()).build();
	}

	/**
	 * Builds an {@link ApiOperation} annotation that also carries Swagger tags.
	 *
	 * @param constPool the constant pool, never {@code null}
	 * @param value     brief description of the operation
	 * @param notes     verbose description of the operation
	 * @param tags      the operation tags
	 * @param response  the response payload class
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes, String[] tags,
			Class<?> response) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, ""))
				.addStringMember("tags", tags == null ? new String[0] : tags)
				.addClassMember("response", response != null ? response.getName() : Void.class.getName()).build();
	}

	/**
	 * Builds an {@link ApiOperation} annotation that also declares a response
	 * container.
	 *
	 * @param constPool         the constant pool, never {@code null}
	 * @param value             brief description of the operation
	 * @param notes             verbose description of the operation
	 * @param tags              the operation tags
	 * @param response          the response payload class
	 * @param responseContainer container wrapping the response
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes, String[] tags,
			Class<?> response, String responseContainer) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, ""))
				.addStringMember("tags", tags == null ? new String[0] : tags)
				.addClassMember("response", response != null ? response.getName() : Void.class.getName())
				.addStringMember("responseContainer", StringUtils.defaultString(responseContainer, "")).build();
	}

	/**
	 * Builds an {@link ApiOperation} annotation that also declares a response
	 * reference.
	 *
	 * @param constPool         the constant pool, never {@code null}
	 * @param value             brief description of the operation
	 * @param notes             verbose description of the operation
	 * @param tags              the operation tags
	 * @param response          the response payload class
	 * @param responseContainer container wrapping the response
	 * @param responseReference external schema reference, overrides
	 *                          {@code response}
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes, String[] tags,
			Class<?> response, String responseContainer, String responseReference) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, ""))
				.addStringMember("tags", tags == null ? new String[0] : tags)
				.addClassMember("response", response != null ? response.getName() : Void.class.getName())
				.addStringMember("responseContainer", StringUtils.defaultString(responseContainer, ""))
				.addStringMember("responseReference", StringUtils.defaultString(responseReference, "")).build();
	}

	/**
	 * Builds the full {@link ApiOperation} annotation exposing every relevant
	 * Swagger attribute.
	 *
	 * @param constPool         the constant pool, never {@code null}
	 * @param value             brief description of the operation
	 * @param notes             verbose description of the operation
	 * @param tags              the operation tags
	 * @param response          the response payload class
	 * @param responseContainer container wrapping the response
	 * @param responseReference external schema reference
	 * @param httpMethod        HTTP method used by the operation
	 * @param nickname          the operationId
	 * @param produces          producible media types
	 * @param consumes          consumable media types
	 * @param protocols         supported protocols (http, https, ...)
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes, String[] tags,
			Class<?> response, String responseContainer, String responseReference, String httpMethod, String nickname,
			String produces, String consumes, String protocols) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, ""))
				.addStringMember("tags", tags == null ? new String[0] : tags)
				.addClassMember("response", response != null ? response.getName() : Void.class.getName())
				.addStringMember("responseContainer", StringUtils.defaultString(responseContainer, ""))
				.addStringMember("responseReference", StringUtils.defaultString(responseReference, ""))
				.addStringMember("httpMethod", StringUtils.defaultString(httpMethod, ""))
				.addStringMember("nickname", StringUtils.defaultString(nickname, ""))
				.addStringMember("produces", StringUtils.defaultString(produces, ""))
				.addStringMember("consumes", StringUtils.defaultString(consumes, ""))
				.addStringMember("protocols", StringUtils.defaultString(protocols, "")).build();
	}

	/**
	 * Builds the most verbose variant of the {@link ApiOperation} annotation,
	 * adding visibility flags and a status code.
	 *
	 * @param constPool         the constant pool, never {@code null}
	 * @param value             brief description of the operation
	 * @param notes             verbose description of the operation
	 * @param tags              the operation tags
	 * @param response          the response payload class
	 * @param responseContainer container wrapping the response
	 * @param responseReference external schema reference
	 * @param httpMethod        HTTP method used by the operation
	 * @param nickname          the operationId
	 * @param produces          producible media types
	 * @param consumes          consumable media types
	 * @param protocols         supported protocols (http, https, ...)
	 * @param hidden            whether the operation is hidden
	 * @param code              the HTTP status code of the response
	 * @param ignoreJsonView    whether to ignore JsonView annotations while
	 *                          resolving operations and types
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes, String[] tags,
			Class<?> response, String responseContainer, String responseReference, String httpMethod, String nickname,
			String produces, String consumes, String protocols, boolean hidden, int code, boolean ignoreJsonView) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, ""))
				.addStringMember("tags", tags == null ? new String[0] : tags)
				.addClassMember("response", response != null ? response.getName() : Void.class.getName())
				.addStringMember("responseContainer", StringUtils.defaultString(responseContainer, ""))
				.addStringMember("responseReference", StringUtils.defaultString(responseReference, ""))
				.addStringMember("httpMethod", StringUtils.defaultString(httpMethod, ""))
				.addStringMember("nickname", StringUtils.defaultString(nickname, ""))
				.addStringMember("produces", StringUtils.defaultString(produces, ""))
				.addStringMember("consumes", StringUtils.defaultString(consumes, ""))
				.addStringMember("protocols", StringUtils.defaultString(protocols, ""))
				.addBooleanMember("hidden", hidden)
				.addIntegerMember("code", code)
				.addBooleanMember("ignoreJsonView", ignoreJsonView).build();
	}

	/**
	 * Builds an {@link ApiImplicitParams} annotation wrapping an array of
	 * {@link MvcApiImplicitParam} descriptors as nested
	 * {@link ApiImplicitParam} entries.
	 *
	 * @param constPool         the constant pool, never {@code null}
	 * @param apiImplicitParams the parameter descriptors to wrap
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiImplicitParams(ConstPool constPool, MvcApiImplicitParam ... apiImplicitParams) {

		Annotation[] values = new Annotation[apiImplicitParams.length];
		int i = 0;
		for (MvcApiImplicitParam param : apiImplicitParams) {

			values[i] = CtAnnotationBuilder.create(ApiImplicitParam.class, constPool)
					.addStringMember("name", StringUtils.defaultString(param.getName(), ""))
					.addStringMember("value", StringUtils.defaultString(param.getValue(), ""))
					.addStringMember("defaultValue", StringUtils.defaultString(param.getDefaultValue(), ""))
					.addStringMember("allowableValues", StringUtils.defaultString(param.getAllowableValues(), ""))
					.addBooleanMember("required", param.isRequired())
					.addStringMember("access", StringUtils.defaultString(param.getAccess(), ""))
					.addBooleanMember("allowMultiple", param.isAllowEmptyValue())
					.addStringMember("dataType", StringUtils.defaultString(param.getDataType(), ""))
					.addClassMember("dataTypeClass", param.getDataTypeClass() == null ? Void.class.getName() : param.getDataTypeClass().getName())
					.addStringMember("paramType", StringUtils.defaultString(param.getParamType(), ""))
					.addStringMember("example", StringUtils.defaultString(param.getExample(), ""))
					.addStringMember("type", StringUtils.defaultString(param.getType(), ""))
					.addStringMember("format", StringUtils.defaultString(param.getFormat(), ""))
					.addBooleanMember("allowEmptyValue", param.isAllowEmptyValue())
					.addBooleanMember("readOnly", param.isReadOnly())
					.addStringMember("collectionFormat", StringUtils.defaultString(param.getCollectionFormat(), ""))
					.build();
			i++;
		}

		return CtAnnotationBuilder.create(ApiImplicitParams.class, constPool)
				.addAnnotationMember("value", values).build();
	}

	/**
	 * Builds an {@link ApiResponses} annotation wrapping an array of
	 * {@link MvcApiResponse} descriptors as nested {@link ApiResponse}
	 * entries.
	 *
	 * @param constPool     the constant pool, never {@code null}
	 * @param apiResponses  the response descriptors to wrap
	 * @return the annotation ready to be added to a method
	 * @since 3.0.0
	 */
	public static Annotation annotApiResponses(ConstPool constPool, MvcApiResponse ... apiResponses) {

		Annotation[] values = new Annotation[apiResponses.length];
		int i = 0;
		for (MvcApiResponse param : apiResponses) {

			values[i] = CtAnnotationBuilder.create(ApiResponse.class, constPool)
					.addIntegerMember("code", param.getCode())
					.addStringMember("message", StringUtils.defaultString(param.getMessage(), ""))
					.addClassMember("response", param.getResponse() == null ? Void.class.getName() : param.getResponse().getName())
					.addStringMember("reference", StringUtils.defaultString(param.getReference(), ""))
					.addStringMember("responseContainer", StringUtils.defaultString(param.getResponseContainer(), ""))
					.build();
			i++;
		}

		return CtAnnotationBuilder.create(ApiResponses.class, constPool)
				.addAnnotationMember("value", values).build();

	}

}