package Utils;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StopWordsHandler {

    public static void loadStopwords(String sourceFile, String depositFile) throws IOException {
        if (!validateStopwords(sourceFile)){
            throw (new IOException());
        }
        String destinyFile = "resources\\".concat(depositFile);
        if (validateStopwords(destinyFile)) {
            if (Files.isSameFile(Paths.get(sourceFile), Paths.get(destinyFile))) {
                return;
            }
        }
        File inputSWFile = new File(sourceFile);
        File SWDeposit = new File(destinyFile);
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


    public static void saveStopwords(String sourceFile, String indexDir) throws IOException {
        String inputFile = "resources\\".concat(sourceFile);
        String destinyFile = indexDir.concat("\\Stopwords.txt");
        if (!validateStopwords(inputFile)){
            throw (new IOException());
        }
        if (validateStopwords(destinyFile)) {
            if (Files.isSameFile(Paths.get(inputFile), Paths.get(destinyFile))) {
                return;
            }
        }
        File inputSWFile = new File(inputFile);
        File SWDeposit = new File(destinyFile);
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