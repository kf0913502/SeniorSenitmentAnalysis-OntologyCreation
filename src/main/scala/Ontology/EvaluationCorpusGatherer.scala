package Ontology

/**
 * Created by karAdmin on 5/18/2015.
 */
case class EvaluationCorpusGatherer(file : String) {

  var features = Set[String]()
  var reviews = Array[String]()
  var sentimentSentences = List[(String, List[(String, Int)])]()
  def getCorpus(): List[String] = {
    var result = List[String]()


    parseFiles(file)
  }

  def parseFiles(file : String): List[String] =
  {
    var source = io.Source.fromFile(file)
    try
    {
      val str = source.mkString
      reviews =  str.split("\\[t\\]").map(x => x.replaceAll("\n.*##", ""))
      features = str.replaceAll("\\[t\\][^\n]*\n", "").replaceAll("((##[^\n]*\n)|,|(\\[u\\])|(\\[p\\])|(\\[s\\])|(\\[cc\\])|(\\[cs\\]))", "").split("\\[[-+][0-9]\\]").map(_.trim).toSet
      val R = "(.*)##(.*)\r".r
      val R2 = "(.*)\\[[+]?(.*)\\]".r
      val sentences  = str.replaceAll("\\[t\\][^\n]*\n", "").replaceAll("((\\[u\\])|(\\[p\\])|(\\[s\\])|(\\[cc\\])|(\\[cs\\]))", "").split("\n")
      val splitSentences = sentences.map(u => {u match{
        case R(a, b) => (b, a)
        case _ => ("","")
      }})

      sentimentSentences = splitSentences.map{case (b,a) => (b,a.split(",").map(v => v match{
        case R2(f, s) => (f.asInstanceOf[String], s.toInt)
        case _ => ("",0)
      }).toList)}.toList.filter{case (a,b) => !b.contains(("", 0))}
      reviews.toList
    }
    finally source.close()


  }

}
