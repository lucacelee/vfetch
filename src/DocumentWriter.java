import java.io.FileWriter; // https://docs.oracle.com/javase/8/docs/api/java/io/FileWriter.html
import java.io.IOException;

//When the application is run, the user can choose to write the output to a file. This class lets 
//you do that. It takes a string for the name of the file and a string for the document to be 
//written to the file. It uses a FileWriter object to write the document to the file. When the
//app is run with the -o (output) flag, the output will be written to a file instead of the console.
// In vfetch.java, DocumentWriter.write() is called to save the generated html string to a specified file.


public class DocumentWriter {
    public static void write(String name, String document) {
        try {
            FileWriter myWriter = new FileWriter(name); // Constructs a filewriter object given a file name.
            myWriter.write(document);
            myWriter.close(); // must close manually to flush and commit changes, if you dont, the file will be empty.
            vfetch.println("\nSuccessfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
