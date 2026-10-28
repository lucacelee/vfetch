import java.util.List;

public class HtmlFormatter {
    private final String heading;
    private final List<PciDevice> pciDevices;
    private final List<USBDevice> usbDevices;
    private final List<Core> cores;
    private final double cpuLoad;
    private final Ram ram;

    private boolean formatted = false;

    private String html = """
            <!DOCTYPE html>
            <html>
                <style>
                    table {
                        border-collapse: collapse;
                        border: 2px solid rgb(140 140 140);
                        font-family: sans-serif;
                        font-size: 0.8rem;
                        letter-spacing: 1px;
                    }

                    caption {
                        caption-side: bottom;
                        padding: 10px;
                        font-weight: bold;
                    }

                    thead,
                    tfoot {
                        background-color: rgb(228 240 245);
                    }

                    th,
                    td {
                        border: 1px solid rgb(160 160 160);
                        padding: 8px 10px;
                    }

                    td:last-of-type {
                        text-align: center;
                    }

                    tbody > tr:nth-of-type(even) {
                        background-color: rgb(237 238 242);
                    }

                    tfoot th {
                        text-align: right;
                    }

                    tfoot td {
                        font-weight: bold;
                    }

                    body {
                        font-family: sans-serif;
                    }

                    .clickable {
                        cursor: pointer;
                    }
                </style>
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                    <title>!title;</title>
                </head>
                <body>
                    !body;
                    <!-- Script gracefully taken from https://www.w3schools.com/howto/howto_js_sort_table.asp -->
                    <script>
                        function sortTable(n, tableId, alphabetical) {
                            var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
                            table = document.getElementById(tableId);
                            switching = true;
                            // Set the sorting direction to ascending:
                            dir = "asc";
                            /* Make a loop that will continue until
                            no switching has been done: */
                            while (switching) {
                                // Start by saying: no switching is done:
                                switching = false;
                                rows = table.rows;
                                /* Loop through all table rows (except the
                                first, which contains table headers): */
                                for (i = 1; i < (rows.length - 1); i++) {
                                    // Start by saying there should be no switching:
                                    shouldSwitch = false;
                                    /* Get the two elements you want to compare,
                                    one from current row and one from the next: */
                                    x = rows[i].getElementsByTagName("TD")[n];
                                    y = rows[i + 1].getElementsByTagName("TD")[n];
                                    /* Check if the two rows should switch place,
                                    based on the direction, asc or desc: */
                                    if (alphabetical) {
                                        if (dir == "asc") {
                                            if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                                            // If so, mark as a switch and break the loop:
                                            shouldSwitch = true;
                                            break;
                                            }
                                        } else if (dir == "desc") {
                                            if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                                            // If so, mark as a switch and break the loop:
                                            shouldSwitch = true;
                                            break;
                                            }
                                        }
                                    } else {
                                        if (dir == "asc") {
                                            if (Number(x.innerHTML) > Number(y.innerHTML)) {
                                                shouldSwitch = true;
                                                break;
                                            }
                                        } else if (dir == "desc") {
                                            if (Number(x.innerHTML) < Number(y.innerHTML)) {
                                                shouldSwitch = true;
                                                break;
                                            }
                                        }    
                                    }
                                }
                                if (shouldSwitch) {
                                    /* If a switch has been marked, make the switch
                                    and mark that a switch has been done: */
                                    rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
                                    switching = true;
                                    // Each time a switch is done, increase this count by 1:
                                    switchcount ++;
                                } else {
                                    /* If no switching has been done AND the direction is "asc",
                                    set the direction to "desc" and run the while loop again. */
                                    if (switchcount == 0 && dir == "asc") {
                                        dir = "desc";
                                        switching = true;
                                    }
                                }
                            }
                        }
                    </script>
                </body>
            </html>
            """;

    public HtmlFormatter(String heading, List<PciDevice> pciDevices, List<USBDevice> usbDevices, double cpuLoad, List<Core> cores, Ram ram) {
        this.heading = heading;
        this.pciDevices = pciDevices;
        this.usbDevices = usbDevices;
        this.cpuLoad = cpuLoad;
        this.cores = cores;
        this.ram = ram;
    }

    public String htmlDocument() {
        if (!formatted) return "";
        return html;
    }

    public void formatHTML() {
        html = html.replaceAll("!title;", heading);
        String body = "";

        // Populating the HTML with data
        body += "<h1>" + heading + "</h1>\n";
        body += """
                        <h2>CPU Information</h2>
                        <table>
                            <caption>
                                CPU load per core
                            </caption>
                            <thead>
                                <tr>
                                    <th scope="col">
                                        Core
                                    </th>
                                    <th scope="col">
                                        Load
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                """;
        for (Core c : cores) {
            body += String.format("""
                                    <tr>
                                        <td>%d</td>
                                        <td>%.2f%%</td>
                                    <tr>
                    """, c.core(), c.load());
        }
        body += String.format("""
                            </tbody>
                            <tfoot>
                                <tr>
                                    <th scope="row" colspan="1">Total load</th>
                                    <td>%.2f%%</td>
                                </tr>
                            </tfoot>
                        </table>
                """, cpuLoad);
        body += String.format("""
                        <h2>RAM Information</h2>
                        <table>
                            <caption>
                                RAM usage breakdown
                            </caption>
                            <thead>
                                <tr>
                                    <th scope="col">
                                    </th>
                                    <th scope="col">
                                        Used RAM
                                    </th>
                                    <th scope="col">
                                        Free RAM
                                    </th>
                                    <th scope="col">
                                        Total RAM
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <th scope="row">GiB</th>
                                    <td>%.2f</td>
                                    <td>%.2f</td>
                                    <td>%.2f</td>
                                </tr>
                                <tr>
                                    <th scope="row">Percentage</th>
                                    <td>%.2f%%</td>
                                    <td>%.2f%%</td>
                                    <td>%.2f%%</td>
                                </tr>
                            </tbody>
                        </table>
                """, 
                ram.used(),
                ram.free(),
                ram.total(),
                ram.usedPercentage()*100,
                ram.freePercentage()*100,
                ram.totalPercentage()*100
            );
        body += "        <h2>PCI Device Information</h2>\n";
        body += "        <p>You can sort the columns by clicking on the labels.</p>\n";
        body += busDeviceTable(pciDevices);
        body += "        <h2>USB Device Information</h2>\n";
        body += "        <p>You can sort the columns by clicking on the labels.</p>\n";
        body += busDeviceTable(usbDevices);
        // We will need another one for USB devices
        html = html.replaceAll("!body;", body);
        formatted = true;
    }

    private String busDeviceTable(List<? extends BusDevice> devices) {
        String busType = devices.get(0).busType().toLowerCase();
        String table = "";
        table += String.format("""
                        <table id="%s">
                            <caption>
                                %s bus devices
                            </caption>
                            <thead>
                                <tr>
                                    <th scope="col", onclick="sortTable(0, '%s', false)", class="clickable">
                                        Bus
                                    </th>
                                    <th scope="col", onclick="sortTable(1, '%s', false)", class="clickable">
                                        Device
                                    </th>
                """, busType, (busType == "pci") ? "PCI" : "USB", busType, busType);
        if (busType == "pci") {
            table += """
                                        <th scope="col", onclick="sortTable(2, 'pci', false)", class="clickable">
                                            Function
                                        </th>
                                        <th scope="col", onclick="sortTable(3, 'pci', true)", class="clickable">
                                            Address
                                        </th>
                    """;
        }
        int sub = (busType == "pci") ? 0 : 2;
        table += String.format("""
                                    <th scope="col", onclick="sortTable(%d, '%s', false)", class="clickable">
                                        ID
                                    </th>
                                    <th scope="col", onclick="sortTable(%d, '%s', false)", class="clickable">
                                        Class Code
                                    </th>
                                    <th scope="col", onclick="sortTable(%d, '%s', true)", class="clickable">
                                        Class
                                    </th>
                                    <th scope="col", onclick="sortTable(%d, '%s', true)", class="clickable">
                                        Vendor Name
                                    </th>
                                    <th scope="col", onclick="sortTable(%d, '%s', true)", class="clickable">
                                        Device Name
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                """, 4 - sub, busType, 5 - sub, busType, 6 - sub, busType, 7 - sub, busType, 8 - sub, busType);
        for (BusDevice d : devices) {
            table += "            <tr>\n";
            table += String.format("""
                                    <td>
                                        %d
                                    </td>
                                    <td>
                                        %d
                                    </td>
                    """, d.getBus(), d.getDevice());
            if (busType == "pci") {
                table += String.format("""
                                        <td>
                                            %d
                                        </td>
                                        <td>
                                            %s
                                        </td>
                        """, d.getFunction(), d.getAddress());
            }
            table += String.format("""
                                    <td>
                                        %d
                                    </td>
                                    <td>
                                        %d
                                    </td>
                                    <td>
                                        %s
                                    </td>
                                    <td>
                                        %s
                                    </td>
                                    <td>
                                        %s
                                    </td>
                    """, d.getDeviceId(), d.getClassCode(), d.getClassName(), d.getVendorName(), d.getDeviceName());
            table += "            </tr>\n";
        }
        table += """
                            </tbody>
                        </table>
                """;
        return table;
    }
}