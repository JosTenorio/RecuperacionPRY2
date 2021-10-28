package Utils;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.channels.FileChannel;

public class StopWordsHandler {

    public static void loadStopwords(String sourceFile, String depositFile) throws IOException {
        File inputSWFile = new File(sourceFile);
        File SWDeposit = new File(depositFile);
        if (inputSWFile.equals(SWDeposit)) {
            System.out.println("El archivo de stopwords es el mismo.");
            return;
        }
        if(!SWDeposit.exists()) {
            SWDeposit.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(inputSWFile).getChannel();
            destination = new FileOutputStream(SWDeposit).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    private static boolean validateStopwords (String stopWordsPath) {
        File f = new File(stopWordsPath);
        return (f.exists() && !f.isDirectory() && FilenameUtils.getExtension(stopWordsPath).equals("txt"));
    }
}