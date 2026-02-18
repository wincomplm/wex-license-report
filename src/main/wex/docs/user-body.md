## User Guide

This tool is designed to work only if you have implemented license group names & assigments based on the load file in the linked case. You don't have to use the load file, however the group names must match.
[CS429921 - Introducing the New Role-Based License Package in Windchill PDMLink](https://www.ptc.com/en/support/article/CS429921)

### UI Implementation

There is one main UI page

[License Report](/Windchill/netmarkets/jsp/com/wincomplm/wex/license/report/index.jsp)

Which is an admin only page and will be rejected for other users.

You may add a prefix in front of the group names if needed to delineate company/division notes etc.
For the tool to work, you need to add a pipe and the quantity of licenses you are entitled to
ex the "Windchill Design Engineering License" group: CorpDivision1 - Windchill Design Engineering License | 50



