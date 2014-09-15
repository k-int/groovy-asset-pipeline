package com.k_int.asset.pipeline.groovy

import grails.util.Holders
import groovy.text.TemplateEngine
import groovy.util.logging.Log4j

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

import asset.pipeline.AssetCompiler
import asset.pipeline.AssetFile

@Log4j
class GroovyAssetPreProcessor {

  def config
  TemplateEngine engine
  def binding
  
  static def extensions = ['grass']

  GroovyAssetPreProcessor(AssetCompiler compiler) {
    this()
  }

  GroovyAssetPreProcessor() {
    
    // Statically get the application context.
    GrailsApplication grailsApplication = Holders.grailsApplication
    ApplicationContext applicationContext = Holders.applicationContext
    
    // Use the GSP engine.
    this.engine = applicationContext.getBean("groovyPagesTemplateEngine")

    // Bindings which are passed through to the TemplateEngine
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
