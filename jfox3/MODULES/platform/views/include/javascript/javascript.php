<?php
if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point');
/*********************************************************************************
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
 ********************************************************************************/
/*********************************************************************************

 * Description:  Creates the runtime database connection.
 ********************************************************************************/
class javascript{
	var $formname = 'form';
	var $script = '';
	var $sugarbean = null;
	function setFormName($name){
		$this->formname = $name;
	}

	function javascript(){
		global $app_strings, $current_user, $sugar_config;

		static $dec_sep = null;
		static $num_grp_sep = null;
		
		if($dec_sep == null) {
			$user_dec_sep = $current_user->getPreference('dec_sep');
			$dec_sep = (empty($user_dec_sep) ? $sugar_config['default_decimal_seperator'] : $user_dec_sep);
		}
		if($num_grp_sep == null) {
	 		$user_num_grp_sep = $current_user->getPreference('num_grp_sep');
			$num_grp_sep = (empty($user_num_grp_sep) ? $sugar_config['default_number_grouping_seperator'] : $user_num_grp_sep);
		}
		$this->script .= "num_grp_sep = '$num_grp_sep';\n
						 dec_sep = '$dec_sep';\n";  
	}
	
	function setSugarBean($sugar){
		$this->sugarbean = $sugar;
	}

	function addRequiredFields($prefix=''){
			if(isset($this->sugarbean->required_fields)){
				foreach($this->sugarbean->required_fields as $field=>$value){
					$this->addField($field,'true', $prefix);
				}
			}
	}


	function addField($field,$required, $prefix='', $displayField=''){
		if(isset($this->sugarbean->field_name_map[$field]['vname'])){
			if(empty($required)){
				if(isset($this->sugarbean->field_name_map[$field]['required']) && $this->sugarbean->field_name_map[$field]['required']){
					$required = 'true';
				}else{
					$required = 'false';	
				}
				if(isset($this->sugarbean->required_fields[$field]) && $this->sugarbean->required_fields[$field]){
					$required = 'true';
				}
				if($field == 'id'){
					$required = 'false';	
				}	
						
			}
			if(isset($this->sugarbean->field_name_map[$field]['validation'])){
				switch($this->sugarbean->field_name_map[$field]['validation']['type']){
					case 'range': 
						$min = 0;
						$max = 100;
						if(isset($this->sugarbean->field_name_map[$field]['validation']['min'])){
							$min = $this->sugarbean->field_name_map[$field]['validation']['min'];
						}
						if(isset($this->sugarbean->field_name_map[$field]['validation']['max'])){
							$max = $this->sugarbean->field_name_map[$field]['validation']['max'];
						}
						if($min > $max){
							$max = $min;
						}
						if(!empty($displayField)){
							$dispField = $displayField;
						}
						else{
							$dispField = $field;
						}
						$this->addFieldRange($dispField,$this->sugarbean->field_name_map[$field]['type'],$this->sugarbean->field_name_map[$field]['vname'],$required,$prefix, $min, $max );	
						break;
					case 'isbefore':
						$compareTo = $this->sugarbean->field_name_map[$field]['validation']['compareto'];
						if(!empty($displayField)){
							$dispField = $displayField;
						}
						else{
							$dispField = $field;
						}
						if(!empty($this->sugarbean->field_name_map[$field]['validation']['blank']) && $this->sugarbean->field_name_map[$field]['validation']['blank']) 
						$this->addFieldDateBeforeAllowBlank($dispField,$this->sugarbean->field_name_map[$field]['type'],$this->sugarbean->field_name_map[$field]['vname'],$required,$prefix, $compareTo );
						else $this->addFieldDateBefore($dispField,$this->sugarbean->field_name_map[$field]['type'],$this->sugarbean->field_name_map[$field]['vname'],$required,$prefix, $compareTo );
						break;
					default: 
						if(!empty($displayField)){
							$dispField = $displayField;
						}
						else{
							$dispField = $field;
						}
						
						$type = (!empty($this->sugarbean->field_name_map[$field]['custom_type']))?$this->sugarbean->field_name_map[$field]['custom_type']:$this->sugarbean->field_name_map[$field]['type'];
						
						$this->addFieldGeneric($dispField,$type,$this->sugarbean->field_name_map[$field]['vname'],$required,$prefix );	
						break;
				}
			}else{
				if(!empty($displayField)){
							$dispField = $displayField;
						}
						else{
							$dispField = $field;
						}
					$type = (!empty($this->sugarbean->field_name_map[$field]['custom_type']))?$this->sugarbean->field_name_map[$field]['custom_type']:$this->sugarbean->field_name_map[$field]['type'];
					if(!empty($this->sugarbean->field_name_map[$dispField]['isMultiSelect']))$dispField .='[]';
					$this->addFieldGeneric($dispField,$type,$this->sugarbean->field_name_map[$field]['vname'],$required,$prefix );
			}
		}else{
			$GLOBALS['log']->debug('No VarDef Label For ' . $field . ' in module ' . $this->sugarbean->module_dir ); 	
		}

	}


	function stripEndColon($modString)
	{
		if(substr($modString, -1, 1) == ":")
			$modString = substr($modString, 0, (strlen($modString) - 1));
		if(substr($modString, -2, 2) == ": ")
			$modString = substr($modString, 0, (strlen($modString) - 2));
		return $modString;
		
	}
	
	function addFieldGeneric($field, $type,$displayName, $required, $prefix=''){
		
		$this->script .= "addToValidate('".$this->formname."', '".$prefix.$field."', '".$type . "', $required,'". $this->stripEndColon(translate($displayName)) . "' );\n";
	}
	function addFieldRange($field, $type,$displayName, $required, $prefix='',$min, $max){
		$this->script .= "addToValidateRange('".$this->formname."', '".$prefix.$field."', '".$type . "', $required,'".$this->stripEndColon(translate($displayName)) . "', $min, $max );\n";
	}
	
	function addFieldDateBefore($field, $type,$displayName, $required, $prefix='',$compareTo){
		$this->script .= "addToValidateDateBefore('".$this->formname."', '".$prefix.$field."', '".$type . "', $required,'".$this->stripEndColon(translate($displayName)) . "', '$compareTo' );\n";
	}

	function addFieldDateBeforeAllowBlank($field, $type, $displayName, $required, $prefix='', $compareTo, $allowBlank='true'){
		$this->script .= "addToValidateDateBeforeAllowBlank('".$this->formname."', '".$prefix.$field."', '".$type . "', $required,'".$this->stripEndColon(translate($displayName)) . "', '$compareTo', '$allowBlank' );\n";
	}
	
	function addToValidateBinaryDependency($field, $type, $displayName, $required, $prefix='',$compareTo){
		$this->script .= "addToValidateBinaryDependency('".$this->formname."', '".$prefix.$field."', '".$type . "', $required,'".$this->stripEndColon(translate($displayName)) . "', '$compareTo' );\n";
	}
    
    function addToValidateComparison($field, $type, $displayName, $required, $prefix='',$compareTo){
        $this->script .= "addToValidateComparison('".$this->formname."', '".$prefix.$field."', '".$type . "', $required,'".$this->stripEndColon(translate($displayName)) . "', '$compareTo' );\n";
    }

	function addAllFields($prefix,$skip_fields=null){
		if (!isset($skip_fields))
		{
			$skip_fields = array();
		}
		foreach($this->sugarbean->field_name_map as $field=>$value){
			if (! isset($skip_fields[$field]))
			{
				if (isset($value['type'])) {
					if ($value['type'] != 'link') {						
			  			$this->addField($field, '', $prefix);
					}
				}
			}
		}
	}

	function getScript($showScriptTag = true){
		$tempScript = $this->script;
		$this->script = "";
		if($showScriptTag){
			$this->script = "<script type=\"text/javascript\">\n";
		}
		
		$this->script .= $tempScript;

		if($showScriptTag){
			$this->script .= "</script>";
		}
		return $this->script;
	}
}
?>
