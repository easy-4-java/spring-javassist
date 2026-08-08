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
package org.springframework.javassist.bytecode;

import java.lang.reflect.InvocationHandler;

/**
 * Common super-class for every Javassist-generated endpoint proxy.
 *
 * <p>Every controller produced by an {@code *CtClassBuilder} derives from
 * {@code EndpointApi}; the generated subclass owns the actual HTTP handling
 * methods, while {@code EndpointApi} itself stores the {@link InvocationHandler}
 * that forwards each call to the developer-supplied business object.</p>
 *
 * <p>Two constructors are provided &mdash; a no-arg one (used when the
 * framework instantiates the controller reflectively after Spring binds
 * autowired collaborators) and an {@code InvocationHandler}-aware one (used by
 * {@code CtClassBuilder#toInstance(InvocationHandler)} for direct, hand-built
 * proxies).</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see InvocationHandler
 * @see CtClassBuilder#toInstance(InvocationHandler)
 * @see ReactiveHandler
 */
public abstract class EndpointApi {

	/**
	 * The handler that every generated method delegates to. May be {@code null}
	 * when the controller is created via the no-arg constructor and is wired
	 * up later by Spring through the {@code handler} field.
	 */
	protected InvocationHandler handler;

	/**
	 * Creates an instance without binding a handler. The {@link #handler}
	 * field stays {@code null} until one is assigned.
	 *
	 * @since 3.0.0
	 */
	public EndpointApi() {
	}

	/**
	 * Creates an instance that delegates all method invocations to the given
	 * handler.
	 *
	 * @param handler the invocation handler that backs this proxy, may be
	 *                {@code null} when no business object is available yet
	 * @since 3.0.0
	 */
	public EndpointApi(InvocationHandler handler) {
		this.handler = handler;
	}

	/**
	 * Returns the handler used by the generated controller methods.
	 *
	 * @return the current {@link InvocationHandler}, or {@code null} if none
	 *         has been bound
	 * @since 3.0.0
	 */
	public InvocationHandler getHandler() {
		return handler;
	}

}