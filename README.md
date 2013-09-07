Espresso Karol — A simple gui editor for [javakarol]
=====================================================

**Espresso Karol** is a simple gui editor for [javakarol], which can be used by
students who are only starting to learn Java and yet have problems to write whole
class structures.
It is written in java and uses [gradle] for building.


Features
-----------

* class tree of `ROBOTER` and `WELT` showing methods and constructors of these classes
* editor: syntax highlighting, line numbers, undo, redo
* error/output console
* automatic compilation and running of javakarol programs
* save and restore
* flexible configuration via modes and [EProperties] files [(manual)][eprops-man]
* gradle build and ecj: no dependencies except for a JRE


Dependencies
------------

* Java Runtime Environment 1.6 or 1.7
* [gradle] is installed by [gradlew]
* the rest is managed automatically by [gradle]


Usage
-----------

1. Build it with [gradle]/[gradlew].
2. Put it in your preferred installation directory.
3. Configure it: edit `espresso.props`, an EProperties file.
   A manual for EProperties files can be read [here][eprops-man].
   You can find a list of java system-properties [here][sys-props].
4. Run the start script:
    * UNIX: `bin/espresso`
    * Windows: `bin\espresso.bat`



Modes
-----------

In the default mode, only the body of the `main` method of a
javakarol program has to be written, but the preset parts of the class can be
configured by creating other modes.

All modes are located in the directory `modes` or `src/dist/modes`.
They are constituted of a directory whose name is the mode name, which contains three
files:

* `code`: An executable java class template. `// karolcode` is substituted by the
  javakarol code from the editor.
* `desc` (optional): A description for this mode which is displayed above the editor
  pane.
* `text` (optional): A default text that is automatically inserted in the editor pane.

For examples please have a look at the built-in modes.



Copyright
-----------

Copyright (c) 2013 Johannes Rosenberger <jo.rosenberger(at)gmx-topmail.de>

This program and all of its files are released under a BSD Style License.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
DAMAGE.

For more details please read the `LICENSE` file.

----------------------------------------

###3rd-party libs


####[JSyntaxPane] v1.0.0

Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com

It is released under the [Apache License, Version 2.0][apl]
(see `3rd-party-licenses/jsyntaxpane.txt`).
The source code can be obtained [here][JSyntaxPane].


####[juniversalchardet] v1.0.3

is released under the [Mozilla Public License Version 1.1 (MPLv1.1)][mpl]
(`see 3rd-party-licenses/juniversalchardet.txt`).
The source code can be obtained [here][juniversalchardet].


####[Jakarta Commons Logging][comm-log] v1.1.1

Copyright 2001-2007 The Apache Software Foundation

It is released under the [Apache License, Version 2.0][apl]
(see `3rd-party-licenses/commons-logging.txt`).
The source code can be obtained [here][comm-log].


###Eclipse Compiler for Java (ecj) v4.2.2+

is released under the [Eclipse Public License v 1.0][epl]
(see `3rd-party-licenses/ecj.txt`).
The source code can be obtained [here][ecj-source]


####[sdreams] v0.1

Copyright (c) 2013 Johannes Rosenberger

It is released under a [BSD Style License], as this program
(see `LICENSE`).
The source code can be obtained [here][sdreams].


####[EProperties] v1.1.5

Copyright 2009, Paul Bemowski

It is released under the [Apache License, Version 2.0][apl]
(see `3rd-party-licenses/eproperties.txt`).
The source code can be obtained [here][EProperties].


####[javakarol] v1.1

Copyright (c) 2008 Ulli Freiberger, (c) 2013 Ulli Freiberger

It is released under a custom license. You can find it in the javakarol documentation:
In `JavaKarolHandbuch.doc` in `lib/javakarol.zip` (if downloaded)
or [here as PDF][jkarol-pdf].



[gradle]: http://www.gradle.org/
[gradlew]: http://www.gradle.org/docs/current/userguide/gradle_wrapper.html

[apl]: http://www.apache.org/licenses/LICENSE-2.0
[mpl]: http://www.mozilla.org/MPL/1.1/
[epl]: http://www.eclipse.org/legal/epl-v10.html

[JSyntaxPane]: https://github.com/Sciss/JSyntaxPane
[juniversalchardet]: https://code.google.com/p/juniversalchardet/
[comm-log]: http://commons.apache.org/proper/commons-logging/
[ecj-source]: http://wiki.eclipse.org/Stardust/Source_Code#Browsing_the_Source_Code
[sdreams]: https://github.com/jorsn/sdreams
[EProperties]: http://code.google.com/p/eproperties/
[eprops-man]: http://eproperties.googlecode.com/svn/docs/1.1.2/manual/index.html
[sys-props]: http://docs.oracle.com/javase/7/docs/api/java/lang/System.html#getProperties()
[javakarol]: http://www.schule.bayern.de/karol/jkarol.htm
[jkarol-pdf]: http://www.schule.bayern.de/karol/data/jkhandbuch.pdf
