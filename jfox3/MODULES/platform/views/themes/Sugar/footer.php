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
global $app_strings;
global $max_tabs;
?>
<!--end body panes-->
	</td>
	</tr>
	</table>
</td>
</tr>
<?php 
if($action=='Login') {
	if ($sugar_config['login_nav']!=false) {
?>
<tr>    
	<td colspan="3" class="aboveFooter"><img src="include/images/blank.gif" width="1" height="5" border="0"></td>
</tr>
<tr>    
	<td colspan="3" align="center" class="footer">
	<?php 

	if(isset($current_user)){
		$numTabs = count ($modListHeader);
		$i=1;
		foreach($modListHeader as $module_name) {
		?>
		<A href="index.php?module=<?php echo $module_name ?>&action=index" class="footerLink"><?php echo $app_list_strings['moduleList'][$module_name] ?></A><?php 
		if ($i > $numTabs-1 or $i == $max_tabs) echo "";
		else echo " | ";
		if ($i == $max_tabs)
		echo "<br>";
		$i++;
} }?>
	 
	  </td>
</tr>
<?php }
	} else {?>

<tr>    
	<td colspan="3" class="aboveFooter"><img src="include/images/blank.gif" width="1" height="5" border="0"></td>
</tr>
<tr>    
	<td colspan="3" align="center" class="footer">
	<?php 

	if(isset($current_user)){
		$numTabs = count ($modListHeader);
		$i=1;
		foreach($modListHeader as $module_name=>$module_val) {
		?>
		<A href="index.php?module=<?php echo $module_name ?>&action=index" class="footerLink"><?php echo $app_list_strings['moduleList'][$module_name] ?></A><?php 
		if ($i > $numTabs-1 or $i == $max_tabs) echo "";
		else echo " | ";
		if ($i == $max_tabs)
		echo "<br>";
		$i++;
} }?>
	  </td>
</tr>
	
	<?php }?>
</table>
