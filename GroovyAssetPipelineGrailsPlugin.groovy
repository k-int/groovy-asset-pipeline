import java.util.Collection;

import asset.pipeline.AssetFile
import asset.pipeline.AssetHelper

import com.k_int.asset.pipeline.groovy.*

class GroovyAssetPipelineGrailsPlugin {
    // The plugin version
    def version = "1.2"

    // The version or versions of Grails the plugin is designed for.
    def grailsVersion = "2.4 > *"

    def title = "SimpleTemplateEngine support for Grails Asset Pipeline"

    def author = "Steve Osguthorpe"
    def authorEmail = "steve.osguthorpe@k-int.com"

    def description = '''\
Provides SimpleTemplateEngine processing support (including Grails Config access) to asset pipeline files.\
2 items are bound to the template for convenient access withihn your templates. \"config\" and \"grailsApplpication\".\
The dolar sign '$' should be escaped (\$) to stop the template from interpreting as a groovy expression.
'''
//    def documentation = ""

    def organization = [ name: "Knowledge Integration", url: "http://www.k-int.com/" ]
    //def issueManagement = [ system: "GITHUB", url: "http://github.com/tednaleid/groovy-template-grails-asset-pipeline/issues" ]
    def scm = [ url: "http://github.com/k-int/groovy-asset-pipeline" ]
    
    def doWithDynamicMethods = { ctx ->
  
      // Grab the current values.
      def current_specs = AssetHelper.assetSpecs
      
      final ExtendedAssetSpecList listener = new ExtendedAssetSpecList()
      
      // Add all the current values to the new list.
      listener.addAll(current_specs)
      
      // Add a getter for the assetspecs so users will grab that instead.  
      AssetHelper.metaClass.static.getAssetSpecs = { ->
        listener
      }
    }
}