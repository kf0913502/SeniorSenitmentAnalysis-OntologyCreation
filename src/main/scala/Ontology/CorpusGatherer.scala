package Ontology

/**
 * Created by karAdmin on 5/2/2015.
 */

import play.api.libs.json.Json
import scalaj.http.{HttpOptions, Http}
case class CorpusGatherer(category : String) {

  def getCorpus(): List[String] =
  {
    val responseBody = Http("http://127.0.0.1:9001/getAllProductsReviewsInCategory?category=" + category).option(HttpOptions.connTimeout(100000)).option(HttpOptions.readTimeout(500000)).asString.body.replaceAll(":null", ":\"null\"")
    val parsedJson = Json.parse(responseBody)
    val modelJsonObject = parsedJson.validate[List[APPModel.CustomerReview]]

    modelJsonObject.get.map(a => a.text).take(1000)
  }


}
