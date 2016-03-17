class Object
	def is_a?(c)
		if c == JAVA::java.lang.Object
			return true
		end
		return super(c)
	end

	def equals(o)
		return eql?(o)
	end

	def hashCode
		return hash.to_int32
	end

	def toString
		return to_s.to_j
	end
end
