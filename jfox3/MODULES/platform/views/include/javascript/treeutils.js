/**
 * 将resultList的值转化为Tree
 * @param treeDiv 显示树的div区域
 * @param resultList 域列表
 * @param addLabelEvent 增加label点击事件
 **/
function initYUITree(treeview, jsonDataList) {
    var dataList = eval(jsonDataList);

    for (var j = 0; j < dataList.length; j++) {
        var jsonObj = dataList[j];
        if (j == 0) {
            eval("var node_" + jsonObj.ID + " = new YAHOO.widget.TextNode(jsonObj.NAME, treeview.getRoot(), true);");
        }
        else {
            try {
                eval("var node_" + jsonObj.ID + "= new YAHOO.widget.TextNode(jsonObj.NAME, eval(node_" + jsonObj.PARENT_ID + "), true);");
            }
            catch(e) {
                eval("var node_" + jsonObj.ID + " = new YAHOO.widget.TextNode(jsonObj.NAME, treeview.getRoot(), false);");
            }
        }
    }
    treeview.draw();
}
