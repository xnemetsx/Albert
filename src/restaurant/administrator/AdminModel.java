package restaurant.administrator;

import restaurant.kitchen.Dish;
import restaurant.kitchen.Menu;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Аркадий on 31.03.2016.
 */
public class AdminModel {
    private final String MENU_PATH = "src/restaurant/administrator/menu.txt";
    private Menu oldMenu;
    private Menu newMenu;
    private List<Dish> needImageDishes = new ArrayList<>();
    private List<Dish> notNeedImageDishes = new ArrayList<>();
    private List<Dish> statusChangedDishes = new ArrayList<>();

    public AdminModel() {
        oldMenu = initMenu();
        try {
            newMenu = oldMenu.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public List<Dish> getNeedImageDishes() {
        return needImageDishes;
    }

    public List<Dish> getNotNeedImageDishes() {
        return notNeedImageDishes;
    }

    public List<Dish> getStatusChangedDishes() {
        return statusChangedDishes;
    }

    public Menu getNewMenu() {
        return newMenu;
    }

    private Menu initMenu() {
        try(FileInputStream fis = new FileInputStream(MENU_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            Menu menu = (Menu) ois.readObject();
            fis.close();
            return menu;
        } catch (IOException | ClassNotFoundException e) {
            return new Menu();
        }
    }

    public boolean changeDishStatus(String dishName, boolean deleted) {
        Dish dish = oldMenu.setDeletedAndGetDishByName(dishName, deleted);
        if(dish != null) {
            statusChangedDishes.remove(dish);
            statusChangedDishes.add(dish);
            return true;
        } else {
            return setDeletedInNewAddedDishes(dishName, deleted);
        }
    }

    /**
     * @param dishName
     * @param deleted
     * @return true if newDishes contained dish with dishName
     * and deleted field was changed; false, if it didn't contain
     * such dish.
     */
    private boolean setDeletedInNewAddedDishes(String dishName, boolean deleted) {
        for(Dish dish: needImageDishes) {
            if(dish.getName().equals(dishName)) {
                dish.setDeleted(deleted);
                return true;
            }
        }
        for(Dish dish: notNeedImageDishes) {
            if(dish.getName().equals(dishName)) {
                dish.setDeleted(deleted);
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if dish is new and was added to newMenu,
     * false if dish is already exists and was edited
     */
    public boolean addOrEditDish(
            String type, String name, String shortDesc,
            String fullDesc, String imagePath, double price) {
        System.out.println(String.format(
                "\ntype: %s\nname: %s\nshortDesc: %s\nfullDesc: %s\nimagePath: %s\nprice: %f",
                type, name, shortDesc, fullDesc, imagePath, price));

        Dish existedDish = oldMenu.getDishByTypeAndName(type, name);
        if(existedDish != null) {
            processExistedDish(existedDish, shortDesc, fullDesc, imagePath, price);
            return false;
        } else {
            return processNewDish(type, name, shortDesc, fullDesc, imagePath, price);
        }
        //TODO
    }

    /**
     * @param type
     * @param name
     * @param shortDesc
     * @param fullDesc
     * @param imagePath
     * @param price
     * @return true if dish wasn't added earlier,
     * false in another case
     */
    private boolean processNewDish(
            String type, String name, String shortDesc,
            String fullDesc, String imagePath, double price) {
        Dish newDish = new Dish(type, name, fullDesc, shortDesc, imagePath, price);

        boolean alreadyAdded = newMenu.removeDish(newDish);
        if(alreadyAdded) {
            notNeedImageDishes.remove(newDish);
            needImageDishes.remove(newDish);
        }

        newMenu.addDish(newDish);
        if("no image".equals(imagePath)) {
            notNeedImageDishes.add(newDish);
        } else {
            needImageDishes.add(newDish);
        }

        return !alreadyAdded;
    }

    private void processExistedDish(Dish existedDish, String shortDesc, String fullDesc, String imagePath, double price) {
        existedDish.setFullDesc(fullDesc);
        existedDish.setShortDesc(shortDesc);
        existedDish.setPrice(price);

        if("no image".equals(imagePath)) {
            notNeedImageDishes.add(existedDish);
        } else {
            existedDish.setImagePath(imagePath);
            needImageDishes.add(existedDish);
        }

        newMenu.removeDish(existedDish);
        newMenu.addDish(existedDish);
    }

    public Menu getTestMenu() {
        newMenu = new Menu();
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
        newMenu.addDish(dish1);
        newMenu.addDish(dish2);
        newMenu.addDish(dish3);
        newMenu.addDish(dish4);
        newMenu.addDish(dish5);
        newMenu.addDish(dish6);
        newMenu.addDish(dish7);
        return newMenu;
    }

    public void serializeNewMenu() {
        updateOldDishesInNewMenu();
        try {
            FileOutputStream fos = new FileOutputStream(MENU_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(newMenu);
            oos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateOldDishesInNewMenu() {
        for(Dish changedDish: statusChangedDishes) {
            Dish dish = newMenu.getDishByTypeAndName(
                    changedDish.getType(), changedDish.getName());
            dish.setDeleted(changedDish.isDeleted());
        }
    }
}
