import java.util.List;

public class HtmlFormatter {
    private final String heading;
    private final List<PciDevice> pciDevices;
    // private final List<UsbDevice> usbDevices; -- Will hopefully exist when we merge Helena's PR
    private final List<Core> cores;
    private final double cpuLoad;
    private final Ram ram;

    private String html = """
            <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
            "http://www.w3.org/TR/html4/loose.dtd">
            <html>
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                    <title>$title;</title>
                </head>
                <body>
                    $body;
                </body>
            </html>
            """;

    public HtmlFormatter(String heading, List<PciDevice> pciDevices, double cpuLoad, List<Core> cores, Ram ram) {
        this.heading = heading;
        this.pciDevices = pciDevices;
        this.cpuLoad = cpuLoad;
        this.cores = cores;
        this.ram = ram;
    }

    public void formatHTML() {
        html.replace("$title;", heading);
        String body = "";

        // Populating the HTML with data
        body += "        <h1>" + heading + "</h1>";
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
                """, ram.used(), ram.free(), ram.total(), ram.usedPercentage(), ram.freePercentage(), ram.totalPercentage());
    }
}