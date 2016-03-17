class String
	def j
		return to_j
	end

	def to_byte
		return to_i.to_byte
	end

	def to_char
		return to_i.to_char
	end

	def to_int16
		return to_i.to_int16
	end

	def to_int32
		return to_i.to_int32
	end

	def to_int32!
		return to_i.to_int32!
	end

	def to_int64
		return to_i.to_int64
	end

	def to_int64!
		return to_i.to_int64!
	end

	def to_float32
		return to_f.to_float32
	end

	def to_float64
		return to_f.to_float64
	end
end
