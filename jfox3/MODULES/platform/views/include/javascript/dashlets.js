/**
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



SUGAR.dashlets = function() {
	return {
		/**
		 * Generic javascript method to use post a form 
		 * 
		 * @param object theForm pointer to the form object
		 * @param function callback function to call after for form is sent
		 *
		 * @return bool false
		 */ 
		postForm: function(theForm, callback) {	
			var success = function(data) {
				if(data) {
					callback(data.responseText);
				}
			}
			YAHOO.util.Connect.setForm(theForm); 
			var cObj = YAHOO.util.Connect.asyncRequest('POST', 'index.php', {success: success, failure: success});
			return false;
		},
		/**
		 * Generic javascript method to use Dashlet methods
		 * 
		 * @param string dashletId Id of the dashlet being call
		 * @param string methodName method to be called (function in the dashlet class)
		 * @param string postData data to send (eg foo=bar&foo2=bar2...)
		 * @param bool refreshAfter refreash the dashlet after sending data
		 * @param function callback function to be called after dashlet is refreshed (or not refresed) 
		 */ 
		callMethod: function(dashletId, methodName, postData, refreshAfter, callback) {
        	ajaxStatus.showStatus(SUGAR.language.get('app_strings', 'LBL_SAVING'));
        	response = function(data) {
        		ajaxStatus.hideStatus();
				if(refreshAfter) SUGAR.sugarHome.retrieveDashlet(dashletId);
				if(callback) {
					callback(data.responseText);
				}
        	}
	    	post = 'to_pdf=1&module=Home&action=CallMethodDashlet&method=' + methodName + '&id=' + dashletId + '&' + postData;
			var cObj = YAHOO.util.Connect.asyncRequest('POST','index.php', 
							  {success: response, failure: response}, post);
		}
	 };
}();
