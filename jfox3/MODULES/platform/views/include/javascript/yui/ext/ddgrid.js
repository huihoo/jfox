YAHOO.ext.grid.DDGrid = function(){
    YAHOO.ext.grid.DDGrid.superclass.constructor.apply(this, arguments);
    this.enableDragDrop = true;
    this.on('dragdrop', this.onRowsDropped, this, true);
    // enables dropping rows from this grid on this grid
    // required for row ordering via D&D
    this.selfTarget = true;
    this.selectAfterDrop = true;
};

YAHOO.extendX(YAHOO.ext.grid.DDGrid, YAHOO.ext.grid.Grid, {
    render : function(){
        YAHOO.ext.grid.DDGrid.superclass.render.call(this);
        if(this.selfTarget){
            this.target = new YAHOO.util.DDTarget(this.container.id, 'GridDD');
        }
    },
    
    transferRows : function(indexes, targetGrid, targetRow, copy){
        var dm = this.dataModel;
        var targetDm = targetGrid.getDataModel();
        var rowData = dm.getRows(indexes);
        if(!copy){
            indexes.sort();
            for(var i = 0, len = indexes.length; i < len; i++) {
           	    dm.removeRow(indexes[i]-i);
            }
        }
        this.selModel.unlock();
        this.selModel.clearSelections();
        this.selModel.lock();
        var selStart;
        if(targetRow != null && targetRow < targetDm.getRowCount()){
            targetDm.insertRows(targetRow, rowData);
            selStart = targetRow;
        }else{
            targetDm.addRows(rowData);
            selStart = targetDm.getRowCount()-indexes.length;
        }
        if(this.selectAfterDrop){
            var sm = targetGrid.getSelectionModel();
            sm.unlock();
            sm.selectRange(selStart, selStart+indexes.length-1);
            sm.lock();
        }
    },
    
    getTargetRow : function(e){
        YAHOO.util.Event.stopEvent(e);
        var xy = YAHOO.util.Event.getXY(e);
        var cell = this.getView().getCellAtPoint(xy[0], xy[1]);
        return cell ? Math.max(0, cell[1]) : null;
    },
    
    onRowsDropped : function(grid, dd, id, e){
       if(id == this.id) { // only handle if it's a row order drop
           var indexes = this.getSelectedRowIndexes();
           this.transferRows(indexes, this, this.getTargetRow(e));
       }
    }
});
