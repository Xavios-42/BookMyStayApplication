import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

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
    }

    static class Reservation {
        private String guestName;
        private String roomType;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }

        public String getGuestName() {
            return guestName;
        }

        public String getRoomType() {
            return roomType;
        }

        public void displayRequest() {
            System.out.println("Reservation Request -> Guest: " + guestName +
                    ", Room Type: " + roomType);
        }
    }

    static class BookingRequestQueue {
        private Queue<Reservation> requestQueue;

        public BookingRequestQueue() {
            requestQueue = new LinkedList<>();
        }

        public void addRequest(Reservation reservation) {
            requestQueue.add(reservation);
            System.out.println("Request added for " + reservation.getGuestName());
        }

        public void displayQueue() {
            System.out.println("Current Booking Requests (FIFO):");
            for (Reservation r : requestQueue) {
                r.displayRequest();
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

        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Suite Room");
        Reservation r3 = new Reservation("Charlie", "Double Room");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        bookingQueue.displayQueue();
    }
}