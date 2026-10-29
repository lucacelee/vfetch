# vfetch
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
## How to build
To build the program, you simply need to be in the project directory and run `./gradlew build`. On Windows, you can type `gradlew.bat build`, hoerver it will not display information for you.
## Document exporting
If you pass the `-o` / `--output` flag to the program, it will export a HTML document with your details. If you don't want to get text in the terminal, you can also pass `-s` / `--silence`

For data that keeps changing ad can be kept track of, the program allows you to view the HTML document on a web-page. Pass `-l` / `--live-updates` to launch a http server after conducting tests.
## Data sources
The data is gathered from the following files:
- `/proc/cpuinfo` and `/proc/stat` for CPU information
- `/proc/meminfo` for RAM information
- `/sys/bus/pci/devices` folder for PCI devices
- `/sys/bus/usb/devices` folder for USB devices
