package restaurant.client;

import restaurant.kitchen.Dish;
import restaurant.kitchen.Menu;
import restaurant.kitchen.Order;

import java.io.*;

/**
 * Created by Аркадий on 20.03.2016.
 */
public class ClientModel {
    private final String MENU_PATH = "src/restaurant/client/menu.txt";
    private final String DISH_IMAGES_PATH = "src/restaurant/client/view/resources/dishimages/";

    private ClientController controller;

    private Menu menu;
    private Order order;
    private int tableNumber;
    private String currentClientName;
    private double currentBill;
    private double finalBill;

    public ClientModel(ClientController controller) {
        this.controller = controller;

        try(FileInputStream fileIS = new FileInputStream(MENU_PATH);
            ObjectInputStream objectIS = new ObjectInputStream(fileIS)) {
            menu = (Menu) objectIS.readObject();
            fileIS.close();
        } catch (IOException | ClassNotFoundException e) {
            menu = new Menu();
        }
    }

    public double getFinalBill() {
        return finalBill;
    }

    public void setFinalBill(double finalBill) {
        this.finalBill = finalBill;
    }

    public double getCurrentBill() {
        return currentBill;
    }

    public void setCurrentBill(double currentBill) {
        this.currentBill = currentBill;
        if(currentBill < 0) {
            this.currentBill = 0;
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getCurrentClientName() {
        return currentClientName;
    }

    public void setCurrentClientName(String currentClientName) {
        this.currentClientName = currentClientName;
    }

    public Menu getTestMenu() {
        Menu menu = new Menu();
        Dish dish1 = new Dish("burgers", "EXTRA LONG BUTTERY CHEESEBURGER",
                "The Extra Long Buttery Cheeseburger features two beef patties " +
                        "topped with freshly cut onions, crisp iceberg lettuce, ketchup, " +
                        "melted American cheese, a buttery garlic flavored sauce and a " +
                        "creamy mayonnaise spread, all served on a warm toasted hoagie bun.",
                "The Extra Long Buttery Cheeseburger features two beef patties " +
                        "topped with freshly cut onions, crisp iceberg lettuce, ketchup",
                null, 1.6);
        Dish dish2 = new Dish("burgers", "BACON & CHEESE WHOPPER® SANDWICH", null,
                "Our Bacon and Cheese WHOPPER® Sandwich is a ¼ lb.* of savory " +
                        "flame-grilled beef topped with thick-cut smoked bacon",
                null, 2);
        Dish dish3 = new Dish("burgers", "EXTRA LONG CHEESEBURGER", null,
                "Our Extra Long Cheeseburger features two beef patties topped with " +
                        "freshly cut onions, crisp iceburg lettuce, ketchup, melted",
                null, 1.5);
        Dish dish4 = new Dish("burgers", "WHOPPER® SANDWICH", null,
                "Our WHOPPER® Sandwich is a ¼ lb* of savory flame-grilled beef " +
                        "topped with juicy tomatoes, fresh lettuce, creamy ",
                null, 2.3);
        Dish dish5 = new Dish("burgers", "TRIPLE WHOPPER® SANDWICH", null,
                "Our TRIPLE WHOPPER® Sandwich boasts three ¼ lb* savory " +
                        "flame-grilled beef patties topped with juicy tomatoes, fresh ",
                null, 3);
        Dish dish6 = new Dish("burgers", "BIG KING™ SANDWICH", null,
                "Our BIG KING™ Sandwich features two savory flame-grilled beef patties" +
                        ", topped with, melted American cheese, fresh cut ",
                null, 3);
        Dish dish7 = new Dish("beverages", "SPRITE®", null,
                "Let Sprite® refresh your day with the great taste of lemon-lime.",
                null, 0.5);
        menu.addDish(dish1);
        menu.addDish(dish2);
        menu.addDish(dish3);
        menu.addDish(dish4);
        menu.addDish(dish5);
        menu.addDish(dish6);
        menu.addDish(dish7);
        return menu;
    }

    public void addDishToOrder(Dish dish) {
        if(order == null){
            order = new Order();
            order.setClientName(currentClientName);
        }
        order.addDish(dish);
    }

    public void setOrderEmpty() {
        Order emptyOrder = new Order();
        emptyOrder.setClientName(getCurrentClientName());
        setOrder(emptyOrder);
    }

    public void changeDishStatus(Dish dish) {
        Dish changingDish = menu.getDishByTypeAndName(
                dish.getType(), dish.getName());
        changingDish.setDeleted(dish.isDeleted());
    }

    public void processAddedOrEditedDish(Dish dish, boolean needImage) throws IOException {
        boolean alreadyExisted = processAsExistedDish(dish, needImage);
        if(!alreadyExisted) {
            processAsNewDish(dish, needImage);
        }
    }

    private boolean processAsExistedDish(Dish dish, boolean needImage) throws IOException {
        Dish existedDish = menu.getDishByTypeAndName(
                dish.getType(), dish.getName());
        if(existedDish != null) {
            existedDish.setFullDesc(dish.getFullDesc());
            existedDish.setShortDesc(dish.getShortDesc());
            existedDish.setPrice(dish.getPrice());
            if(needImage) {
                String imagePath = dish.getImagePath();
                String imageName = subImageName(imagePath);
                imagePath = DISH_IMAGES_PATH + imageName;
                existedDish.setImagePath(imagePath);
                controller.downloadImage(imagePath);
            }
            return true;
        }
        return false;
    }

    private String subImageName(String imagePath) {
        String imageName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
        imageName = imageName.substring(imageName.lastIndexOf('\\') + 1);
        return imageName;
    }

    private void processAsNewDish(Dish dish, boolean needImage) throws IOException {
        menu.addDish(dish);
        if(needImage) {
            String imagePath = dish.getImagePath();
            System.out.println(imagePath);
            String imageName = subImageName(imagePath);
            System.out.println(imageName);
            dish.setImagePath(DISH_IMAGES_PATH + imageName);
            controller.downloadImage(dish.getImagePath());
        }
    }

    public void serializeMenu() {
        try {
            FileOutputStream fos = new FileOutputStream(MENU_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(menu);
            oos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
