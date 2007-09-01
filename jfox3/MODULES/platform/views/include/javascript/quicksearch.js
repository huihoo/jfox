/*
* WICK: Web Input Completion Kit
* http://wick.sourceforge.net/
* Copyright (c) 2004, Christopher T. Holland
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* 
* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of the Christopher T. Holland, nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
* 
* Portions created by SugarCRM are Copyright (C) SugarCRM, Inc.

*/

/* start dhtml building blocks */
function freezeEvent(e) {
	if (e.preventDefault) e.preventDefault();
	e.returnValue = false;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return false;
}//freezeEvent

function isWithinNode(e,i,c,t,obj) {
	answer = false;
	te = e;
	while(te && !answer) {
		if((te.id && (te.id == i)) || (te.className && (te.className == i+"Class"))
				|| (!t && c && te.className && (te.className == c))
				|| (!t && c && te.className && (te.className.indexOf(c) != -1))
				|| (t && te.tagName && (te.tagName.toLowerCase() == t))
				|| (obj && (te == obj))
			) {
			answer = te;
		} else {
			te = te.parentNode;
		}
	}
	return te;
}//isWithinNode

function getEvent(event) {
	return (event ? event : window.event);
}//getEvent()

function getEventElement(e) {
	return (e.srcElement ? e.srcElement: (e.target ? e.target : e.currentTarget));
}//getEventElement()

function findElementPosX(obj) {
	curleft = 0;
	if (obj.offsetParent) {
		while (obj.offsetParent) {
			curleft += obj.offsetLeft;
			obj = obj.offsetParent;
		}
	}//if offsetParent exists
	else if (obj.x)
		curleft += obj.x
	return curleft;
}//findElementPosX

function findElementPosY(obj) {
	curtop = 0;
	if (obj.offsetParent) {
		while (obj.offsetParent) {
			curtop += obj.offsetTop;
			obj = obj.offsetParent;
		}
	}//if offsetParent exists
	else if (obj.y)
		curtop += obj.y
	return curtop;
}//findElementPosY

/* end dhtml building blocks */

function handleKeyPress(event) {
	e = getEvent(event);
	eL = getEventElement(e);
	sqs_tab_action = false;
	upEl = isWithinNode(eL,null,"sqsEnabled",null,null);
	sqs_id = eL["id"];
	
	kc = e["keyCode"];
	if(kc == 13 || kc == 9){
		if(sqs_lookup_active)return;
		sqs_tab_action = true;
	}
	if (siw && ((kc == 13) || (kc == 9))) {
		siw.selectingSomething = true;
		if (siw.isSafari) inputBox.blur();   //hack to "wake up" safari
		try { siw.inputBox.focus(); }
		catch(e) { return; }
//		siw.inputBox.value = siw.inputBox.value;//.replace(/[ \r\n\t\f\s]+$/gi,' ');
		hideSmartInputFloater(true);
	} else if (upEl && (kc != 38) && (kc != 40) && (kc != 37) && (kc != 39) && (kc != 13) && (kc != 27)) {
		if (!siw || (siw && !siw.selectingSomething)) {
			handleInput(event);
		}
	} else if (siw && siw.inputBox) {
		siw.internalCall = true;
		siw.inputBox.focus(); //kinda part of the hack.
	}
}//handleKeyPress()

// handle any actual user action that will result in displaying results
function handleInput(event) {
	e = getEvent(event);
	eL = getEventElement(e);
	upEl = isWithinNode(eL,null,"sqsEnabled",null,null);
	if(siw && upEl.value == ''){
		hideSmartInputFloater(false);
	}
		
	
	sqs_id = eL["id"];
	if(sqs_id != 'debug' && sqs_id != 'left' && sqs_id != '' && typeof(sqs_objects) != 'undefined') {
		if(typeof(sqs_objects[sqs_id]['disable']) != 'undefined' && sqs_objects[sqs_id]['disable'] == true) return; // if sqs is disabled
		if(typeof(sqs_old_values) && typeof(sqs_old_values[sqs_id]) == 'undefined') {
			sqs_old_values[sqs_id] = '';
		}
		makeRPCCall(upEl, sqs_id, rpc_timeout);	
	}
}

function handleOnPropertyChange(event) {
	if(!from_popup_return) handleInput(event);
}

function doCall( sqs_id, sqs_query, value){
	if(value != upEl.value || value == '')return;
	if (typeof(window['callDelay']) != "undefined" ) window.clearTimeout(callDelay);
	if(value.length < sqs_min_length)return;
				// wait gif
				x = findElementPosX(upEl) - 19;
				y = findElementPosY(upEl);
				floaterWait.style.left = x;
				floaterWait.style.top = y;
				floaterWait.style.display="block";
				floaterWait.style.visibility="visible";	

				for(var i = 0; i < sqs_objects[sqs_id]['conditions'].length; i++) {
					if(typeof(sqs_objects[sqs_id]['conditions'][i]['source']) != 'undefined'){
						src_elem = document.getElementById(sqs_objects[sqs_id]['conditions'][i]['source']);
						if (typeof(src_elem)!='undefined' && src_elem.value != ''){
							sqs_objects[sqs_id]['conditions'][i]['value'] = JSON.stringifyNoSecurity(src_elem.value);
						}
					}else{
						sqs_objects[sqs_id]['conditions'][i]['value'] = JSON.stringifyNoSecurity(sqs_query);
					}
				}
				
				postData = 'data=' + JSON.stringifyNoSecurity(sqs_objects[sqs_id]) + '&module=Home&action=quicksearchQuery&to_pdf=1';
				sqs_request_id++;
				sqs_last_query = sqs_query;
				//hideSmartInputFloater(false);
				sqs_query = sqs_last_query;
				sqs_lookup_active = true;
				YAHOO.util.Connect.asyncRequest('POST', 'index.php', {success: SugarQuickSearchObject.display, failure: SugarQuickSearchObject.fail, argument:{id: sqs_request_id, query: sqs_query}}, postData);
				

}

function makeRPCCall(upEl, sqs_id, delay) {
	if(sqs_tab_action)return;
	if(typeof(sqs_objects[sqs_id]) != 'undefined') {
		
		if(sqs_objects[sqs_id]['multi']) 
			sqs_query = getUserInputToMatch(document.getElementById(sqs_id).value);
		else 
			sqs_query = document.getElementById(sqs_id).value;
		
		if(typeof(siw) != 'undefined' && sqs_query == '') hideSmartInputFloater(false);
		
		sqs_query = sqs_query.replace(/\\/gi,'').replace(/\[/gi,'').replace(/\(/gi,'').replace(/\./gi,'\.').replace(/\?/gi,'');
		
		sqs_query = sqs_query.replace(/[()]+/g,'');
		
		
		if (sqs_query.length > 0) {
			
			if(sqs_query.length > sqs_last_query.length &&  sqs_old_values[sqs_id].length > 0 && sqs_query.indexOf(sqs_old_values[sqs_id]) == 0
							&& typeof(collection) != 'undefined' && collection.length > 0 && collection.length < sqs_objects[sqs_id]['limit']) { // don't make an RPC call, use cache
				
				processSmartInput(upEl, sqs_id);
			}
			else{
				hideSmartInputFloater(false);
				clearSQSLookupData(false);
				clearPopulateList(upEl.id, sqs_id);
				if (typeof(window['callDelay']) != "undefined" ) window.clearTimeout(callDelay);
				
				callDelay = window.setTimeout('doCall(sqs_id, sqs_query,upEl.value)', delay);	
			}
		}
		
		sqs_old_values[sqs_id] = sqs_query;
		sqs_object_id = sqs_id;
	}
}


// SugarCRM

function handleKeyDown(event) {
	e = getEvent(event);
	eL = getEventElement(e);
	sqs_tab_action = false;
	if (siw && (kc = e["keyCode"])) {
		if (kc == 40) { // down arrow
			siw.selectingSomething = true;
			freezeEvent(e);
			if (siw.isGecko) siw.inputBox.blur(); /* Gecko hack */
			selectNextSmartInputMatchItem();
		} else if (kc == 38) { // up arrow
			siw.selectingSomething = true;
			freezeEvent(e);
			if (siw.isGecko) siw.inputBox.blur();
			selectPreviousSmartInputMatchItem();
		} else if ((kc == 13) || (kc == 9)) { // tab or enter
			siw.selectingSomething = true;
		    sqs_tab_action = true;
			activateCurrentSmartInputMatch();
			freezeEvent(e);
		} else if (kc == 27)  {
			hideSmartInputFloater(false);
			freezeEvent(e);
		} else { // everything else
			siw.selectingSomething = false;
		}
		try{siw.internalCall = true; siw.inputBox.focus();} catch(e) {return;} // top scrolling in firefox for down/up keys
	}
}//handleKeyDown()

function handleFocus(event) {
	e = getEvent(event);
	eL = getEventElement(e);
	sqs_id = eL["id"];
	if(sqs_focus_id != '' && sqs_id != '' && sqs_id != sqs_focus_id){
		document.getElementById(sqs_focus_id).focus();
		return;
	}
	sqs_focus_id = '';
	if(siw && siw.internalCall) {
		siw.internalCall = false;
		return;
	}
	sqs_tab_action=true;
	from_popup_return = false;
	
	
	if (focEl = isWithinNode(eL,null,"sqsEnabled",null,null)) {
		if (typeof(siw) != 'undefined' && (!siw || (siw && !siw.selectingSomething))) {
			sqs_old_values[sqs_id] = ''
			sqs_original_value = eL.value;
			handleInput(event);
		}
	}
}//handleFocus()

function handleBlur(event) {
	if(sqs_id != '' && sqs_focus_id != '' && sqs_id != sqs_focus_id){
		document.getElementById(sqs_focus_id).focus();
		return;
	}
	sqs_focus_id = '';
	e = getEvent(event);
	eL = getEventElement(e);
	if (blurEl = isWithinNode(eL,null,"sqsEnabled",null,null)) {
		sqs_original_value = '';
		toggleMultis(eL['id'], false);
		id = eL['id'];
		if(sqs_must_match && eL.value != '' && sqs_id != '' && (!siw || (siw.selectingSomething && sqs_tab_action || !siw.selectingSomething))) {
			failBlur = !sqsFilledOut(sqs_id);			
			if(failBlur){
				sqs_focus_id = sqs_id;
				window.setTimeout('handleUnBlur(id)', 10);
			
			}else{
				
				clearSQSLookupData(true);
			}
		}else{
			if (typeof(window['callDelay']) != "undefined" ) window.clearTimeout(callDelay);
		}
	}
}//handleBlur()

function sqsFilledOut(sqs_id){
	filledOut = true;
	if(typeof sqs_objects[sqs_id] != 'undefined'){
			for(i in sqs_objects[sqs_id]['populate_list']){
				j =sqs_objects[sqs_id]['populate_list'][i];
				if(document.getElementById(j).value == ''){
					filledOut = false;
				}
			}
			}
	return filledOut;
}
function handleUnBlur(id){
if(sqs_focus_id == '')return;
if(typeof(win) != 'undefined' && win.closed != true){
	document.getElementById(id).value = '';
	hideSmartInputFloater(true);
	clearPopulateList(id,id);
	clearSQSLookupData(true);
}
	document.getElementById(id).focus();
	sqs_tab_action = false;
	if (typeof(window['callDelay']) != "undefined"  && sqs_query != '' && sqs_query != sqs_last_query ){
			doCall(sqs_id, sqs_query, upEl.value);
	}
}
function clearSQSLookupData(full){
	if(full){
		sqs_last_query = '';
		sqs_query = '';
	}
	if(siw){
		siw.matchCollection = new Array();
		
	}
	sqs_original_value = '';
	collection = new Array();
	collection_extended = new Array();
}
function handleClick(event) {
	e2 = getEvent(event);
	eL2 = getEventElement(e2);
	toggleMultis(eL2['id'], true);
	if (siw && siw.selectingSomething) {
		selectFromMouseClick();
	}
}//handleClick()

function handleMouseOver(event) {
	e = getEvent(event);
	eL = getEventElement(e);
	if (siw && (mEl = isWithinNode(eL,null,"sqsMatchedSmartInputItem",null,null))) {
		siw.selectingSomething = true;
		selectFromMouseOver(mEl);
	} else if (isWithinNode(eL,null,"siwCredit",null,null)) {
		siw.selectingSomething = true;
	}else if (siw) {
		siw.selectingSomething = false;
	}
}//handleMouseOver

function showSmartInputFloater() {
if (!siw) siw = new smartInputWindow(); // moved to call
if (!siw.floater.style.display || (siw.floater.style.display=="none")) {
	if (!siw.customFloater) {
		x = findElementPosX(siw.inputBox);
		y = findElementPosY(siw.inputBox) + siw.inputBox.offsetHeight;
		//hack: browser-specific adjustments.
		if (!siw.isGecko && !siw.isWinIE) x += 8;
		if (!siw.isGecko && !siw.isWinIE) y += 10;
		siw.floater.style.left = x;
		siw.floater.style.top = y;
	} else {
	}
	siw.floater.style.display="block";
	siw.floater.style.visibility="visible";
	if(siw.isWinIE) {
		siw.floaterIframe.style.display="block";
		siw.floaterIframe.style.visibility="visible";	
		resizeSmartInputIframe();
	}
	siw.floater.style.opacity = 1;
	siw.floater.style.filter = 'alpha(opacity=100)';
	siw.floater.getElementsByTagName('td')[0].getElementsByTagName('div')[0].style.opacity = 1;
	siw.floater.getElementsByTagName('td')[0].getElementsByTagName('div')[0].style.filter = 'alpha(opacity=100)';
	siw.floaterIframe.style.opacity = 1;
	siw.floaterIframe.style.filter = 'alpha(opacity=100)';
}
}//showSmartInputFloater()

function clearPopulateList(id, sqs_id){
		if(sqs_tab_action)return;
		for(j = 1; j < sqs_objects[sqs_id]['populate_list'].length; j++) {
			if(id != sqs_objects[sqs_id]['populate_list'][j]){
				document.getElementById(sqs_objects[sqs_id]['populate_list'][j]).value = '';
			}
		}
}
function hideSmartInputFloater(selected) {
	postblur = false;
	if (siw) {
		siw.blurring = true;
		if(typeof(callDelay) != 'undefined') window.clearTimeout(callDelay); // stop the last AJAX call
		if(typeof(sqs_object_id) != 'undefined') {
			user_input = document.getElementById(sqs_object_id).value;
			var multi = false;
			if(selected && typeof sqs_objects[sqs_object_id]['multi'] != 'undefined' && sqs_objects[sqs_object_id]['multi']) { // handle multiselect quicksearches
				if(typeof sqs_objects[sqs_object_id]['multi_populate_list'] == 'undefined') 
					sqs_objects[sqs_object_id]['multi_populate_list'] = new Array();
				if(typeof sqs_objects[sqs_object_id]['multi_populate_list'][user_input] == 'undefined' || sqs_objects[sqs_object_id]['multi_populate_list'][user_input] == null) 
					sqs_objects[sqs_object_id]['multi_populate_list'][user_input] = new Array();
				sqs_objects[sqs_object_id]['multi_populate_list'][user_input][document.getElementById(sqs_object_id).name] = user_input;
				multi = true;
			}
			
			for(j = 1; j < sqs_objects[sqs_object_id]['populate_list'].length; j++) {
				
				if(typeof(collection_extended) != 'undefined' && typeof(sqs_objects) != 'undefined' && typeof(collection_extended[user_input]) != 'undefined' 
							&& typeof(collection_extended[user_input][sqs_objects[sqs_object_id]['field_list'][j]]) != 'undefined') {
								if(multi) { // handle multiselect quicksearches
									sqs_objects[sqs_object_id]['multi_populate_list'][user_input][sqs_objects[sqs_object_id]['populate_list'][j]] = 
										collection_extended[user_input][sqs_objects[sqs_object_id]['field_list'][j]];
										
								}
								else {
									document.getElementById(sqs_objects[sqs_object_id]['populate_list'][j]).value = 
											collection_extended[user_input][sqs_objects[sqs_object_id]['field_list'][j]];
						
								}
								postblur = true;
				}
				else {
					document.getElementById(sqs_objects[sqs_object_id]['populate_list'][j]).value = '';
				}
			}
			
			if(multi) {
				sqsCreateSpans(sqs_object_id);
				document.getElementById(sqs_object_id).value = '';
			}
			//	collection = new Array(); // taken out to avoid clearing list when using arrow keys
			if(typeof(sqs_objects[sqs_object_id]['post_onblur_function']) != 'undefined' && postblur) {
				eval(sqs_objects[sqs_object_id]['post_onblur_function'] + '(collection_extended[user_input], sqs_object_id)');
			}
		}
	
		sqs_query = '';
		// fade effect
		theDiv = siw.floaterContent.getElementsByTagName('div')[0];
		
		if((selected && user_input != '' && sqsFilledOut(sqs_object_id)) || user_input == ''  || sqs_query.indexOf(sqs_last_query) != 0){
			hideSmartInputFloaterComplete();
		}
	}//siw exists
}//hideSmartInputFloater

function sqsCreateSpans(sqs_object_id) {
	multi_populate_el = document.getElementById(sqs_objects[sqs_object_id]['multi_populate']);
	multi_populate_el.innerHTML = ''; // clear it out and rebuild
	list = new Array();
	countWP = 0;
	for(wp in sqs_objects[sqs_object_id]['multi_populate_list']) {
		if(sqs_objects[sqs_object_id]['multi_populate_list'][wp] != null && wp != '') {
			countWP++;
			span = '<a href="#" onclick="return sqsRemove(\'' + wp + '\', \'' + sqs_object_id + '\' )">' + wp;
			for(field in sqs_objects[sqs_object_id]['multi_populate_list'][wp]) {
				span += '<input type="hidden" name="' + field + '[]" value="' + sqs_objects[sqs_object_id]['multi_populate_list'][wp][field] + '">';
			}
			span += '</a>';
			list.push(span);
//			multi_populate_el.innerHTML += wp;
		}
	}

	multi_populate_el.innerHTML = '<span>' + countWP + ' selected items</span>'
							+ '<div style="height: 20px;">'
							+ '<div style="padding: 2px 2px 2px 2px; border: 1px black solid; background: #fff; overflow: auto; width: 100px; height: 100px; position: absolute;" id="' + sqs_object_id + '_info">' + list.join(',<br>') + '</div></div>';
	toggleMultis(sqs_object_id, true);
}

// show or hide the list of multi selects
function toggleMultis(sqs_object_id, show) {
	multi_div = document.getElementById(sqs_object_id + '_info');
	if(multi_div && show) { // show it
		multi_div.style.height = '100px';
		multi_div.style.width = '100px';
	}
	if(multi_div && !show) { // hide it
		multi_div.style.height = '25px';
		multi_div.style.width = '100px';	
	}
}

function sqsRemove(item, sqs_object_id) {
	sqs_objects[sqs_object_id]['multi_populate_list'][item] = null;
	sqsCreateSpans(sqs_object_id);
	return false;
}

function hideSmartInputFloaterComplete() {
	if (siw) {
		siw.floater.style.display="none";
		siw.floater.style.visibility="hidden";
		siw.floaterIframe.style.display="none";
		siw.floaterIframe.style.visibility="hidden";
		siw.blurring = false;
		siw = null;
	}
}

function setOpacity(value, theDiv) {
	if (siw && theDiv) {
		newOpacity = 100 - value * 20; 
		siw.floater.style.opacity = 1 - value / 20;
		siw.floater.style.filter = 'alpha(opacity=' + newOpacity + ')';
		theDiv.style.opacity = 1 - value / 20;
		theDiv.style.filter = 'alpha(opacity=' + newOpacity + ')';
		siw.floaterIframe.style.opacity = 1 - value / 20;
		siw.floaterIframe.style.filter = 'alpha(opacity=' + newOpacity + ')';
	}
}

function processSmartInput(inputBox, sqs_id) {
	if (!siw) siw = new smartInputWindow();
	siw.inputBox = inputBox;
	
	try { classData = inputBox.className.split(" "); }
	catch(e) {return;}
	
	siwDirectives = null;
	for (i=0;(!siwDirectives && classData[i]);i++) {
		if (classData[i].indexOf("sqsEnabled") != -1)
			siwDirectives = classData[i];
	}
	
	if (siwDirectives && (siwDirectives.indexOf(":") != -1)) {
		siw.customFloater = true;
		newFloaterId = siwDirectives.split(":")[1];
		siw.floater = document.getElementById(newFloaterId);
		siw.floaterContent = siw.floater.getElementsByTagName("div")[0];
	}
	
	//********************8
	setSmartInputData();
	
	//*********************
	if (siw.matchCollection && (siw.matchCollection.length > 0)) selectSmartInputMatchItem(0);
	
	//*********************
	content = getSmartInputBoxContent();
	
//	alert('content: ' + content);
	if (content) {
		modifySmartInputBoxContent(content);
		showSmartInputFloater();
	} else {
		// display no match
		
		if(typeof(sqs_query) != "undefined" && sqs_query.length > 0 && (typeof(sqs_objects[sqs_id]['disable']) == 'undefined' || sqs_objects[sqs_id]['disable'] == false)) {
				//if(sqs_query.indexOf(sqs_last_query) != 0){
					//hideSmartInputFloater(true);
				//}else{
					modifySmartInputBoxContent('<div class="sqsNoMatch">' + sqs_objects[sqs_id]['no_match_text'] + '</div>');
					showSmartInputFloater();
				//}
			}
	}
}//processSmartInput()

function smartInputMatch(cleanValue, value) {
	this.cleanValue = cleanValue;
	this.value = value;
	this.isSelected = false;
}//smartInputMatch

function simplify(s) {
	var unicode = '';
	var badChars = new Object();
	badChars['('] = 1;
	badChars[')'] = 1;	
	
	for(var i=0; i<s.length; i++) {
		var tempChar = '';
		if(badChars[s.charAt(i)] != undefined){
			continue;
		} else if(s.charCodeAt(i) > 128) {
			tempChar = s.charCodeAt(i).toString(16);
			while(tempChar.length < 4) {
				tempChar = "0" + tempChar;
			}
			tempChar = "\\u" + tempChar;
		} else {
			tempChar = s.charAt(i);
		}
		unicode += tempChar;
	}
	// cn: removed regex as MBCS was confusing it.
	return unicode;
}//simplify

function getUserInputToMatch(s) {
	a = s;
	fields = s.split(";");
	if (fields.length > 0) a = fields[fields.length - 1];
	return a; //.replace(/[()]+/g,'');
}//getUserInputToMatch

function getUserInputBase() {
	try {
		s = siw.inputBox.value;
	}
	catch(e) { return; }
	a = s;
	if ((lastComma = s.lastIndexOf(";")) != -1) {
		a = a.replace(/^(.*\;[ \r\n\t\f\s]*).*$/i,'$1');
	}
	else
		a = "";
	return a;
}//getUserInputBase()

function runMatchingLogic(userInput, standalone) {
	userInput = simplify(userInput);
	uifc = userInput.charAt(0).toLowerCase();
	if (uifc == '"') uifc = (n = userInput.charAt(1)) ? n.toLowerCase() : "z";
	if (standalone) userInput = uifc;
	if (siw) siw.matchCollection = new Array();
	if (typeof(collection) == "undefined" ) return;
	pointerToCollectionToUse = collection;
	
//alert("colletion[0]: " + collection[0]); // garbled
	
	if (siw && (userInput.length == 1) && (!collectionIndex[uifc])) {
		siw.buildIndex = true;
	} else if (siw) {
		siw.buildIndex = false;
	}
	
	tempCollection = new Array();

//alert("userInput: " + userInput);
	re1m = new RegExp("^([\(\) \"\>\<\-]*)("+userInput+")","i");
	re2m = new RegExp("([ \"\>\<\-]+)("+userInput+")","i");
	re1 = new RegExp("^([\(\) \"\}\{\-]*)("+userInput+")","gi");
	re2 = new RegExp("([ \"\}\{\-]+)("+userInput+")","gi");
	for (i=0,j=0;(i<pointerToCollectionToUse.length);i++) {
		displayMatches = ((!standalone) && (j < siw.MAX_MATCHES));
		entry = pointerToCollectionToUse[i];
		mEntry = simplify(entry);
		mEntry = entry;
		

			// cn: ensures we get a full list regardless
//			siw.matchCollection[i] = new smartInputMatch(entry, entry);


		if (!standalone && (mEntry.indexOf(userInput) == 0)) {
			userInput = userInput.replace(/\>/gi,'\\}').replace(/\< ?/gi,'\\{');
			re = new RegExp("(" + userInput + ")","i");
			if (displayMatches) {
				siw.matchCollection[j] = new smartInputMatch(entry, mEntry.replace(/\>/gi,'}').replace(/\< ?/gi,'{').replace(re,"<b>$1</b>"));
			}
			
			tempCollection[j] = entry;
			j++;		
		} else if (mEntry.match(re1m)) {
			if (!standalone && displayMatches) {
				siw.matchCollection[j] = new smartInputMatch(entry, mEntry.replace(/\>/gi,'}').replace(/\</gi,'{').replace(re1,"$1<b>$2</b>"));
			}
			tempCollection[j] = entry;
			j++;
		}
	}//loop thru collection
	if (siw) {
		siw.lastUserInput = userInput;
		siw.revisedCollection = tempCollection.join(",").split(",");
		collectionIndex[userInput] = tempCollection.join(",").split(",");
	}
	if (standalone || siw.buildIndex) {
		collectionIndex[uifc] = tempCollection.join(",").split(",");
		if (siw) siw.buildIndex = false;
	}
}//runMatchingLogic

function setSmartInputData() {
	if (typeof(siw) != 'undefined') {
		if (siw) {
			orgUserInput = siw.inputBox.value;
			if(typeof(sqs_objects) != 'undefined' && typeof(sqs_id) != 'undefined' && typeof(sqs_objects[sqs_id]) != 'undefined' 
						&& sqs_objects[sqs_id]['multi']) orgUserInput = getUserInputToMatch(orgUserInput);
			userInput = orgUserInput;//.replace(/\(/gi,'').replace(/\)/gi,'').replace(/\\/gi,'').replace(/\[/gi,'').replace(/\./gi,'\.').replace(/\?/gi,'');
//			alert("userInput: " + userInput);
			
			if (userInput && (userInput != "") && (userInput != '"')) {
				// ***************************
				runMatchingLogic(userInput);
			}//if userinput not blank and is meaningful
			else {
				siw.matchCollection = null;
			}
		}//siw exists ... uhmkaaayyyyy
	}
}//setSmartInputData

function getSmartInputBoxContent() {
	a = null;

//		alert("siw: " + siw.matchCollection.length); //3

	if (siw && siw.matchCollection && (siw.matchCollection.length > 0)) {
		a = '';
		
		
		for (i = 0;i < siw.matchCollection.length; i++) {
			selectedString = siw.matchCollection[i].isSelected ? ' sqsSelectedSmartInputItem' : '';
			a += '<p class="sqsMatchedSmartInputItem' + selectedString + '">' + siw.matchCollection[i].value.replace(/\{ */gi,"&lt;").replace(/\} */gi,"&gt;") + '</p>';
		}//
	}//siw exists
	return a;
}//getSmartInputBoxContent

function modifySmartInputBoxContent(content) {
	if(!siw.blurring) {
		siw.floaterContent.innerHTML = '<div id="sqsSmartInputResults">' + content +'</div>';
		resizeSmartInputIframe();
		siw.matchListDisplay = document.getElementById("sqsSmartInputResults");
	}
}//modifySmartInputBoxContent()

function selectFromMouseOver(o) {
	currentIndex = getCurrentlySelectedSmartInputItem();
	if (currentIndex != null) deSelectSmartInputMatchItem(currentIndex);
	newIndex = getIndexFromElement(o);
	selectSmartInputMatchItem(newIndex);
	modifySmartInputBoxContent(getSmartInputBoxContent());
}//selectFromMouseOver

function selectFromMouseClick() {
	activateCurrentSmartInputMatch();
	siw.inputBox.focus();
	hideSmartInputFloater(true);
}//selectFromMouseClick

function getIndexFromElement(o) {
	index = 0;
	while(o = o.previousSibling) {
	index++;
	}//
	return index;
}//getIndexFromElement

function getCurrentlySelectedSmartInputItem() {
	answer = null;
	if(typeof(siw.matchCollection) != 'undefined' && typeof(siw.matchCollection.length) != 'undefined') {
		for (i = 0; ((i < siw.matchCollection.length) && !answer) ; i++) {
			if (siw.matchCollection[i].isSelected)
				answer = i;
		}
	}
	return answer;
}//getCurrentlySelectedSmartInputItem

function selectSmartInputMatchItem(index) {
	if(typeof(siw.matchCollection[index]) != 'undefined') 
		siw.matchCollection[index].isSelected = true;
}//selectSmartInputMatchItem()

function deSelectSmartInputMatchItem(index) {
	siw.matchCollection[index].isSelected = false;
}//deSelectSmartInputMatchItem()

function selectNextSmartInputMatchItem() {
	currentIndex = getCurrentlySelectedSmartInputItem();
	if (currentIndex != null) {
		deSelectSmartInputMatchItem(currentIndex);
		if ((currentIndex + 1) < siw.matchCollection.length)
	 		selectSmartInputMatchItem(currentIndex + 1);
		else
			selectSmartInputMatchItem(0);
	} else {
		selectSmartInputMatchItem(0);
	}
	modifySmartInputBoxContent(getSmartInputBoxContent());
}//selectNextSmartInputMatchItem

function selectPreviousSmartInputMatchItem() {
	currentIndex = getCurrentlySelectedSmartInputItem();
	if (currentIndex != null) {
		deSelectSmartInputMatchItem(currentIndex);
		if ((currentIndex - 1) >= 0)
	 		selectSmartInputMatchItem(currentIndex - 1);
		else
			selectSmartInputMatchItem(siw.matchCollection.length - 1);
	} else {
		selectSmartInputMatchItem(siw.matchCollection.length - 1);
	}
	modifySmartInputBoxContent(getSmartInputBoxContent());
}//selectPreviousSmartInputMatchItem

var selIndex;

function activateCurrentSmartInputMatch() {
	baseValue = getUserInputBase();
	if ((selIndex = getCurrentlySelectedSmartInputItem()) != null) {
		addedValue = siw.matchCollection[selIndex].cleanValue;
		theString = (baseValue ? baseValue : "") + addedValue;
		siw.inputBox.value = theString;
		runMatchingLogic(addedValue, true);		
		// fill other fields in populate_list
	}
}//activateCurrentSmartInputMatch

function smartInputWindow () {
	this.originalInput = '';
	this.customFloater = false;
	this.floater = document.getElementById("smartInputFloater");
	this.floaterContent = document.getElementById("smartInputFloaterContent");
	this.selectedSmartInputItem = null;
	this.MAX_MATCHES = 12;
	this.isGecko = (navigator.userAgent.indexOf("Gecko/200") != -1);
	this.isSafari = (navigator.userAgent.indexOf("Safari") != -1);
	this.isWinIE = ((navigator.userAgent.indexOf("Win") != -1 ) && (navigator.userAgent.indexOf("MSIE") != -1 ));
	this.floaterIframe = document.getElementById("smartInputFloaterIframe");
	this.blurring = false; // is siw blurring
	this.internalCall = false; // internally calling focus/blur events
}//smartInputWindow Object

function resizeSmartInputIframe() { // to adjust iframe
//hack: for some reason IE sometimes doesn't see the CSS z-index
	if (siw.floater.style.zIndex <= 0) siw.floater.style.zIndex = 3;
	//show Iframe, which can float over SELECTs, but below floater, so they both go on top of SELECT elements in IE
	siw.floaterIframe.style.width = siw.floater.offsetWidth;
	siw.floaterIframe.style.height = siw.floater.offsetHeight;
	siw.floaterIframe.style.top = siw.floater.style.top;
	siw.floaterIframe.style.left = siw.floater.style.left;
	siw.floaterIframe.style.zIndex = siw.floater.style.zIndex - 2;
}//resizeSmartInputIframe()

function registerSmartInputListeners() {
	inputs = document.getElementsByTagName("input");
	texts = document.getElementsByTagName("textarea");
	allinputs = new Array();
	z = 0;
	y = 0;
	while(inputs[z]) {
		allinputs[z] = inputs[z];
		z++;
	}//
	while(texts[y]) {
		allinputs[z] = texts[y];
		z++;
		y++;
	}//
	
	for (i=0; i < allinputs.length;i++) {
		if ((c = allinputs[i].className) && (c.indexOf("sqsEnabled") != -1)) {
			allinputs[i].setAttribute("autocomplete","OFF");
			allinputs[i].onfocus = handleFocus;
			allinputs[i].onblur = handleBlur;
			allinputs[i].onkeydown = handleKeyDown;
			allinputs[i].onkeyup = handleKeyPress;
//			allinputs[i].onpropertychange = handleOnPropertyChange;
			if(allinputs[i].addEventListener)
			    allinputs[i].addEventListener("input", handleOnPropertyChange, true); 
		}
	}//loop thru inputs
}//registerSmartInputListeners

siw = null;

if (document.addEventListener) {
	document.addEventListener("keydown", handleKeyDown, false);
	document.addEventListener("keyup", handleKeyPress, false);
	document.addEventListener("click", handleClick, false);
	document.addEventListener("mouseover", handleMouseOver, false);
//	document.addEventListener("focus", handleFocus, false);
} else {
	document.onkeydown = handleKeyDown;
	document.onkeyup = handleKeyPress;
	document.onmouseup = handleClick;
	document.onmouseover = handleMouseOver;
//	document.onfocus = handleFocus;
}

// call if your input is generated with dhtml
function registerSingleSmartInputListener(input) {
	if ((c = input.className) && (c.indexOf("sqsEnabled") != -1)) {
		input.setAttribute("autocomplete","OFF");
		input.onfocus = handleFocus;
		input.onblur = handleBlur;
		input.onkeydown = handleKeyDown;
		input.onkeyup = handleKeyPress;
//		input.onpropertychange = handleOnPropertyChange;
		if(input.addEventListener)
		    input.addEventListener("input", handleOnPropertyChange, true); 
	}
}//registerSingleSmartInputListener

YAHOO.util.Event.addListener(window, "load", registerSmartInputListeners);
  
document.write(
'<table id="smartInputFloater" class="sqsFloater" cellpadding="0" cellspacing="0"><tr><td id="smartInputFloaterContent" nowrap="nowrap">'
+'<\/td><\/tr><\/table>' + '<iframe id="smartInputFloaterIframe" class="sqsFloater" name="smartInputFloaterIframeName" src="javascript:false;" scrolling="no" frameborder="0"></iframe>' +
'<div class="sqsFloater" id="smartInputFloaterWait"><img src="' + sqsWaitGif + '"></div>'
);

collectionIndex = new Array();
collection_extended = new Array();

// SugarCRM
function sqs_checkForm() {
	answer = true;
	if (siw && siw.selectingSomething)
		answer = false;
	return answer;
}

var sqs_object_id;
var sqs_focus_id = '';
var sqs_original_value = '';
var rpc_timeout = 500;
var floaterWait = document.getElementById("smartInputFloaterWait");
var from_popup_return = false;
var sqs_request_id = 0;
var sqs_min_length = 1;
var sqs_must_match = true;
sqs_old_values = new Array();
var sqs_last_query = '';
var sqs_tab_action = false;
var sqs_lookup_active = false;
function SugarQS() { }
// failed call
SugarQS.prototype.fail = function(result) {

}

// successful call
SugarQS.prototype.display = function(result) {
	if (typeof(result) == 'undefined')
		return;
	floaterWait.style.display="none";
	floaterWait.style.visibility="hidden";
	if(sqs_lookup_active && result.argument['id'] == sqs_request_id)sqs_lookup_active = false;
	if(upEl.value == '' || result.argument['id'] != sqs_request_id || (result.argument['query'] != sqs_query && sqs_query != '')){	
		return;
	}
	names = eval(result.responseText);

	collection = new Array();
	collection_extended = new Array();

	for(i = 0; i < names.length; i++) { 
		escaped_name = names[i].fields[sqs_objects[sqs_id].field_list[0]].replace(/&amp;/gi,'&').replace(/&lt;/gi,'<').replace(/&gt;/gi,'>').replace(/&#039;/gi,'\'').replace(/&quot;/gi,'"');
		
		collection[i] = escaped_name; 

		collection_extended[escaped_name] = new Array();


		for(j = 0; j < sqs_objects[sqs_id]['field_list'].length; j++) { // fill in populate list
			collection_extended[escaped_name][sqs_objects[sqs_id]['field_list'][j]] = names[i].fields[sqs_objects[sqs_id].field_list[j]];
		}
	}
	
		
	processSmartInput(upEl, sqs_id);
}
	
SugarQuickSearchObject = new SugarQS();
if(typeof(sqs_objects) == 'undefined') sqs_objects = new Array();
// END SugarCRM
