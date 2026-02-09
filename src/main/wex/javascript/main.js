const tableIds = ["licenses-data-table"];

$(document).ready(function () {
    initializeTables();
});

function initializeTables() {
    tableIds.forEach(function(id) {
        try {
            tableUtils.createTable(id);
        } catch (e) {
            console.error("Failed to initialize table: " + id, e);
        }
    });
}

function refreshAllTables() {
    tableIds.forEach(function(id) {
        try {
            tableUtils.refreshTable(id);
        } catch (e) {
            console.error("Failed to refresh table: " + id, e);
        }
    });
}

var getUrl = function (method) {
    return "/Windchill/netmarkets/jsp/com/wincomplm/wex/license/report/" + method + ".jsp";
};
