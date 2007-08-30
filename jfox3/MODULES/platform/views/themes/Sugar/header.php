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

 * Description:  Contains a variety of utility functions used to display UI
 * components such as form headers and footers.  Intended to be modified on a per
 * theme basis.
 ********************************************************************************/

require_once('XTemplate/xtpl.php');
require_once("data/Tracker.php");
require_once("include/utils.php");
require_once("include/globalControlLinks.php");

global $currentModule;
global $moduleList;
global $theme;
global $max_tabs;
$theme_path="themes/".$theme."/";
$image_path=$theme_path."images/";
require_once($theme_path.'layout_utils.php');
require($theme_path.'config.php');


global $app_strings;
$default_charset = $sugar_config['default_charset'];
$module_path="modules/".$currentModule."/";
load_menu($module_path);
$xtpl=new XTemplate ($theme_path."header.html");
$xtpl->assign("APP", $app_strings);


if(isset($app_strings['LBL_CHARSET']))
{
	$xtpl->assign("LBL_CHARSET", $app_strings['LBL_CHARSET']);
}
else
{
	$xtpl->assign("LBL_CHARSET", $default_charset);
}

$xtpl->assign("THEME", $theme);
$xtpl->assign("IMAGE_PATH", $image_path);
$xtpl->assign("PRINT_URL", "index.php?".$GLOBALS['request_string']);
$xtpl->assign("MODULE_NAME", $currentModule);
$xtpl->assign("DATE", date("Y-m-d"));
$xtpl->assign("TITLE", $app_strings['LBL_SEARCH']);
if(isset($_REQUEST['RTL']) && $_REQUEST['RTL'] == 'RTL'){
		$_SESSION['RTL'] = true;
	}
	if(isset($_REQUEST['LTR']) && $_REQUEST['LTR'] == 'LTR'){
		unset($_SESSION['RTL']);
	}
	if(isset($_SESSION['RTL']) && $_SESSION['RTL']){
		$xtpl->assign("DIR", 'dir="RTL"');
	}
if ($current_user->first_name != '') $xtpl->assign("CURRENT_USER", $current_user->first_name);
else $xtpl->assign("CURRENT_USER", $current_user->user_name);

$xtpl->assign("CURRENT_USER_ID", $current_user->id);

$i = 0;
foreach($global_control_links as $key => $value) {
	foreach ($value as $linkattribute => $attributevalue) {
		if($linkattribute == 'linkinfo') {
			foreach ($attributevalue as $label => $url) {
				$xtpl->assign("GCL_LABEL", $label);
				$xtpl->assign("GCL_URL", $url);
				if (isset($sub_menu[$key]) && $sub_menu[$key]) {
					$xtpl->assign("GCL_MENU", "id='".$key."Handle' onmouseover=' tbButtonMouseOver(this.id,18,\"\",0);'");
					$xtpl->assign("MENU_ARROW", "<img src='".$image_path."menuarrow.gif' alt='' align='absmiddle' id='".$key."Handle' style='margin-bottom: 1px; margin-left:2px; cursor: pointer; cursor: hand;' align='absmiddle' onmouseover='tbButtonMouseOver(this.id,18,\"\",0);'>");
				} else {
					$xtpl->assign("GCL_MENU", "");
					$xtpl->assign("MENU_ARROW", "");
				}
				if($i < sizeof($global_control_links)-1) {
					$xtpl->assign("SEPARATOR", "|");
				} else {
					$xtpl->assign("SEPARATOR", "");
				}
			}
		}

		if($linkattribute == 'submenu') {
			if (is_array($attributevalue)) {
				foreach ($attributevalue as $submenulinkkey => $submenulinkinfo) {
					foreach ($submenulinkinfo as $submenulinklabel => $submenulinkurl) {
					$xtpl->assign("GCL_SUBMENU_LINK_LABEL", $submenulinklabel);
					$xtpl->assign("GCL_SUBMENU_LINK_URL", $submenulinkurl);
					}
					$xtpl->assign("GCL_SUBMENU_KEY", $key);
					$xtpl->assign("GCL_SUBMENU_LINK_KEY", $submenulinkkey);
					$xtpl->parse("main.gcl_submenu.gcl_submenu_items");
				}

			}
			$xtpl->assign("GCL_SUBMENU_KEY", $key);
			$xtpl->parse("main.gcl_submenu");
		}
	}
	$xtpl->parse("main.global_control_links");
	$i++;
}

if (isset($_REQUEST['query_string'])) $xtpl->assign("SEARCH", $_REQUEST['query_string']);

if ($action == "EditView" || $action == "Login") $xtpl->assign("ONLOAD", 'onload="set_focus()"');

// Loop through the module list.
// For each tab that is off, parse a tab_off.
// For the current tab, parse a tab_on
$modListHeader = $moduleList;


if($action=='Login' && $sugar_config['login_nav']==false)
{

    $modListHeader = array();
}
else
{
    $modListHeader = query_module_access_list($current_user);
}


if(isset($current_user->id))
{
	$user_max_tabs = $current_user->getPreference('max_tabs');
	if($user_max_tabs > 0)
		$max_tabs = $user_max_tabs;
    elseif(!isset($max_tabs) || $max_tabs <= 0)
        $max_tabs = $GLOBALS['sugar_config']['default_max_tabs'];
}

$modListHeader = get_val_array($modListHeader);

// Associates Modules with tabs
$activities= array("Calls","Meetings","Tasks","Notes");

if (in_array("Calendar",$moduleList)) {

	$cal_activities= array("Calls","Meetings");
	if (in_array($currentModule,$cal_activities)) {
		$currentModule = "Calendar";
	} else  {
		if (in_array($currentModule,$activities)) {
			$currentModule = "Activities";
		}
	}


} else {
	if (in_array($currentModule,$activities)) {
		$currentModule = "Activities";

	}
}

/* Select navigation paradigm */
$user_navigation_paradigm = $current_user->getPreference('navigation_paradigm');
if(!isset($user_navigation_paradigm)) $user_navigation_paradigm = $GLOBALS['sugar_config']['default_navigation_paradigm'];
$useGroupTabs = $user_navigation_paradigm == 'gm';

// jostrow: fixes bug #9712, by making sure not to build the groupTabs list on the Login screen when login_nav==false
if($useGroupTabs && !empty($modListHeader))
{
    $max_subtabs = $current_user->getPreference('max_subtabs');
    if(!isset($max_subtabs) || $max_subtabs <= 0) $max_subtabs = $GLOBALS['sugar_config']['default_max_subtabs'];

    require_once('include/GroupedTabs/GroupedTabStructure.php');

    $groupedTabsClass = new GroupedTabStructure();

    $groupedTabStructure = $groupedTabsClass->get_tab_structure();



    /* Insert iFrame tabs into the grouped tab structure.
     * If the iFrame tab isn't hidden, then put the iFrames
     * in the same tab group (each one that tab occurs in).
     * Otherwise make a special tab for it. */
    require_once('modules/iFrames/iFrame.php');
    $iFrame = new iFrame();
    $frames = $iFrame->lookup_frames('tab');
    $framesShown = false;
    foreach($groupedTabStructure as $mainTab => $subModules)
    {
        if(!empty($frames) && in_array('iFrames', $subModules['modules']))
        {
            $framesShown = true;
            foreach($frames as $name => $value)
            {
                $groupedTabStructure[$mainTab]['modules'] []= 'f:'.$value[4];
            }
        }
    }
    if(!$framesShown)
    {
        foreach($frames as $name => $value)
        {
            $groupedTabStructure[$app_strings['LBL_TABGROUP_MY_PORTALS']]['modules'] []= 'f:'.$value[4];
        }
    }



    if(!isset($_COOKIE['parentTab']))
    {
        $gak = array_keys($groupedTabStructure);
        $_COOKIE['parentTab'] = $gak[0];
    }

    //echo "<script>console.log('c:{$_COOKIE['parentTab']};');</script>";
    /* If we're in the right tab already, then yay. */
    if(!empty($groupedTabStructure[$_COOKIE['parentTab']]) && in_array($currentModule, $groupedTabStructure[$_COOKIE['parentTab']]['modules']))
    {
        $_SESSION['parentTab'] = $_COOKIE['parentTab'];
        //echo "<script>console.log('a');</script>";
    }
    else
    {
        /* Find proper parent */
        $found = false;
        foreach($groupedTabStructure as $mainTab => $subModules)
        {
            if(in_array($currentModule, $subModules['modules']))
            {
                $_COOKIE['parentTab'] = $_SESSION['parentTab'] = $mainTab;
                $found = true;
                break;
            }
        }
        if(!$found)
        {
            $gak = array_keys($groupedTabStructure);
            $_COOKIE['parentTab'] = $_SESSION['parentTab'] = $gak[0];
        }
    }
    //echo "<script>console.log('s:{$_SESSION['parentTab']};c:{$_COOKIE['parentTab']};cm:$currentModule;');</script>";


    if(isset($_SESSION['moreMainTab']) && in_array($_COOKIE['parentTab'], array_slice(array_keys($groupedTabStructure), $max_tabs)))
    {
        $_SESSION['moreMainTab'] = $_COOKIE['parentTab'];
    }
    elseif(!empty($groupedTabStructure))
    {
    	$keys = array_keys($groupedTabStructure);
        $_SESSION['moreMainTab'] = $keys[min(count($keys),$max_tabs)-1];
    }
    else
    {
    	$_SESSION['moreMainTab'] = '';
    }

    $_COOKIE['parentTab'] = $_SESSION['parentTab'];

    $xtpl->assign('MAIN_MORE_TAB', $_SESSION['moreMainTab']);
    $xtpl->assign('PARENT_TAB', $_SESSION['parentTab']);

    //echo "<script>console.log('php: {$_COOKIE['parentTab']}');console.log('js: ' + Get_Cookie('parentTab'));</script>";
    $mainTabs = array_slice($groupedTabStructure, 0, $max_tabs);
    $i = 0;
    $deferredTab = 0;
    $moreShownAlready = false;
    foreach($groupedTabStructure as $mainTab => $subModules)
    {
        if(empty($subModules['modules']))
        {
            continue;
        }
        $i++;

        if(($mainTab == $_SESSION['moreMainTab']) && ($i <= $max_tabs))
        {
        	$moreShownAlready = true;
        }

        /* Swap the moreTab with the $max_tabs one */
        if(($i == $max_tabs) && !$moreShownAlready)
        {
            $deferredTab = $mainTab;
            $mainTab = $_SESSION['moreMainTab'];
            $subModules = $groupedTabStructure[$mainTab];
        }
        elseif(($mainTab == $_SESSION['moreMainTab']) && !$moreShownAlready)
        {
            $mainTab = $deferredTab;
            $subModules = $groupedTabStructure[$mainTab];
        }

        $isIframe = false;
        $subModule = reset($subModules['modules']);
        if(strpos($subModule, 'f:') === 0)
        {
            $subModule = substr($subModule, 2);
            $isIframe = true;
        }

        $xtpl->assign("TAB_URL", $isIframe?"index.php?module=iFrames&action=index&record={$frames[$subModule][0]}&tab={$frames[$subModule][2]}":"index.php?module=$subModule&action=index");

        $xtpl->assign("TAB_NAME", $mainTab);
        $xtpl->assign("MODULE_KEY", $mainTab);

        $tabClass = "otherTab";
        $otherTab = "currentTab";
        $xtpl->assign("TAB_CLASS", "otherTab");
        $xtpl->assign("OTHER_TAB", "currentTab");



        if($mainTab == $_SESSION['parentTab'] && in_array($currentModule, $subModules['modules']))
        {
        	$subtabMoreShownAlready = false;
        }
        else
        {
        	$subtabMoreShownAlready = true;
        }
        $end = min(count($subModules['modules']),$max_subtabs);
        for($j = 0; $j < $end; $j++)
        {
            $subModule = array_shift($subModules['modules']);

            if(($subModule == $currentModule) && ($i <= $max_tabs))
            {
                $subtabMoreShownAlready = true;
            }
            /* Swap the current tab with the $max_subtabs one.
             * deferredSubtab will be handled in the 'more' loop below */
            if(($j == $end-1) && !$subtabMoreShownAlready)
            {
                $deferredSubtab = $subModule;
                $subModule = $currentModule;
            }

            $isIframe = false;
            if(strpos($subModule, 'f:') === 0)
            {
            	$subModule = substr($subModule, 2);
                $isIframe = true;
            }

            if($subModule == $currentModule)
            {
                $xtpl->assign("SUB_LINK_CLASS", 'activeSubTabLink');
                $xtpl->assign("SUB_CLASS", 'activeSubTab');
            }
            else
            {
            	$xtpl->assign("SUB_CLASS", 'subTab');
            	$xtpl->assign("SUB_LINK_CLASS", 'subTabLink');
            }
            $xtpl->assign("SUB_URL", $isIframe?"index.php?module=iFrames&action=index&record={$frames[$subModule][0]}&tab={$frames[$subModule][2]}":"index.php?module=$subModule&action=index");
            $xtpl->assign("SUB_NAME", $isIframe?$subModule:$app_list_strings['moduleList'][$subModule]);
            $xtpl->assign("SUB_MORE", "");
            if($j == $end-1)
            {
                if(count($subModules['modules']))
                {
                	$xtpl->assign("SUB_MORE", "<li nowrap=\"nowrap\">&nbsp;<span class=\"subTabMore\" id=\\'MoreSub{$mainTab}Handle\\' style=\\' margin-left:2px; cursor: pointer; cursor: hand;\\' align=\\'absmiddle\\' onmouseover=\\'tbButtonMouseOver(this.id,\"\",\"\",0);\\'>&gt;&gt;</span></li>");
                }
            }
            $xtpl->parse("main.group_tabs_js.js_subtabs.js_subtabs_html");
        }

        $xtpl->assign("MAIN_TAB_NAME", $mainTab);
        $xtpl->parse("main.group_tabs_js.js_subtabs");

        $xtpl->assign("MENU_NAME", $mainTab);
        foreach ($subModules['modules'] as $subModuleName)
        {
            /* If currentModule shows up in the more list,
             * then we should have whown it above and
             * deffered one in it's place.
             */
            if($subModuleName == $currentModule && !$subtabMoreShownAlready)
            {
            	$subModuleName = $deferredSubtab;
            }

            $isIframe = false;
            if(strpos($subModuleName, 'f:') === 0)
            {
                $subModuleName = substr($subModuleName, 2);
                $isIframe = true;
            }

            $xtpl->assign("SUB_URL", $isIframe?"index.php?module=iFrames&action=index&record={$frames[$subModuleName][0]}&tab={$frames[$subModuleName][2]}":"index.php?module=$subModuleName&action=index");
            $xtpl->assign("MODULE_NAME", $isIframe?$subModuleName:$app_list_strings['moduleList'][$subModuleName]);
            $xtpl->assign("MODULE_KEY", $subModuleName);
            $xtpl->parse("main.moresubmenu.moresubmenuitem");
        }
        if(!empty($subModules['modules']))
        {
            $xtpl->parse("main.moresubmenu");
        }


        if($i <= $max_tabs)
        {
            if ($i==$max_tabs && count($groupedTabStructure)>$max_tabs) {
                //$xtpl->assign("MORE", "<img src='".$image_path."more.gif' alt='' align='absmiddle' id='MoreHandle' style=' margin-left:2px; cursor: pointer; cursor: hand;' align='absmiddle' onmouseover='tbButtonMouseOver(this.id,\"\",\"\",0);'>");
                $xtpl->assign("MORE_HANDLE_CLASS", 'otherTab');
                $xtpl->parse("main.moreHandle");
            }

            $xtpl->parse("main.tab");
        }
        else
        {
            if($_SESSION['moreMainTab']!=$mainTab){
                $xtpl->assign("MODULE_NAME", $mainTab);
                $xtpl->assign("MODULE_KEY", $mainTab);
                $xtpl->parse("main.more");
            }
        }
    }

    if(!empty($groupedTabStructure))
    {
        $xtpl->parse("main.group_tabs_js");
        $xtpl->parse("main.group_subtabs_js");
    }
    else
    {
    	$xtpl->parse("main.no_group_placeholder_js");
    }
}
else
{
    /* Use the normal tabs style: one tab per module */
    $modListHeaderClone = $modListHeader;
    require_once('modules/iFrames/iFrame.php');
    $iFrame = new iFrame();
    $frames = $iFrame->lookup_frames('tab');
    foreach($frames as $name => $values){
        $modListHeaderClone[$name] = $values;
    }
    $numb_tabs=count($modListHeaderClone);
    $moreListHeader = array_slice($modListHeaderClone, $max_tabs-1,$numb_tabs);

    $defaultMore = array_slice($moreListHeader, 0,1);
    if ($max_tabs==$numb_tabs) {
        $preListHeader = array_slice($modListHeaderClone, 0,$max_tabs);
    } else {
        $preListHeader = array_slice($modListHeaderClone, 0,$max_tabs-1);
    }
    //_pp(array($_SESSION['moreTab'], $defaultMore, $moreListHeader));
    if (!isset($_SESSION['moreTab']) || !in_array($_SESSION['moreTab'],$modListHeaderClone)) {
        foreach ($defaultMore as $module){
            $_SESSION['moreTab'] = $module;
        }
    }
    if (isset($_SESSION['moreTab']) and in_array($_SESSION['moreTab'],$preListHeader)) {
        foreach ($defaultMore as $module){
            $_SESSION['moreTab'] = $module;
        }
    }
    if(in_array($currentModule,$moreListHeader)) {
        $_SESSION['moreTab'] = $currentModule;
    } elseif($currentModule=="iFrames" and isset($_REQUEST['record']) and isset($_REQUEST['tab'])) {

        $frame = $iFrame->lookup_frame_by_record_id($_REQUEST['record']);
        foreach($frame as $name => $values){
        $_SESSION['moreTab'] = $values;
        }
    }

    if ($numb_tabs>$max_tabs){
        $preListHeader[] = $_SESSION['moreTab'];
    }

    $i=0;
    foreach($preListHeader as $module_name)
    {
            if (!is_array($module_name)) {
            $xtpl->assign("MODULE_NAME", $app_list_strings['moduleList'][$module_name]);
            $xtpl->assign("MODULE_KEY", $module_name);


            if($module_name == $currentModule && ($module_name != 'iFrames' || empty($_REQUEST['record'])|| (!empty($_REQUEST['tab']) && $_REQUEST['tab']=='false')))
            {
                $tabClass = "currentTab";
                $otherTab = "currentTab";
                $xtpl->assign("TAB_CLASS", "currentTab");
                $xtpl->assign("OTHER_TAB", "currentTab");
            }
            else
            {
                $tabClass = "otherTab";
                $otherTab = "currentTab";
                $xtpl->assign("TAB_CLASS", "otherTab");
                $xtpl->assign("OTHER_TAB", "currentTab");
            }
            $xtpl->assign("LINK", '<a   class="'.$tabClass.'Link"  href="index.php?module='.$module_name.'&action=index">'.$app_list_strings['moduleList'][$module_name].'</A>');
            } else {

            $xtpl->assign("MODULE_NAME", $module_name[4]);
            $xtpl->assign("MODULE_KEY", $module_name[3]);


            if($module_name[3] == $currentModule && (!empty($_REQUEST['record']) and $_REQUEST['record']==$module_name[0] and !empty($_REQUEST['tab'])))
            {
                $tabClass = "currentTab";
                $otherTab = "currentTab";
                $xtpl->assign("TAB_CLASS", "currentTab");
                $xtpl->assign("OTHER_TAB", "currentTab");
            }
            else
            {
                $tabClass = "otherTab";
                $otherTab = "currentTab";
                $xtpl->assign("TAB_CLASS", "otherTab");
                $xtpl->assign("OTHER_TAB", "currentTab");
            }
            $xtpl->assign("LINK", '<a   class="'.$tabClass.'Link"  href="index.php?module='.$module_name[3].'&action=index&record='.$module_name[0].'&tab='.$module_name[2].'">'.$module_name[4].'</A>');

            }

            if ($i==$max_tabs-1 and $numb_tabs>$max_tabs) {
                //$xtpl->assign("MORE", "<img src='".$image_path."more.gif' alt='' align='absmiddle' id='MoreHandle' style=' margin-left:2px; cursor: pointer; cursor: hand;' align='absmiddle' onmouseover='tbButtonMouseOver(this.id,\"\",\"\",0);'>");
                $xtpl->assign("MORE_HANDLE_CLASS", 'otherTab');
                $xtpl->parse("main.moreHandle");
            }
            $xtpl->parse("main.module_tab");
        $i++;
    }

    foreach ($moreListHeader as $module_name) {
        if (!is_array($module_name)) {
            if($module_name!=$currentModule and $_SESSION['moreTab']!=$module_name){
                $xtpl->assign("MODULE_NAME", $app_list_strings['moduleList'][$module_name]);
                $xtpl->assign("MODULE_KEY", $module_name);
                $xtpl->assign("TAB_URL", "index.php?module=$module_name&action=index");
                $xtpl->parse("main.more");
            }
        } else {
            if (!isset($_REQUEST['record']) or $_REQUEST['record']!=$module_name[0]) {
                $xtpl->assign("MODULE_NAME", $module_name[4]);
                $xtpl->assign("MODULE_KEY", $module_name[3]);
                $xtpl->assign("TAB_URL", "index.php?module=iFrames&action=index&record={$module_name[0]}&tab={$module_name[2]}");
                $xtpl->assign("MODULE_QUERY", '&record='.$module_name[0].'&tab='.$module_name[2]);
                $xtpl->parse("main.more");
            }

        }

    }

    $xtpl->parse("main.no_group_placeholder_js");
}

// foreach color
if(!empty($theme_colors))
{
    $i = 0;
    foreach($theme_colors as $color)
    {
        $xtpl->assign('COLOR_NAME', $color);
        if((empty($_COOKIE[$theme.'_color_style']) || $color == $_COOKIE[$theme.'_color_style']) && (!empty($_COOKIE[$theme.'_color_style']) || !$i))
        {
        	$xtpl->assign('COLOR_STYLE_REL', 'stylesheet');
        	$xtpl->assign('STARTING_COLOR_NAME', $color);
        }
        else
        {
        	$xtpl->assign('COLOR_STYLE_REL', 'alternate stylesheet');
        }
		$xtpl->parse("main.color");
        $xtpl->parse("main.color_sheet");
        $i++;
    }
}
// foreach font
if(!empty($theme_fonts))
{
    $i = 0;
    foreach($theme_fonts as $font)
    {
        $xtpl->assign('FONT_NAME', $font);
        if((empty($_COOKIE[$theme.'_font_style']) || $font == $_COOKIE[$theme.'_font_style']) && (!empty($_COOKIE[$theme.'_font_style']) || !$i))
        {
            $xtpl->assign('FONT_STYLE_REL', 'stylesheet');
            $xtpl->assign('STARTING_FONT_NAME', $font);
        }
        else
        {
            $xtpl->assign('FONT_STYLE_REL', 'alternate stylesheet');
        }
        $xtpl->parse("main.font");
        $xtpl->parse("main.font_sheet");
        $i++;
    }
}



//$moreTabs = array_slice($groupedTabStructure, $max_tabs);
//foreach ($moreTabs as $mainTab => $subModules) {
//	if($mainTab!=$currentModule and $_SESSION['moreMainTab']!=$mainTab){
//		$xtpl->assign("MODULE_NAME", $mainTab);
//		$xtpl->assign("MODULE_KEY", $mainTab);
//		$xtpl->parse("main.more");
//	}
//}

unset($name);
unset($id);

//include('modules/iFrames/header.php');
// Assign the module name back to the current module.
$xtpl->assign("MODULE_NAME", $currentModule);

/* Get preference for shortcuts and last_viewed list placement */
$last_view_swap = $current_user->getPreference('swap_last_viewed');
if(!isset($last_view_swap)) $last_view_swap = $GLOBALS['sugar_config']['default_swap_last_viewed'];
$shortcuts_swap = $current_user->getPreference('swap_shortcuts');
if(!isset($shortcuts_swap)) $shortcuts_swap = $GLOBALS['sugar_config']['default_swap_shortcuts'];

$i = 0;
foreach($module_menu as $menu_item)
{
	$subModuleCheck = 0;
	$subModuleCheckArray = array("Tasks", "Calls", "Meetings", "Notes","Prospects");

	if(isset($menu_item[3]))
	{
		if(in_array($menu_item[3], $subModuleCheckArray) && (array_key_exists("Calendar", $modListHeader) ||
			array_key_exists("Activities", $modListHeader)))
				$subModuleCheck = 1;
	}

	if(!isset($menu_item[3])|| !isset($modListHeader) || (isset($menu_item[3]) && (key_exists($menu_item[3],$modListHeader) || $subModuleCheck)))
    {
    	$after_this = current($module_menu);

    	if ($menu_item[1] != 'Deleted Items')
        {
    		$xtpl->assign("URL", $menu_item[0]);
    		$xtpl->assign("LABEL", $menu_item[1]);
    		$xtpl->assign("SC_MODULE_NAME", $menu_item[2]);
            $xtpl->assign("SC_ID", $i++);
    		$xtpl->assign("SC_IMAGE", get_image($image_path.$menu_item[2],"alt='".$menu_item[1]."'  border='0' align='absmiddle'"));
    		if (empty($after_this)) $xtpl->assign("SEPARATOR", "");
    		else $xtpl->assign("SEPARATOR", "</br>");
    	}
        else
        {
    		$xtpl->assign("DELETED_ITEMS_URL", $menu_item[0]);
    		$xtpl->assign("DELETED_ITEMS_LABEL", $menu_item[1]);
    	}

        if(!$shortcuts_swap)
        {
            if($menu_item[0] !== "#")
            {
                $xtpl->parse("main.hide_shortcut.hide_shortcut_item");
                $xtpl->parse("main.left_form.sub_menu.sub_menu_item");
            }
            else
            {
                $xtpl->parse("main.hide_shortcut.hide_shortcut_special");
                $xtpl->parse("main.left_form.sub_menu.sub_menu_special");
            }
        }
        else
        {
            $xtpl->parse("main.left_form_shortcuts.shortcut_item");
        }
    }
}

if(!$shortcuts_swap)
{
    $xtpl->parse("main.left_form.sub_menu");
}

$xtpl->assign("SHORTCUTS", $app_strings['LBL_SHORTCUTS']);

if (isset($_SESSION["authenticated_user_id"]))
{
    $xtpl->assign("TITLE_LAST_VIEW", $app_strings['LBL_LAST_VIEWED']);
    $xtpl->parse("main.left_form_search");
    $xtpl->parse("main.welcome");
    if(!$shortcuts_swap)
    {
        $xtpl->parse("main.left_form");
        $xtpl->parse("main.hide_shortcut");
    }
    else
    {
        $xtpl->parse("main.left_form_shortcuts");
    }




    $tracker = new Tracker();
    $history = $tracker->get_recently_viewed($current_user->id);

    $current_row=1;

    if (count($history) > 0)
    {
    	$i = 0;
        foreach($history as $row)
        {
    		$xtpl->assign("RECENT_LABEL", getTrackerSubstring($row['item_summary']));
    		$xtpl->assign("RECENT_LABEL_FULL",$row['item_summary']);
    		$xtpl->assign("MODULE_NAME",$row['module_name']);
    		$xtpl->assign("ROW_NUMBER",$current_row);
            $xtpl->assign("RL_ID", $i++);
    		$xtpl->assign("RL_IMAGE",get_image($image_path.$row['module_name'],'border="0" align="absmiddle" alt="'.$row['item_summary'].'"'));
    		$xtpl->assign("RECENT_URL","index.php?module=$row[module_name]&action=DetailView&record=$row[item_id]");

    		if ($current_row < 9)
            {
    			if(!$last_view_swap)
                {
                    $xtpl->parse("main.left_form_recent_view.left_form_recent_view_row");
                }
                else
                {
                    $xtpl->parse("main.left_form_recent.sub_menu.sub_menu_item");
                    $xtpl->parse("main.hide_recent.hide_recent_item");
                }
    		}
    		$current_row++;
    	}
    }
    else
    {
        $xtpl->assign("RL_ID", 0);
        if(!$last_view_swap)
        {
            $xtpl->parse("main.left_form_recent_view.left_form_recent_view_empty");
        }
        else
        {
            $xtpl->parse("main.left_form_recent.sub_menu.left_form_recent_view_empty");
            $xtpl->parse("main.hide_recent.left_form_recent_view_empty");
        }
    }

    if(!$last_view_swap)
    {
        $xtpl->parse("main.left_form_recent_view");
    }
    else
    {
        $xtpl->parse("main.left_form_recent.sub_menu");
        $xtpl->parse("main.left_form_recent");
        $xtpl->parse("main.hide_recent");
    }

    $new_record_form = false;

	if (!empty($currentModule)) {
		require_once("modules/".$currentModule."/Forms.php");
	}

    if ($currentModule && $action == "index" && function_exists('get_new_record_form')) {
    	$xtpl->assign("NEW_RECORD", get_new_record_form());
    	$xtpl->parse("main.left_form_new_record");
        $new_record_form = true;
    }

    if(!$shortcuts_swap || $last_view_swap || $new_record_form)
    {
        if(!$shortcuts_swap || $last_view_swap)
        {
            $xtpl->parse("main.left_form_otd.left_form_otd_icon");
        }
        else
        {
        	$xtpl->parse("main.left_form_otd.left_form_otd_noicon");
        }
        $xtpl->parse("main.left_form_otd");
        $xtpl->parse("main.left_form_ctd");
    }
    else
    {
        $xtpl->parse("main.left_form_empty_td");
    }
}
else
{
	$xtpl->parse("main.left_form_search_placeholder");
}

$xtpl->parse("main");
$xtpl->out("main");

?>
