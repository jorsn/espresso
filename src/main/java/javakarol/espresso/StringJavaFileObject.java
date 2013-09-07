package javakarol.espresso;


import javax.tools.SimpleJavaFileObject;

import java.net.URI;

public class StringJavaFileObject extends SimpleJavaFileObject {
	private final CharSequence code;

	public StringJavaFileObject( String name, CharSequence code ) {
		super( URI.create( "file:///" + name.replace( '.', '/' ) + Kind.SOURCE.extension ),
				Kind.SOURCE );
		this.code = code;
	}

	@Override
	public CharSequence getCharContent( boolean ignoreEncodingErrors ) {
		return code;
	}
}
