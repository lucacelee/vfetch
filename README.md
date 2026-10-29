# vfetch

`vfetch` is a Java hardware inspector CLI application that gathers, analyzes, and displays system specifications and CPU data on Linux systems.
Short for "verbose fetch", this is a Java program that fetches and displays some interesting information about your computer, if it is a UNIX-like OS. This program displays:
- CPU details
    - Model
    - Cores
    - Hyperthreading
- RAM details
- Memory hierarchy
- PCI devices
    - Sorted alphabetically and
    - In address order
- USB devices
- Parallelism benefits
- 
---

## How to Build and Run

### Prerequisites
* **Operating System:** Linux. Requires access to `/proc` and `/sys` virtual filesystems.
* **Java:** Java 21 JDK or higher.

---

### Option 1: Using Gradle (Recommended)

The repository includes a Gradle Wrapper (`gradlew`), ensuring the application is built using Gradle 9.8.2 to make running the application easier.

1. **Build the application:**
   run this command:

   ./gradlew build
   
   On Windows, you can type `gradlew.bat build`, hoerver it will not display information for you.

   The output will show in your terminal.
   
   ## Document exporting
    If you pass the `-o` / `--output` flag to the program, it will export a HTML document with your details. If you don't want to get text in the terminal, you can also pass `-s` / `--silence`


### Option 2: Using standard Java terminal commands (less fun)

1. **Create output directory**
    run this command: 
    mkdir -p out

2. **Compile all java source files**
    run this command:
    javac -d out src/*.java

3. **Run vfetch (main file)**
    run this command
    java -cp out vfetch 

    You have the option of generating a html output report here by using the -o flag
    run:
    java -cp out vfetch -o

    This will generate a folder with the default name 'Hardware Details.html' in the root of the project folder.
    Opening that folder should open a html tab on your browser.

## Where the information comes from

- **Processor Model & Core Count** | `CpuInfo.java` |
- **CPU Busy & Idle Ticks** | `CpuInfo.java`, `Trial.java` |
- **Core Load Representation** | `Core.java` |
- **Memory (RAM) Usage** | `memInfo.java`, `Ram.java` |
- **CPU Cache Hierarchy Info** | `MemHierarchy.java`, `Cache.java` |
- **Memory Latency Benchmark** | `MemHierarchy.java`, `AccessTime.java`|
- **PCI Devices** | `pciInfo.java`, `PciDevice.java` |
- **USB Devices** | `usbInfo.java`, `USBDevice.java` |
- **Device & Class Name Lookup** | `idLookup.java` |
- **Shared Hardware Device Base** | `BusDevice.java`, `BusType.java` |
- **Multi-Core Parallelism Benchmark** | `Parallelism.java`, `ParallelismRecord.java` |
- **HTML Report Formatting** | `HtmlFormatter.java` |
- **HTML File Output** | `DocumentWriter.java` |
- **CLI Execution & Coordination** | `vfetch.java` |

For data that keeps changing ad can be kept track of, the program allows you to view the HTML document on a web-page. Pass `-l` / `--live-updates` to launch a http server after conducting tests.
## Data sources
The data is gathered from the following files:
- `/proc/cpuinfo` and `/proc/stat` for CPU information
- `/proc/meminfo` for RAM information
- `/sys/bus/pci/devices` folder for PCI devices
- `/sys/bus/usb/devices` folder for USB devices
