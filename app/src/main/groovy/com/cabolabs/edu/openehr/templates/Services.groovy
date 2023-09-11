package com.cabolabs.edu.openehr.templates

import java.nio.file.Path
import java.nio.file.Files
import com.cabolabs.openehr.opt.parser.OperationalTemplateParser
import com.cabolabs.openehr.opt.model.OperationalTemplate

class Services {

   static OperationalTemplate parse(Path pathToOpt)
   {
      if (!Files.exists(pathToOpt))
      {
         println "Service.parse() ERROR: file $pathToOpt doesn't exist"
         return
      }

      def parser = new OperationalTemplateParser()

      def optFile = pathToOpt.toFile() //new File(getClass().getResource(path).toURI())
      def text = optFile.getText()

      return parser.parse(text)
   }
}