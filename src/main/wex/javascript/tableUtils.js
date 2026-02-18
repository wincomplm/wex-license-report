var tableUtils = {

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

    getTableSettings: function () {
        return {
            "fnDrawCallback": function (oSettings) {
                $(".dataTable").parent().css({"white-space": "nowrap", "overflow-x": "auto"});
            },
            "ordering": true,
            "order": [[1, "asc"]],
            columns: [
                {name: 'id'},
                {name: 'Group Name'},
                {name: 'Total License Count'},
                {name: 'Members'},
                {name: 'Remaining License Count'},
                {name: 'Context'}
            ],
            columnDefs: [
                {
                    "targets": 0,
                    "visible": false,
                    "searchable": false
                },
                {
                    "targets": [1, 2, 3, 4, 5],
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
    },

    refreshTable: function (id) {
        tableUtils.populateTableData(id);
    },

    populateTableData: function (id) {
        tableUtils.clearTable(id);
        tableUtils.getLicenseData().then(function(data) {
            if (data && data.length > 0) {
                tableUtils.addTableData(id, data);
            }
        }).catch(function(error) {
            console.error("Error loading license data:", error);
        });
    },

    getLicenseData: function () {
        return new Promise(function(resolve, reject) {
            $.ajax({
                url: getUrl("api/getLicenseData"),
                method: "GET",
                dataType: "json",
                success: function(response) {
                    resolve(response || []);
                },
                error: function(xhr, status, error) {
                    console.error("AJAX error:", status, error);
                    reject(error);
                }
            });
        });
    }
};
