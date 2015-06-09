/**
 * Created by karAdmin on 5/31/2015.
 */

import Ontology._
import play.api.libs.json.Json

import scala.pickling.Defaults._
import scala.pickling.binary._
import scalaj.http.{HttpOptions, Http}




object ontologyCreation /*extends App*/{


  println(Http("http://localhost:9001/insertOntologyTree").
    postData(Json.toJson(Constructor("laptop", "").createOntologyTree()).toString()).
    headers(Seq("content-Type" -> "text/plain")).asString)
}
