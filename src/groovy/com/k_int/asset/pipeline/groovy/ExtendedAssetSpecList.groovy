package com.k_int.asset.pipeline.groovy

import groovy.util.logging.Log4j;
import asset.pipeline.AssetFile

@Log4j
class ExtendedAssetSpecList extends ArrayList {

  private Object extendObject(Object o) {

    log.debug ("attempting to extend ${o}")
    
    if (o!= null) {
      Class c = null
      if (o instanceof String) {
        // Try and get the class from the string.
        try {
          c = Class.forName((o as String))
        } catch (Throwable t) {
          log.error("Supplied string is not a class name")
          c = null
        }
      } else if (o instanceof Class) {
        
        // It's a class.
        c = o as Class
      }
  
      if (c!= null) {
  
        // Test for AssetFile type.
        if (AssetFile.class.isAssignableFrom(c)) {
  
          // Get the defined asset file extensions.
          def ex = c.extensions
  
          // List of new extensions.
          def newExt = []
  
          if (!c.processors.contains(GroovyAssetPreProcessor)) {
            log.debug ("Adding GroovyAssetProcessor to ${c}")
            c.processors = [GroovyAssetPreProcessor] + c.processors
            log.debug ("Processors ${c.processors}")
          }
  
          ex.each {orig_ext ->
            GroovyAssetPreProcessor.extensions.each { supported_ex ->
  
              String extension = "${supported_ex}.${orig_ext}"
              if (!c.extensions.contains(extension)) {
  
                log.debug("Adding ${supported_ex}.${orig_ext} support")
                newExt << extension
              }
            }
          }
  
          // Add any extensions needed.
          c.extensions += newExt
  
          // Return the object.
          return o
        }
      }
      
      
    }

    log.debug ("None AssetFile class object added to asset specs.")
    return o
  }

  @Override
  public void add (int index, Object element) {
    super.add(index, extendObject(element))
  }

  @Override
  public boolean add (Object e) {
    return super.add(extendObject(e))
  }

  @Override
  public boolean addAll (Collection c) {

    boolean success = true
    c.each { success = success && add(it) }
    success
  }

  @Override
  public boolean addAll (int index, Collection c) {

    boolean success = true
    int pos = index
    c.each { success = success && add(pos, it); pos++ }
    success
  }
  
  public def plus (def obj) {
    log.debug ("Addition detected! Using leftshift instead.")
    return this.leftShift(obj)
  }
}

