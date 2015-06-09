/**
 * Created by karAdmin on 5/2/2015.
 */


import Ontology._
import play.api.libs.json.Json
import java.io._


import scalaj.http.{HttpOptions, Http}

import SentimentAnalysis.nlpWrapper


object test extends App{

    /*println(Http("http://localhost:9001/insertOntologyTree").
    postData(Json.toJson(Constructor("telephone").createOntologyTree()).toString()).
    headers(Seq("content-Type" -> "text/plain")).asString)*/
    val wrapper = nlpWrapper("tokenize, ssplit, pos, lemma")
  val files = List(/*("camera", "Canon G3.txt"), ("ipod", "Creative Labs Nomad Jukebox Zen Xtra 40GB.txt"), ("camera", "Nikon coolpix 4300.txt"), */("telephone", "Nokia 6610.txt"),
    ("camera", "Canon PowerShot SD500.txt"), ("camera", "Canon S100.txt"), ("router", "Hitachi router.txt"), ("router", "Linksys Router.txt"), ("ipod", "MicroMP3.txt"), ("telephone", "Nokia 6600.txt"), ("software", "norton.txt"))
  files.foreach(x => {

    val ot = Constructor(x._1, "")
    val tree = ot.createOntologyTree()
    val df = tree.getBFSNodes().flatMap(_.features).toSet

    val e = EvaluationCorpusGatherer("C:\\customer review data\\" + x._2)
    val f = e.getCorpus()



    def getLemma(x : String) : String = {(wrapper.getTokenLemma(wrapper.getTokens(wrapper.getSentences(x)(0))(0)))}

    e.sentimentSentences = e.sentimentSentences.map{
      case (a,b) => (a,  b.map{case (x,d) => ((getLemma(x) + " ").trim, d) })
    }
    var testFeaturesLemma = e.sentimentSentences.map{case(a,b) => b.map{case(f,s) => f}}.flatten.toSet// .mkString(" ").split(" ").toSet


    //TOTAL FOR ALL FEATURES
    println("Product: " + x._2 + " Category: " + x._1)
    println("Recall: " + df.intersect(testFeaturesLemma).size / testFeaturesLemma.size.asInstanceOf[Double])
    println("Percision: " + df.intersect(testFeaturesLemma).size / df.size.asInstanceOf[Double])

    var percision = 0.0
    var recall = 0.0
    var matches = 0
    //AVERAGE FOR SENTENCES
    e.sentimentSentences.foreach
    {
      case (a,b) =>
      {
        matches = 0
        b.foreach {
          case (c,d) => {
            val lemC = wrapper.getTokenLemma(wrapper.getTokens(wrapper.getSentences(c)(0))(0))
            if (df.contains(lemC)) matches += 1
          }
        }
        recall += matches/b.size.asInstanceOf[Double]
      }
    }

      recall /= e.sentimentSentences.size

      println("Average Sentence Recall: " + recall)
      println("Average Sentence Percision: " + percision)

    val overallSentiments = e.sentimentSentences.map{case (a,b) => b}.flatten.groupBy{case (f,s) => f}.map{case (k,v) => k -> v.fold(0){case (a : (Int),b : (String, Int)) =>  a + {if (b._2 > 0) 1 else if (b._2 == 0) 0 else -1} }}

    println(e.sentimentSentences.map{case (a,b) => b}.flatten.groupBy{case (f,s) => f}("weight").size)
    println(overallSentiments("weight"))
    println(overallSentiments)
      val sentTree = SentimentAnalysis.SentimentCalculator.calcSentiment(e.sentimentSentences, tree)
      println(sentTree)




      var accuracy = 0.0
      var matchedFeatures = 0

      def absDiff(x : Int, y : Int)  ={ math.abs(x-y)}
      sentTree.getBFSNodes().foreach(x =>
      {
        if (overallSentiments.contains(x.features(0)))
        {
          matchedFeatures +=1
          if (overallSentiments(x.features(0)).asInstanceOf[Int] * x.sentiment >= 0)
            accuracy +=1
        }
      })

    println("product level sentiment accuracy :" + accuracy/ matchedFeatures)



    }
    )


}
