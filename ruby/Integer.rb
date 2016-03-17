class Integer
	def as_ruby
		return self
	end

	def to_byte
		return Byte[self]
	end

	def to_byte!
		if -0x80 <= self && self <= 0x7F
			return Byte[self]
		end
		raise RangeError
	end

	def to_char
		return Char[self]
	end

	def to_char!
		if 0x0000 <= self && self <= 0xFFFF
			return Char[self]
		end
		raise RangeError
	end

	def to_int16
		return Int16[self]
	end

	def to_int16!
		if -0x8000 <= self && self <= 0x7FFF
			return Int16[self]
		end
		raise RangeError
	end

	def to_int32
		return Int32[self]
	end

	def to_int32!
		if -0x80000000 <= self && self <= 0x7FFFFFFF
			return Int32[self]
		end
		raise RangeError
	end

	def to_int64
		return Int64[self]
	end

	def to_int64!
		if -0x8000000000000000 <= self && self <= 0x7FFFFFFFFFFFFFFF
			return Int64[self]
		end
		raise RangeError
	end

	def to_float32
		return Float32[self]
	end

	def to_float64
		return Float64[self]
	end

	alias_method :byte, :to_byte
	alias_method :char, :to_char
	alias_method :i16, :to_int16
	alias_method :i32, :to_int32
	alias_method :i64, :to_int64
	alias_method :f32, :to_float32
	alias_method :f64, :to_float64
end
