Gem::Specification.new do |s|
	s.platform = "ruby"
	s.name = "infraruby-java"
	s.version = "4.0.0"
	s.licenses = ["MIT"]
	s.author = "InfraRuby Vision"
	s.email = "rubygems@infraruby.com"
	s.homepage = "http://infraruby.com/"
	s.summary = "InfraRuby Java integration for Ruby interpreters"
	s.description = "InfraRuby Java integration for Ruby interpreters"
	s.extensions = ["ext/primitive/extconf.rb"]
	s.files = Dir["**/*"]
	s.add_runtime_dependency "infraruby-base", "~> 4.0"
	s.add_development_dependency "infraruby-task", "~> 4.0"
	s.add_development_dependency "rspec", "~> 3.0"
end
