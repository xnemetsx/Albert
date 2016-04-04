package restaurant.administrator;

import restaurant.administrator.view.AdminView;

/**
 * Created by Аркадий on 31.03.2016.
 */
public class AdminController {
    private AdminModel model = new AdminModel();
    private AdminView view = new AdminView(this, model);

    public static void main(String[] args) {
        AdminController adminController = new AdminController();
        adminController.view.initView();
    }

    public void startServer() {
        model.serializeNewMenu();
        final String address = view.askServerAddress();
        final int port = view.askServerPort();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Server.start(AdminController.this, address, port, model.getNeedImageDishes(),
                        model.getNotNeedImageDishes(), model.getStatusChangedDishes());
            }
        }).start();
    }

    public void updateConnectionsInfo(String text) {
        view.updateConnectionsInfo(text);
    }
}
