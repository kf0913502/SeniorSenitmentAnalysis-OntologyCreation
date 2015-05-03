package Ontology

/**
 * Created by karAdmin on 5/2/2015.
 */

import Model._
import play.api.libs.json.Json

import scalaj.http.{HttpOptions, Http}
object ConceptsGatherer
{
  val heirarchial = List("IsA", "PartOf", "HasA", "AtLocation", "Desires", "ObstructedBy")
  val synonym = List("Synonym", "DerivedFrom", "RelatedTo")
  val functionality = List("UsedFor", "HasProperty", "CapableOf", "DefinedAs", "Causes")
}
case class ConceptsGatherer(category : String) {

  var relations : List[ConceptRelations] = null
  def extractRelations() : List[ConceptRelations] =
  {
    val responseBody = Http("http://127.0.0.1:8084/data/5.3/c/en/" + category + "?limit=10000").option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).asString.body.replaceAll("null", "\"null\"")
    val parsedJson = Json.parse(responseBody)
    val modelJsonObject = parsedJson.validate[ConceptNetModel.ConceptNetEntry]



    relations = modelJsonObject.get.edges.map(i => {
      val target = if(i.end.split("/")(3) == category) i.start.split("/")(3)  else i.end.split("/")(3)
      ConceptRelations(target,i.rel.split("/")(2),i.weight)
    })

    relations

  }


}