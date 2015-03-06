// includeTargets << new File(assetPipelinePluginDir, "scripts/_AssetCompile.groovy")

eventAssetPrecompileStart = { assetConfig ->
  
  // Replace the asset specs with our special list implementation that will ensure our pre-processor
  // is run first. Since asset-pipeline separated the asset core the assetSpecs property was made final.
  // We extend using the meta-class.
  Class ExtendedAssetSpecList = Class.forName("com.k_int.asset.pipeline.groovy.ExtendedAssetSpecList")
  Class AssetHelper = Class.forName("asset.pipeline.AssetHelper") 
  def current_specs = AssetHelper.assetSpecs
      
  final def listener = ExtendedAssetSpecList.newInstance()
  
  // Add all the current values to the new list.
  listener.addAll(current_specs)
  
  // Add a getter for the assetspecs so our special listening list will be returned instead.  
  AssetHelper.metaClass.static.getAssetSpecs = { ->
    listener
  }
    
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
