/* StringJavaFileObject - JavaFileObject to compile java code from a String.
 * 
 * Copyright (c) 2013, Johannes Rosenberger <jo.rosenberger(at)gmx-topmail.de>
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
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
