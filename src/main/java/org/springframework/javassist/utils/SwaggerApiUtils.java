/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
package org.springframework.javassist.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.javassist.bytecode.definition.MvcApiImplicitParam;
import org.springframework.javassist.bytecode.definition.MvcApiResponse;

import io.github.hiwepy.javassist.bytecode.CtAnnotationBuilder;

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
 * https://www.cnblogs.com/sunfie/p/5154246.html
 * @author <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class SwaggerApiUtils {

	/**
	 * 构造 @Api 注解
	 * @param constPool {@link ConstPool} instance
	 * @param tags 标签名称
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotApi(ConstPool constPool, String... tags) {

		tags = ArrayUtils.isEmpty(tags) ? new String[] { "" } : tags;
		return CtAnnotationBuilder.create(Api.class, constPool).addStringMember("tags", tags).build();

	} 
	
	/**
	 * 构造 @ApiIgnore 注解
	 * @param constPool {@link ConstPool} instance
	 * @param desc 描述标签
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotApiIgnore(ConstPool constPool, String desc) {
		return CtAnnotationBuilder.create(springfox.documentation.annotations.ApiIgnore.class, constPool).addStringMember("value", desc).build();
	}

	/**
	 * 构造 @ApiKeyAuthDefinition 注解
	 * @param constPool {@link ConstPool} instance
	 * @param name The name of the header or query parameter to be used.
	 * @param key Key used to refer to this security definition
	 * @param desc A short description for security scheme.
	 * @param in The location of the API key. Valid values are "query" or "header"
	 * @return {@link Annotation} instance
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
	 * 构造 @ApiOperation 注解
	 * 
	 * @param constPool         {@link ConstPool} instance
	 * @param value             Provides a brief description of this operation.
	 *                          Should be 120 characters or less for proper
	 *                          visibility in Swagger-UI.
	 * @param notes             A verbose description of the operation.
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, "")).build();
	}
	
	/**
	 * 构造 @ApiOperation 注解
	 * 
	 * @param constPool         {@link ConstPool} instance
	 * @param value             Provides a brief description of this operation.
	 *                          Should be 120 characters or less for proper
	 *                          visibility in Swagger-UI.
	 * @param notes             A verbose description of the operation.
	 * @param response          The response type of the operation. If the value
	 *                          used is a class representing a primitive
	 *                          ({@code Integer}, {@code Long}, ...) the
	 *                          corresponding primitive type will be used.
	 * @return {@link Annotation} instance
	 */
	public static Annotation annotApiOperation(ConstPool constPool, String value, String notes, 
			Class<?> response) {

		return CtAnnotationBuilder.create(ApiOperation.class, constPool)
				.addStringMember("value", StringUtils.defaultString(value, ""))
				.addStringMember("notes", StringUtils.defaultString(notes, ""))
				.addClassMember("response", response != null ? response.getName() : Void.class.getName()).build();
	}
	
	/**
	 * 构造 @ApiOperation 注解
	 * 
	 * @param constPool         {@link ConstPool} instance
	 * @param value             Provides a brief description of this operation.
	 *                          Should be 120 characters or less for proper
	 *                          visibility in Swagger-UI.
	 * @param notes             A verbose description of the operation.
	 * @param tags              A list of tags for API documentation control.
	 * @param response          The response type of the operation. If the value
	 *                          used is a class representing a primitive
	 *                          ({@code Integer}, {@code Long}, ...) the
	 *                          corresponding primitive type will be used.
	 * @return {@link Annotation} instance
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
	 * 构造 @ApiOperation 注解
	 * 
	 * @param constPool         {@link ConstPool} instance
	 * @param value             Provides a brief description of this operation.
	 *                          Should be 120 characters or less for proper
	 *                          visibility in Swagger-UI.
	 * @param notes             A verbose description of the operation.
	 * @param tags              A list of tags for API documentation control.
	 * @param response          The response type of the operation. If the value
	 *                          used is a class representing a primitive
	 *                          ({@code Integer}, {@code Long}, ...) the
	 *                          corresponding primitive type will be used.
	 * @param responseContainer Declares a container wrapping the response. Valid
	 *                          values are "List", "Set" or "Map". Any other value
	 *                          will be ignored.
	 * @return {@link Annotation} instance
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
	 * 构造 @ApiOperation 注解
	 * 
	 * @param constPool         {@link ConstPool} instance
	 * @param value             Provides a brief description of this operation.
	 *                          Should be 120 characters or less for proper
	 *                          visibility in Swagger-UI.
	 * @param notes             A verbose description of the operation.
	 * @param tags              A list of tags for API documentation control.
	 * @param response          The response type of the operation. If the value
	 *                          used is a class representing a primitive
	 *                          ({@code Integer}, {@code Long}, ...) the
	 *                          corresponding primitive type will be used.
	 * @param responseContainer Declares a container wrapping the response. Valid
	 *                          values are "List", "Set" or "Map". Any other value
	 *                          will be ignored.
	 * @param responseReference Specifies a reference to the response type. The
	 *                          specified reference can be either local or remote
	 *                          and will be used as-is, and will override any
	 *                          specified response() class.
	 * @return {@link Annotation} instance
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
	 * 构造 @ApiOperation 注解
	 * 
	 * @param constPool         {@link ConstPool} instance
	 * @param value             Provides a brief description of this operation.
	 *                          Should be 120 characters or less for proper
	 *                          visibility in Swagger-UI.
	 * @param notes             A verbose description of the operation.
	 * @param tags              A list of tags for API documentation control.
	 * @param response          The response type of the operation. If the value
	 *                          used is a class representing a primitive
	 *                          ({@code Integer}, {@code Long}, ...) the
	 *                          corresponding primitive type will be used.
	 * @param responseContainer Declares a container wrapping the response. Valid
	 *                          values are "List", "Set" or "Map". Any other value
	 *                          will be ignored.
	 * @param responseReference Specifies a reference to the response type. The
	 *                          specified reference can be either local or remote
	 *                          and will be used as-is, and will override any
	 *                          specified response() class.
	 * @param httpMethod        Corresponds to the `method` field as the HTTP method
	 *                          used. Acceptable values are "GET", "HEAD", "POST",
	 *                          "PUT", "DELETE", "OPTIONS" and "PATCH".
	 * @param nickname          The operationId is used by third-party tools to
	 *                          uniquely identify this operation. In Swagger 2.0,
	 *                          this is no longer mandatory and if not provided will
	 *                          remain empty.
	 * @param produces          Takes in comma-separated values of content types.
	 *                          For example, "application/json, application/xml"
	 *                          would suggest this operation generates JSON and XML
	 *                          output.
	 * @param consumes          Takes in comma-separated values of content types.
	 *                          For example, "application/json, application/xml"
	 *                          would suggest this API Resource accepts JSON and XML
	 *                          input.
	 * @param protocols         Sets specific protocols (schemes) for this
	 *                          operation. Comma-separated values of the available
	 *                          protocols. Possible values: http, https, ws, wss.
	 * @return {@link Annotation} instance
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
	 * 构造 @ApiOperation 注解
	 * 
	 * @param constPool         {@link ConstPool} instance
	 * @param value             Provides a brief description of this operation.
	 *                          Should be 120 characters or less for proper
	 *                          visibility in Swagger-UI.
	 * @param notes             A verbose description of the operation.
	 * @param tags              A list of tags for API documentation control.
	 * @param response          The response type of the operation. If the value
	 *                          used is a class representing a primitive
	 *                          ({@code Integer}, {@code Long}, ...) the
	 *                          corresponding primitive type will be used.
	 * @param responseContainer Declares a container wrapping the response. Valid
	 *                          values are "List", "Set" or "Map". Any other value
	 *                          will be ignored.
	 * @param responseReference Specifies a reference to the response type. The
	 *                          specified reference can be either local or remote
	 *                          and will be used as-is, and will override any
	 *                          specified response() class.
	 * @param httpMethod        Corresponds to the `method` field as the HTTP method
	 *                          used. Acceptable values are "GET", "HEAD", "POST",
	 *                          "PUT", "DELETE", "OPTIONS" and "PATCH".
	 * @param nickname          The operationId is used by third-party tools to
	 *                          uniquely identify this operation. In Swagger 2.0,
	 *                          this is no longer mandatory and if not provided will
	 *                          remain empty.
	 * @param produces          Takes in comma-separated values of content types.
	 *                          For example, "application/json, application/xml"
	 *                          would suggest this operation generates JSON and XML
	 *                          output.
	 * @param consumes          Takes in comma-separated values of content types.
	 *                          For example, "application/json, application/xml"
	 *                          would suggest this API Resource accepts JSON and XML
	 *                          input.
	 * @param protocols         Sets specific protocols (schemes) for this
	 *                          operation. Comma-separated values of the available
	 *                          protocols. Possible values: http, https, ws, wss.
	 * @param hidden            Hides the operation from the list of operations.
	 * @param code              The HTTP status code of the response.
	 * @param ignoreJsonView    Ignores JsonView annotations while resolving
	 *                          operations and types. For backward compatibility
	 * @return {@link Annotation} instance
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
	 * 构造 @ApiImplicitParams 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param apiImplicitParams 	: {@link MvcApiImplicitParam} array instance
	 * @return {@link Annotation} instance
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
	 * 构造 @ApiResponses 注解
	 * @param constPool : {@link ConstPool} instance
	 * @param apiResponses 	: {@link MvcApiResponse} array instance
	 * @return {@link Annotation} instance
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
