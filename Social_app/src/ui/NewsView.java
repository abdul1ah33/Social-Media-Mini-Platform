package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.NewsArticle;
import service.NewsService;

import java.util.ArrayList;

public class NewsView {

    private final ScrollPane scrollPane;
    private final VBox rootContainer;
    private final VBox newsContainer;
    private final NewsService newsService;
    
    private ArrayList<NewsArticle> allArticles;
    private int currentPage = 0;
    private static final int ARTICLES_PER_PAGE = 10;
    private Button loadMoreBtn;

    public NewsView() {
        this.newsService = new NewsService();
        this.allArticles = new ArrayList<>();

        rootContainer = new VBox(20);
        rootContainer.getStyleClass().add("main-container");
        rootContainer.setPadding(new Insets(20));
        rootContainer.setAlignment(Pos.TOP_CENTER);

        // Header
        HBox header = createHeader();

        // News Container
        newsContainer = new VBox(20);
        newsContainer.setAlignment(Pos.TOP_CENTER);
        newsContainer.setMaxWidth(750);

        // Load More Button
        loadMoreBtn = new Button("Load More News");
        loadMoreBtn.getStyleClass().add("primary-button");
        loadMoreBtn.setPrefWidth(300);
        loadMoreBtn.setPrefHeight(45);
        loadMoreBtn.setVisible(false);
        loadMoreBtn.setOnAction(e -> loadNextPage());

        rootContainer.getChildren().addAll(header, newsContainer, loadMoreBtn);

        scrollPane = new ScrollPane(rootContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Load initial news
        loadNews();
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10, 0, 20, 0));

        // Icon
        Text icon = new Text("📰");
        icon.setStyle("-fx-font-size: 36px;");

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("Breaking News");
        title.getStyleClass().add("title-text");
        title.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-fill: linear-gradient(to right, #60a5fa, #a78bfa); " +
            "-fx-font-weight: bold;"
        );

        Text subtitle = new Text("Latest stories from Wall Street Journal");
        subtitle.getStyleClass().add("subtitle-text");
        subtitle.setStyle("-fx-font-size: 14px; -fx-fill: #94a3b8;");

        titleBox.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(icon, titleBox);
        return header;
    }

    private void loadNews() {
        newsContainer.getChildren().clear();
        
        // Loading indicator
        ProgressIndicator loading = new ProgressIndicator();
        loading.setMaxSize(50, 50);
        Text loadingText = new Text("Fetching latest news...");
        loadingText.setStyle("-fx-fill: #94a3b8; -fx-font-size: 16px;");
        
        VBox loadingBox = new VBox(15, loading, loadingText);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(50));
        newsContainer.getChildren().add(loadingBox);

        // Fetch news in background
        new Thread(() -> {
            try {
                allArticles = newsService.fetchLatestNews();
                currentPage = 0;

                Platform.runLater(() -> {
                    newsContainer.getChildren().clear();

                    if (allArticles.isEmpty()) {
                        Label emptyLabel = new Label("No news available at the moment.");
                        emptyLabel.getStyleClass().add("subtitle-text");
                        emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #94a3b8;");
                        newsContainer.getChildren().add(emptyLabel);
                        loadMoreBtn.setVisible(false);
                    } else {
                        displayPage(0);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    newsContainer.getChildren().clear();
                    Label errorLabel = new Label("Failed to load news. Please check your connection.");
                    errorLabel.getStyleClass().add("error-label");
                    errorLabel.setStyle("-fx-font-size: 16px;");
                    newsContainer.getChildren().add(errorLabel);
                    loadMoreBtn.setVisible(false);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void displayPage(int page) {
        newsContainer.getChildren().clear();
        
        int start = page * ARTICLES_PER_PAGE;
        int end = Math.min(start + ARTICLES_PER_PAGE, allArticles.size());
        
        // Show page indicator
        if (page > 0) {
            Text pageInfo = new Text("Showing " + (start + 1) + " - " + end + " of " + allArticles.size() + " articles");
            pageInfo.setStyle("-fx-fill: #94a3b8; -fx-font-size: 13px;");
            HBox pageInfoBox = new HBox(pageInfo);
            pageInfoBox.setAlignment(Pos.CENTER);
            pageInfoBox.setPadding(new Insets(0, 0, 10, 0));
            newsContainer.getChildren().add(pageInfoBox);
        }
        
        for (int i = start; i < end; i++) {
            newsContainer.getChildren().add(new NewsCard(allArticles.get(i)));
        }
        
        // Show/Hide Load More button
        loadMoreBtn.setVisible(end < allArticles.size());
        
        if (end < allArticles.size()) {
            int remaining = allArticles.size() - end;
            loadMoreBtn.setText("Load More (" + Math.min(remaining, ARTICLES_PER_PAGE) + " more articles)");
        }
    }

    private void loadNextPage() {
        currentPage++;
        
        // Show loading on button
        loadMoreBtn.setDisable(true);
        loadMoreBtn.setText("Loading...");
        
        // Simulate delay for smooth UX
        new Thread(() -> {
            try {
                Thread.sleep(300);
                Platform.runLater(() -> {
                    int start = currentPage * ARTICLES_PER_PAGE;
                    int end = Math.min(start + ARTICLES_PER_PAGE, allArticles.size());
                    
                    for (int i = start; i < end; i++) {
                        newsContainer.getChildren().add(new NewsCard(allArticles.get(i)));
                    }
                    
                    loadMoreBtn.setDisable(false);
                    
                    if (end < allArticles.size()) {
                        int remaining = allArticles.size() - end;
                        loadMoreBtn.setText("Load More (" + Math.min(remaining, ARTICLES_PER_PAGE) + " more articles)");
                        loadMoreBtn.setVisible(true);
                    } else {
                        loadMoreBtn.setVisible(false);
                    }
                    
                    // Scroll to new content
                    scrollPane.setVvalue(scrollPane.getVvalue() + 0.1);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Parent getView() {
        return scrollPane;
    }

    public void refresh() {
        loadNews();
    }
}
