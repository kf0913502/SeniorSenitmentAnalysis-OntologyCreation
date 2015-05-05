/**
 * Created by kkk on 3/16/2015.
 */
import play.api.libs.json.Json

import scala.collection.mutable.Queue

package object DataCollectionModel {

  case class Product(codes: Map[String, String], name: String, categoryName: String = "DEFAULT", parentCategoryName: String = "")

  case class Desc(codes: Map[String, String], sellerURL: String, descText: String)

  case class WebPosting(codes: Map[String, String], price: String, sellerURL: String, postingURL: String)

  case class ExpertReview(codes: Map[String, String], reviewURL: String, title: String, websiteName: String)

  case class CustomerReview(codes: Map[String, String], title: String, var text: String, websiteName: String)

  case class ProductImage(codes: Map[String, String], URL: String, sellerURL : String)

  case class WebSeller(URL : String, logo : String, name : String)

  case class WebOffer(codes : List[Map[String, String]], sellerURL : String, price : String, desc : String, startDate : String, endDate : String, viewCount : String)

  case class WebPriceReduction(codes : Map[String, String], sellerURL : String, newPrice : String, oldPrice : String)

  case class Question(question : String, answers : List[String], productCodes : Map[String, String])
  case class Related(C1 : Map[String, String], C2 : Map[String, String])

  case class OntologyNode(var children : List[OntologyNode], var features : List[String], var sentiment : Double)

  case class OntologyTree(var root : OntologyNode, category : String)
  {
    def getBFSNodes(): List[OntologyNode] =
    {
      var nodes : List[OntologyNode] = List()
      var toVisit : Queue[OntologyNode] = Queue()
      toVisit.enqueue(root)
      while(toVisit.length != 0)
      {
        var node = toVisit.dequeue
        node.children.foreach(toVisit.enqueue(_))
        nodes = nodes :+ node
      }
      nodes
    }

    def cutAtHeight(height : Int, node : OntologyNode = root): Unit =
    {
      if (node.children.isEmpty || height <1) return
      if (height == 1) node.children = List()
      else node.children.foreach(cutAtHeight(height -1, _))



    }
    def getTreeDepth(node : OntologyNode = root, acc: Int = 1) : Int =
    {
      if (node.children.isEmpty) return acc
      node.children.map(x => getTreeDepth(x, acc + 1)).max
    }

    def aggregateSentiment(node : OntologyNode = root, depth : Int = getTreeDepth()): Double =
    {
      if (node.children.isEmpty) return node.sentiment * depth
      node.sentiment = depth * node.sentiment + node.children.foldLeft(0.0){(a,b) => a + aggregateSentiment(b, depth-1)}
      node.sentiment
    }

  }
  
  implicit val ProductRead = Json.reads[Product]
  implicit val DescRead = Json.reads[Desc]
  implicit val WebPostingRead = Json.reads[WebPosting]
  implicit val ExpertReviewRead = Json.reads[ExpertReview]
  implicit val CustomerReviewRead = Json.reads[CustomerReview]
  implicit val ProductImageRead = Json.reads[ProductImage]
  implicit val WebSellerRead = Json.reads[WebSeller]
  implicit val OfferRead = Json.reads[WebOffer]
  implicit val relatedRead = Json.reads[Related]
  implicit val questionRead = Json.reads[Question]
  implicit val WebPriceReductionRead = Json.reads[WebPriceReduction]
  implicit val OntologyNodeRead = Json.reads[OntologyNode]
  implicit val OntologyTreeRead = Json.reads[OntologyTree]

  implicit val Productwrite = Json.writes[Product]
  implicit val Descwrite = Json.writes[Desc]
  implicit val WebPostingwrite = Json.writes[WebPosting]
  implicit val ExpertReviewwrite = Json.writes[ExpertReview]
  implicit val CustomerReviewwrite = Json.writes[CustomerReview]
  implicit val ProductImagewrite = Json.writes[ProductImage]
  implicit val WebSellerwrite = Json.writes[WebSeller]
  implicit val Offerwrite = Json.writes[WebOffer]
  implicit val relatedwrite = Json.writes[Related]
  implicit val questionwrite = Json.writes[Question]
  implicit val WebPriceReductionwrite = Json.writes[WebPriceReduction]
  implicit val OntologyNodewrite = Json.writes[OntologyNode]
  implicit val OntologyTreewrite = Json.writes[OntologyTree]
}