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
package org.springframework.javassist.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Associates a generated controller method with the auxiliary payload (a primary
 * key plus a JSON document) the framework uses to short-circuit request
 * handling.
 *
 * <p>{@code @WebBound} is emitted by {@code EndpointApiUtils#annotWebBound}
 * onto every method whose binding metadata is not {@code null}. When the
 * generated proxy is invoked, the binding layer looks the annotation up to
 * decide whether a cached lookup against {@link #uid()} should be attempted
 * before falling back to the supplied {@link #json()} template.</p>
 *
 * <p>The annotation may be applied to classes (for default per-method binding)
 * or methods (for binding specific to that handler). It is retained at runtime
 * and is inheritable, allowing subclasses to inherit a parent's binding.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.springframework.javassist.utils.EndpointApiUtils#annotWebBound(javassist.bytecode.ConstPool, org.springframework.javassist.bytecode.definition.MvcBound)
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebBound {

	/**
	 * The unique identifier of the bound data record &mdash; typically the
	 * primary key used to fetch additional context.
	 *
	 * @return the bound record identifier, defaults to an empty string
	 */
	String uid() default "";

	/**
	 * A JSON payload that the framework should expose to the handler when the
	 * binding layer cannot resolve {@link #uid()} against a registry.
	 *
	 * @return the default JSON payload, defaults to an empty JSON object
	 */
	String json() default "{}";

}