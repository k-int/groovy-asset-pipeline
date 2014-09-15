import java.util.Collection;

import asset.pipeline.AssetFile
import asset.pipeline.AssetHelper;

import com.k_int.asset.pipeline.groovy.*

class GroovyAssetPipelineGrailsPlugin {
    // The plugin version
    def version = "1.0"

    // The version or versions of Grails the plugin is designed for.
    def grailsVersion = "2.3 > *"

    def title = "GSP Support for Grails Asset Pipeline"

    def author = "Steve Osguthorpe"
    def authorEmail = "steve.osguthorpe@k-int.com"

    def description = 'Provides GSP Template support (including Grails Config access) to asset pipeline files.'
//    def documentation = ""

    def organization = [ name: "Knowledge Integration", url: "http://www.k-int.com/" ]
    //def issueManagement = [ system: "GITHUB", url: "http://github.com/tednaleid/groovy-template-grails-asset-pipeline/issues" ]
    //def scm = [ url: "http://github.com/tednaleid/groovy-template-grails-asset-pipeline" ]
    
    def doWithDynamicMethods = { ctx ->
      
      // Replace the asset helper list with a new list implementation that ensures our preprocessor is present.
      def current_specs = AssetHelper.assetSpecs
      
      AssetHelper.assetSpecs = new ExtendedAssetSpecList()
      AssetHelper.assetSpecs.addAll(current_specs)
    }
}