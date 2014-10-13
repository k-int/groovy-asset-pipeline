// includeTargets << new File(assetPipelinePluginDir, "scripts/_AssetCompile.groovy")
import com.k_int.asset.pipeline.groovy.ExtendedAssetSpecList
import asset.pipeline.AssetHelper

eventAssetPrecompileStart = { assetConfig ->
  
  // Replace with our special listeneing list.
  def current_specs = AssetHelper.assetSpecs
  AssetHelper.assetSpecs = new ExtendedAssetSpecList()
  AssetHelper.assetSpecs.addAll(current_specs)
  
  // We need to supply the context and the application here so that our
  // pre-processor can access them when run from a script.
//  assetConfig.grailsApplication = grailsApplication
//  ctx: appCtx, grailsApplication: grailsApp
  
  loadApp()
  
  // loadApp creates the following vars.
  // grailsApp
  // pluginManager
  // servletContext
  // appCtx
  assetConfig."grailsApplication" = grailsApp
  assetConfig."servletContext" = servletContext
  assetConfig."applicationContext" = appCtx
  assetConfig."classLoader" = classLoader
}
