InfraRuby.if(RUBY_ENGINE == "ruby") {

	JAVA.module_eval do
		__package(:java) {
			__package(:lang) {
				__class "Object", JAVA::BasicObject do
					def equals(o)
						return equal?(o)
					end

					def hashCode
						return __id__.to_int32
					end

					def toString
						return inspect.to_j
					end
				end

				__class "Throwable", JAVA::java.lang.Object do
					def initialize(message = nix)
						super()
						if message.nix?
							@message = nil
						else
							@message = message
						end
						return
					end

					def toString
						if @message.nil?
							return self.class.toString
						else
							return "#{self.class}: #{@message}".j
						end
					end

					def getMessage
						return @message
					end
				end
			}
		}
	end

}

JAVA::Value.class_eval do
	def pretty_print(pp)
		return
	end
end

JAVA::java.lang.Object.class_eval do
	def ===(o)
		return equals(o)
	end

	def eql?(o)
		return equals(o)
	end

	def hash
		return hashCode.to_i
	end

	def to_s
		return String.from_j(toString)
	end
end

class << JAVA::java.lang.Object
	def ===(o)
		if self == JAVA::java.lang.Object
			return true
		end
		return super(o)
	end
end

InfraRuby.if(RUBY_ENGINE == "ruby") {

	JAVA::java.lang.Throwable.class_eval do
		def exception
			return JAVA::Exception.new(self)
		end
	end

}

InfraRuby.if(RUBY_ENGINE == "jruby") {

	JAVA::java.lang.Throwable.class_eval do
		def exception
			return self
		end
	end

}

class << JAVA::java.lang.Throwable
	def ===(o)
		if self == JAVA::java.lang.Throwable
			if o.class <= Exception
				return true
			end
		end
		return super(o)
	end

	def exception(*args)
		return new(*args).exception
	end
end
