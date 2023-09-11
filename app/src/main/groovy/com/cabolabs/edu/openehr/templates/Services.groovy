package com.cabolabs.edu.openehr.templates

import java.nio.file.Path
import java.nio.file.Files
import com.cabolabs.openehr.opt.parser.OperationalTemplateParser
import com.cabolabs.openehr.opt.model.OperationalTemplate
import com.cabolabs.openehr.opt.model.*
import com.cabolabs.openehr.opt.model.primitive.*
import com.cabolabs.openehr.opt.model.domain.*

class Services {

   static String v = '├'
   static String h = '─'
   static String p = '│'
   static String l = '└'

   static OperationalTemplate opt

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

   static void traverse(Path pathToOpt)
   {
      opt = parse(pathToOpt)

      // Interval.metaClass.toString = {
      //    "Interval\t" + (delegate.lower != null ? delegate.lower.toString() : '*') +'..'+ (delegate.upper != null ? delegate.upper.toString() : '*')
      // }

      // Services.Traverse
      Services.Traverse traverse = new Services.Traverse()
      traverse.run(opt)
   }

   static void render(Path pathToOpt, String templatePath)
   {
      opt = parse(pathToOpt)

      List<Constraint> cs = opt.getNodes(templatePath)

      Services.ConstraintRender render = new Services.ConstraintRender()
      cs.each {
         render.render(it)
      }
   }


   // UTILITIES
   // --------------------------------------

   static getArchetypeIdFromTemplatePath(String templatePath)
   {
      def archetype_id

      // get archetype_id from the templatePath since the node doesn't have an arcehtype_node_id set in the template
      def matcher = (templatePath =~ /.*\[archetype_id=(.*?)\].*/) // match last archetype_id in path
      if (!matcher.matches())
      {
         //println "Can't find archetype_id in template path '${templatePath}'"
         archetype_id = opt.definition.archetypeId // take the archetype id from the root because it's not in the path
      }
      else
      {
         // matcher[0] is a list of two elements when a group is defined, see https://docs.groovy-lang.org/latest/html/groovy-jdk/java/util/regex/Matcher.html#getAt(int)
         // 0: the matched string
         // 1: the matched group
         archetype_id = matcher[0][1]
      }

      return archetype_id
   }

   static class Traverse {

      int level = 0
      OperationalTemplate opt

      def run(OperationalTemplate opt)
      {
         this.opt = opt
         run(opt.definition)
      }

      def indent(boolean last = false)
      {
         //"  ".multiply(level) + v +" "
         if (last)
           (p +' ').multiply(level) + l + ' '
         else
           (p +' ').multiply(level) + v + ' '
      }

      def run(ObjectNode o)
      {
         println indent() + o.rmTypeName +' ('+ o.text + '): '+ o.templatePath
         o.attributes.each {
            level ++
            run(it)
            level --
         }
      }

      def run(PrimitiveObjectNode c)
      {
         run(c.item)
      }

      def run(CString c)
      {
         if (c.pattern) println indent(true) + c.pattern
         else println indent(true) + c.list
      }

      def run(CInteger c)
      {
         if (c.range) println indent(true) + c.range.toString()
         else println indent(true) + c.list
      }

      def run(CDateTime c)
      {
         println indent(true) + c.pattern
      }

      def run(CDate c)
      {
         println indent(true) + c.pattern
      }

      def run(CTime c)
      {
         println indent(true) + c.pattern
      }

      def run(CReal c)
      {
         if (c.range) println indent(true) + c.range.toString()
         else println indent(true) + 'ANY'
      }

      def run(CDuration c)
      {
         if (c.range) println indent(true) + c.range.toString()
         else println indent(true) + c.pattern
      }

      def run(CBoolean c)
      {
         println indent(true) + "trueValid: "+ c.trueValid +" falseValid: "+ c.falseValid
      }

      def run(CDvOrdinal c)
      {
         def archetype_id

         c.list.each {
            if (it.symbol.terminologyId == 'local')
            {
               archetype_id = getArchetypeIdFromTemplatePath(c.templatePath)
               println indent(it == c.list.last()) + c.rmTypeName +"\t"+ it.value +" "+ it.symbol.terminologyId +'::'+ it.symbol.codeString+' ('+ opt.getTerm(archetype_id, it.symbol.codeString) +')'
            }
            else
            {
               println indent(it == c.list.last()) + c.rmTypeName +"\t"+ it.value +" "+ it.symbol.terminologyId +'::'+ it.symbol.codeString
            }
         }
      }

      def run(CDvQuantity c)
      {
         c.list.each {
            println indent(it == c.list.last()) + c.rmTypeName +"\t"+ it.units +" "+ (it.magnitude ? (it.magnitude.lower +".."+ it.magnitude.upper) : ('*..*'))
         }
      }

      def run(CCodePhrase c)
      {
         if (c.terminologyId == 'local')
         {
            def archetype_id = getArchetypeIdFromTemplatePath(c.templatePath)

            c.codeList.each {
               println indent(it == c.codeList.last()) + c.rmTypeName +"\t"+ c.terminologyId +"::"+ it +' ('+ opt.getTerm(archetype_id, it) +')'
            }
         }
         else
         {
            // TODO: if it's openEHR get the term from that terminology
            c.codeList.each {
               println indent(it == c.codeList.last()) + c.rmTypeName +"\t"+ c.terminologyId +"::"+ it
            }
         }
      }

      /*
      def run(ArchetypeSlot c)
      {
         println indent(false) + "SLOT: "+ c.path()
         c.includes.each { assertion ->
            println indent(assertion == c.includes.last()) + assertion.stringExpression
         }
      }
      */

      def run(AttributeNode a)
      {
         println indent() + a.rmAttributeName +": "+ a.templatePath
         a.children.each {
            level++
            run(it)
            level --
         }
      }
   }

      // Use to render single constraint nodes
   static class ConstraintRender {

      def render(ObjectNode o)
      {
         println o.rmTypeName +': '+ o.templatePath

         println o.rmTypeName +' ('+ o.text + ') <'+ o.class.simpleName +'>'

         // NOTE: this will detect any missing methods specific for the constraint type
         println o.getClass()
      }

      def render(PrimitiveObjectNode c)
      {
         render(c.item)
      }

      def render(CString c)
      {
         if (c.pattern) println c.pattern
         else println c.list
      }

      def render(CInteger c)
      {
         if (c.range) println c.range.toString()
         else println c.list
      }

      def render(CDateTime c)
      {
         println c.pattern
      }

      def render(CDate c)
      {
         println c.pattern
      }

      def render(CTime c)
      {
         println c.pattern
      }

      def render(CReal c)
      {
         if (c.interval) println c.interval.toString()
         else println 'ANY'
      }

      def render(CDuration c)
      {
         if (c.range) println c.range.toString()
         else println c.pattern
      }

      def render(CBoolean c)
      {
         println "trueValid: "+ c.trueValid +" falseValid: "+ c.falseValid
      }

      def render(CDvOrdinal c)
      {
         // c.list.each {
         //    println c.rmTypeName +"\t"+ it.value +" "+ it.symbol.terminologyId +'::'+ it.symbol.codeString
         // }

         def archetype_id

         c.list.each {
            if (it.symbol.terminologyId == 'local')
            {
               archetype_id = getArchetypeIdFromTemplatePath(c.templatePath)
               println c.rmTypeName +"\t"+ it.value +" "+ it.symbol.terminologyId +'::'+ it.symbol.codeString+' ('+ opt.getTerm(archetype_id, it.symbol.codeString) +')'
            }
            else
            {
               println c.rmTypeName +"\t"+ it.value +" "+ it.symbol.terminologyId +'::'+ it.symbol.codeString
            }
         }
      }

      def render(CDvQuantity c)
      {
         println c.rmTypeName +' <'+ c.class.simpleName +'>'
         c.list.each {
            println it.units +' '+ (it.magnitude ? (it.magnitude.lower +".."+ it.magnitude.upper) : ('*..*'))
         }
      }

      def render(CCodePhrase c)
      {
         println c.rmTypeName +' <'+ c.class.simpleName +'>'

         if (c.terminologyId == 'local')
         {
            def archetype_id = getArchetypeIdFromTemplatePath(c.templatePath)

            c.codeList.each {
               println c.rmTypeName +"\t"+ c.terminologyId +"::"+ it +' ('+ opt.getTerm(archetype_id, it) +')'
            }
         }
         else
         {
            // TODO: if it's openEHR get the term from that terminology
            c.codeList.each {
               println c.rmTypeName +"\t"+ c.terminologyId +"::"+ it
            }
         }
      }

      // def render(ArchetypeSlot c)
      // {
      //    println c.rmTypeName +' <'+ c.class.simpleName +'>'
      //    c.includes.each { assertion ->
      //       println assertion.stringExpression
      //    }
      // }
   }
}