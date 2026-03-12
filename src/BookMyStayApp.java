import java.util.HashMap;

public class BookMyStayApp {

    abstract static class Room {
        private String roomType;
        private int numberOfBeds;
        private double price;

        public Room(String roomType, int numberOfBeds, double price) {
            this.roomType = roomType;
            this.numberOfBeds = numberOfBeds;
            this.price = price;
        }

        public String getRoomType() {
            return roomType;
        }

        public int getNumberOfBeds() {
            return numberOfBeds;
        }

        public double getPrice() {
            return price;
        }

        public abstract void displayDetails();
    }

    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 1000.0);
        }
        @Override
        public void displayDetails() {
            System.out.println("Type: " + getRoomType() +
                    ", Beds: " + getNumberOfBeds() +
                    ", Price: ₹" + getPrice());
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 1800.0);
        }
        @Override
        public void displayDetails() {
            System.out.println("Type: " + getRoomType() +
                    ", Beds: " + getNumberOfBeds() +
                    ", Price: ₹" + getPrice());
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 3500.0);
        }
        @Override
        public void displayDetails() {
            System.out.println("Type: " + getRoomType() +
                    ", Beds: " + getNumberOfBeds() +
                    ", Price: ₹" + getPrice());
        }
    }

    static class RoomInventory {
        private HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
        }

        public void addRoomType(String roomType, int count) {
            inventory.put(roomType, count);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public void updateAvailability(String roomType, int newCount) {
            if (inventory.containsKey(roomType)) {
                inventory.put(roomType, newCount);
            }
        }

        public void displayInventory() {
            System.out.println("Current Inventory:");
            for (String type : inventory.keySet()) {
                System.out.println(type + " -> Available: " + inventory.get(type));
            }
        }
    }

    public static void main(String[] args) {
        Room single = new SingleRoom();
        Room doubleR = new DoubleRoom();
        Room suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.getRoomType(), 5);
        inventory.addRoomType(doubleR.getRoomType(), 3);
        inventory.addRoomType(suite.getRoomType(), 2);

        single.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(single.getRoomType()));

        doubleR.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(doubleR.getRoomType()));

        suite.displayDetails();
        System.out.println("Available: " + inventory.getAvailability(suite.getRoomType()));

        inventory.displayInventory();
    }
}