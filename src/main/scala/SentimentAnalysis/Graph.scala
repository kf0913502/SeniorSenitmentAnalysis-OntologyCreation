package SentimentAnalysis

/**
 * Created by abdelrazektarek on 5/2/15.
 */
abstract class Graph {
  type Edge <: IEdge
  type Node <: INode
  abstract class INode {
    def connectWith(node: Node): Edge
  }
  abstract class IEdge {
    def a: Node
    def b: Node
    def opposite(n: Node): Option[Node]
  }
  def nodes: List[Node]
  def edges: List[Edge]
  def addNode: Node
}

abstract class UndirectedGraph extends Graph {
  class EdgeImpl(one: Node, other: Node) extends IEdge {
    def a = one
    def b = other
    def opposite(n: Node): Option[Node] =
      if(n == a) Some(b)
      else if(n == b) Some(a)
      else None
  }

  class NodeImpl extends INode {
    this: Node =>
    def connectWith(node: Node): Edge = {
      val edge = newEdge(this, node)
      edges = edge :: edges;
      edge
    }
    override def toString:String =
      (nodes.length - nodes.indexOf(this == _)).toString()
  }

  protected def newNode: Node
  protected def newEdge(one: Node, other: Node): Edge

  var nodes: List[Node] = Nil
  var edges: List[Edge] = Nil

  def addNode: Node = {
    val node = newNode
    nodes = node :: nodes
    node
  }
}

class ConcreteUndirectedGraph extends UndirectedGraph {
  type Node = NodeImpl
  type Edge = EdgeImpl
  protected def newNode: Node = new Node
  protected def newEdge(one: Node, other: Node): Edge =
    new Edge(one, other)
}


