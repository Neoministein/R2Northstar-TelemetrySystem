package com.neo.r2.ts.impl.map.heatmap;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

class FullTelemetrySystemTest {

    protected static final String GENERAL_PIPE_NAME = "\\\\.\\pipe\\GameDataPipe";
    protected static final String HOST = "http://127.0.0.1:8090/api/v1";
    protected static final String ADMIN_USER_TOKEN = "bChtOMrODQOt160xG5medkKbN894FJyElx9V9ggXQ6D3R6S";
    protected static final String NAMED_PIPE_SERVER_LOCATION = System.getProperty("user.dir") + "/../Other/CSNamedPipeServer.exe";

    @Test
    void run() throws Exception{
        startNamedPipeServer().run();
        Thread.sleep(1000);
        String pipeName = getNameFromMasterPipe();
        Thread.sleep(3000);
        try {
            RandomAccessFile pipe = new RandomAccessFile(pipeName, "rw");
            try (BufferedReader br = new BufferedReader(new FileReader(Thread.currentThread().getContextClassLoader().getResource("RecordedMatch.txt").getFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Thread.sleep(10);
                    pipe.write(line.getBytes());
                }
            }
            pipe.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    String getNameFromMasterPipe() {
        try {
            // Connect to the pipe
            RandomAccessFile pipe = new RandomAccessFile(GENERAL_PIPE_NAME, "r");
            byte[] inputArray = new byte[512 * 2];
            pipe.read(inputArray);
            StringBuilder sb = new StringBuilder();
            for (char c: new String(inputArray, StandardCharsets.UTF_16LE).toCharArray()) {
                if (c == '\u0000') {
                    break;
                }
                sb.append(c);
            }
            System.out.println(sb);
            pipe.close();
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    Runnable startNamedPipeServer() {
        return () -> {
            try {
                Runtime.getRuntime().exec(NAMED_PIPE_SERVER_LOCATION + " " + ADMIN_USER_TOKEN + " " + HOST);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }
}
