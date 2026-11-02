package pe.edu.objetoshallados.session;

import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.model.Role;
import pe.edu.objetoshallados.model.User;

public final class UserSession {
    private static User user = new User(1, "Invitado", "invitado@encuentra.pe", "-", Role.ESTUDIANTE);
    private static LostItem selectedItem;
    private static long selectedClaimId;

    private UserSession() {}

    public static User get() {
        return user;
    }

    public static void start(User authenticatedUser) {
        user = authenticatedUser;
    }

    public static void clear() {
        user = new User(1, "Invitado", "invitado@encuentra.pe", "-", Role.ESTUDIANTE);
        selectedItem = null;
        selectedClaimId = 0;
    }

    public static LostItem selectedItem() {
        return selectedItem;
    }

    public static void selectItem(LostItem item) {
        selectedItem = item;
    }

    public static long selectedClaimId() {
        return selectedClaimId;
    }

    public static void selectClaim(long id) {
        selectedClaimId = id;
    }
}
