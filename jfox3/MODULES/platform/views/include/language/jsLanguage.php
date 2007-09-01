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
 
class jsLanguage {
    
    /**
     * Creates javascript versions of language files
     */
    function jsLanguage() {
    }
    
    function createAppStringsCache($lang = 'en_us') {
        // cn: bug 8242 - non-US langpack chokes
        $app_strings = return_application_language($lang);
        $app_list_strings = return_app_list_strings_language($lang);
        
        $json = getJSONobj();
        $app_list_strings_encoded = $json->encode($app_list_strings);
        $app_strings_encoded = $json->encode($app_strings);
        
        $str = <<<EOQ
SUGAR.language.setLanguage('app_strings', $app_strings_encoded);
SUGAR.language.setLanguage('app_list_strings', $app_list_strings_encoded);
EOQ;
        
        $cacheDir = create_cache_directory('jsLanguage/');
        if($fh = @fopen($cacheDir . $lang . '.js', "w")){
            fputs($fh, $str, strlen($str));
            fclose($fh);
        }
    }
    
    function createModuleStringsCache($moduleDir, $lang = 'en_us') {
        $json = getJSONobj();

        // cn: bug 8242 - non-US langpack chokes
        $mod_strings = return_module_language($lang, $moduleDir);
        $mod_strings_encoded = $json->encode($mod_strings);
        $str = "SUGAR.language.setLanguage('" . $moduleDir . "', " . $mod_strings_encoded . ");";
        
        $cacheDir = create_cache_directory('jsLanguage/' . $moduleDir . '/');
        
        if($fh = @fopen($cacheDir . $lang . '.js', "w")){
            fputs($fh, $str, strlen($str));
            fclose($fh);
        }
    }

}
?>
