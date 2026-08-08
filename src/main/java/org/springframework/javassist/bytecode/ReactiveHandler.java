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

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive counterpart of {@link EndpointApi} used by
 * {@code ReactiveHandlerCtClassBuilder} when materialising Spring WebFlux
 * functional-style handlers at runtime.
 *
 * <p>Like {@link EndpointApi}, the generated subclass carries the
 * {@link InvocationHandler} that performs the actual work, but the framework
 * here expects publishers (a {@link Mono} for single-value replies or a
 * {@link Flux} for streams) instead of blocking return values. The default
 * implementations return empty publishers so that subclasses only have to
 * override what they actually care about.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see EndpointApi
 * @see org.springframework.javassist.bytecode.ReactiveHandlerCtClassBuilder
 */
public abstract class ReactiveHandler extends EndpointApi {

	/**
	 * Creates an instance without binding a handler; equivalent to the
	 * super-class no-arg constructor.
	 *
	 * @since 3.0.0
	 */
	public ReactiveHandler() {
	}

	/**
	 * Creates an instance that delegates method invocations to the supplied
	 * handler.
	 *
	 * @param handler the invocation handler backing this reactive proxy, may be
	 *                {@code null} when the handler will be assigned later
	 * @since 3.0.0
	 */
	public ReactiveHandler(InvocationHandler handler) {
		super(handler);
	}

	/**
	 * Default single-value handler. Returns an empty {@link Mono} so that
	 * generated subclasses that do not override the method will produce a
	 * 404-style response rather than blocking.
	 *
	 * @param request the inbound reactive request, never {@code null}
	 * @return a {@link Mono} that completes without emitting any
	 *         {@link ServerResponse}, never {@code null}
	 * @since 3.0.0
	 */
	public Mono<ServerResponse> mono(ServerRequest request){
		return Mono.empty();
	}

	/**
	 * Default streaming handler. Returns an empty {@link Flux} so generated
	 * subclasses that do not override the method produce no responses rather
	 * than blocking.
	 *
	 * @param request the inbound reactive request, never {@code null}
	 * @return a {@link Flux} that completes without emitting any
	 *         {@link ServerResponse}, never {@code null}
	 * @since 3.0.0
	 */
	public Flux<ServerResponse> flux(ServerRequest request){
		return Flux.empty();
	}

}