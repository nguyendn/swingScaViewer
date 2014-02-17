/*Copyright 2012 Jean-Louis PASTUREL 
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/

class TraiterHotspot5And6 {
  def metInit(tab: Array[String] = null) {
    // To reinitialise static variable if necessary

    TraiterHotspot5And6.structHotSpot = null
    TraiterHotspot5And6.enrCurrent = null
    TraiterHotspot5And6.dateInMillis = 0
    TraiterHotspot5And6.dateInMillisFullGC = 0
    TraiterHotspot5And6.dateInMillisMinorGC = 0
    TraiterHotspot5And6.isStructFilled = false
    TraiterHotspot5And6.circleArray = new CircleArray(10)
    TraiterHotspot5And6.minorGCCircleArray = new CircleArray(10)
    TraiterHotspot5And6.majorGCCircleArray = new CircleArray(10)
    TraiterHotspot5And6.cmsSweepGCCircleArray = new CircleArray(10)
  }
  def retour(tabStr: Array[String]): Double =
    {

      if (TraiterHotspot5And6.enrCurrent == null) {
        TraiterHotspot5And6.enrCurrent = tabStr(0)
        TraiterHotspot5And6.isStructFilled = false
        TraiterHotspot5And6.dateInMillis = 0
        TraiterHotspot5And6.dateInMillisFullGC = 0
        TraiterHotspot5And6.dateInMillisMinorGC = 0
        traiterEnr(tabStr)
      } else if (!TraiterHotspot5And6.enrCurrent.equals(tabStr(0))) {
        TraiterHotspot5And6.isStructFilled = false
        traiterEnr(tabStr)

      }

      // Faire les retours ici
      tabStr(1) match {
        case "sizeYoungGenerationBefore" => TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore
        case "sizeYoungGenerationAfter" => TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter
        case "sizeOldGenerationBefore" => TraiterHotspot5And6.structHotSpot.sizeOldGenerationBefore
        case "sizeOldGenerationAfter" => TraiterHotspot5And6.structHotSpot.sizeOldGenerationAfter
        case "sizeHeapBefore" => TraiterHotspot5And6.structHotSpot.sizeHeapBefore
        case "sizeHeapAfter" => TraiterHotspot5And6.structHotSpot.sizeHeapAfter
        case "sizePermGenBefore" => TraiterHotspot5And6.structHotSpot.sizePermGenBefore
        case "sizePermGenAfter" => TraiterHotspot5And6.structHotSpot.sizePermGenAfter
        case "minorGCDuration" => TraiterHotspot5And6.structHotSpot.minorGCDuration
        case "throughput" => TraiterHotspot5And6.structHotSpot.throughput
        case "fullGCDuration" => TraiterHotspot5And6.structHotSpot.fullGCDuration
        case "cmsConcurrentMarkDuration" => TraiterHotspot5And6.structHotSpot.cmsConcurrentMarkDuration
        case "cmsConcurrentSweep" => TraiterHotspot5And6.structHotSpot.cmsConcurrentSweep
        case "cmsConcurrentPreclean" => TraiterHotspot5And6.structHotSpot.cmsConcurrentPreclean
        case "fullGCSizeYoungGenerationBefore" => TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationBefore
        case "fullGCSizeYoungGenerationAfter" => TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationAfter
        case "fullGCSizeOldGenerationBefore" => TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore
        case "fullGCSizeOldGenerationAfter" => TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter
        case "fullGCSizeHeapBefore" => TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore
        case "fullGCSizeHeapAfter" => TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter
        case "sysTimeSpent" => TraiterHotspot5And6.structHotSpot.sysTimeSpent
        case "userTimeSpent" => TraiterHotspot5And6.structHotSpot.userTimeSpent
        case "realTimeSpent" => TraiterHotspot5And6.structHotSpot.realTimeSpent
        case "memThroughput" => TraiterHotspot5And6.structHotSpot.memThroughput
        case "minorGCFrequency" => TraiterHotspot5And6.structHotSpot.minorGCFrequency
        case "majorGCFrequency" => TraiterHotspot5And6.structHotSpot.majorGCFrequency
        case "cmsSweepGCFrequency" => TraiterHotspot5And6.structHotSpot.cmsSweepGCFrequency
        case "minorGCPeriod" => TraiterHotspot5And6.structHotSpot.minorGCPeriod
        case "majorGCPeriod" => TraiterHotspot5And6.structHotSpot.majorGCPeriod
        case "cmsSweepGCPeriod" => TraiterHotspot5And6.structHotSpot.cmsSweepGCPeriod
        case _ => Double.NaN

      }

    }

  private def traiterEnr(tabStr: Array[String]) {
    // trouver la date:

    val regDate = """\d+(\.|,)\d+:""".r
    var match0 = regDate.findFirstIn(tabStr(0))
    if (None != match0) {
      // extraire la date
      var ext1 = match0.get
      ext1 = ext1.substring(0, ext1.indexOf(":")) // supprimer le dernier caractere
      //var dateCurrentInMillis = ext1.split("\\.")(0).toLong * 1000 + ext1.split("\\.")(1).toLong
      var dateCurrentInMillis = (ext1.replaceAll(",", ".").toDouble * 1000).toLong
      remplirStruct(dateCurrentInMillis, tabStr)

    }
  }

  private def remplirStruct(dateCurrent: Long, tabStr: Array[String]) {

    // println("enr="+tabStr(0))
    TraiterHotspot5And6.structHotSpot = new StructHotspot5And6
    // remplissage  sizeYoungGenerationBefore
    TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore = Double.NaN
    var reg1 = """\[GC.+?(DefNew:|ParNew:|PSYoungGen:)\s+\d+(K|M)""".r
    var ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }

    // remplissage  sizeYoungGenerationAfter
    TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter = Double.NaN
    reg1 = """\[GC.+?(DefNew:|ParNew:|PSYoungGen:)\s+\d+(K|M)->\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }

    // remplissage  sizeHeapBefore
    TraiterHotspot5And6.structHotSpot.sizeHeapBefore = Double.NaN
    reg1 = """\[(GC|Full\s+GC).+?(DefNew:|ParNew:|PSYoungGen:|Tenured:|PSOldGen:)[^\]]+\]\s+\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizeHeapBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.sizeHeapBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }
    // remplissage  sizeHeapAfter
    TraiterHotspot5And6.structHotSpot.sizeHeapAfter = Double.NaN
    reg1 = """\[(GC|Full\s+GC).+?(DefNew:|ParNew:|PSYoungGen|Tenured:|PSOldGen:)[^\]]+\]\s+\d+(K|M)->\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizeHeapAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.sizeHeapAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }
    // remplissage Throughput Memoire
    // println("Avant Traitement throughput memor"+TraiterHotspot5And6.structHotSpot.sizeHeapBefore+ " " +TraiterHotspot5And6.structHotSpot.sizeHeapAfter)
    TraiterHotspot5And6.structHotSpot.memThroughput = Double.NaN
    if (!(TraiterHotspot5And6.structHotSpot.sizeHeapBefore.isNaN()) && !(TraiterHotspot5And6.structHotSpot.sizeHeapAfter.isNaN())) {
      var sweeped = TraiterHotspot5And6.structHotSpot.sizeHeapBefore - TraiterHotspot5And6.structHotSpot.sizeHeapAfter
      // println("Traitement throughput memor")
      TraiterHotspot5And6.circleArray.put((dateCurrent, sweeped))
      TraiterHotspot5And6.structHotSpot.memThroughput = TraiterHotspot5And6.circleArray.throughput
    }
    //      else
    //      {
    //        println("Cannot compute throughput memory for : datetime :"+dateCurrent )
    //        println("TraiterHotspot5And6.structHotSpot.sizeHeapBefore="+TraiterHotspot5And6.structHotSpot.sizeHeapBefore)
    //         println("TraiterHotspot5And6.structHotSpot.sizeHeapAfter="+TraiterHotspot5And6.structHotSpot.sizeHeapAfter)
    //        
    //      }

    // remplissage   sizeOldGenerationBefore
    TraiterHotspot5And6.structHotSpot.sizeOldGenerationBefore = Double.NaN
    if (!TraiterHotspot5And6.structHotSpot.sizeHeapBefore.isNaN && !TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore.isNaN) {

      TraiterHotspot5And6.structHotSpot.sizeOldGenerationBefore = TraiterHotspot5And6.structHotSpot.sizeHeapBefore - TraiterHotspot5And6.structHotSpot.sizeYoungGenerationBefore
    }

    // remplissage   sizeOldGenerationAfter
    TraiterHotspot5And6.structHotSpot.sizeOldGenerationAfter = Double.NaN
    if (!TraiterHotspot5And6.structHotSpot.sizeHeapAfter.isNaN && !TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter.isNaN) {

      TraiterHotspot5And6.structHotSpot.sizeOldGenerationAfter = TraiterHotspot5And6.structHotSpot.sizeHeapAfter - TraiterHotspot5And6.structHotSpot.sizeYoungGenerationAfter
    }

    // remplissage   sizePermGenBefore
    TraiterHotspot5And6.structHotSpot.sizePermGenBefore = Double.NaN
    reg1 = """(CMS Perm :|PSPermGen:|Perm\s+:)\s+\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizePermGenBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.sizePermGenBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }
    // remplissage   sizePermGenAfter
    TraiterHotspot5And6.structHotSpot.sizePermGenAfter = Double.NaN
    reg1 = """(CMS Perm :|PSPermGen:|Perm\s+:)\s+\d+(K|M)->\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.sizePermGenAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.sizePermGenAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }

    // remplissage   minorGCDuration
    TraiterHotspot5And6.structHotSpot.minorGCFrequency = Double.NaN
    TraiterHotspot5And6.structHotSpot.minorGCPeriod = Double.NaN
    TraiterHotspot5And6.structHotSpot.minorGCDuration = Double.NaN
    
    reg1 = """\[GC.+?(\[Times|\]\s*$)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      //  println("ext1.get="+ext1.get)
      TraiterHotspot5And6.minorGCCircleArray.put((dateCurrent, 1))
      TraiterHotspot5And6.structHotSpot.minorGCFrequency = TraiterHotspot5And6.minorGCCircleArray.freq
       TraiterHotspot5And6.structHotSpot.minorGCPeriod = TraiterHotspot5And6.minorGCCircleArray.period
      var ext2 = """\d+(\.|,)\d+\s*secs\]\s*(\[Times$|$)""".r.findFirstIn(ext1.get)
      var ext3 = """\d+(\.|,)\d+""".r.findFirstIn(ext2.get)
      TraiterHotspot5And6.structHotSpot.minorGCDuration = ext3.get.replaceAll(",", ".").toDouble * 1000

    }
    // remplissage   fullGCDuration
    TraiterHotspot5And6.structHotSpot.fullGCDuration = Double.NaN
    TraiterHotspot5And6.structHotSpot.majorGCPeriod = Double.NaN
    TraiterHotspot5And6.structHotSpot.majorGCFrequency = Double.NaN
    
    reg1 = """Full\s+GC.+?(\[Times|\]\s*$)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      TraiterHotspot5And6.majorGCCircleArray.put((dateCurrent, 1))
      TraiterHotspot5And6.structHotSpot.majorGCFrequency = TraiterHotspot5And6.majorGCCircleArray.freq
      TraiterHotspot5And6.structHotSpot.majorGCPeriod = TraiterHotspot5And6.majorGCCircleArray.period
      var ext2 = """\d+(\.|,)\d+\s+secs\]\s*(\[Times$|$)""".r.findFirstIn(ext1.get)
      var ext3 = """\d+(\.|,)\d+""".r.findFirstIn(ext2.get)
      //  println("fullGC ext3.get="+ext3.get)
      // println("Double eext3.get.toDouble * 1000="+ext3.get.toDouble * 1000D)
      TraiterHotspot5And6.structHotSpot.fullGCDuration = ext3.get.replaceAll(",", ".").toDouble * 1000

    }

    // remplissage  throughput
    TraiterHotspot5And6.structHotSpot.throughput = Double.NaN
    if (!TraiterHotspot5And6.structHotSpot.minorGCDuration.isNaN) {
      // println("throughput minorGC minorDuration="+TraiterHotspot5And6.structHotSpot.minorGCDuration+" ; dateCurrent ="+dateCurrent+ " ; datePrev="+TraiterHotspot5And6.dateInMillis)
      TraiterHotspot5And6.structHotSpot.throughput = 100 * (1 - (TraiterHotspot5And6.structHotSpot.minorGCDuration / (dateCurrent - TraiterHotspot5And6.dateInMillisMinorGC).toDouble))
      if (tabStr(0).contains("[GC") || tabStr(0).contains("[Full GC")) TraiterHotspot5And6.dateInMillisMinorGC = dateCurrent
    }
    if (!TraiterHotspot5And6.structHotSpot.fullGCDuration.isNaN) {
      //  println("throughput majorGC fullGCDuration="+TraiterHotspot5And6.structHotSpot.fullGCDuration+" ; dateCurrent ="+dateCurrent+ " ; datePrev="+TraiterHotspot5And6.dateInMillis)

      TraiterHotspot5And6.structHotSpot.throughput = 100 * (1 - (TraiterHotspot5And6.structHotSpot.fullGCDuration / (dateCurrent - TraiterHotspot5And6.dateInMillisFullGC).toDouble))
      if (tabStr(0).contains("[GC") || tabStr(0).contains("[Full GC")) TraiterHotspot5And6.dateInMillisFullGC = dateCurrent
    }

    // remplissage    cmsConcurrentMarkDuration
    TraiterHotspot5And6.structHotSpot.cmsConcurrentMarkDuration = Double.NaN
    reg1 = """CMS-concurrent-mark:\s+[^\]]+""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(\.|,)\d+\s+secs""".r.findFirstIn(ext1.get)
    
      TraiterHotspot5And6.structHotSpot.cmsConcurrentMarkDuration = """\d+(\.|,)\d+""".r.findFirstIn(ext2.get).get.replaceAll(",", ".").toDouble * 1000

    }
    // remplissage    cmsConcurrentSweep
    TraiterHotspot5And6.structHotSpot.cmsConcurrentSweep = Double.NaN
    TraiterHotspot5And6.structHotSpot.cmsSweepGCFrequency= Double.NaN
    TraiterHotspot5And6.structHotSpot.cmsSweepGCPeriod= Double.NaN
    reg1 = """CMS-concurrent-sweep:\s+[^\]]+""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(\.|,)\d+\s+secs""".r.findFirstIn(ext1.get)
      TraiterHotspot5And6.cmsSweepGCCircleArray.put((dateCurrent, 1))
      TraiterHotspot5And6.structHotSpot.cmsSweepGCFrequency = TraiterHotspot5And6.cmsSweepGCCircleArray.freq
        TraiterHotspot5And6.structHotSpot.cmsSweepGCPeriod = TraiterHotspot5And6.cmsSweepGCCircleArray.period
      TraiterHotspot5And6.structHotSpot.cmsConcurrentSweep = """\d+(\.|,)\d+""".r.findFirstIn(ext2.get).get.replaceAll(",", ".").toDouble * 1000

    }
    // remplissage   cmsConcurrentPreclean
    TraiterHotspot5And6.structHotSpot.cmsConcurrentPreclean = Double.NaN
    reg1 = """CMS-concurrent-preclean:\s+[^\]]+""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(\.|,)\d+\s+secs""".r.findFirstIn(ext1.get)

      TraiterHotspot5And6.structHotSpot.cmsConcurrentPreclean = """\d+(\.|,)\d+""".r.findFirstIn(ext2.get).get.replaceAll(",", ".").toDouble * 1000

    }

    // remplissage  fullGCSizeOldGenerationBefore
    TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore = Double.NaN
    reg1 = """Full GC.+(PSOldGen:|CMS:|Tenured:)\s+\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }
    // remplissage  fullGCSizeOldGenerationAfter
    TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter = Double.NaN
    reg1 = """Full GC.+(PSOldGen:|CMS:|Tenured:)\s+\d+(K|M)->\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }
    // remplissage  fullGCSizeHeapBefore
    TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore = Double.NaN

    reg1 = """Full GC.+(PSOldGen:|CMS:|Tenured:)[^\]]+?\]\s+\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      //  System.out.println("remplissage fullGCSizeHeapBefore");
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
      //System.out.println("remplissage fullGCSizeHeapBefore="+TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore);
    }

    // remplissage  fullGCSizeHeapAfter
    TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter = Double.NaN
    reg1 = """Full GC.+(PSOldGen:|CMS:|Tenured:)[^\]]+?\]\s+\d+(K|M)->\d+(K|M)""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      var ext2 = """\d+(K|M)$""".r.findFirstIn(ext1.get)
      if (ext2.get.contains("K")) {
        TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024
      } else {
        TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter = """\d+""".r.findFirstIn(ext2.get).get.toDouble * 1024 * 1024
      }
    }
    // remplissage fullGCSizeYoungGenerationBefore
    TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationBefore = Double.NaN
    if (!TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore.isNaN && !TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore.isNaN) {

      TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationBefore = TraiterHotspot5And6.structHotSpot.fullGCSizeHeapBefore - TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationBefore
    }
    // remplissage fullGCSizeYoungGenerationAfter
    TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationAfter = Double.NaN
    if (!TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationAfter.isNaN && !TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter.isNaN) {

      TraiterHotspot5And6.structHotSpot.fullGCSizeYoungGenerationAfter = TraiterHotspot5And6.structHotSpot.fullGCSizeHeapAfter - TraiterHotspot5And6.structHotSpot.fullGCSizeOldGenerationAfter
    }

    // Test de presence des temps syst, user et real
    TraiterHotspot5And6.structHotSpot.sysTimeSpent = Double.NaN
    TraiterHotspot5And6.structHotSpot.userTimeSpent = Double.NaN
    TraiterHotspot5And6.structHotSpot.realTimeSpent = Double.NaN
    reg1 = """\[Times[^\]]+""".r
    ext1 = reg1.findFirstIn(tabStr(0))
    if (None != ext1) {
      // remplissage  sysTimeSpent
      // [Times: user=0.13 sys=0.00, real=0.03 secs] 
      var reg2 = """sys=\d+(\.|,)\d+""".r
      var ext2 = reg2.findFirstIn(ext1.get)
      TraiterHotspot5And6.structHotSpot.sysTimeSpent = """\d+(\.|,)\d+""".r.findFirstIn(ext2.get).get.replaceAll(",", ".").toDouble * 1000
      // remplissage  userTimeSpent
      reg2 = """user=\d+(\.|,)\d+""".r
      ext2 = reg2.findFirstIn(ext1.get)
      TraiterHotspot5And6.structHotSpot.userTimeSpent = """\d+(\.|,)\d+""".r.findFirstIn(ext2.get).get.replaceAll(",", ".").toDouble * 1000
      // remplissage  realTimeSpent
      reg2 = """real=\d+\.\d+""".r
      ext2 = reg2.findFirstIn(ext1.get)
      TraiterHotspot5And6.structHotSpot.realTimeSpent = """\d+(\.|,)\d+""".r.findFirstIn(ext2.get).get.replaceAll(",", ".").toDouble * 1000
    }

    // On ne mesure les dates  qu entre les GC
    if (tabStr(0).contains("[GC") || tabStr(0).contains("[Full GC")) TraiterHotspot5And6.dateInMillis = dateCurrent
    TraiterHotspot5And6.enrCurrent = tabStr(0)
    TraiterHotspot5And6.isStructFilled = true
  }

}
object TraiterHotspot5And6 {

  var structHotSpot: StructHotspot5And6 = null
  var circleArray: CircleArray = null
  var minorGCCircleArray: CircleArray = null
  var majorGCCircleArray: CircleArray = null
  var cmsSweepGCCircleArray: CircleArray = null
  var enrCurrent: String = null
  var isStructFilled = false
  var dateInMillis: Long = 0
  var dateInMillisFullGC = 0L
  var dateInMillisMinorGC = 0L
}

class StructHotspot5And6 {
  var sizeYoungGenerationBefore = Double.NaN
  var sizeYoungGenerationAfter = Double.NaN
  var sizeOldGenerationBefore = Double.NaN
  var sizeOldGenerationAfter = Double.NaN
  var sizeHeapBefore = Double.NaN
  var sizeHeapAfter = Double.NaN
  var sizePermGenBefore = Double.NaN
  var sizePermGenAfter = Double.NaN
  var minorGCDuration = Double.NaN
  var throughput = Double.NaN
  var fullGCDuration = Double.NaN

  var cmsConcurrentMarkDuration = Double.NaN
  var cmsConcurrentSweep = Double.NaN
  var cmsConcurrentPreclean = Double.NaN
  var fullGCSizeYoungGenerationBefore = Double.NaN
  var fullGCSizeYoungGenerationAfter = Double.NaN
  var fullGCSizeOldGenerationBefore = Double.NaN
  var fullGCSizeOldGenerationAfter = Double.NaN
  var fullGCSizeHeapBefore = Double.NaN
  var fullGCSizeHeapAfter = Double.NaN
  var sysTimeSpent = Double.NaN
  var userTimeSpent = Double.NaN
  var realTimeSpent = Double.NaN
  var memThroughput = Double.NaN
  var minorGCFrequency = Double.NaN
  var majorGCFrequency = Double.NaN
  var cmsSweepGCFrequency = Double.NaN
   var minorGCPeriod = Double.NaN
  var majorGCPeriod = Double.NaN
  var cmsSweepGCPeriod = Double.NaN
}

