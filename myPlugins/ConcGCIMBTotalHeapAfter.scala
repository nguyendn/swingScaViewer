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
class ConcGCIMBTotalHeapAfter {
  def metInit(tab:Array[String]=null) {
    // To reinitialise static variable if necessary
    // Nothing to do
  }
  def retour(tabStr: Array[String]): Double =
    {

      var retour = Double.NaN
      // recuperation premiere valeur <nursery freebytes="0" totalbytes="
      val patNursery = "</gc>\\s+<nursery freebytes=\"\\d+\" totalbytes=\"\\d+".r
      val patTenured = "</gc>\\s*.+?\\s*<tenured freebytes=\"\\d+\" totalbytes=\"\\d+".r

      val matchNursery = patNursery.findFirstIn(tabStr(0))
      var val1 = ""
      var val2 = ""
      var db1 = Double.NaN
      var db2 = Double.NaN
      if (None != matchNursery) {
        val tmpVal1 = matchNursery.get

        val match2 = "\\d+$".r.findFirstIn(matchNursery.get)

        if (None != match2) {
          val1 = match2.get

        }
        val pat = "<nursery freebytes=\"\\d+".r
        val match3 = pat.findFirstIn(tmpVal1)
        if (None != match3) {
          val val3 = match3.get
          var match4 = "\\d+$".r.findFirstIn(val3);
          if (None != match4) {
            var val4 = match4.get
            db1 = val1.toDouble - val4.toDouble
          }

        }

      }
      val matchTenured = patTenured.findFirstIn(tabStr(0))
      if (None != matchTenured) {
        val2 = matchTenured.get
        var tmpVal2 = val2
        val match2 = "\\d+$".r.findFirstIn(val2)
        if (None != match2) {
          val2 = match2.get
        }

        val pat = "tenured freebytes=\"\\d+".r
        val match3 = pat.findFirstIn(tmpVal2)
        if (None != match3) {
          val val3 = match3.get
          val match4 = "\\d+$".r.findFirstIn(val3)
          if (None != match4) {
            val val4 = match4.get
            db2 = val2.toDouble - val4.toDouble
          }

        }

      }

      db1 + db2;

    }
}