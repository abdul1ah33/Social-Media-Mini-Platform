package service;

import model.NewsArticle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsService {

    private static final String API_KEY = "8b3ff15eac904e5c84d7fee690ef35a5";
    private static final String BASE_URL = "https://newsapi.org/v2/everything?domains=wsj.com&apiKey=" + API_KEY;

    public ArrayList<NewsArticle> fetchLatestNews() {
        ArrayList<NewsArticle> articles = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL + "&pageSize=50&sortBy=publishedAt");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON manually
                String jsonResponse = response.toString();
                articles = parseArticles(jsonResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return articles;
    }

    private ArrayList<NewsArticle> parseArticles(String json) {
        ArrayList<NewsArticle> articles = new ArrayList<>();

        try {
            // Extract articles array
            Pattern articlesPattern = Pattern.compile("\"articles\"\\s*:\\s*\\[(.*?)\\]\\s*\\}", Pattern.DOTALL);
            Matcher articlesMatcher = articlesPattern.matcher(json);

            if (articlesMatcher.find()) {
                String articlesJson = articlesMatcher.group(1);

                // Split by article objects
                String[] articleBlocks = articlesJson.split("\\},\\s*\\{");

                for (String block : articleBlocks) {
                    if (!block.trim().isEmpty()) {
                        NewsArticle article = parseArticle(block);
                        if (article != null) {
                            articles.add(article);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return articles;
    }

    private NewsArticle parseArticle(String articleJson) {
        try {
            NewsArticle article = new NewsArticle();

            // Parse title
            String title = extractValue(articleJson, "title");
            article.setTitle(title != null ? title : "No Title");

            // Parse description
            String description = extractValue(articleJson, "description");
            article.setDescription(description != null ? description : "No Description");

            // Parse url
            String url = extractValue(articleJson, "url");
            article.setUrl(url != null ? url : "");

            // Parse image
            String urlToImage = extractValue(articleJson, "urlToImage");
            article.setUrlToImage(urlToImage);

            // Parse published date
            String publishedAt = extractValue(articleJson, "publishedAt");
            if (publishedAt != null && !publishedAt.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    article.setPublishedAt(LocalDateTime.parse(publishedAt, formatter));
                } catch (Exception e) {
                    article.setPublishedAt(LocalDateTime.now());
                }
            }

            // Parse source name
            Pattern sourcePattern = Pattern.compile("\"source\"\\s*:\\s*\\{[^}]*\"name\"\\s*:\\s*\"([^\"]*)\"");
            Matcher sourceMatcher = sourcePattern.matcher(articleJson);
            if (sourceMatcher.find()) {
                article.setSourceName(sourceMatcher.group(1));
            } else {
                article.setSourceName("Unknown Source");
            }

            // Parse author
            String author = extractValue(articleJson, "author");
            article.setAuthor(author != null ? author : "Unknown");

            return article;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractValue(String json, String key) {
        try {
            Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                String value = matcher.group(1);
                // Unescape common characters
                value = value.replace("\\\"", "\"")
                            .replace("\\\\", "\\")
                            .replace("\\n", "\n")
                            .replace("\\r", "\r")
                            .replace("\\t", "\t");
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
