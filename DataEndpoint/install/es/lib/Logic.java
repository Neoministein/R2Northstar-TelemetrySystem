

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class Scratch {
    public static void main(String[] args) throws IOException {
        File folder = new File(System.getProperty("user.dir") + "\\template");

        for (final File fileEntry : folder.listFiles()) {
            String content = readFile(fileEntry.toPath(), StandardCharsets.UTF_8);
            postTemplateName(content);
        }
    }

    static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    static void postTemplateName(String body) throws IOException {
        URL url = new URL ("http://localhost:9200/index_template/template_1");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        System.out.println(con.getResponseCode());
    }
}