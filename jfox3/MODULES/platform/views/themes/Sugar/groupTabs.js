/**
 * groupTabs javascript file
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
 


SUGAR.themes.updateDelay = 250;

SUGAR.themes.updateSubTabHtml = function(mainTab){
	var subTabs = document.getElementById("subtabs");
	subTabs.innerHTML = SUGAR.themes.subTabs[mainTab];
};

SUGAR.themes.updateSubTabs = function(mainTab, htmlTab){
	if(typeof htmlTab != 'undefined'){
		SUGAR.themes.updateSubTabHtml(htmlTab);
	}else{
		SUGAR.themes.updateSubTabHtml(mainTab);
	}
	
	if(SUGAR.themes.activeTab){
		var oldActive = document.getElementById(SUGAR.themes.activeTab + "_tab");
		oldActive.getElementsByTagName('tr')[0].getElementsByTagName('td')[0].className = "otherTabLeft";
		oldActive.getElementsByTagName('tr')[0].getElementsByTagName('td')[1].className = "otherTab";
		oldActive.getElementsByTagName('tr')[0].getElementsByTagName('td')[2].className = "otherTabRight";
	}
	
	var newActive = document.getElementById(mainTab + "_tab");
	newActive.getElementsByTagName('tr')[0].getElementsByTagName('td')[0].className = "currentTabLeft";
	newActive.getElementsByTagName('tr')[0].getElementsByTagName('td')[1].className = "currentTab";
	newActive.getElementsByTagName('tr')[0].getElementsByTagName('td')[2].className = "currentTabRight";
	SUGAR.themes.activeTab = mainTab;
};

SUGAR.themes.updateMoreTab = function(tabName){
	var moreTab = document.getElementById(SUGAR.themes.moreTab + "_tab");
	if(SUGAR.themes.moreTabUrl == null){
		SUGAR.themes.moreTabUrl = moreTab.getElementsByTagName('tr')[0].getElementsByTagName('td')[1].getElementsByTagName('a')[0].href;
	}
	var href = (tabName!=SUGAR.themes.moreTab)?document.getElementById(tabName + "Handle").href:SUGAR.themes.moreTabUrl;
	moreTab.onmouseover = function(){SUGAR.themes.updateSubTabsDelay(SUGAR.themes.moreTab,tabName);};
	moreTab.getElementsByTagName('tr')[0].getElementsByTagName('td')[0].getElementsByTagName('img')[0].alt = tabName;
	moreTab.getElementsByTagName('tr')[0].getElementsByTagName('td')[1].getElementsByTagName('a')[0].innerHTML = tabName;
	moreTab.getElementsByTagName('tr')[0].getElementsByTagName('td')[1].getElementsByTagName('a')[0].href = href;
	moreTab.getElementsByTagName('tr')[0].getElementsByTagName('td')[1].getElementsByTagName('a')[0].onclick = function(){SUGAR.themes.chooseTab(tabName);return true;};
	moreTab.getElementsByTagName('tr')[0].getElementsByTagName('td')[2].getElementsByTagName('img')[0].alt = tabName;
};

SUGAR.themes.resetTabs = function(){
	SUGAR.themes.updateSubTabs(SUGAR.themes.startTab);
	SUGAR.themes.updateMoreTab(SUGAR.themes.moreTab);
}

SUGAR.themes.firstReset = function(){
	document.getElementById(SUGAR.themes.startTab + "_tab").getElementsByTagName('tr')[0].getElementsByTagName('td')[1].getElementsByTagName('a')[0].className = 'currentTablink';
	SUGAR.themes.resetTabs();
}

SUGAR.themes.setResetTimer = function(){
	window.clearTimeout(SUGAR.themes.updateSubTabsTimer);
	window.clearTimeout(SUGAR.themes.resetSubTabsTimer);
	SUGAR.themes.resetSubTabsTimer = window.setTimeout('SUGAR.themes.resetTabs();', 1000);
}

SUGAR.themes.updateSubTabsDelay = function(mainTab, htmlTab, moreTab){
	var htmlTabArg = '';
	if(typeof htmlTab != 'undefined'){
		htmlTabArg = ', "'+htmlTab+'"';
	}
	var moreTabCode = '';
	if(typeof moreTab != 'undefined'){
		moreTabCode = 'SUGAR.themes.updateMoreTab("'+moreTab+'");';
	}
	window.clearTimeout(SUGAR.themes.updateSubTabsTimer);
	SUGAR.themes.updateSubTabsTimer = window.setTimeout('SUGAR.themes.updateSubTabs("'+mainTab+'"'+htmlTabArg+');'+moreTabCode, SUGAR.themes.updateDelay);
}

SUGAR.themes.chooseTab = function(tab){
	Set_Cookie('parentTab',tab,30,'/','','');
}
