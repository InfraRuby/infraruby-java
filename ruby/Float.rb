class Float
	def as_ruby
		return self
	end

	def to_float32
		return Float32[self]
	end

	def to_float64
		return Float64[self]
	end

	alias_method :f32, :to_float32
	alias_method :f64, :to_float64
end
