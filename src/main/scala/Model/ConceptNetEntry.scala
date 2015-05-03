package Model

/**
 * Created by karAdmin on 5/2/2015.
 */
import play.api.libs.json.Json

package object ConceptNetModel
{


  case class ConceptNetEntry(edges : List[Relation], numFound : Int)

  case class Relation(context : String, dataset : String, end : String, features : List[String], id : String, license : String, rel : String, source_uri : String,
                      sources : List[String], start : String, surfaceText : String, uri : String, weight : Double)

  implicit val RelationRead = Json.reads[Relation]
  implicit val ConceptNetEntryRead = Json.reads[ConceptNetEntry]

  implicit val RelationWrites = Json.writes[Relation]
  implicit val ConceptNetEntryWrites = Json.writes[ConceptNetEntry]

}