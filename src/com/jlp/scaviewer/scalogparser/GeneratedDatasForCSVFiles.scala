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
package com.jlp.scaviewer.scalogparser
import scala.collection.mutable.HashMap

case class GeneratedDatasForCSVFiles ( var valCols:ValuesColumns, pivCol:PivotColumn,namePiv:String,hmCumulEnr:HashMap[Long,MyCumulEnregistrement]) {

  def getValCols():ValuesColumns={valCols}
  def getPivCol():PivotColumn={pivCol}
  
}