/**
 * Javascript file for Sugar
 *
 * The contents of this file are subject to the SugarCRM Public License Version
 * 1.1.3 ("License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.sugarcrm.com/SPL
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * All copies of the Covered Code must include on each user interface screen:
 *    (i) the "Powered by SugarCRM" logo and
 *    (ii) the SugarCRM copyright notice
 * in the same form as they appear in the distribution.  See full license for
 * requirements.
 *
 * The Original Code is: SugarCRM Open Source
 * The Initial Developer of the Original Code is SugarCRM, Inc.
 * Portions created by SugarCRM are Copyright (C) 2004-2006 SugarCRM, Inc.;
 * All Rights Reserved.
 * Contributor(s): ______________________________________.
 */

/**
 * @class
 * Default UI code used internally by the Grid. Documentation to come.
 * @constructor
 */
YAHOO.ext.grid.ChildGridView = function(){
    YAHOO.ext.grid.ChildGridView.superclass.constructor.call(this);
};
YAHOO.extendX(YAHOO.ext.grid.ChildGridView, YAHOO.ext.grid.GridView);

YAHOO.ext.grid.ChildGridView.prototype.insertRows = function(dataModel, firstRow, lastRow, offset){
       // this.updateBodyHeight();
       // this.adjustForScroll(true);
        var renderers = this.getColumnRenderers();
        var dindexes = this.getDataIndexes();
        var colCount = this.grid.colModel.getColumnCount();
        var beforeRow = null;
        var bt = this.getBodyTable();
       
        if(!offset){
        	offset = 0;
        }
        if(firstRow+offset < bt.childNodes.length){
            beforeRow = bt.childNodes[firstRow];
        }

        for(var rowIndex = firstRow; rowIndex <= lastRow; rowIndex++){
            var row = document.createElement('span');
            row.className = 'ygrid-row';
            row.style.top = (rowIndex * this.getRowHeight()) + 'px';

            this.renderRow(dataModel, row, rowIndex+offset, colCount, renderers, dindexes);
            
            if(beforeRow){         	
            	bt.insertBefore(row, beforeRow);
            }else{
                bt.appendChild(row);
            }  
        }
        this.updateRowIndexes(firstRow);
        this.adjustForScroll();
};

YAHOO.ext.grid.ChildGridView.prototype.toggleClick = function(e){
    	var selectedRows = this.grid.selModel.selectedRows;
    	if(selectedRows.length >= 1){
    		var rowIndex = selectedRows[0].rowIndex;
    		
    		var html = selectedRows[0].innerHTML;
    		
    		var pattern = 'togglespan_(\\d)';
    		var re = new RegExp(pattern);
    		var match = re.exec(html);
    		if(match){
	    		var rowId = match[1];
	    		var toggleSpan = document.getElementById('togglespan_'+rowId);
	    		if(toggleSpan.className == 'collapsed'){
	    			toggleSpan.innerHTML = "<img src='themes/Sugar/images/basic_search.gif' id='toggleImg_"+rowIndex+"'>";
	    			toggleSpan.className = 'expanded';    		
	    			this.insertRows(this.grid.dataModel, rowIndex+1, rowIndex+this.grid.dataModel.getRowById(rowId).children.length,  this.grid.dataModel.getRowIndexById(rowId) - rowIndex);
	    		}else{
	    			toggleSpan.innerHTML = "<img src='themes/Sugar/images/advanced_search.gif' id='toggleImg_"+rowIndex+"'>";
	    			toggleSpan.className = 'collapsed';    		
	    			this.deleteRows(this.grid.dataModel, rowIndex+1, rowIndex+this.grid.dataModel.getRowById(rowId).children.length);
	    		}
	    	}//fi
    		
    		//alert(this.grid.dataModel.data[rowIndex].children.length);		
    	}//fi
};
YAHOO.ext.grid.ChildGridView.prototype.renderRow = function(dataModel, row, rowIndex, colCount, renderers, dindexes){
   
		//here let's create the toggle image
		var toggletd = document.createElement('span');
		var togglespan = document.createElement('span');
		if(dataModel.data[rowIndex].isParent){
			togglespan.id = 'togglespan_'+dataModel.data[rowIndex].id;
			toggletd.appendChild(togglespan);
			togglespan.innerHTML = "<img src='themes/Sugar/images/basic_search.gif' id='toggleImg_"+dataModel.data[rowIndex].id+"'>";	
			togglespan.className = 'expanded';	
			row.appendChild(toggletd);
			YAHOO.ext.EventManager.on('togglespan_'+dataModel.data[rowIndex].id, 'click', this.toggleClick, this, true);
		}else{
			toggletd.appendChild(togglespan);
			//togglespan.innerHTML = "<img src='include/javascript/yui/ext/assets/images/grid/ln.gif'>";
			row.appendChild(toggletd);
		}
        for(var colIndex = 0; colIndex < colCount; colIndex++){
            var td = document.createElement('span');
            td.className = 'ygrid-col ygrid-col-' + colIndex + (colIndex == colCount-1 ? ' ygrid-col-last' : '');
            td.columnIndex = colIndex;
            td.tabIndex = 0;
            var span = document.createElement('span');
            span.className = 'ygrid-cell-text';
            td.appendChild(span);
           
            var val = renderers[colIndex](dataModel.getValueAt(rowIndex, dindexes[colIndex]), rowIndex, colIndex);           
            if(val == '') val = '&nbsp;';
            span.innerHTML = val;
            row.appendChild(td);
        }
        YAHOO.ext.EventManager.on(row, 'dblclick', this.showDialog, this, true);
};

YAHOO.ext.grid.ChildGridView.prototype.showDialog = function(e){
            	dialog = new YAHOO.widget.SimpleDialog("dialog", 
														{width: "300px",
															fixedcenter: true,
															visible: false,
															draggable: false,
															close: true,
															text: "Display Some documentation here",
															icon: YAHOO.widget.SimpleDialog.ICON_HELP,
															constraintoviewport: true
														} );
				dialog.setHeader("Documentation");
					
				// Render the Dialog
				dialog.render('documentation-div');
				dialog.show();
};
//do not need to use this function
YAHOO.ext.grid.ChildGridView.prototype.renderChildRows = function(data, rowIndex, bt){
    	var childSpan = document.createElement('span');
    	childSpan.id = 'childSpan_'+rowIndex;
    	childSpan.style.display = 'block';
    	for(var i = 0; i < data.children.length; i++){
    		var childRow = document.createElement('span');
            childRow.className = 'ygrid-row';
            childRow.style.top = ((i * this.rowHeight)) + 'px';
            //var togglespan = document.createElement('span');
			//childRow.appendChild(togglespan);
    		//TODO have to determine the length to iterate through
    		for(var j = 0; j < 2; j++){
    		    var td = document.createElement('span');
    		    td.className = 'ygrid-col ygrid-col-' + j + (j == 2-1 ? ' ygrid-col-last' : '');
            	td.columnIndex = j;
            	td.tabIndex = 0;
    		    var span = document.createElement('span');
            	span.className = 'ygrid-cell-text';
            	td.appendChild(span);
            	var val = data.children[i][j];
            	if(val == '') val = '&nbsp;';
            	span.innerHTML = val;
            	childRow.appendChild(td);           	
    		}
    		//alert(bt.innerHTML);
    		bt.appendChild(childRow);
    		//alert(bt.innerHTML);
    	}//rof
    	//bt.appendChild(childSpan);
    	//alert(bt.innerHTML);
    	//alert(releaseSpan.innerHTML);
    	//rowSpan.appendChild(releaseSpan);
    	//alert(rowSpan.innerHTML);	
};
YAHOO.ext.grid.ChildGridView.prototype.deleteRows = function(dataModel, firstRow, lastRow){
        this.updateBodyHeight();
     
        // first make sure they are deselected
        this.grid.selModel.deselectRange(firstRow, lastRow);
        var bt = this.getBodyTable();
        var rows = []; // get references because the rowIndex will change
        for(var rowIndex = firstRow; rowIndex <= lastRow; rowIndex++){
            rows.push(bt.childNodes[rowIndex]);
        }
        for(var i = 0; i < rows.length; i++){
			if(rows[i])
            	bt.removeChild(rows[i]);
            rows[i] = null;
        }
        rows = null;
        this.updateRowIndexes(firstRow);
        this.adjustForScroll();
};
