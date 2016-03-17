infraruby-java
==============

This gem provides the InfraRuby Java integration for Ruby interpreters.


Example
-------

	require "infraruby-java"

	m = JAVA::byte[1].new
	m[0] = 3.byte
	m[0].class # => Byte
	m.length.class # => Int32


Support
-------

http://infraruby.com/
