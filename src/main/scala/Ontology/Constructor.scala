package Ontology

/**
 * Created by karAdmin on 5/2/2015.
 */

import Model._
import SentimentAnalysis.nlpWrapper

import scala.collection.mutable.Queue

case class Constructor (category : String, testFile : String){


  var testFeatures = Set[String]()

  def getFrequentFeatures(): List[String] =
  {
    nlpWrapper("tokenize, ssplit, pos, lemma").getFrequentNouns(CorpusGatherer(category).getCorpus(), 0.01)

  }
  def createOntologyTree(): DataCollectionModel.OntologyTree =
  {
    var features = getFrequentFeatures()
    features = features.filter(_ != category)
    val root = DataCollectionModel.OntologyNode(List(), List(category), 0)
    val tree = DataCollectionModel.OntologyTree(root,category)

    val toVisit = Queue(root)
    var relationsMap = Map[(String, String), List[ConceptRelations]]()
    while(toVisit.length != 0) {
      val node = toVisit.dequeue
      relationsMap ++= ConceptsGatherer(node.features(0)).extractRelations().groupBy {
        case x if ConceptsGatherer.heirarchial.contains(x.relType) => (node.features(0), "heirarchial")
        case x if ConceptsGatherer.synonym.contains(x.relType) => (node.features(0), "synonym")
        case x if ConceptsGatherer.functionality.contains(x.relType) => (node.features(0), "functionality")
        case _ => (node.features(0), "other")
      }

      if (relationsMap.contains(node.features(0), "heirarchial")) {
        relationsMap((node.features(0), "heirarchial")).foreach(i => if (features.contains(i.target)) {
          features = features.filter(_ != i.target)
          val T = DataCollectionModel.OntologyNode(List(), List(i.target), 0)
          node.children = node.children :+ T
          toVisit.enqueue(T)

        })
      }
    }

      tree.getBFSNodes().foreach(n =>
        if (relationsMap.contains(n.features(0), "synonym"))
        {
          relationsMap((n.features(0),"synonym")).foreach(i => {
            if (features.contains(i.target)) {
              features = features.filter(_ != i.target)
              n.features :+= i.target}
          })

       })

    /*    val bfsNodes = tree.getBFSNodes()
    bfsNodes.foreach(n => if (!n.features.isEmpty && relationsMap.contains(n.features(0), "synonym")){
      relationsMap((n.features(0),"synonym")).foreach(i => {if (features.contains(i.target)) {
        features = features.filter(_ != i.target)
        n.features :+= i.target
      }
      else
      {
        val synNodes = bfsNodes.filter(_.features.contains(i.target))
        n.features ++= synNodes.foldLeft(List[String]())((a,b) => a ++ b.features)
        synNodes.foreach(_.features = List())
      }

      })

    })*/

    tree.getBFSNodes().foreach(n => if (relationsMap.contains(n.features(0), "functionality")){
      relationsMap((n.features(0),"functionality")).foreach(i => if (features.contains(i.target)) {
        features = features.filter(_ != i.target)
        val T = DataCollectionModel.OntologyNode(List(),List(i.target),0)
        n.children = n.children :+ T
      })


    })
    tree
  }

}
