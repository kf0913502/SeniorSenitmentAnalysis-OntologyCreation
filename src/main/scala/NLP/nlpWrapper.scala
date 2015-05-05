package NLP

import java.util.Properties
import java.util.ArrayList

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation

import scala.collection.JavaConversions._
import edu.stanford.nlp.ling.{CoreLabel, CoreAnnotations}
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.trees.{TypedDependency, PennTreebankLanguagePack, TreebankLanguagePack, Tree}
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation
import edu.stanford.nlp.util.CoreMap
import edu.stanford.nlp.parser.lexparser.LexicalizedParser

/**
 * Created by abdelrazektarek on 5/2/15.
 */
case class nlpWrapper(annotators: String) {
  val props: Properties = new Properties
  props.setProperty("annotators", annotators)
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)
  val lp: LexicalizedParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz")

  def getSentences(text: String): List[CoreMap] = {
    // create an empty Annotation just with the given text
    val document: Annotation = new Annotation(text)

    // run all Annotators on this text
    pipeline.annotate(document)

    // these are all the sentences in this document
    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
    val sentences: List[CoreMap] = document.get(classOf[CoreAnnotations.SentencesAnnotation]).toList
    sentences.toList
  }

  def groupConsecuetiveNouns(sentence : CoreMap): List[List[CoreLabel]] =
  {
    var nouns = List[List[CoreLabel]]()
    getTokens(sentence).foldLeft(List[CoreLabel]()){
      (a,b) => if (getTokenPOS(b).contains("NN")) a :+ b
        else {if (!a.isEmpty)nouns :+= a; List[CoreLabel]()}
    }

    nouns
  }

  def getFrequentNouns(texts : List[String], threshold : Double) : List[String] =
  {
    val sentences =  getSentences(texts.mkString(". "))
    var nouns = Map[String, Int]()
    sentences.foreach(s => getTokens(s).foreach(x => {
      if (getTokenPOS(x).contains("NN"))
        nouns = nouns + (getTokenLemma(x).toLowerCase -> {nouns.getOrElse(getTokenLemma(x).toLowerCase,0) +1} )
    }))

    nouns.filter({case (k,v) => v.asInstanceOf[Double]/sentences.size > threshold}).keySet.toList


  }
  def getTokenText(token:CoreLabel): String ={
    token.get(classOf[CoreAnnotations.TextAnnotation])
  }

  def getTokenLemma(token:CoreLabel) : String = {
    token.get(classOf[LemmaAnnotation])
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
    val parse: Tree = lp.apply(this.getTokens(Sentence))

    var phraseList: List[Tree] = List()
    for ( subtree:Tree <- parse)
    {

      if(subtree.label().value().equals(tag))
      {

        phraseList +:= (subtree)
        println("Tree:\n"+subtree);

      }
    }

    phraseList.toList
  }

  def getTokens(text: CoreMap): List[CoreLabel] ={
    text.get(classOf[CoreAnnotations.TokensAnnotation]).toList
  }

  def getDependencies(Sentence:CoreMap): List[TypedDependency] ={
    val tree: Tree = Sentence.get(classOf[TreeAnnotation])
    // Get dependency tree
    val tlp: TreebankLanguagePack = new PennTreebankLanguagePack()
    val gsf = tlp.grammaticalStructureFactory
    val gs = gsf.newGrammaticalStructure(tree)
    val td = gs.typedDependenciesCollapsed
    println("Dependencies:\n"+td)
    td.toList
  }


}
