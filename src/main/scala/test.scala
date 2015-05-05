/**
 * Created by karAdmin on 5/2/2015.
 */


import Ontology._
import NLP._
object test extends App{


  var x = nlpWrapper("tokenize, ssplit, pos")

  var y = x.getSentences("asdasd I go to school by long and good battery life. kariem was good. school was great.")

  //println(x.getTokenPOS(x.getTokens(y.get(0)).get(2)))
  //x.getTokens(y(0)).foreach(k => {println(k + x.getTokenPOS(k))})
 // println(x.groupConsecuetiveNouns(y(0)))
  //println(x.getTokensWithTag(y(0),"NP"))
  val review = "So I made the switch from using Android to the iPhone back in October, and I've been using the iPhone 6 for the past few months now and can give a detailed review on what it's like to switch over. Before this switch, I've used the Samsung Galaxy S2 (first smartphone ever!) and also the Nexus 4. Since I'm a tech enthusiast, I'm well versed and have played around with many other Android devices, including all the big names, Galaxy S5, HTC One M8 and M7, One Plus One, and so on. Here are my thoughts:\n\nThings that the iPhone does really well (both hardware and software-wise):\n1. Camera. The behind the scene software for digitally capturing an image is definitely the strongest sell for the iPhone. Other than the S5 and Note 4, no smartphone really comes close to having the same kind of image quality (no matter the megapixels) compared to the iPhone. This was one of the reasons for me to switch over since I've started to dabble with photography and wanted a really good camera in my smartphone. (Side note, if you read a lot of tech blogs, there is a notion that in the near future our smartphones won't accurately describe our devices anymore since making a phone call is probably one of the least commonly used features on a smartphone when you look at any average user. Cameras, social media, emails all take a higher usage rate than making a call... really interesting, but anyway, back to the review)"
  println(x.getFrequentNouns(List(review), 0.5))



}
