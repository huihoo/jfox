/**
 * colors.js javascript file
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
 


/* By classifying stylesheets into types, we can
 * ensure that only one color and one font sheet
 * are active at a time. */
SUGAR.themes.changeColor = function(colorName){
	return SUGAR.themes.changeStyle('color', colorName);
}

SUGAR.themes.changeFont = function(fontName){
	return SUGAR.themes.changeStyle('font', fontName);
}

SUGAR.themes.changeStyle = function(styleType, newStyle){
	var styles = document.getElementsByTagName("link");
	var found = null;
	var next = null;
	for(var i=0; (link = styles[i]); i++) {
		if(link.getAttribute("rel").indexOf("style") != -1 && link.getAttribute("title") && link.getAttribute("title").indexOf(styleType+':') == 0) {
			if(link.getAttribute("title") == styleType+':'+newStyle) {
				next = link;
				if(found) break;
			}
			if(link.getAttribute("rel") == 'stylesheet') {
				found = link;
				continue;
			}
		}
	}
	if(next != null) {
		if(found != null) {
			found.disabled = true;
			found.setAttribute('rel', 'alternate stylesheet');
		}
		next.disabled = false;
		next.setAttribute('rel', 'stylesheet');
		Set_Cookie('Sugar_'+styleType+'_style', newStyle, 30,'/','','');
	}
};

