if RUBY_PLATFORM =~ /java/
	File.write("Makefile", <<-END)
install:
	END
else
	require "mkmf"
	create_makefile("primitive/primitive")
end
