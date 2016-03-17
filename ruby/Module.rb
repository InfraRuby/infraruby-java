class Module
	def j_import(c)
		const_set(c.name[/\w+$/], c)
		return
	end
end
