package com.company.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.apps.util.Console;
import com.apps.util.Prompter;

import java.util.Scanner;

public class Player {
    public String name;
    public double hp = 10;
    public int dp = 1;
    public List<String> inventory = new ArrayList<>();
    private JsonTools tools = new JsonTools();
    private String currentRoom = "Beach Shack";
    private Map<String, String> directions;
    private ArrayList<String> locationItems;
    private ArrayList<String> locationNPC;
    Prompter prompter = new Prompter(new Scanner(System.in));
    ArrayList<Map<String, Object>> locationData = tools.readJson("location.json");
    ArrayList<Map<String, Object>> characterData = tools.readJson("character.json");

    public Player() {
    }

    public Player(String name, double hp, int dp, List<String> inventory) {
        this.name = name;
        this.hp = hp;
        this.dp = dp;
        this.inventory = inventory;
    }

    public void newPlayer() {
        System.out.println("\nNew Game Created");
        setPlayerName(prompter.prompt("\nAhoy, What is your name adventurer? "));
        System.out.println();
    }

    public void status() {
        for (Map<String, Object> entry : locationData) {
            if (entry.get("name").equals(currentRoom)) {
                directions = (Map<String, String>) entry.get("directions");
                locationItems = (ArrayList<String>) entry.get("items");
                locationNPC = (ArrayList<String>) entry.get("NPC");

                System.out.printf("Location: %s \n", entry.get("name"));
                Console.pause(1000);
                System.out.printf("\nDescription: %s ", entry.get("description"));
                Console.pause(1000);
                System.out.println("\nDirections: ");
                directions.forEach((k, v) -> {
                    if (v.length() > 0) {
                        System.out.printf("%s: %s\n", k, v);
                    }
                });
                Console.pause(1000);
                if (!locationNPC.isEmpty()) {
                    System.out.printf("\nCharacters present: \n");
                    locationNPC.forEach(e -> System.out.println(e));
                    System.out.println("\n");
                }
                Console.pause(1000);
                if (!locationItems.isEmpty()) {
                    System.out.printf("Items you see: \n");
                    locationItems.forEach(e -> System.out.println(e));
                    System.out.println("\n");
                }
                Console.pause(1000);
                System.out.printf("HP: %s     Damage Points: %s      Inventory: %s ", hp, dp, inventory);
            }

        }
    }

    public void grabItem(String item) {
        if (!item.equals("parrot") && locationItems.contains(item)) {
            //remove from the location
            locationItems.remove(item);
            //add to inventory
            inventory.add(item);
            this.locationItems = locationItems;
            if (inventory.contains("sword")) {
                dp = 5;
            }
        }
        if (inventory.contains("cracker") && locationItems.contains("parrot") && item.equals("parrot")) {
            inventory.remove("cracker");
            inventory.add(item);
            locationItems.remove(item);
            System.out.println("You were able to grab the parrot by feeding it a cracker.");
        } else if (!inventory.contains("cracker") && locationItems.contains("parrot") && item.equals("parrot")) {
            System.out.println("You were not able to grab the Parrot.\n");
        }
    }

    public void useItem(String item) {
        String file = "item.json";
        ArrayList<Map<String, Object>> itemData = tools.readJson(file);
        if (locationItems.contains(item) || inventory.contains(item)) {
            for (Map<String, Object> entry : itemData) {
                if (inventory.contains(item) && entry.get("name").toString().toLowerCase().equals(item)) {
                    System.out.println(entry.get("description") + "\n");
                    if (item.equals("mango")) {
                        hp += 5;
                        inventory.remove(item);
                    } else if (item.equals("banana")) {
                        hp += 10;
                        inventory.remove(item);
                    } else if (item.equals("sword")) {
                        System.out.println("In order to wield the sword, please enter 'ATTACK' [name]");
                    } else {
                        System.out.println("You can't use that item in this manner. Don't be a fool.");
                    }
                }
            }
        } else {
            System.out.println("You can not use this item");
        }
    }

    public void dropItem(String item) {
        if (!item.equals("parrot") && locationItems.contains(item)) {
            //remove from the location
            locationItems.add(item);
            //add to inventory
            inventory.remove(item);
        }
    }

    public void talk(String name) {
        if (locationNPC.contains(name)) {
            for (Map<String, Object> entry : characterData) {
                if (entry.get("name").equals(name)) {
                    while (true) {
                        System.out.println("Speaking to: " + entry.get("name"));
                        Map<String, String> dialogue = (Map<String, String>) entry.get("quote");
                        System.out.println(dialogue.get("initial"));
                        if (dialogue.containsKey("quest")) {
                            handleQuest(entry, dialogue);
                            break;
                        } else if (entry.containsKey("items")) {
                            ArrayList<String> itemsArray = (ArrayList<String>) entry.get("items");
                            for (String item : itemsArray) {
                                inventory.add(item);
                                System.out.println(item + " was added to inventory.\n");
                            }
                            entry.remove("items");
                        }
                        break;
                    }
                }

            }
        } else {
            System.out.println("Invalid Name");
        }
    }

    public void go(String directionInput) throws NullPointerException {
        if (directions.containsKey(directionInput)) {
            String location = directions.get(directionInput);
            if (!location.equals("Boat") && !location.equals("Monkey Temple")) {
                currentRoom = location;
            } else if (inventory.contains("Boat Pass") && location.equals("Boat")) {
                currentRoom = location;
            } else if (!inventory.contains("Boat Pass") && location.equals("Boat")) {
                System.out.println("Get a Boat Pass from a Pirate Captain\n");
            }
        } else {
            System.out.println("Invalid Direction");
        }
    }

    public void look(String item) {
        String file = "item.json";
        ArrayList<Map<String, Object>> itemData = tools.readJson(file);
        if (locationItems.contains(item) || inventory.contains(item)) {
            for (Map<String, Object> entry : itemData) {
                if (entry.get("name").toString().toLowerCase().equals(item)) {
                    System.out.println(entry.get("description") + "\n");
                }
            }
        }
    }

    public void attack(String name) {
        if (locationNPC.contains(name)) {
            for (Map<String, Object> entry : characterData) {
                if (entry.get("name").equals(name)) {
                    while (true) {
                        System.out.println(entry.get("name") + "'s current hp is : " + entry.get("hp"));
                        System.out.println("You are attacking: " + entry.get("name"));
                        Double points = (Double) entry.get("hp");
                        points -= dp;
                        entry.put("hp", points);
                        System.out.println(entry.get("name") + "'s hp after attack is : " + points);
                        Double damage = (Double) entry.get("dp");
                        hp -= damage;
                        System.out.println(entry.get("name") + " has attacked you back. Your HP is now " + hp);

                        if (points <= 0 && entry.containsKey("items")) {
                            System.out.println(name + " has wasted " + entry.get("name") + "!");
                            ArrayList<String> itemsArray = (ArrayList<String>) entry.get("items");
                            for (String item : itemsArray) {
                                inventory.add(item);
                                System.out.println(entry.get("name") + "'s " + item + " has been added to your inventory");
                            }
                        }
                        break;
                    }
                }
            }
        } else {
            System.out.println("Invalid Target");
        }
    }

    private void handleQuest(Map<String, Object> entry, Map<String, String> dialogue) {
        List<String> req = (List<String>) entry.get("questReq");
        if (inventory.containsAll(req)) {
            System.out.println(dialogue.get("reward"));
            if (entry.containsKey("reward")) {
                ArrayList<String> rewardsArray = (ArrayList<String>) entry.get("reward");
                for (String reward : rewardsArray) {
                    inventory.add(reward);
                    System.out.println(reward + " was added to inventory.\n");
                }
                entry.remove("reward");
                inventory.removeAll(req);
            }
        } else {
            System.out.println(dialogue.get("quest"));
            if (dialogue.containsKey("yes")) {
                String userInput = prompter.prompt("");
                if (userInput.equals("yes")) {
                    System.out.println(dialogue.get("yes"));
                    if (entry.containsKey("items")) {
                        ArrayList<String> itemsArray = (ArrayList<String>) entry.get("items");
                        for (String item : itemsArray) {
                            inventory.add(item);
                            System.out.println(item + " was added to inventory.\n");
                        }
                        entry.remove("items");
                    }
                }
                if (userInput.equals("no")) {
                    System.out.println(dialogue.get("no"));
                }
            }
        }
    }



    private void setPlayerName(String name) {
        this.name = name;
    }

    public String getPlayerName() {
        return name;
    }

    public List<String> getItems(Player player) {
        return player.inventory;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }
}
