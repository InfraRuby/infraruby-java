module Boolean
	class << self
		def [](o)
			return o
		end
	end

	TRUE = Boolean[true]
	FALSE = Boolean[false]

	def to_boolean
		return self
	end
end
