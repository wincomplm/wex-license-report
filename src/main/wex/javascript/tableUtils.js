var tableUtils = {

    addTemplateTableRow: function (id, rowData) {
        var dt = $("#" + id).DataTable();
        var colid = dt.column('Actions:name').index();
        var actions = rowData[colid];
        rowData[colid] = "";
        var row = dt.row.add(rowData);
        var rowInd = row.index();
        var node = dt.cell(rowInd, colid).node();
        $(node).append(actions);
        dt.draw();
        return row;
    },

    addTableData: function (id, data) {
        var dt = $("#" + id).DataTable();
        dt.clear();
        dt.rows.add(data).draw();
    },

    clearTable: function (id) {
        var dt = $("#" + id).DataTable();
        dt.clear();
        dt.draw();
    },

    clearTables: function (tableType) {
        $("div[type=" + tableType + "]").each(function (index) {
            var id = $(this).attr("table");
            this.clrearTable(id);
        });
    },

    getTablesData: function (type) {
        var tablesData = {};
        $("div[type=" + type + "]").each(function (index) {
            var id = $(this).attr("table");
            var dt = $("#" + id).DataTable();
            var tdata = dt.rows().data().toArray();
            tablesData[id] = tdata;
        });
        return tablesData;
    },

    getTableSettings: function () {
        return {
            "fnDrawCallback": function (oSettings) {
                $(".dataTable").parent().css({"white-space": "nowrap", "overflow-x": "auto"});
            },
            dom: 'Bfrtip',
            buttons: ['csv'],
            "ordering": true,
            "order": [[1, "asc"]],
            columns: [
                {name: 'id'},
                {name: 'Name'},
                {name: 'Group'}
            ],
            columnDefs: [
                {
                    "targets": 0,
                    "visible": false,
                    "searchable": false
                },
                {
                    "targets": [1, 2],
                    "visible": true,
                    "searchable": true
                }
            ]
        };
    },

    createTable: function (id) {
        var settings = tableUtils.getTableSettings();
        $("#" + id).DataTable(settings);
        tableUtils.populateTableData(id);
        tableUtils.addButtonActions(id);
    },

    refreshTable: function (id) {
        $("#" + id).DataTable().ajax.reload(null, false);
    },

    populateTableData: function (id) {
        tableUtils.clearTable(id);
//        getDataBlocks().then((dataBlocks) => {
//            for (var dataBlockId in dataBlocks) {
//                var dataBlock = dataBlocks[dataBlockId];
//                var blockType = dataBlock.type;
//                if (blockType === tableType) {
//                    var actions = tableUtils.getActions();
//                    var rowData = [dataBlock.id, dataBlock.name, dataBlock.description, actions];
//                    tableUtils.addTemplateTableRow(id, rowData);
//                }
//            }
//        });
    },
    
    getLicenseData: function () {
        
    }


};