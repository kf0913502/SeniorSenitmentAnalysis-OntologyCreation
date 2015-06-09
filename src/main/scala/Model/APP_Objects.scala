
import play.api.libs.json.Json
/**
 * Created by kkk on 3/14/2015.
 */

/* These are the model entities and they are used for communicating with the Web Service*/

package object APPModel {


  case class Product(info: ProductInfo,  descriptions: List[String],
                     webPosting: List[WebPosting], localPosting: List[LocalPosting], customerReviews: List[CustomerReview], expertReviews: List[ExpertReview],
                     offers : List[WebOffer], related : List[ProductInfo], questions : List[Question], priceReductions : List[PriceReduction])

  case class ProductInfo(codes: Map[String, String], name: String, category: Category, dateAdded: String = "", images: List[String])

  case class WebPosting(price: Double, seller: WebBasedSeller, URL: String)

  case class LocalPosting(price: Double, location: Location)

  case class Location(longitutde: Double, latitude: Double)

  case class ExpertReview(URL: String, title: String, webSiteName: String, id: String = "")

  case class CustomerReview(title: String, var text: String, dateAdded: String, sourceWebsite: String, id: String = "")


  case class WebBasedSeller(logo: String, URL: String, id: String = "")

  case class UserAccount(username: String, password: String, firstName: String, lastName: String, email: String, active: String)

  case class Category(id: String, parentId: String, name: String)

  case class WebOffer(products : List[ProductInfo], desc : String,  price : String, seller : WebBasedSeller, startDate : String, endDate : String)

  case class PriceReduction(seller : WebBasedSeller, product : ProductInfo , newPrice : String, oldPrice : String)

  case class Question(question : String, answers : List[String])


  /*These are declarations needed to allow the Play Json library to automatically serialize case classes without manually defining a json parser*/
  implicit val categoryFormat = Json.writes[Category]
  implicit val productInfoFormat = Json.writes[ProductInfo]
  implicit val webSellerFormat = Json.writes[WebBasedSeller]
  implicit val customerReviewFormat = Json.writes[CustomerReview]
  implicit val expertReviewFormat = Json.writes[ExpertReview]
  implicit val locationFormat = Json.writes[Location]
  implicit val localPostingFormat = Json.writes[LocalPosting]
  implicit val webPostingFormat = Json.writes[WebPosting]
  implicit val webOfferFormat = Json.writes[WebOffer]
  implicit val questionFormat = Json.writes[Question]
  implicit val priceReductionFormat = Json.writes[PriceReduction]
  implicit val productFormat = Json.writes[Product]

  implicit val RcategoryFormat = Json.reads[Category]
  implicit val RproductInfoFormat = Json.reads[ProductInfo]
  implicit val RwebSellerFormat = Json.reads[WebBasedSeller]
  implicit val RcustomerReviewFormat = Json.reads[CustomerReview]
  implicit val RexpertReviewFormat = Json.reads[ExpertReview]
  implicit val RlocationFormat = Json.reads[Location]
  implicit val RlocalPostingFormat = Json.reads[LocalPosting]
  implicit val RwebPostingFormat = Json.reads[WebPosting]
  implicit val RwebOfferFormat = Json.reads[WebOffer]
  implicit val RquestionFormat = Json.reads[Question]
  implicit val RpriceReductionFormat = Json.reads[PriceReduction]
  implicit val RproductFormat = Json.reads[Product]


}