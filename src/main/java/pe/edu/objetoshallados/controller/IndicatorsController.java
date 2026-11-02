package pe.edu.objetoshallados.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.DashboardStats;

public final class IndicatorsController extends BaseController {
    @FXML private Label availableLabel;
    @FXML private Label publishedLabel;
    @FXML private Label pendingLabel;
    @FXML private Label returnedLabel;
    @FXML private PieChart statusChart;
    @FXML private BarChart<String, Number> categoryChart;

    @FXML
    private void initialize() {
        DashboardStats stats = AppContext.indicators().snapshot();
        availableLabel.setText(String.valueOf(stats.available()));
        publishedLabel.setText(String.valueOf(stats.published()));
        pendingLabel.setText(String.valueOf(stats.pendingClaims()));
        returnedLabel.setText(String.valueOf(stats.returned()));
        statusChart.getData().setAll(
                new PieChart.Data("Disponibles", stats.available()),
                new PieChart.Data("En reclamo", stats.inClaim()),
                new PieChart.Data("Devueltos", stats.returned())
        );
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Objetos");
        stats.byCategory().forEach((category, value) -> series.getData().add(new XYChart.Data<>(category, value)));
        categoryChart.getData().setAll(series);
    }
}
