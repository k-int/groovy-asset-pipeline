package com.k_int.asset.pipeline.groovy

import grails.util.Holders
import groovy.text.TemplateEngine
import groovy.util.logging.Log4j

import org.codehaus.groovy.grails.commons.ApplicationAttributes
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator
import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.codehaus.groovy.grails.web.pages.TagLibraryLookup
import org.codehaus.groovy.grails.web.pages.ext.jsp.TagLibraryResolver
import org.springframework.context.ApplicationContext
import org.springframework.web.context.WebApplicationContext

import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile

@Log4j
class GroovyAssetPreProcessor {

  private static TemplateEngine engine
  def binding
  static def extensions = ['grass']
  
  
  GroovyAssetPreProcessor(AssetCompiler compiler = null) {
    GrailsApplication grailsApplication
    
    if (!compiler) {
      log.debug("Constructing GSP template pre-processor.")
      grailsApplication = Holders.getGrailsApplication()
      
      if (!engine) {
        engine = Holders.getApplicationContext().getBean("groovyPagesTemplateEngine")
      }
      
    } else {
      log.debug("Constructing GSP template pre-processor for pre-compilation.")
      
      // Called from script. We read the attributes from the options passed from our event listener.
      grailsApplication = compiler.options."grailsApplication"
      
      
      // We have to create an engine and should probably only do this once.
      if (!engine) {
       
        def srvCtx = compiler.options."servletContext"
        ApplicationContext appCtx = compiler.options."applicationContext"
        DefaultGrailsPluginManager pluginManager = Holders.getPluginManager()
        
        pluginManager.registerProvidedArtefacts(null)
        
        // Configure the app. This will load all beans etc.
        GrailsRuntimeConfigurator cfg = new GrailsRuntimeConfigurator(grailsApplication, appCtx)
        cfg.reconfigure(appCtx, srvCtx, true)
        
        // Bind a mock web request...
        grails.util.GrailsWebUtil.bindMockWebRequest(appCtx)
        
        // Create the tag library lookup.
        TagLibraryLookup tll = new TagLibraryLookup()
        tll.setApplicationContext(appCtx)
        tll.setGrailsApplication(grailsApplication)
        
        // Create teh tag lib resolver.
        TagLibraryResolver tlr = new TagLibraryResolver()
        tlr.setGrailsApplication(grailsApplication)
        tlr.setServletContext(srvCtx)
        
        log.debug ("Creating GSP Template engine.")
        engine = new GroovyPagesTemplateEngine()
        GroovyPagesTemplateEngine en = engine
        en.setApplicationContext(appCtx)
        en.setServletContext(srvCtx)
        en.setClassLoader(compiler.options."classLoader")
        en.setTagLibraryLookup(tll)
        en.setJspTagLibraryResolver(tlr)
      }
    }
    
    log.debug ("Adding bindings to pass through.")
    this.binding = [
      grailsApplication: grailsApplication,
      config: grailsApplication.config
    ]
  }

  def process(String inputText, AssetFile assetFile) {

    // Just check the extension... If it matches one we process then we should
    // act on it.
    if (inputText && extensions.find { assetFile?.file?.getPath()?.matches("^.+\\.${it}\\..+") }) {

      // Attempt to process the file.
      log.debug("Processing assetFile: ${assetFile.file.name} as GSP")
      StringWriter stringWriter = new StringWriter()
      try {
        engine.createTemplate(inputText, assetFile.file.name).make(binding).writeTo(stringWriter)
        return stringWriter.toString()
      } catch (Exception e) {
        log.error("Error compiling groovy template asset file", e)
        throw e
      } finally {
        stringWriter.close()
      }
    }

    log.debug("GSP processor skipping assetFile: ${assetFile.file.name}")
    // Just return the original if we don't recognise this file.
    return inputText
  }
}
