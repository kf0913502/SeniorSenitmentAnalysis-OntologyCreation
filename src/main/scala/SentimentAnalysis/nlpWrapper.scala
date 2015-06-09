package SentimentAnalysis

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation
import edu.stanford.nlp.ling.{CoreAnnotations, CoreLabel}
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation
import edu.stanford.nlp.trees.{PennTreebankLanguagePack, Tree, TreebankLanguagePack, TypedDependency}
import edu.stanford.nlp.util.CoreMap
import scala.collection.JavaConversions._
/**
 * Created by abdelrazektarek on 5/2/15.
 */


case class nlpWrapper(annotators: String) {
  val props: Properties = new Properties
  props.setProperty("annotators", annotators)
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)

  def getTokenLemma(token:CoreLabel) : String = {
    token.get(classOf[LemmaAnnotation])
  }


  def getSentences(text: String): List[CoreMap] = {
    // create an empty Annotation just with the given text
    var document: Annotation = new Annotation(text)

    // run all Annotators on this text
    pipeline.annotate(document)

    // these are all the sentences in this document
    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
    var sentences: List[CoreMap] = document.get(classOf[CoreAnnotations.SentencesAnnotation]).toList
    document = null

    sentences.toList

  }

  def removePunc(tokens: List[CoreLabel]): List[CoreLabel] ={
    tokens.filter(!getTokenText(_).matches(("\\p{Punct}+")))
  }

  def groupConsecuetiveNouns(sentence: CoreMap): List[List[CoreLabel]] = {
    var nouns = List[List[CoreLabel]]()
    getTokens(sentence).foldLeft(List[CoreLabel]()) {
      (a, b) => if (getTokenPOS(b).contains("NN")) a :+ b
      else {
        if (!a.isEmpty) nouns :+= a; List[CoreLabel]()
      }
    }
    //println("nouns: "+nouns)
    nouns
  }

  def groupConsecuetiveNounsIndexs(sentence: CoreMap): List[List[Int]] = {
    //    val features = Map[String, List]
    //    var nouns = List[List[Int]]()
    //    getTokens(sentence).zipWithIndex.foldLeft(List[Int]()){
    //      case (a,(value,index)) => if (getTokenPOS(value).contains("NN")) a :+ index
    //      else {if (!a.isEmpty)nouns :+= a; List[Int]()}
    //    }
    var nouns = List[List[Int]]()
    getTokens(sentence).zipWithIndex.foldLeft(List[Int]()) {
      case (a, (value, index)) =>
        if (getTokenPOS(value).contains("NN"))
          a :+ index
        else {
          if (!a.isEmpty)
            nouns :+= a;
          List[Int]()
        }
    }

    nouns
  }

  def getFrequentNouns(texts : List[String], threshold : Double) : List[String] =
  {
    val sentences =  getSentences(texts.mkString(". "))
    var nouns = Map[String, Int]()
    sentences.foreach(s => getTokens(s).foreach(x => {
      if (getTokenPOS(x).contains("NN"))
        nouns = nouns + (getTokenText(x).toLowerCase -> {nouns.get(getTokenText(x).toLowerCase).getOrElse(0) +1} )
    }))

    nouns.filter({case (k,v) => v.asInstanceOf[Double]/sentences.size > threshold}).keySet.toList


  }

  def getTokenText(token:CoreLabel): String ={
    token.get(classOf[CoreAnnotations.TextAnnotation])
  }

  def getTokenPOS(token:CoreLabel): String ={
    token.get(classOf[CoreAnnotations.PartOfSpeechAnnotation])
  }

  def getTokenNER(token:CoreLabel): String ={
    token.get(classOf[CoreAnnotations.NamedEntityTagAnnotation])
  }

  def getSentiment(token:CoreLabel): String ={
    token.get(classOf[SentimentCoreAnnotations.SentimentClass])
  }

  def getSentiment(Sentence:CoreMap): String ={
    Sentence.get(classOf[SentimentCoreAnnotations.SentimentClass])
  }

  def getTokensWithTag(Sentence:CoreMap, tag:String): List[Tree] ={
    val parse: Tree = Sentence.get(classOf[TreeAnnotation])

    var phraseList: List[Tree] = List()
    for ( subtree:Tree <- parse)
    {

      if(subtree.label().value().equals(tag))
      {

        phraseList +:= (subtree)
        //println("Tree:\n"+subtree);

      }
    }

    phraseList.toList
  }

  def getTokens(text: CoreMap): List[CoreLabel] ={
    text.get(classOf[CoreAnnotations.TokensAnnotation]).toList
  }

  def getDependencies(Sentence:CoreMap): List[TypedDependency] ={
    var tree: Tree = Sentence.get(classOf[TreeAnnotation])
    // Get dependency tree
    var tlp: TreebankLanguagePack = new PennTreebankLanguagePack()
    var gsf = tlp.grammaticalStructureFactory
    var gs = gsf.newGrammaticalStructure(tree)
    var td = gs.typedDependenciesCollapsed

    tree = null
    tlp = null
    gsf = null
    gs = null
    //println("Dependencies:\n"+td)
    td.toList
  }


}
