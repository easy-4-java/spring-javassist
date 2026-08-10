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
 * Marks a dynamically generated field with the original parameter name so that
 * it can be recovered at runtime from a Javassist-built proxy.
 *
 * <p>When a controller method is materialised by
 * {@code EndpointApiCtClassBuilder}, the runtime argument names that the
 * developer wrote in source code are not preserved by the underlying
 * {@code javassist} API. The generated field is therefore annotated with
 * {@link ParamName} to record the original logical name, allowing downstream
 * code (such as the {@code @WebParam} machinery inside
 * {@code EndpointApiUtils#annotParams}) to look up that name without resorting
 * to reflection on compiler-generated argument names.</p>
 *
 * <p>The annotation is retained at runtime, is inheritable, and may be applied
 * to fields only &mdash; applying it to a method or type would be meaningless
 * because the generated binding always targets a synthetic field.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.springframework.javassist.utils.EndpointApiUtils#annotParams(javassist.bytecode.ConstPool, org.springframework.javassist.bytecode.definition.MvcParam[])
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ParamName {

	/**
	 * The original parameter name to be carried by the annotated field.
	 *
	 * @return the original parameter name, never {@code null}
	 */
	String name();

}