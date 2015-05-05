package Ontology

/**
 * Created by karAdmin on 5/2/2015.
 */

import play.api.libs.json.Json
import scalaj.http.{HttpOptions, Http}
case class CorpusGatherer(category : String) {

  def getCorpus(): List[String] =
  {
    val responseBody = Http("http://127.0.0.1:9001/getAllProductsInCategory?name=" + category).option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000)).asString.body
    val parsedJson = Json.parse(responseBody)
    val modelJsonObject = parsedJson.validate[List[APPModel.Product]]

    modelJsonObject.get.map(a => a.customerReviews.map(b => b.text)).flatten
  }


}
