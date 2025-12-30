package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.NewsArticle;

import java.awt.*;
import java.net.URI;
import java.time.format.DateTimeFormatter;

public class NewsCard extends StackPane {

    private final NewsArticle article;

    public NewsCard(NewsArticle article) {
        this.article = article;
        initializeUI();
    }

    private void initializeUI() {
        this.getStyleClass().add("news-card");
        this.setPrefHeight(400);
        this.setMaxWidth(700);
        this.setStyle("-fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);");

        // Background Image Layer
        StackPane imageLayer = new StackPane();
        imageLayer.setStyle("-fx-background-radius: 16;");
        
        if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
            // Load image in background
            new Thread(() -> {
                try {
                    Image image = new Image(article.getUrlToImage(), 700, 400, false, true, true);
                    javafx.application.Platform.runLater(() -> {
                        BackgroundImage bgImage = new BackgroundImage(
                            image,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(100, 100, true, true, false, true)
                        );
                        imageLayer.setBackground(new Background(bgImage));
                    });
                } catch (Exception e) {
                    // Use gradient fallback
                    javafx.application.Platform.runLater(() -> setFallbackBackground(imageLayer));
                }
            }).start();
        } else {
            setFallbackBackground(imageLayer);
        }

        // Dark Gradient Overlay for text readability
        Region overlay = new Region();
        overlay.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " +
            "rgba(0,0,0,0.3) 0%, " +
            "rgba(0,0,0,0.5) 50%, " +
            "rgba(0,0,0,0.9) 100%); " +
            "-fx-background-radius: 16;"
        );

        // Content Layer
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.BOTTOM_LEFT);

        // Header (Source + Time)
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Text source = new Text(article.getSourceName().toUpperCase());
        source.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-fill: #60a5fa; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1);"
        );

        Text separator = new Text("•");
        separator.setStyle("-fx-fill: white; -fx-font-size: 11px;");

        Text time = new Text(getTimeAgo());
        time.setStyle(
            "-fx-fill: rgba(255,255,255,0.9); " +
            "-fx-font-size: 11px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1);"
        );

        header.getChildren().addAll(source, separator, time);

        // Title
        Text title = new Text(article.getTitle());
        title.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-fill: white; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 5, 0, 0, 2);"
        );
        title.setWrappingWidth(640);

        // Description
        Text description = new Text(
            article.getDescription() != null && article.getDescription().length() > 150
                ? article.getDescription().substring(0, 150) + "..."
                : article.getDescription()
        );
        description.setStyle(
            "-fx-fill: rgba(255,255,255,0.95); " +
            "-fx-font-size: 14px; " +
            "-fx-line-spacing: 2; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1);"
        );
        description.setWrappingWidth(640);

        // Read More Link
        Hyperlink readMore = new Hyperlink("Read Full Article →");
        readMore.setStyle(
            "-fx-text-fill: #93c5fd; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-underline: false; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1);"
        );
        readMore.setOnAction(e -> openInBrowser(article.getUrl()));

        contentBox.getChildren().addAll(header, title, description, readMore);

        // Stack layers
        this.getChildren().addAll(imageLayer, overlay, contentBox);

        // Hover effect
        this.setOnMouseEntered(e -> {
            this.setStyle(
                "-fx-background-radius: 16; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(59, 130, 246, 0.6), 20, 0, 0, 8); " +
                "-fx-cursor: hand;"
            );
        });
        this.setOnMouseExited(e -> {
            this.setStyle(
                "-fx-background-radius: 16; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);"
            );
        });
    }

    private void setFallbackBackground(StackPane layer) {
        // Gradient fallback if no image
        String[] gradients = {
            "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
            "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
            "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)",
            "linear-gradient(135deg, #fa709a 0%, #fee140 100%)"
        };
        int index = Math.abs(article.getTitle().hashCode()) % gradients.length;
        layer.setStyle("-fx-background-color: " + gradients[index] + "; -fx-background-radius: 16;");
    }

    private String getTimeAgo() {
        if (article.getPublishedAt() == null) return "Recently";
        
        java.time.Duration duration = java.time.Duration.between(article.getPublishedAt(), java.time.LocalDateTime.now());
        long hours = duration.toHours();
        
        if (hours < 1) return duration.toMinutes() + "m ago";
        if (hours < 24) return hours + "h ago";
        
        long days = duration.toDays();
        if (days == 1) return "Yesterday";
        if (days < 7) return days + "d ago";
        
        return article.getPublishedAt().format(DateTimeFormatter.ofPattern("MMM dd"));
    }

    private void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
