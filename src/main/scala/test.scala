/**
 * Created by karAdmin on 5/2/2015.
 */


import Ontology._
import NLP._
object test extends App{


  var x = nlpWrapper("tokenize, ssplit, pos, lemma")

  var y = x.getSentences("asdasd I go to school by long and good battery life. kariem was good. school was great.")

  //println(x.getTokenPOS(x.getTokens(y.get(0)).get(2)))
  //x.getTokens(y(0)).foreach(k => {println(k + x.getTokenPOS(k))})
 // println(x.groupConsecuetiveNouns(y(0)))
  //println(x.getTokensWithTag(y(0),"NP"))
  val review = "I have so many phones and cameras"
  println(x.getFrequentNouns(List(review), 0.1))



}
