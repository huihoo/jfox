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
 * This is an implementation of a DataModel used by the Grid. It works 
 * with XML data. 
 * <br>Example schema from Amazon search:
 * <pre><code>
 * var schema = {
 *     tagName: 'Item',
 *     id: 'ASIN',
 *     fields: ['Author', 'Title', 'Manufacturer', 'ProductGroup']
 * };
 
 var childSchema = {
 *     tagName: 'Item',
 *     id: 'ASIN',
 *     fields: ['Author', 'Title', 'Manufacturer', 'ProductGroup']
 * };
 * </code></pre>
 * @extends YAHOO.ext.grid.LoadableDataModel
 * @constructor
 * @param {Object} schema The schema to use
 * @param {Object} childSchema The child schema to use
 * @param {XMLDocument} xml An XML document to load immediately
*/
YAHOO.ext.grid.XMLChildDataModel = function(schema, childSchema, xml){
    YAHOO.ext.grid.XMLDataModel.superclass.constructor.call(this, YAHOO.ext.grid.LoadableDataModel.XML);
    /**@private*/
   
    this.schema = schema;
    this.childSchema = childSchema;
    this.xml = xml;
    if(xml){
        this.loadData(xml);
    }
};
YAHOO.extendX(YAHOO.ext.grid.XMLChildDataModel, YAHOO.ext.grid.LoadableDataModel);

/**
 * Overrides loadData in LoadableDataModel to process XML
 * @param {XMLDocument} doc The document to load
 * @param {<i>Function</i>} callback (optional) callback to call when loading is complete
 * @param {<i>Boolean</i>} keepExisting (optional) true to keep existing data
 * @param {<i>Number</i>} insertIndex (optional) if present, loaded data is inserted at the specified index instead of overwriting existing data
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.loadData = function(doc, callback, keepExisting, insertIndex){
	this.xml = doc;
	var idField = this.schema.id;
	var fields = this.schema.fields;
	//child fields
	var childIdField = this.childSchema.id;
	var childFields = this.childSchema.fields;
	if(this.schema.totalTag){
	    this.totalCount = null;
	    var totalNode = doc.getElementsByTagName(this.schema.totalTag);
	    if(totalNode && totalNode.item(0) && totalNode.item(0).firstChild) {
            var v = parseInt(totalNode.item(0).firstChild.nodeValue, 10);
            if(!isNaN(v)){
                this.totalCount = v;
            }
    	}
	}
	var rowData = [];
	var nodes = doc.getElementsByTagName(this.schema.tagName);
    if(nodes && nodes.length > 0) {
    	var idCount = 0;
	    for(var i = 0; i < nodes.length; i++) {
	    	//get the ith parent node
	        var node = nodes.item(i);
	        var colData = [];
	        //here we will add the concept of child nodes
	        colData.node = node;
	        colData.id = String(idCount);//this.getNamedValue(node, idField, String(idCount));
	       
	        colData.isParent = true;
	        colData.parentId = colData.id;
	        for(var j = 0; j < fields.length; j++) {
	            var val = this.getNamedValue(node, fields[j], "");
	            if(this.preprocessors[j]){
	                val = this.preprocessors[j](val);
	            }//fi
	            colData.push(val);
	        }//rof
	        idCount++;
	        //now that we have the parent data let's try to get the 
	        //child data
	        //rowData.push(colData); 
	        var childNodes = node.getElementsByTagName(this.childSchema.tagName);
	       
	       var children = [];
	        if(childNodes && childNodes.length > 0){
	        	for(var k = 0; k < childNodes.length; k++){
	        		var childNode = childNodes.item(k);

	        		var	childData = [];
	        		childData.node = childNode;
	        		childData.id = String(idCount);//this.getNamedValue(childNode, childIdField, String(idCount));
	        		childData.isParent = false;
	        		//alert(colData.id);
	        		//alert("idCount: "+idCount);
	        		childData.parentId = colData.id;
	        		for(var l = 0; l < childFields.length; l++) {
	            		var val = this.getNamedValue(childNode, childFields[l], "");
	            		if(this.preprocessors[l]){
	                		val = this.preprocessors[l](val);
	            		}//fi	            		 
	            		childData.push(val);
	            	}//rof
	            	idCount++;

	            	children.push(childData);
	            	//rowData.push(childData);
	        	}//rof
	        	colData.children = children;	        	
	        }//fi

	        rowData.push(colData);
	        if(colData.children){
		        for(var p = 0; p < colData.children.length; p++){
		        	rowData.push(colData.children[p]);
		        }//rof
		    }//fi
	    }//rof
    }
     if(keepExisting !== true){
       YAHOO.ext.grid.XMLChildDataModel.superclass.removeAll.call(this);
	}
	if(typeof insertIndex != 'number'){
	    insertIndex = this.getRowCount();
	}
    YAHOO.ext.grid.XMLChildDataModel.superclass.insertRows.call(this, insertIndex, rowData);
    if(typeof callback == 'function'){
    	callback(this, true);
    }

    this.fireLoadEvent();
};

/**
 * Overload
 * Returns the column data for the specified rows as a 
 * multi-dimensional array: rows[3][0] would give you the value of row 4, column 0. 
 * @param {Array} indexes The row indexes to fetch
 * @return {Array}
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.getRows = function(indexes){
    var data = this.data;
    var r = [];
    for(var i = 0; i < indexes.length; i++){
       r.push(data[indexes[i]]);
       if(data[indexes[i]].isParent){
       	for(var j = 0; j <  data[indexes[i]].children.length; j++){
       		r.push(data[indexes[i]+j+1]);
       	}//rof
       }//fi
    }
    return r;
};

/**
 * Adds a row to this DataModel and syncs the XML document
 * @param {String} id The id of the row, if null the next row index is used
 * @param {Array} cellValues The cell values for this row
 * @return {Number} The index of the new row
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.addRow = function(id, cellValues){
    var newIndex = this.getRowCount();
    var node = this.createNode(this.xml, id, cellValues);
    cellValues.id = id || newIndex;
    cellValues.node = node;
    YAHOO.ext.grid.XMLChildDataModel.superclass.addRow.call(this, cellValues);
    return newIndex;
};

/**
 * Inserts a row into this DataModel and syncs the XML document
 * @param {Number} index The index to insert the row
 * @param {String} id The id of the row, if null the next row index is used
 * @param {Array} cellValues The cell values for this row
 * @return {Number} The index of the new row
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.insertRow = function(index, id, cellValues){
    var node = this.createNode(this.xml, id, cellValues);
    cellValues.id = id || this.getRowCount();
    cellValues.node = node;
    YAHOO.ext.grid.XMLChildDataModel.superclass.insertRow.call(this, index, cellValues);
    return index;
};

/**
 * Retrieve a row by the id
 * @param {String} id The id of the row, if null the next row index is used
 * @return {Object} The row with the corresponding id
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.getRowById = function(id){
	for(var i = 0; i < this.data.length; i++){

		if(this.data[i].id == id){
			
			return this.data[i];
		}
	}
	return null;
};

/**
 * Retrieve the row index with the corresponding id
 * @param {String} id The id of the row, if null the next row index is used
 * @return {Object} The index of the row with the corresponding id
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.getRowIndexById = function(id){
	for(var i = 0; i < this.data.length; i++){
		if(this.data[i].id == id)
			return i;
	}
	return null;
};

/**
 * Removes the row from DataModel and syncs the XML document
 * @param {Number} index The index of the row to remove
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.removeRow = function(index){

    var node = this.data[index].node; 

	var rIndex = 0;
    if(this.data[index].isParent){
    	for(var i = 0; i < this.data[index].children.length; i++){
   	
    		var cNode = this.data[index + 1].node; 
    	
    		if(cNode.parentNode != null) 		
    			cNode.parentNode.removeChild(cNode);
    			
    		YAHOO.ext.grid.XMLChildDataModel.superclass.removeRow.call(this, index + 1, index + 1);
    		rIndex++;
    	}//rof
    }else{
    	//this is a child node   	 
    	var topNode = this.getRowById(this.data[index].parentId);
    	
    	if(topNode){
    		var newChildren = [];
    		for(var i = 0; i < topNode.children.length; i++){
    			if(this.data[index].id != topNode.children[i].id){
    				newChildren.push(topNode.children[i]);	
    			}
    		}//rof
    		topNode.children = newChildren;
    	}//fi
    }

  	if(node.parentNode != null) 
    	node.parentNode.removeChild(node);
    YAHOO.ext.grid.XMLChildDataModel.superclass.removeRow.call(this, index, index);
};

YAHOO.ext.grid.XMLChildDataModel.prototype.getNode = function(rowIndex){
    return this.data[rowIndex].node;
};

/**
 * Override this method to define your own node creation routine for when new rows are added.
 * By default this method clones the first node and sets the column values in the newly cloned node.
 * @param {XMLDocument} xmlDoc The xml document being used by this model
 * @param {Array} colData The column data for the new node
 * @return {XMLNode} The created node
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.createNode = function(xmlDoc, id, colData){
    var template = this.data[0].node;
    var newNode = template.cloneNode(true);
    var fields = this.schema.fields;
    for(var i = 0; i < fields.length; i++){
        var nodeValue = colData[i];
        if(this.postprocessors[i]){
            nodeValue = this.postprocessors[i](nodeValue);
        }
        this.setNamedValue(newNode, fields[i], nodeValue);
    }
    if(id){
        this.setNamedValue(newNode, this.schema.idField, id);
    }
    template.parentNode.appendChild(newNode);
    return newNode;
};

/**
 * Convenience function looks for value in attributes, then in children tags - also 
 * normalizes namespace matches (ie matches ns:tag, FireFox matches tag and not ns:tag).
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.getNamedValue = function(node, name, defaultValue){
	if(!node || !name){
		return defaultValue;
	}
	var nodeValue = defaultValue;
    var attrNode = node.attributes.getNamedItem(name);
    if(attrNode) {
    	nodeValue = attrNode.value;
    } else {
        var childNode = node.getElementsByTagName(name);
        if(childNode && childNode.item(0) && childNode.item(0).firstChild) {
            nodeValue = childNode.item(0).firstChild.nodeValue;
    	}else{
    	    // try to strip namespace for FireFox
    	    var index = name.indexOf(':');
    	    if(index > 0){
    	        return this.getNamedValue(node, name.substr(index+1), defaultValue);
    	    }
    	}
    }
    return nodeValue;
};

/**
 * Convenience function set a value in the underlying xml node.
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.setNamedValue = function(node, name, value){
	if(!node || !name){
		return;
	}
	var attrNode = node.attributes.getNamedItem(name);
    if(attrNode) {
    	attrNode.value = value;
    	return;
    }
    var childNode = node.getElementsByTagName(name);
    if(childNode && childNode.item(0) && childNode.item(0).firstChild) {
        childNode.item(0).firstChild.nodeValue = value;
    }else{
	    // try to strip namespace for FireFox
	    var index = name.indexOf(':');
	    if(index > 0){
	        this.setNamedValue(node, name.substr(index+1), value);
	    }
	}
};

/**
 * Overrides DefaultDataModel.setValueAt to update the underlying XML Document
 * @param {Object} value The new value
 * @param {Number} rowIndex
 * @param {Number} colIndex
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.setValueAt = function(value, rowIndex, colIndex){
    var node = this.data[rowIndex].node;
    if(node){
        var nodeValue = value;
        if(this.postprocessors[colIndex]){
            nodeValue = this.postprocessors[colIndex](value);
        }
        this.setNamedValue(node, this.schema.fields[colIndex], nodeValue);
    }
    YAHOO.ext.grid.XMLDataModel.superclass.setValueAt.call(this, value, rowIndex, colIndex);
};

/**
 * Overrides getRowId in DefaultDataModel to return the ID value of the specified node. 
 * @param {Number} rowIndex
 * @return {Number}
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.getRowId = function(rowIndex){
	//alert(rowIndex);
    return this.data[rowIndex].id;
};

/**
 * Return the number of children in the grid
 * @return {Number}
 */
YAHOO.ext.grid.XMLChildDataModel.prototype.getChildrenCount = function(){
	var count = 0;
	for(var i = 0; i < this.data.length; i++){
		if(!this.data[i].isParent)
			count++;
	}
	return count;
};
