/**
 * Created by karAdmin on 5/2/2015.
 */


import Ontology._
import NLP._
object test extends App{


  var x = nlpWrapper("tokenize, ssplit, pos")

  var y = x.getSentences("asdasd I go to school by battery life.")

  //println(x.getTokenPOS(x.getTokens(y.get(0)).get(2)))
  //x.getTokens(y(0)).foreach(k => {println(k + x.getTokenPOS(k))})
  println(x.getTokensWithTag(y(0),"NP"))



}
