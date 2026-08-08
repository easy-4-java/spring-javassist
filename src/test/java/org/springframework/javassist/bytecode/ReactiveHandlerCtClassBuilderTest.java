package org.springframework.javassist.bytecode;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.javassist.bytecode.definition.MvcBound;

import io.github.easy4j.javassist.utils.ClassPoolFactory;

import javassist.ClassPool;
import javassist.CtClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveHandlerCtClassBuilderTest {

    private final ClassPool pool = ClassPoolFactory.getDefaultPool();

    @Test
    public void shouldCreateWithClassname() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder("org.test.RHCB1");
        assertNotNull(builder.build());
    }

    @Test
    public void shouldCreateWithPoolAndClassname() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder(pool, "org.test.RHCB2");
        assertNotNull(builder.build());
    }

    @Test
    public void shouldAddMonoMethod() throws Exception {
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new ReactiveHandlerCtClassBuilder("org.test.RHCBMono")
                .monoMethod(bound)
                .build();
        assertNotNull(ct);
        assertNotNull(ct.getDeclaredMethod("mono"));
        ct.detach();
    }

    @Test
    public void shouldAddFluxMethod() throws Exception {
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new ReactiveHandlerCtClassBuilder("org.test.RHCBFlux")
                .fluxMethod(bound)
                .build();
        assertNotNull(ct);
        assertNotNull(ct.getDeclaredMethod("flux"));
        ct.detach();
    }

    @Test
    public void shouldAddNewMethod() throws Exception {
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new ReactiveHandlerCtClassBuilder("org.test.RHCBNew")
                .newMethod(Mono.class, "custom", bound)
                .build();
        assertNotNull(ct);
        assertNotNull(ct.getDeclaredMethod("custom"));
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithNullReturn() throws Exception {
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new ReactiveHandlerCtClassBuilder("org.test.RHCBNewN")
                .newMethod(null, "custom", bound)
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldRemoveMono() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder("org.test.RHCBRMono");
        builder.monoMethod(new MvcBound("1", "{}"));
        builder.removeMono();
        CtClass ct = builder.build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldRemoveFlux() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder("org.test.RHCBRFlux");
        builder.fluxMethod(new MvcBound("1", "{}"));
        builder.removeFlux();
        CtClass ct = builder.build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldRemoveMethod() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder("org.test.RHCBRM");
        builder.monoMethod(new MvcBound("1", "{}"));
        builder.removeMethod("mono");
        CtClass ct = builder.build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldRemoveMethodNoop() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder("org.test.RHCBRMN");
        builder.removeMethod("nonexistent");
        assertNotNull(builder.build());
    }

    @Test
    public void shouldRemoveMonoNoop() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder("org.test.RHCBRMonoN");
        builder.removeMono();
        assertNotNull(builder.build());
    }

    @Test
    public void shouldRemoveFluxNoop() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder("org.test.RHCBRFluxN");
        builder.removeFlux();
        assertNotNull(builder.build());
    }

    @Test
    public void shouldChainFluentMethods() throws Exception {
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new ReactiveHandlerCtClassBuilder("org.test.RHCBChain")
                .monoMethod(bound)
                .fluxMethod(bound)
                .newMethod(Flux.class, "custom", bound)
                .makeField("public int count = 0;")
                .newField(String.class, "uid", "\"abc\"")
                .autowired(String.class, "service", true)
                .bind("uid123", "{}")
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldChainFluentOverriddenMethods() throws Exception {
        ReactiveHandlerCtClassBuilder builder = new ReactiveHandlerCtClassBuilder("org.test.RHCBChainO");
        ReactiveHandlerCtClassBuilder result = builder
                .autowired(String.class, "svc", true)
                .bind(new MvcBound("1", "{}"))
                .bind("2", "{}")
                .makeField("public int x = 0;")
                .makeMethod("public void test() {}")
                .newField(String.class, "f", "\"v\"")
                .removeField("f");
        assertNotNull(result);
        assertNotNull(result.build());
    }

    @Test
    public void shouldHaveMonoAndFluxConstants() {
        assertEquals("mono", ReactiveHandlerCtClassBuilder.METHOD_MONO_NAME);
        assertEquals("flux", ReactiveHandlerCtClassBuilder.METHOD_FLUX_NAME);
    }
}
