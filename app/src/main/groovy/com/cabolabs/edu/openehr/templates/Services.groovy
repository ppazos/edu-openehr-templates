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
         //println o.getClass().getSimpleName() +"\t"+ o.rmTypeName.padLeft(15) +"\t"+ o.path()

         //println indent() + o.rmTypeName +': '+ o.path

         //println o.getClass().getSimpleName() != 'ObjectNode' ? o.getClass().getSimpleName() : ''

         println indent() + o.rmTypeName +' ('+ o.text + '): '+ o.path
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
         if (c.interval) println indent(true) + c.interval.toString()
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
         if (c.interval) println indent(true) + c.interval.toString()
         else println indent(true) + c.list
      }

      def run(CDuration c)
      {
         if (c.interval) println indent(true) + c.interval.toString()
         else println indent(true) + c.value
      }

      def run(CBoolean c)
      {
         println indent(true) + "trueValid: "+ c.trueValid +" falseValid: "+ c.falseValid
      }

      def run(CDvOrdinal c)
      {
         c.list.each {
            println indent(it == c.list.last()) + c.rmTypeName +"\t"+ it.value +" "+ it.symbol.terminologyId +'::'+ it.symbol.codeString
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
         c.codeList.each {
            println indent(it == c.codeList.last()) + c.rmTypeName +"\t"+ c.terminologyId +"::"+ it
         }
      }

      /*
      def run(ConstraintRef c)
      {
         println indent(true) + 'CREF ('+ c.reference +'): '+ c.path()
      }

      def run(ArchetypeInternalRef c)
      {
         println indent(true) + 'IREF ('+ c.targetPath +'): '+ c.path()
      }

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
         //println a.getClass().getSimpleName() +"\t"+ a.rmAttributeName.padLeft(15) +"\t"+ a.path()
         println indent() + a.rmAttributeName +": "+ a.path
         a.children.each {
            level++
            run(it)
            level --
         }
      }
   }
}