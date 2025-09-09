import java.util.*;

// ===== Seat Class =====
class Seat {
    protected String seatNo;
    protected String section;
    protected String type;
    protected double price;
    protected String status;

    public Seat(String seatNo, String section, String type, double price) {
        this.seatNo = seatNo;
        this.section = section;
        this.type = type;
        this.price = price;
        this.status = "Available";
    }

    public String getSeatNo() { return seatNo; }
    public String getSection() { return section; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
    public void setPrice(double price) { this.price = price; }

    public double calculatePrice() { return price; }

    public void display() {
        System.out.println(seatNo + " (" + section + ", " + type + ") - " + price + " - " + status);
    }
}

// ===== VIPSeat (Inheritance + Overriding) =====
class VIPSeat extends Seat {
    public VIPSeat(String seatNo, String section) {
        super(seatNo, section, "VIP", 200.0);
    }

    @Override
    public double calculatePrice() {
        return price * 1.2;
    }

    @Override
    public void display() {
        System.out.println("[VIP] " + seatNo + " (" + section + ") - " + calculatePrice() + " - " + status);
    }
}

// ===== RegularSeat (Inheritance + Overriding) =====
class RegularSeat extends Seat {
    public RegularSeat(String seatNo, String section) {
        super(seatNo, section, "Regular", 100.0);
    }

    @Override
    public double calculatePrice() {
        return price;
    }

    @Override
    public void display() {
        System.out.println("[Regular] " + seatNo + " (" + section + ") - " + calculatePrice() + " - " + status);
    }
}

// ===== Event Class =====
class Event {
    private String eventId;
    private String name;
    private String date;
    private String venue;
    private List<Seat> seatMap;

    public Event(String eventId, String name, String date, String venue) {
        this.eventId = eventId;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.seatMap = new ArrayList<>();
    }

    public String getEventId() { return eventId; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getVenue() { return venue; }
    public List<Seat> getSeatMap() { return seatMap; }

    public void addSeat(Seat s) { seatMap.add(s); }

    public void displaySeats() {
        for (Seat s : seatMap) s.display();
    }

    // Method overloading
    public List<Seat> findSeats(int count) {
        List<Seat> available = new ArrayList<>();
        for (Seat s : seatMap) {
            if (s.getStatus().equals("Available")) {
                available.add(s);
                if (available.size() == count) break;
            }
        }
        return available;
    }

    public List<Seat> findSeats(String type) {
        List<Seat> available = new ArrayList<>();
        for (Seat s : seatMap) {
            if (s.getStatus().equals("Available") && s.getType().equalsIgnoreCase(type)) {
                available.add(s);
            }
        }
        return available;
    }

    public double occupancyPercent() {
        int total = seatMap.size();
        int booked = 0;
        for (Seat s : seatMap) if (s.getStatus().equals("Booked")) booked++;
        return (booked * 100.0) / total;
    }
}

// ===== Booking Class =====
class Booking {
    private String bookingId;
    private String eventId;
    private List<Seat> seats;
    private String buyer;
    private double amount;
    private String state;

    public Booking(String bookingId, String eventId, List<Seat> seats, String buyer) {
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.seats = seats;
        this.buyer = buyer;
        this.amount = computeTotal();
        this.state = "Confirmed";
    }

    private double computeTotal() {
        double sum = 0;
        for (Seat s : seats) sum += s.calculatePrice();
        return sum;
    }

    public String getBookingId() { return bookingId; }
    public String getEventId() { return eventId; }
    public String getBuyer() { return buyer; }
    public double getAmount() { return amount; }
    public String getState() { return state; }

    public void cancel() {
        this.state = "Cancelled";
        for (Seat s : seats) s.setStatus("Available");
    }

    public void printReceipt() {
        System.out.println("\n--- Receipt ---");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Event ID: " + eventId);
        System.out.println("Buyer: " + buyer);
        System.out.println("Seats:");
        for (Seat s : seats) System.out.println("  - " + s.getSeatNo() + " (" + s.getType() + ")");
        System.out.println("Total Amount: " + amount);
        System.out.println("Status: " + state);
        System.out.println("----------------\n");
    }
}

// ===== TicketingService Class =====
class TicketingService {
    private List<Event> events;
    private Map<String, Booking> bookings;

    public TicketingService() {
        this.events = new ArrayList<>();
        this.bookings = new HashMap<>();
    }

    public void addEvent(Event e) { events.add(e); }

    public Event searchEvent(String eventId) {
        for (Event e : events) if (e.getEventId().equals(eventId)) return e;
        return null;
    }

    public Booking bookSeats(String eventId, List<Seat> seats, String buyer) {
        for (Seat s : seats) {
            if (!s.getStatus().equals("Available")) {
                System.out.println("Seat " + s.getSeatNo() + " not available!");
                return null;
            }
        }
        for (Seat s : seats) s.setStatus("Booked");
        Booking booking = new Booking(UUID.randomUUID().toString(), eventId, seats, buyer);
        bookings.put(booking.getBookingId(), booking);
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking b = bookings.get(bookingId);
        if (b != null && b.getState().equals("Confirmed")) {
            b.cancel();
            System.out.println("Booking " + bookingId + " cancelled and refunded.");
        }
    }

    public void showSummary() {
        for (Event e : events) {
            System.out.println("Event: " + e.getName() + " (" + e.getEventId() + ")");
            System.out.println("Occupancy: " + e.occupancyPercent() + "%");
        }
    }
}

// ===== Main Class (Entry Point) =====
public class TicketAppMain {
    public static void main(String[] args) {
        TicketingService service = new TicketingService();

        Event concert = new Event("E001", "Rock Concert", "2025-10-10", "Stadium");

        for (int i = 1; i <= 5; i++) concert.addSeat(new RegularSeat("R" + i, "A"));
        for (int i = 1; i <= 3; i++) concert.addSeat(new VIPSeat("V" + i, "B"));

        service.addEvent(concert);

        System.out.println("=== Available Seats ===");
        concert.displaySeats();

        List<Seat> seatsToBook = concert.findSeats("VIP");
        Booking b1 = service.bookSeats(concert.getEventId(), seatsToBook.subList(0, 2), "Alice");

        if (b1 != null) b1.printReceipt();

        service.cancelBooking(b1.getBookingId());
        service.showSummary();
    }
}
