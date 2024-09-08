import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
    public static void saveObject(String filename, Object object){
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);   
            out.writeObject(object);
            out.close();
            fileOut.close();
            System.out.println("Serialized object is saved in " + filename);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Saving serialized object error: " + e.getMessage());
        }
    }

    public static <T extends Object> T getSerializedObject(String filename) throws Exception{
          try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            T object = (T) in.readObject();
            in.close();
            fileIn.close();

            return object;
        } catch (Exception exception) {
            throw exception;
        } 
    }
}
