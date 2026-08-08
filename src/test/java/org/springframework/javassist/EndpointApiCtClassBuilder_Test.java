package org.springframework.javassist;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.javassist.bytecode.EndpointApiCtClassBuilder;
import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.bytecode.definition.MvcMethod;
import org.springframework.javassist.bytecode.definition.MvcParam;
import org.springframework.javassist.bytecode.definition.MvcParamFrom;
import org.springframework.web.bind.annotation.RequestMethod;

import javassist.CtClass;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EndpointApiCtClassBuilder_Test {

	@Test
	public void testBuild() throws Exception {

		CtClass ctClass = new EndpointApiCtClassBuilder("org.apache.cxf.spring.boot.FirstCaseV2")
				.newMethod("sayHello", "say/{word}", RequestMethod.POST, MediaType.ALL_VALUE, new MvcBound("100212"),
						new MvcParam(String.class, "text"))
				.newMethod(ResponseEntity.class,
						new MvcMethod("sayHello2", new String[] { "say2/{word}", "say22/{word}" }, new RequestMethod[] {RequestMethod.POST, RequestMethod.GET} ),
						new MvcBound("100212"), new MvcParam(String.class, "word", MvcParamFrom.PATH))
				.controller()
				.makeField("public int k = 3;")
				.newField(String.class, "uid", UUID.randomUUID().toString())
				.build();

		// Verify the CtClass was built successfully
		assert ctClass != null;
		assert ctClass.getName().equals("org.apache.cxf.spring.boot.FirstCaseV2");
		assert ctClass.getDeclaredField("k") != null;
		assert ctClass.getDeclaredField("uid") != null;
		assert ctClass.getDeclaredMethod("sayHello") != null;
		assert ctClass.getDeclaredMethod("sayHello2") != null;
		ctClass.detach();
	}

}
