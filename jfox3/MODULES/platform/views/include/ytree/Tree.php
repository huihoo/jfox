<?php
if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point');
/**
 * Tree : interface class for yahoo tree widget.
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
/*usage: initialize the tree, add nodes, generate header for required files inclusion.
 *  	  generate function that has tree data and script to convert data into tree init.
 *	      generate call to tree init function.
 *		  subsequent tree data calls will be served by the node class.  
 *		  tree view by default make ajax based calls for all requests.
 */
require_once('include/entryPoint.php');

require_once ('include/ytree/Tree.php');
require_once ('include/JSON.php');

class Tree {
  var $tree_style='include/ytree/TreeView/css/folders/tree.css';	
  var $_header_files=array(	
							'include/ytree/TreeView/TreeView.js',							
                            'include/ytree/TreeView/TaskNode.js',
							'include/ytree/treeutil.js',
							'include/JSON.js',																					
					 );
					 
  var $_debug_window=false;
  var $_debug_div_name='debug_tree';
  var $_name;
  var $_nodes=array();
  var $json;
  //collection of parmeter properties;
  var $_params=array();
  				   
  function Tree($name) {
		$this->_name=$name;
		$this->json=new JSON(JSON_LOOSE_TYPE);  
  }
  
  //optionally add json.js, required for making AJAX Calls. 
  function include_json_reference($reference=null) {
  	if (empty($reference)) {
  		$this->_header_files[]='include/JSON.js';
  	} else { 
  		$this->_header_files[]=$reference;
  	} 												 
  }
           
  function add_node($node) {
  		$this->_nodes[$node->uid]=$node;
  }
  
// returns html for including necessary javascript files.
  function generate_header() {
    $ret="<link rel='stylesheet' href='{$this->tree_style}'>\n";
   	foreach ($this->_header_files as $filename) {
   		$ret.="<script language='JavaScript' src='{$filename}'></script>\n";	
   	}
	return $ret;
  }
  
//properties set here will be accessible from
//the tree's name space..
  function set_param($name, $value) {
  	if (!empty($name) && !empty($value)) {
 		$this->_params[$name]=$value;
 	}
  }
 	  
  function generate_nodes_array($scriptTags = true) {
	global $sugar_config;
	$node=null;
	$ret=array();
	foreach ($this->_nodes as $node ) {
		$ret['nodes'][]=$node->get_definition();
	}  	
	
	//todo removed site_url setting from here.
	//todo make these variables unique.	
  	$tree_data="var TREE_DATA= " . $this->json->encode($ret) . ";\n";
  	$tree_data.="var param= " . $this->json->encode($this->_params) . ";\n";  	

  	$tree_data.="var mytree;\n";
  	$tree_data.="treeinit(mytree,TREE_DATA,'{$this->_name}',param);\n";
  	if($scriptTags) return '<script>'.$tree_data.'</script>';
    else return $tree_data;
  }
}
?>
