package org.zmpp.textbased;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.zmpp.vm.Machine;
import org.zmpp.vm.ScreenModel;

public class ConsoleMain {

    public static void main(String[] args) throws Exception {
        final byte[] story = readBinaryFile("zmpp/minizork.z3");
        runStoryFile(story);
    }

    public static byte[] readBinaryFile(String filename) throws IOException {
        try (InputStream is = ConsoleMain.class.getClassLoader().getResourceAsStream(filename);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            for (int len = is.read(buffer); len != -1; len = is.read(buffer)) os.write(buffer, 0, len);
            return os.toByteArray();
        }
    }

    public static void runStoryFile(byte[] story) throws Exception {
        ConsoleMachineFactory factory;
        factory = new ConsoleMachineFactory(story);
        Machine machine = factory.buildMachine();
        ScreenModel screen = factory.getScreenModel();
        //currentGame = new GameThread(machine, screen);
        //currentGame.start();
        factory.buildMachine();
        //frame.startMachine();
    }

}
