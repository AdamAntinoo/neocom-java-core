package org.dimensinfin.neocom.annotationprocessor;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.neocom.NeoComLogger;
import org.dimensinfin.neocom.annotation.LogEnterExit;

@AutoService(Processor.class)
public class NeoComAnnotationProcessor extends AbstractProcessor {
	private static Logger logger = LoggerFactory.getLogger( NeoComAnnotationProcessor.class );
	private static final Set<String> supported = new LinkedHashSet<>();

	static {
		supported.add( "LogEnterExit" );
	}

	private NeoComAnnotationProcessor() {}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_8;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return supported;
	}

	@Override
	public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv ) {
		final Set<? extends Element> logEnterExitTargets = roundEnv.getElementsAnnotatedWith( LogEnterExit.class );
		final Iterator<? extends Element> it = logEnterExitTargets.iterator();
		while (it.hasNext()) {
			final Element targetMethod = it.next();
			try {
				logger.info( "targetMethod.getSimpleName().toString(): {}", targetMethod.getSimpleName().toString() );
				this.generateLogEnterExitWrapper( targetMethod.getSimpleName().toString(), targetMethod );
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return true;
	}

	private void generateLogEnterExitWrapper( final String wrappedMethod,
	                                          final Element target ) throws IOException {
		MethodSpec main = MethodSpec.methodBuilder( wrappedMethod )
				.addModifiers( target.getModifiers() )
				.returns( void.class )
//				.addParameter( String[].class, "args" )
				.addStatement( "$T.enter()", NeoComLogger.class )
				.build();
		TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
				.addModifiers( Modifier.PUBLIC, Modifier.FINAL )
				.addMethod( main )
				.build();
		JavaFile javaFile = JavaFile.builder( "com.example.helloworld", helloWorld )
				.build();
		javaFile.writeTo( System.out );
	}
}
