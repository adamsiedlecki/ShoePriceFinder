package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentSearcher {
    public Set<URL> getLinksOnPageThatStartsWithSlash(String address, int timeout, String domainAddress){

        String content = getContent(address, timeout);
        Set<String> links = extractUrlsFromStringThatStartsWithSlash(content);
        Set<URL> urls = new HashSet<>();
        for (int i = 0; i < links.size(); i++) {
            try {
                urls.add(new URL(domainAddress + links.toArray()[i]));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    public Set<URL> getLinksOnPage(String address, int timeout, String domain) {
        System.out.println(address);
        String content = getContent(address, timeout);
        Set<String> links = extractUrlsFromString(content);
        Set<URL> urls = new HashSet<>();
        for (int i = 0; i < links.size(); i++) {
            try {
                urls.add(new URL((String) links.toArray()[i]));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
    public String getContent(String address, int timeout){
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeout);
            String content = readContent(connection);

            connection.disconnect();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    private String readContent(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }
    private Set<String> extractUrlsFromString(String content)
    {
        Set<String> result = new HashSet<>();

        String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find())
        {
            result.add(m.group());
        }

        return result;
    }
    private Set<String> extractUrlsFromStringThatStartsWithSlash(String content)
    {
        Set<String> result = new HashSet<>();

        String regex = "href=\"/[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find())
        {
            result.add(m.group().substring(6));
        }

        return result;
    }
}
