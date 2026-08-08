package org.springframework.javassist.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationHandler;

import org.junit.Test;
import org.springframework.web.reactive.function.server.ServerRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveHandlerTest {

    @Test
    public void shouldCreateWithNoArgConstructor() {
        ReactiveHandler handler = new ReactiveHandler() {};
        assertNull(handler.getHandler());
    }

    @Test
    public void shouldCreateWithHandler() {
        InvocationHandler invocationHandler = (proxy, method, args) -> null;
        ReactiveHandler handler = new ReactiveHandler(invocationHandler) {};
        assertSame(invocationHandler, handler.getHandler());
    }

    @Test
    public void shouldReturnEmptyMonoByDefault() {
        ReactiveHandler handler = new ReactiveHandler() {};
        Mono<?> result = handler.mono(null);
        assertNotNull(result);
        assertTrue(result.toFuture().isDone());
    }

    @Test
    public void shouldReturnEmptyFluxByDefault() {
        ReactiveHandler handler = new ReactiveHandler() {};
        Flux<?> result = handler.flux(null);
        assertNotNull(result);
    }
}
