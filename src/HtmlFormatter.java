import java.util.ArrayList;
import java.util.List;

public class HtmlFormatter {
    private final String heading;
    private final List<PciDevice> pciDevices;
    private final List<USBDevice> usbDevices;
    private final List<Core> cores;
    private final double cpuLoad;
    private final Ram ram;
    private final ParallelismRecord pResultRecord;
    private final ArrayList<ArrayList<AccessTime>> accessTimeMatrix;
    private final ArrayList<Cache> caches;

    private boolean printingParallelism = true;
    private boolean formatted = false;

    /*  
        The following string is simply a template for the page.
        It mostly consists of embedded CSS (to make it pretty),
        but also a small JavaScript script for table sorting.
        The actual HTML body is left blank, except for a
        replacement string `!body;`, that then gets swapped
        for the real contents of the document.

        The data is formatted and accessed using different
        methods.
    */

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

                    .centre {
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

    public HtmlFormatter(String heading, List<PciDevice> pciDevices, List<USBDevice> usbDevices, double cpuLoad, List<Core> cores, Ram ram, ParallelismRecord parallelismRecord, ArrayList<ArrayList<AccessTime>> accessTimeMatrix, ArrayList<Cache> caches) {
        this.heading = heading;
        this.pciDevices = pciDevices;
        this.usbDevices = usbDevices;
        this.cpuLoad = cpuLoad;
        this.cores = cores;
        this.ram = ram;
        this.pResultRecord = parallelismRecord;
        this.accessTimeMatrix = accessTimeMatrix;
        this.caches = caches;
    }

    // This is to access the document
    public String htmlDocument() {
        if (!formatted) return "";
        return html;
    }

    // This is to format the document
    public void formatHTML() {
        html = html.replaceAll("!title;", heading);
        String body = "";

        // Populating the HTML with data

        /*
            Here, the data gets wrapped in HTML to format it in
            a specific way. Various HTML tags are stored as
            templates in format strings. The data is inserted
            using `String.format()`, the same way you do `printf`,
            and then get appended onto the end of `body`, which
            starts off blank.

            Most of this is just tables, and in HTML tables
            are defined line by line. You go "start row", then
            put a value for each column, and then end the row.
            
            I am using multiline strings
            (noted by using three quotation marks), to save on
            operations. And so, inside of those, format tags
            (so your %d, %n and %f) get turned into real values.
            At the end, `body` replaces "!body;" in the template.
        */

        // Step 1: The heading
        body += "    <h1>" + heading + "</h1>\n";

        // Step 2: CPU Information
        body += "        <h2>CPU Information</h2>";
        body += coresTable(this.cores, this.cpuLoad);
        body += "            <h3>Load and Parallelism</h3>\n";
        body += loadAndParallelismTable();
        body += coresTable(pResultRecord.coreLoads(), pResultRecord.cpuLoad());

        // Step 3: RAM Information
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
        body += memoryHierarchyTable();
        body += cacheTable();

        // Step 4: PCI Device Information
        body += "        <h2>PCI Device Information</h2>\n";
        body += "        <p>You can sort the columns by clicking on the labels.</p>\n";
        body += busDeviceTable(pciDevices);

        // Step 5: USB Device Information
        body += "        <h2>USB Device Information</h2>\n";
        body += "        <p>You can sort the columns by clicking on the labels.</p>\n";
        body += busDeviceTable(usbDevices);

        // Step 6: Fill in the data into the template
        html = html.replaceAll("!body;", body);
        formatted = true;
    }

    private String coresTable(List<Core> cores, double cpuLoad) {
        String body = "";

        // This is the beginning of the table declaration. It also has some headers
        body += """
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

        /* 
            For each core, it creates 2 columns in a row:
                                        +------+------+
                                        | Core | Load |
                                        +------+------+
                                        |  %d  |%.2f%%|
                                            ... etc.
            and fills them with the data using `String.format()`
                                        
        */
        for (Core c : cores) {
            body += String.format("""
                                    <tr>
                                        <td>%d</td>
                                        <td class="centre">%.2f%%</td>
                                    <tr>
                    """, c.core(), c.load());
        }

        // This is the footer of the table with total CPU and the closure of the table
        body += String.format("""
                            </tbody>
                            <tfoot>
                                <tr>
                                    <th scope="row" colspan="1">Total load</th>
                                    <td class="centre">%.2f%%</td>
                                </tr>
                            </tfoot>
                        </table>
                """, cpuLoad);
        return body;
    }

    private String busDeviceTable(List<? extends BusDevice> devices) {
        String busType = devices.get(0).busType().toLowerCase();
        String table = "";

        // New table declaration
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
        
        // PCI devices have extra fields that can be filled, so for them we add 2 extra columns
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
                                        %s
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
                """, 
                4 - sub, busType,
                5 - sub, busType,
                6 - sub, busType,
                (busType == "pci") ? "Class" : "Device Type" ,
                7 - sub, busType,
                8 - sub, busType);

        // For each device, it fills in a table row and adds it to the main text body string
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

        // End of table declaration
        table += """
                            </tbody>
                        </table>
                """;
        return table;
    }

    // Generates the table for the parallelism test
    private String loadAndParallelismTable() {
        if (!printingParallelism) return "";
        String table = "";
        table += String.format("""
                        <table id="parallelism">
                            <caption>
                                Parallelism test for %d available cores
                            </caption>
                            <thead>
                                <tr>
                                    <th scope="col", onclick="sortTable(0, 'parallelism', false)", class="clickable">
                                        Number of Threads
                                    </th>
                                    <th scope="col", onclick="sortTable(1, 'parallelism', false)", class="clickable">
                                        Execution Time (s)
                                    </th>
                                    <th scope="col", onclick="sortTable(2, 'parallelism', false)", class="clickable">
                                        Measured Speedup
                                    </th>
                                    <th scope="col", onclick="sortTable(3, 'parallelism', false)", class="clickable">
                                        Amdahl's Limit
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                """, pResultRecord.coresAvailable());
        int[] loads = {1, 2, 4, pResultRecord.coresAvailable()};
        for (int load : loads) {

            table += "            <tr>\n";
            table += String.format("""
                                    <td>
                                        %d
                                    </td>
                                    <td>
                                        %.4f
                                    </td>
                                    <td>
                                        %.4fx
                                    </td>
                                    <td>
                                        %.4fx
                                    </td>
                    """, 
                    load, 
                    pResultRecord.timeTaken().get(load).doubleValue(), 
                    pResultRecord.speedup().get(load).doubleValue(), 
                    pResultRecord.amdahlSpeedups().get(load).doubleValue());
            table += "            </tr>\n";
        }
        table += """
                            </tbody>
                        </table>
                """;
        return table;
    }

    private String memoryHierarchyTable() {
        String table = "";
        //  All of this is just the headers and caption for the table
        table += """
                <table>
                    <caption>
                        Memory Hierarchy Exploration — Working Set Table
                    </caption>
                    <thead>
                        <tr>
                            <th>
                                Set Size (KiB)
                            </th>
                            <th>
                                Stride 1 (ns)
                            </th>
                            <th>
                                Stride 2 (ns)
                            </th>
                            <th>
                                Stride 4 (ns)
                            </th>
                            <th>
                                Stride 8 (ns)
                            </th>
                            <th>
                                Stride 16 (ns)
                            </th>
                            <th>
                                Stride 32 (ns)
                            </th>
                            <th>
                                Stride 64 (ns)
                            </th>
                            <th>
                                Stride 128 (ns)
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                """;
        
        // This loop is for the rows of the table
        for (ArrayList<AccessTime> accessTimes : accessTimeMatrix) {
            table += "            <tr>\n";
            table += String.format("""
                            <th scope="row"">
                                %d
                            </th>
                    """, accessTimes.get(0).setSize());
            // This is for each column
            for (AccessTime t : accessTimes) {
                table += String.format("""
                                        <td>
                                            %.2f
                                        </td>
                        """, t.time());
            }
            table += "            </tr>\n";
        }
        table += """
                            </tbody>
                        </table>
                """;
        return table;
    }

    private String cacheTable() {
        String table = "";
        table += """
                <table>
                    <caption>
                        Caches present on this machine
                    </caption>
                    <thead>
                        <tr>
                            <th scope="col">
                                Cache Level
                            </th>
                            <th scope="col">
                                Cache Type
                            </th>
                            <th scope="col">
                                Cache Size
                            </th>
                            <th scope="col">
                                Line Size
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                """;
        for (Cache c : caches) {

            table += "            <tr>\n";
            table += String.format("""
                                    <td>
                                        L%s
                                    </td>
                                    <td>
                                        %s
                                    </td>
                                    <td>
                                        %s
                                    </td>
                                    <td>
                                        %s Bytes
                                    </td>
                    """, c.level(), c.type(), c.size(), c.lineSize());
            table += "            </tr>\n";
        }
        table += """
                            </tbody>
                        </table>
                """;
        return table;
    }
}