/**
 *
 * Popup helper functions
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



function send_back(module, id)
{
	var associated_row_data = associated_javascript_data[id];

	// cn: bug 12274 - stripping false-positive security envelope
	eval("var temp_request_data = " + window.document.forms['popup_query_form'].request_data.value);
	if(temp_request_data.jsonObject) {
		var request_data = temp_request_data.jsonObject;
	} else {
		var request_data = temp_request_data; // passed data that is NOT incorrectly encoded via JSON.encode();
	}
	// cn: end bug 12274 fix

	var passthru_data = Object();
	if(typeof(request_data.passthru_data) != 'undefined')
	{
		passthru_data = request_data.passthru_data;
	}
	var form_name = request_data.form_name;
	var field_to_name_array = request_data.field_to_name_array;
	
	var call_back_function = eval("window.opener." + request_data.call_back_function);
	var array_contents = Array();

	// constructs the array of values associated to the bean that the user clicked
	for(var the_key in field_to_name_array)
	{
		if(the_key != 'toJSON')
		{
			var the_name = field_to_name_array[the_key];
			var the_value = '';

			if(module != '' && id != '')
			{
				the_value = associated_row_data[the_key.toUpperCase()];
			}
			
			if (typeof(the_value) == 'string') {
				the_value = the_value.replace(/\r\n/g, '\\n');
			}
			
			array_contents.push('"' + the_name + '":"' + the_value + '"');
		}
	}
	
	eval("var name_to_value_array = {" + array_contents.join(",") + "}");

	var result_data = {"form_name":form_name,"name_to_value_array":name_to_value_array,"passthru_data":passthru_data};
	var close_popup = window.opener.get_close_popup();
	
	call_back_function(result_data);

	if(close_popup)
	{
		window.close();
	}
}

function send_back_selected(module, form, field, error_message)
{
	var array_contents = Array();
	var j=0;
	for (i = 0; i < form.elements.length; i++){
		if(form.elements[i].name == field) { 
			if (form.elements[i].checked == true) {
				++j;
				array_contents.push('"' + "ID_" + j  + '":"' + form.elements[i].value + '"');
			}
		}
	}
	if (array_contents.length ==0 ) {
		window.alert(error_message);	
		return;
	}
	
	eval("var selection_list_array = {" + array_contents.join(",") + "}");
	
	// cn: bug 12274 - stripping false-positive security envelope
	eval("var temp_request_data = " + window.document.forms['popup_query_form'].request_data.value);
	if(temp_request_data.jsonObject) {
		var request_data = temp_request_data.jsonObject;
	} else {
		var request_data = temp_request_data; // passed data that is NOT incorrectly encoded via JSON.encode();
	}
	// cn: end bug 12274 fix

	var passthru_data = Object();
	if(typeof(request_data.passthru_data) != 'undefined')
	{
		passthru_data = request_data.passthru_data;
	}
	var form_name = request_data.form_name;
	var field_to_name_array = request_data.field_to_name_array;
	var call_back_function = eval("window.opener." + request_data.call_back_function);

	var result_data={"form_name":form_name,"selection_list":selection_list_array ,"passthru_data":passthru_data};
	var close_popup = window.opener.get_close_popup();
	
	call_back_function(result_data);

	if(close_popup)
	{
		window.close();
	}
}
