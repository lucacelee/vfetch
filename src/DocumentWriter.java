import java.io.FileWriter;
import java.io.IOException;

public class DocumentWriter {
    public static void write(String name, String document) {
        try {
            FileWriter myWriter = new FileWriter(name);
            myWriter.write(document);
            myWriter.close(); // must close manually
            vfetch.println("\nSuccessfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
