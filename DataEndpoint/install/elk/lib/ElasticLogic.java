

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class Scratch {

    public static final String ELASTIC_URL = System.getProperty("ELASTIC_URL", "http://127.0.0.1:9200");


    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Uploading Pipelines");
        uploadConfig("/elastic/pipeline", "/_ingest/pipeline", "PUT");
        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Uploading Index Templates Component");
        uploadConfig("/elastic/template-component", "/_component_template", "PUT");
        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Uploading Index Templates");
        uploadConfig("/elastic/template", "/_index_template", "POST");
    }

    static void uploadConfig(String fileLocation, String endpoint, String httpMethod) throws Exception {
        File folder = new File(System.getProperty("user.dir") + fileLocation);

        for (final File fileEntry : folder.listFiles()) {
            String content = readFile(fileEntry.toPath());
            sendFileBody(endpoint, httpMethod, content, fileEntry.getName().substring(0, fileEntry.getName().lastIndexOf('.')));
        }
    }

    static String readFile(Path path) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, StandardCharsets.UTF_8);
    }

    static void sendFileBody(String endpoint, String httpMethod, String body, String templateName) throws Exception {
        URL url = new URI( ELASTIC_URL + endpoint + "/" +  templateName).toURL();
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod(httpMethod);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        System.out.println("------------------------------------------------");
        System.out.println("File: " + templateName);
        System.out.println("Status: " + con.getResponseCode());
        System.out.println("ResponseBody:");
        printResponseBody(con);
    }

    static void printResponseBody(HttpURLConnection con) throws IOException {
        BufferedReader br;
        if (100 <= con.getResponseCode() && con.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}