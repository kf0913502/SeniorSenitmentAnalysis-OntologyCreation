package SentimentAnalysis

/**
 * Created by karAdmin on 5/24/2015.
 */
case class WeightedGraph(defaultWeight: Int) extends UndirectedGraph {
  type Node = NodeImpl
  type Edge = EdgeImpl with Weight

  trait Weight {
    var weight = defaultWeight

    def getWeight = weight

    def setWeight(weight: Int): Unit = {
      this.weight = weight
    }
  }

  override protected def newNode: Node = new NodeImpl
  override protected def newEdge(one: Node, other: Node): Edge with Weight =
    new EdgeImpl(one, other) with Weight
}