require "infraruby-base"

require "primitive/primitive"

InfraRuby.if(RUBY_ENGINE == "ruby") {

	module InfraRuby
		module WithPackage
			def __package(symbol, &block)
				name = symbol.to_s
				o = __get(name)
				if o.nil?
					o = JavaPackage.new
					o.name = __join(name)
					__set(name, o)
				end
				o.instance_eval(&block)
				return
			end
		end

		module WithClass
			def __class(name, superclass, &block)
				o = __get(name)
				if o.nil?
					o = Class.new(superclass)
					o.extend(JavaClass)
					o.name = __join(name)
					__set(name, o)
				end
				o.module_eval(&block)
				return
			end

			def __interface(name, &block)
				o = __get(name)
				if o.nil?
					o = Module.new
					o.extend(JavaClass)
					o.name = __join(name)
					__set(name, o)
				end
				o.module_eval(&block)
				return
			end
		end

		module JavaBase
			include WithPackage

			def __get(name)
				return __send__(name)
			rescue
				return nil
			end

			def __set(name, o)
				c = class << self ; self ; end
				c.send(:define_method, name) { o }
				return
			end

			def __join(name)
				return name
			end
		end

		class JavaPackage < BasicObject
			include WithPackage
			include WithClass

			attr_accessor :name

			def inspect
				return @name
			end

			def nil?
				return false
			end

			def __get(name)
				return __send__(name)
			rescue
				return nil
			end

			def __set(name, o)
				c = class << self ; self ; end
				c.send(:define_method, name) { o }
				return
			end

			def __join(name)
				return "#{self.name}.#{name}"
			end
		end

		module JavaClass
			include WithClass
			include JAVA::ClassMixin

			def __get(name)
				return const_get(name)
			rescue
				return nil
			end

			def __set(name, o)
				const_set(name, o)
				return
			end

			def __join(name)
				return "#{self.name}::#{name}"
			end
		end
	end

	module JAVA
		extend InfraRuby::JavaBase
	end

}

InfraRuby.load_parent_library(__dir__)
