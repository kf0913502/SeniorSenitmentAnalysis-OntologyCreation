package SentimentAnalysis

import DataCollectionModel.{OntologyNode, OntologyTree}
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.util.CoreMap
import play.api.libs.json.Json
import SentimentAnalysis.nlpWrapper
import scala.collection.JavaConversions._
import scala.util.control.Breaks._
import scalaj.http.{HttpOptions, Http}

/**
 * Created by abdelrazektarek on 5/2/15.
 */
object SentimentCalculator {

  var wrapper : nlpWrapper = null
  var accuracy = 0
  var matches = 0
  def calcSentiment(sentenceSentiments : List[(String, List[(String, Int)])], OT : OntologyTree) : OntologyTree =  {


    wrapper = new nlpWrapper("tokenize, ssplit, pos, lemma, parse, sentiment")

    var reviews = sentenceSentiments.map(_._1)
    var ontologyTree = OT.copy()
    var reverseBFSNodes = ontologyTree.getBFSNodes().reverse
    sentenceSentiments.foreach( rev => {
      val sentences = wrapper.getSentences(rev._1)
      for (sen <- sentences) {
        //      val sentence = wrapper.removeStopWords(sen)
        val sentence = sen
        var tokens = wrapper.getTokens(sentence)
        var graph = getGraph(sentence, tokens)
        var features = getFeatures(sentence, reverseBFSNodes)
        calculateSentiment(graph, features, tokens, rev._2)

      }//Sentences


    })//Reviews

    //ontologyTree.aggregateSentiment()
    println("sentiment accuracy: " + accuracy.asInstanceOf[Double]/matches)
    ontologyTree

  }


  def getGraph(Sentence: CoreMap, Tokens:List[CoreLabel]): WeightedGraph = {

    val numTokens = Tokens.length
    //println("Number of Tokens: " + numTokens)

    val g = new WeightedGraph(1)
    for (i <- 1 to numTokens) g.addNode


    val dependency = wrapper.getDependencies(Sentence)


    for (d <- dependency) {
      if (!d.reln().toString.equals("root")) {
        //        println("Relation: "+d.reln().toString)
        val Gov = g.nodes.get(d.gov().index() - 1)
        val dep = g.nodes.get(d.dep().index() - 1)
        //        println("Gov: "+d.gov().toString +" index: "+d.gov().index())
        Gov.connectWith(dep)
      }
    }
    g
  }

  def calculateSentiment(graph:WeightedGraph, features:Map[List[CoreLabel],OntologyNode],
                         Tokens:List[CoreLabel], sentimentLabels : List[(String, Int)]): Unit ={

    if(features.isEmpty)
      return

    var featuresIndexs = List[Int]()

    for( f <- features)
      for(term <- f._1) featuresIndexs :+= term.index()-1


    val dijkstra = new Dijkstra[graph.type](graph)
    var results = collection.mutable.Map[OntologyNode, Int]()


    graph.nodes.zipWithIndex.foreach{
      case (word,index) =>
        var nearestFeature = features.head._1
        var nearestFeatureDistance = Int.MaxValue
        if(!featuresIndexs.contains(index)){
          features.foreach{
            case(feature, node) =>
              var nearestTerm = feature(0)
              var nearestTermDistance = Int.MaxValue
              feature.foreach{
                term =>
                  val termNode = graph.nodes.get(term.index()-1)
                  val wordNode = graph.nodes.get(index)
                  val (start, target) = (termNode, wordNode)
                  dijkstra.stopCondition = (S, D, P) => !S.contains(target)
                  val (distance, path) = dijkstra.compute(start, target)
                  if(distance.contains(target) && distance(target) < nearestTermDistance) {
                      nearestTermDistance = distance(target)
                      nearestTerm = term
                    }
//                  println("Shortest-path cost: " + distance(target))
              }
              if (nearestTermDistance < nearestFeatureDistance){
                nearestFeatureDistance = nearestTermDistance
                nearestFeature = feature
              }
          }
          val sent:Int = wrapper.getSentiment(Tokens(index)).toLowerCase() match{
            case "negative"|"very negative" => -1
            case "neutral" => 0
            case "positive"|"very positive" => 1
          }


          var fet = features(nearestFeature)
          results(fet) = results.getOrElse(fet, 0)  + sent

        }
    }

    results.foreach{case(k,v) =>
      k.sentiment += {if (v > 0) 1 else if (v < 0) -1 else 0}
      sentimentLabels.foreach{case (f,s) => if (k.features(0) == f){if (s < 0) matches +=1; if (s < 0 && v < 0) accuracy +=1; }}}

  }

  def getFeatures(Sentence: CoreMap, TreeBFSR: List[OntologyNode]): Map[List[CoreLabel],OntologyNode] = {
    val candidateFeatures = wrapper.groupConsecuetiveNouns(Sentence)
    var features:Map[List[CoreLabel],OntologyNode] = Map()

    for(feature <- candidateFeatures){
      breakable {
        for (node <- TreeBFSR) {
          for (term <- feature)
            if (node.features.contains(wrapper.getTokenText(term))) {
              features += (feature -> node)
              break
            }
        }
      }
    }
    features
  }


}
