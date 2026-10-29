# vfetch

`vfetch` is a Java hardware inspector CLI application that gathers, analyzes, and displays system specifications and CPU data on Linux systems.

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

   The output will show in your terminal.

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
