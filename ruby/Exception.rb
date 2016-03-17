class Exception
	def is_a?(c)
		if c == JAVA::java.lang.Throwable
			return true
		end
		return super(c)
	end

	def getMessage
		return message.to_j
	end
end

InfraRuby.if(RUBY_ENGINE == "ruby") {

	class << Exception
		def ===(o)
			if self == Exception
				if o.class == JAVA::Exception
					return false
				end
			end
			return super(o)
		end
	end

}

InfraRuby.if(RUBY_ENGINE == "jruby") {

	class << Exception
		def ===(o)
			if self == Exception
				if o.class <= Java::java.lang.Throwable
					path = Kernel.caller_locations.first.path
					unless path.end_with?(".ir")
						return true
					end
				end
			end
			return super(o)
		end
	end

}
