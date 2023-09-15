#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'whatsapp_share_plus'
  s.version          = '2.1.0'
  s.swift_version    = '4.1'
  s.summary          = 'Simple way to share a message, link or files from your flutter app'
  s.description      = <<-DESC
Simple way to share a message, link or files from your flutter app
                       DESC
  s.homepage         = 'https://solusibejo.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Solusi Bejo' => 'chandrashibezzo@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'

  s.ios.deployment_target = '8.0'
end

