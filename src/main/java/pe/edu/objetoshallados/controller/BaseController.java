package pe.edu.objetoshallados.controller;

import pe.edu.objetoshallados.util.Navigator;
import pe.edu.objetoshallados.util.ViewSupport;

public abstract class BaseController {
    public void onMenu() { Navigator.goTo("menu-principal.fxml"); }
    public void onCatalog() { Navigator.goTo("catalogo.fxml"); }
    public void onPublish() { Navigator.goTo("publicar-objeto.fxml"); }
    public void onClaims() { Navigator.goTo("mis-reclamos.fxml"); }
    public void onNotices() { Navigator.goTo("avisos.fxml"); }
    public void onAdmin() { Navigator.goTo("admin-panel.fxml"); }
    public void onBack() { onMenu(); }
    public void onLogout() { ViewSupport.logout(); }
}
