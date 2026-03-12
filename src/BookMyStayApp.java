import java.util.*;

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

        public boolean allocateRoom(String roomType) {
            int available = getAvailability(roomType);
            if (available > 0) {
                inventory.put(roomType, available - 1);
                return true;
            }
            return false;
        }
    }

    static class Reservation {
        private String guestName;
        private String roomType;
        private String reservationId;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
            this.reservationId = roomType.replace(" ", "") + "-" + guestName + "-" + UUID.randomUUID().toString().substring(0, 6);
        }

        public String getGuestName() {
            return guestName;
        }

        public String getRoomType() {
            return roomType;
        }

        public String getReservationId() {
            return reservationId;
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

        public Reservation getNextRequest() {
            return requestQueue.poll();
        }

        public boolean hasRequests() {
            return !requestQueue.isEmpty();
        }
    }

    static class BookingService {
        private RoomInventory inventory;
        private HashMap<String, Set<String>> allocatedRooms;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
            this.allocatedRooms = new HashMap<>();
        }

        public Reservation processRequest(Reservation r) {
            String roomType = r.getRoomType();
            if (inventory.allocateRoom(roomType)) {
                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(r.getReservationId());
                System.out.println("Reservation confirmed for " + r.getGuestName() +
                        " -> " + roomType + " | Reservation ID: " + r.getReservationId());
                return r;
            } else {
                System.out.println("Reservation failed for " + r.getGuestName() +
                        " -> " + roomType + " (No availability)");
                return null;
            }
        }
    }

    static class AddOnService {
        private String serviceName;
        private double cost;

        public AddOnService(String serviceName, double cost) {
            this.serviceName = serviceName;
            this.cost = cost;
        }

        public String getServiceName() {
            return serviceName;
        }

        public double getCost() {
            return cost;
        }
    }

    static class AddOnServiceManager {
        private HashMap<String, List<AddOnService>> serviceMap;

        public AddOnServiceManager() {
            serviceMap = new HashMap<>();
        }

        public void addServiceToReservation(String reservationId, AddOnService service) {
            serviceMap.putIfAbsent(reservationId, new ArrayList<>());
            serviceMap.get(reservationId).add(service);
            System.out.println("Service " + service.getServiceName() + " added to Reservation ID: " + reservationId);
        }

        public double calculateTotalServiceCost(String reservationId) {
            List<AddOnService> services = serviceMap.getOrDefault(reservationId, new ArrayList<>());
            double total = 0;
            for (AddOnService s : services) {
                total += s.getCost();
            }
            return total;
        }

        public void displayServices(String reservationId) {
            List<AddOnService> services = serviceMap.getOrDefault(reservationId, new ArrayList<>());
            System.out.println("Services for Reservation ID " + reservationId + ":");
            for (AddOnService s : services) {
                System.out.println("- " + s.getServiceName() + " (₹" + s.getCost() + ")");
            }
        }
    }

    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        BookingService bookingService = new BookingService(inventory);

        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Suite Room");

        Reservation confirmed1 = bookingService.processRequest(r1);
        Reservation confirmed2 = bookingService.processRequest(r2);

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        if (confirmed1 != null) {
            serviceManager.addServiceToReservation(confirmed1.getReservationId(), new AddOnService("Breakfast", 300.0));
            serviceManager.addServiceToReservation(confirmed1.getReservationId(), new AddOnService("Airport Pickup", 800.0));
            serviceManager.displayServices(confirmed1.getReservationId());
            System.out.println("Total Add-On Cost: ₹" + serviceManager.calculateTotalServiceCost(confirmed1.getReservationId()));
        }

        if (confirmed2 != null) {
            serviceManager.addServiceToReservation(confirmed2.getReservationId(), new AddOnService("Spa Access", 1200.0));
            serviceManager.displayServices(confirmed2.getReservationId());
            System.out.println("Total Add-On Cost: ₹" + serviceManager.calculateTotalServiceCost(confirmed2.getReservationId()));
        }
    }
}