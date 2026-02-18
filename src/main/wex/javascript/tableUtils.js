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

    // NEW: Build the Info Page URL for a WTGroup OID, instance-agnostic
    getGroupUrl: function (oid) {
        // window.location.pathname example in Windchill UI:
        //   /Windchill/netmarkets/jsp/com/wincomplm/wex/license/report/yourPage.jsp
        //
        // We want the context root: "Windchill" (2nd segment)
        var path = window.location.pathname || "/";
        var parts = path.split("/");  // ["", "Windchill", "netmarkets", "jsp", ...]
        var contextRoot = parts.length > 1 && parts[1] ? parts[1] : "Windchill";

        // Standard Info Page pattern from CS143441:
        //   /<contextRoot>/app/#ptc1/tcomp/infoPage?oid=<encoded OID>&u8=1
        var base = "/" + contextRoot + "/app/#ptc1/tcomp/infoPage";
        var url = base + "?oid=" + encodeURIComponent(oid) + "&u8=1";

        return url;
    },

    getTableSettings: function () {
        return {
            "fnDrawCallback": function (oSettings) {
                $(".dataTable").parent().css({
                    "white-space": "nowrap",
                    "overflow-x": "auto"
                });
            },
            "ordering": true,
            "order": [[1, "asc"]],
            "lengthMenu": [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
            "pageLength": -1,

            columns: [
                { name: 'id' },                    // row[0] – OID from LicenseGroupData [1
                { name: 'Group Name' },            // row[1]
                { name: 'Total License Count' },   // row[2]
                { name: 'Members' },               // row[3]
                { name: 'Remaining License Count'},// row[4]
                { name: 'Context' }                // row[5]
            ],
            columnDefs: [
                {
                    "targets": 0,
                    "visible": false,
                    "searchable": false
                },
                // Group Name column -> hyperlink to Info Page
                {
                    "targets": 1,
                    "visible": true,
                    "searchable": true,
                    "render": function (data, type, row, meta) {
                        // data  = groupName (row[1])
                        // row[0] = id (WTGroup OID string) from LicenseGroupData 
                        // For sorting & filtering, return plain text
                        if (type === 'sort' || type === 'type' || type === 'filter') {
                            return data;
                        }

                        var oid = row[0];
                        var url = tableUtils.getGroupUrl(oid);

                        // Basic HTML escaping for the link text
                        var safeText = String(data)
                            .replace(/&/g, "&amp;")
                            .replace(/</g, "&lt;")
                            .replace(/>/g, "&gt;")
                            .replace(/"/g, "&quot;")
                            .replace(/'/g, "&#39;");

                        // ⭐ NEW: target="_blank" opens link in a new tab
                        // ⭐ NEW: rel="noopener noreferrer" is best practice to prevent tab hijacking
                        
                        return '<a href="' + url + '" target="_blank" rel="noopener noreferrer">' 
                               + safeText + '</a>';

                    }
                },
                // Remaining columns unchanged
                {
                    "targets": [2, 3, 4, 5],
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