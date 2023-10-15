

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.Charset;

class Scratch {

    public static final String ENDPOINT_URL = System.getProperty("KIBANA_URL", "http://127.0.0.1:5601");
    private static final String LINE = "\r\n";


    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Uploading Data View");
        uploadConfig("/kibana/dataview", "/api/saved_objects/_import?overwrite=true", "POST", false, true);
        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Uploading Search");
        uploadConfig("/kibana/search", "/api/saved_objects/_import?overwrite=true", "POST", false, true);
        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Uploading Map");
        uploadConfig("/kibana/map", "/api/saved_objects/_import?overwrite=true", "POST", false, true);
        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Uploading Dashboard");
        uploadConfig("/kibana/dashboard", "/api/saved_objects/_import?overwrite=true", "POST", false, true);
    }

    static void uploadConfig(String fileLocation, String endpoint, String httpMethod, boolean fileNameInUrl, boolean mutlipart) throws Exception {
        File folder = new File(System.getProperty("user.dir") + fileLocation);

        for (final File fileEntry : folder.listFiles()) {
            String content = readFile(fileEntry.toPath());
            String fileName = fileEntry.getName().substring(0, fileEntry.getName().lastIndexOf('.'));

            sendFileBody(endpoint, httpMethod, content, fileName, fileNameInUrl, mutlipart);
        }
    }

    static String readFile(Path path) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, StandardCharsets.UTF_8);
    }

    static void sendFileBody(String endpoint, String httpMethod, String body, String templateName, boolean fileNameInUrl, boolean mutlipart) throws Exception {
        URL url;
        if (fileNameInUrl) {
            url = new URI( ENDPOINT_URL + endpoint + "/" +  templateName).toURL();
        } else {
            url = new URI( ENDPOINT_URL + endpoint).toURL();
        }

        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        if (!mutlipart) {
            con.setRequestMethod(httpMethod);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("kbn-xsrf", "true");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        } else {
            String boundary = "XXXX";

            con.setRequestMethod(httpMethod);
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            con.setRequestProperty("kbn-xsrf", "true");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                PrintWriter writer =  new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true);
                writer.append("--" + boundary).append(LINE);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + templateName + ".ndjson\"").append(LINE);
                writer.append("Content-Type: application/x-ndjson").append(LINE);
                writer.append("Content-Transfer-Encoding: binary").append(LINE);
                writer.append(LINE);
                writer.flush();

                byte[] input = body.replace("\n", "").replace("\r", "").getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);

                os.flush();
                writer.append(LINE);
                writer.flush();

                writer.flush();
                writer.append("--" + boundary + "--").append(LINE);
                writer.close();
            }
        }

        System.out.println("------------------------------------------------");
        System.out.println("File: " + templateName);
        System.out.println("Status: " + con.getResponseCode());
        if (fileNameInUrl) {
            System.out.println("ResponseBody:");
            printResponseBody(con);
        } else {
            if (300 <= con.getResponseCode()) {
                System.out.println("ResponseBody:");
                printResponseBody(con);
            }
        }
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