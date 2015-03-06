package com.k_int.asset.pipeline.groovy

import grails.util.Holders
import groovy.text.SimpleTemplateEngine
import groovy.util.logging.Log4j

import org.codehaus.groovy.grails.commons.GrailsApplication

import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile

@Log4j
class GroovyAssetPreProcessor {

  private static SimpleTemplateEngine engine
  def binding
  static def extensions = ['grass']
  
  
  GroovyAssetPreProcessor(AssetCompiler compiler = null) {
    GrailsApplication grailsApplication
    
    if (!compiler) {
      grailsApplication = Holders.getGrailsApplication()
      
    } else {
      
      // Called from script. We read the attributes from the options passed from our event listener.
      grailsApplication = compiler.options."grailsApplication"
    }
    
    // Create the simple template engine if we haven't got one.
    if (!engine) {
      log.debug("Constructing Groovy template pre-processor.")
      engine = new SimpleTemplateEngine()
    }
    
    log.debug ("Adding bindings to pass to template.")
    this.binding = [
      grailsApplication: grailsApplication,
      config: grailsApplication.config
    ]
  }

  def process(String inputText, AssetFile assetFile) {

    // Just check the extension... If it matches one we process then we should
    // act on it.
    if (inputText && extensions.find { assetFile?.path?.matches("^.+\\.${it}\\..+") }) {

      // Attempt to process the file.
      log.debug("Processing assetFile: ${assetFile.name} as Groovy text")
      StringWriter stringWriter = new StringWriter()
      try {
        engine.createTemplate(inputText).make(binding).writeTo(stringWriter)
        return stringWriter.toString()
      } catch (Exception e) {
        log.error("Error compiling groovy template asset file", e)
        throw e
      } finally {
        stringWriter.close()
      }
      
    }
    
    log.debug("Groovy processor skipping assetFile: ${assetFile.name}")
    
    // Just return the original if we don't recognise this file.
    return inputText
  }
}
