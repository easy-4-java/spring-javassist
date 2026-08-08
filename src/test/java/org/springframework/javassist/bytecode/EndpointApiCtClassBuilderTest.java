package org.springframework.javassist.bytecode;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.bytecode.definition.MvcMapping;
import org.springframework.javassist.bytecode.definition.MvcMethod;
import org.springframework.javassist.bytecode.definition.MvcParam;
import org.springframework.javassist.bytecode.definition.MvcParamFrom;
import org.springframework.web.bind.annotation.RequestMethod;

import io.github.easy4j.javassist.utils.ClassPoolFactory;

import javassist.ClassPool;
import javassist.CtClass;

public class EndpointApiCtClassBuilderTest {

    private final ClassPool pool = ClassPoolFactory.getDefaultPool();

    @Test
    public void shouldCreateWithClassname() throws Exception {
        EndpointApiCtClassBuilder builder = new EndpointApiCtClassBuilder("org.test.EACB1");
        assertNotNull(builder.build());
    }

    @Test
    public void shouldCreateWithPoolAndClassname() throws Exception {
        EndpointApiCtClassBuilder builder = new EndpointApiCtClassBuilder(pool, "org.test.EACB2");
        assertNotNull(builder.build());
    }

    @Test
    public void shouldAddController() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBCtrl")
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddControllerWithName() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBCtrlN")
                .controller("myCtrl")
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddRestController() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBRCtrl")
                .restController()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddRestControllerWithName() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBRCtrlN")
                .restController("myRest")
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddApi() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBApi")
                .api("tag1", "tag2")
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddApiWithNullTags() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBApiN")
                .api((String[]) null)
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddApiWithEmptyTags() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBApiE")
                .api()
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddApiIgnore() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBApiIg")
                .apiIgnore()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddRequestMappingWithMvcMapping() throws Exception {
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.GET);
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBRM1")
                .requestMapping(mapping)
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddRequestMappingWithPath() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBRM2")
                .requestMapping("/api")
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddRequestMappingFull() throws Exception {
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBRM3")
                .requestMapping("name", new String[]{"/api"},
                        new RequestMethod[]{RequestMethod.GET},
                        new String[]{"p=v"}, new String[]{"h=v"},
                        new String[]{"application/json"}, new String[]{"application/json"})
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithRawValues() throws Exception {
        MvcBound bound = new MvcBound("uid1", "{}");
        MvcParam<?>[] params = new MvcParam[]{new MvcParam<>(String.class, "name")};
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBNM1")
                .newMethod("find", "/find/{name}", RequestMethod.GET, "application/json", bound, params)
                .controller()
                .build();
        assertNotNull(ct);
        assertNotNull(ct.getDeclaredMethod("find"));
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithNoParams() throws Exception {
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBNM2")
                .newMethod("list", "/list", RequestMethod.GET, "application/json", bound)
                .controller()
                .build();
        assertNotNull(ct);
        assertNotNull(ct.getDeclaredMethod("list"));
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithMvcMethod() throws Exception {
        MvcMethod method = new MvcMethod("save", new String[]{"/save"}, RequestMethod.POST);
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBNM3")
                .newMethod(Object.class, method, bound)
                .controller()
                .build();
        assertNotNull(ct);
        assertNotNull(ct.getDeclaredMethod("save"));
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithMvcMethodAndParams() throws Exception {
        MvcMethod method = new MvcMethod("find", new String[]{"/find"}, RequestMethod.GET);
        MvcBound bound = new MvcBound("uid1", "{}");
        MvcParam<?>[] params = new MvcParam[]{
                new MvcParam<>(String.class, "name", MvcParamFrom.PATH),
                new MvcParam<>(Integer.class, "page")
        };
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBNM4")
                .newMethod(Object.class, method, bound, params)
                .controller()
                .build();
        assertNotNull(ct);
        assertNotNull(ct.getDeclaredMethod("find"));
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithMvcMethodMultipleMethods() throws Exception {
        MvcMethod method = new MvcMethod("action", new String[]{"/action"},
                new RequestMethod[]{RequestMethod.GET, RequestMethod.POST});
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBNM5")
                .newMethod(Object.class, method, bound)
                .controller()
                .build();
        assertNotNull(ct);
        assertNotNull(ct.getDeclaredMethod("action"));
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithMvcMethodWithResponseBody() throws Exception {
        MvcMethod method = new MvcMethod("data", new String[]{"/data"}, true, RequestMethod.GET);
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBNM6")
                .newMethod(Object.class, method, bound)
                .restController()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithVoidReturn() throws Exception {
        MvcMethod method = new MvcMethod("delete", new String[]{"/delete"}, RequestMethod.DELETE);
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBNM7")
                .newMethod(Void.class, method, bound)
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAddNewMethodWithNullReturn() throws Exception {
        MvcMethod method = new MvcMethod("noop", new String[]{"/noop"}, RequestMethod.GET);
        MvcBound bound = new MvcBound("uid1", "{}");
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBNM8")
                .newMethod(null, method, bound)
                .controller()
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldRemoveMethod() throws Exception {
        EndpointApiCtClassBuilder builder = new EndpointApiCtClassBuilder("org.test.EACBRM");
        MvcBound bound = new MvcBound("uid1", "{}");
        builder.newMethod("find", "/find", RequestMethod.GET, "application/json", bound);
        builder.removeMethod("find");
        CtClass ct = builder.build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldRemoveMethodNoop() throws Exception {
        EndpointApiCtClassBuilder builder = new EndpointApiCtClassBuilder("org.test.EACBRMN");
        builder.removeMethod("nonexistent");
        assertNotNull(builder.build());
    }

    @Test
    public void shouldRemoveMethodWithParams() throws Exception {
        EndpointApiCtClassBuilder builder = new EndpointApiCtClassBuilder("org.test.EACBRMP");
        MvcBound bound = new MvcBound("uid1", "{}");
        MvcParam<?>[] params = new MvcParam[]{new MvcParam<>(String.class, "name")};
        builder.newMethod("find", "/find", RequestMethod.GET, "application/json", bound, params);
        builder.removeMethod("find", params);
        CtClass ct = builder.build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldRemoveMethodWithParamsNoop() throws Exception {
        EndpointApiCtClassBuilder builder = new EndpointApiCtClassBuilder("org.test.EACBRMPN");
        MvcParam<?>[] params = new MvcParam[]{new MvcParam<>(String.class, "name")};
        builder.removeMethod("nonexistent", params);
        assertNotNull(builder.build());
    }

    @Test
    public void shouldChainFluentMethods() throws Exception {
        MvcBound bound = new MvcBound("uid1", "{}");
        MvcMethod method = new MvcMethod("find", new String[]{"/find"}, RequestMethod.GET);
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBChain")
                .api("tag1")
                .controller("ctrl")
                .requestMapping("/api")
                .newMethod(Object.class, method, bound)
                .newMethod("save", "/save", RequestMethod.POST, "application/json", bound)
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
        EndpointApiCtClassBuilder builder = new EndpointApiCtClassBuilder("org.test.EACBChainO");
        EndpointApiCtClassBuilder result = builder
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
    public void shouldBuildWithApiAndNewMethodWithApiParams() throws Exception {
        MvcBound bound = new MvcBound("uid1", "notes");
        MvcMethod method = new MvcMethod("search", new String[]{"/search"}, RequestMethod.GET);
        MvcParam<?>[] params = new MvcParam[]{
                new MvcParam<>(String.class, "query"),
                new MvcParam<>(Integer.class, "page", "1")
        };
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBApiNM")
                .api("search")
                .controller()
                .requestMapping("/api")
                .newMethod(Object.class, method, bound, params)
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldBuildWithRawMethodAndApiAndMultipleParams() throws Exception {
        MvcBound bound = new MvcBound("uid1", "notes");
        MvcParam<?>[] params = new MvcParam[]{
                new MvcParam<>(String.class, "name"),
                new MvcParam<>(String.class, "body", MvcParamFrom.BODY)
        };
        CtClass ct = new EndpointApiCtClassBuilder("org.test.EACBApiRaw")
                .api("tag1")
                .controller()
                .newMethod("create", "/create", RequestMethod.POST, "application/json", bound, params)
                .build();
        assertNotNull(ct);
        ct.detach();
    }
}
