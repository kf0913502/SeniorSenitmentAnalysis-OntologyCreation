package Ontology

/**
 * Created by karAdmin on 5/2/2015.
 */

import Model._
import NLP.nlpWrapper

import scala.collection.mutable.Queue

case class Constructor (category : String){



  def getFrequentFeatures(): List[String] =
  {
    nlpWrapper("tokenize, ssplit, pos, lemma").getFrequentNouns(CorpusGatherer(category).getCorpus(), 0.4)

  }
  def createOntologyTree(): DataCollectionModel.OntologyTree =
  {
    var features = getFrequentFeatures()
    var root = DataCollectionModel.OntologyNode(List(),List(category),0)
    val tree = DataCollectionModel.OntologyTree(root,category)

    var toVisit = Queue(root)
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
    tree.getBFSNodes().foreach(n => if (relationsMap.contains(n.features(0), "synonym")){
      relationsMap((n.features(0),"synonym")).foreach(i => if (features.contains(i.target)) {
        features = features.filter(_ != i.target)
        n.features :+= i.target
      })
    })


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
