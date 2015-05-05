/**
 * Created by karAdmin on 5/2/2015.
 */


import Ontology._
import play.api.libs.json.Json
import scalaj.http.{HttpOptions, Http}

import NLP._
object test extends App{

  println(Http("http://localhost:9001/insertOntologyTree").
    postData(Json.toJson(Constructor("phone").createOntologyTree()).toString()).
    headers(Seq("content-Type" -> "text/plain")).asString)


}
