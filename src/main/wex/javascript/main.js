const tableIds = ["licenses-data-table"];

$(document).ready(function () {
    try {
        try {
            tableIds.forEach((id) => tableUtils.createTable(id));
        } catch (e) {
            console.log("failed to init table " + id);
            console.err(e);
        }
        // TODO
    } catch (e) {
        console.err(e);
    }
});

var getUrl = function (method) {
    return "/Windchill/netmarkets/jsp/com/wincomplm/wex/license/report/" + method + ".jsp";
};