package org.springframework.javassist;

import java.util.UUID;

import org.junit.Test;
import org.springframework.javassist.bytecode.ReactiveHandlerCtClassBuilder;
import org.springframework.javassist.bytecode.definition.MvcBound;

import javassist.CtClass;
import reactor.core.publisher.Flux;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReactiveHandlerCtClassBuilder_Test {

	@Test
	public void testBuild() throws Exception {

		CtClass ctClass = new ReactiveHandlerCtClassBuilder("org.springframework.javassist.ReactiveHandlerV2")
				.monoMethod(new MvcBound("100212"))
				.fluxMethod(new MvcBound("100213"))
				.newMethod(Flux.class, "sayHello2", new MvcBound("100214"))
				.makeField("public int k = 3;")
				.newField(String.class, "uid", UUID.randomUUID().toString())
				.build();

		// Verify the CtClass was built successfully
		assert ctClass != null;
		assert ctClass.getName().equals("org.springframework.javassist.ReactiveHandlerV2");
		assert ctClass.getDeclaredField("k") != null;
		assert ctClass.getDeclaredField("uid") != null;
		assert ctClass.getDeclaredMethod("mono") != null;
		assert ctClass.getDeclaredMethod("flux") != null;
		assert ctClass.getDeclaredMethod("sayHello2") != null;
		ctClass.detach();
	}

}
