package dev.kajlew.cinema.controller;

import dev.kajlew.cinema.exceptions.ErrorMessage;
import dev.kajlew.cinema.model.Room;
import dev.kajlew.cinema.model.Seat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class Controller {
    private final Map<UUID, Seat> bookedSeats;
    private final Room room;

    public Controller() {
        room = new Room(9, 9);
        bookedSeats = new HashMap<>();
    }

    @GetMapping("/seats")
    public Room getAvailableSeats() {
        return room;
    }

    @PostMapping("/purchase")
    public ResponseEntity<Object> bookSeat(@RequestBody Seat seatInfo) throws JsonProcessingException {
        // Generate a new UUID token
        UUID token = UUID.randomUUID();
        int row = seatInfo.row();
        int column = seatInfo.column();

        if (row < 1 || row > room.total_rows() || column < 1 || column > room.total_columns()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("The number of a row or a " +
                    "column is out of bounds!"));
        }

        Optional<Seat> targetSeat = room.available_seats().stream()
                .filter(seat -> seat.row().equals(row) && seat.column().equals(column))
                .findFirst();

        if (targetSeat.isPresent()) {
            Seat seat = targetSeat.get();

            // Mark the seat as purchased
            room.available_seats().remove(seat);

            // Store the seat with its token
            bookedSeats.put(token, seat);

            // Create the purchase response
            ObjectMapper objectMapper = new ObjectMapper();
            String response = objectMapper.writeValueAsString(Map.of("token", token.toString(), "ticket", seat));

            return ResponseEntity.ok(response);
        } else {
            // Seat is already booked
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("The ticket has been already " +
                    "purchased!"));
        }
    }

    @PostMapping("/return")
    public ResponseEntity<Object> returnTicket(@RequestBody Map<String, String> requestBody) throws JsonProcessingException {
        String token = requestBody.get("token");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Token is missing or empty!"));
        }

        UUID uuidToken;
        try {
            uuidToken = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Invalid token format!"));
        }

        Seat returnedSeat = bookedSeats.get(uuidToken);

        if (returnedSeat != null) {
            // Mark the seat as available again
            room.available_seats().add(returnedSeat);

            // Remove the ticket from the bookedSeats map
            bookedSeats.remove(uuidToken);

            // Create the refund response
            ObjectMapper objectMapper = new ObjectMapper();
            String response = objectMapper.writeValueAsString(Map.of("returned_ticket", returnedSeat));

            return ResponseEntity.ok(response);
        } else {
            // Ticket with the provided token was not found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Wrong token!"));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam Map<String, String> params) {
        String password = params.get("password");

        if (password != null && password.equals("super_secret")) {
            int currentIncome = calculateCurrentIncome();
            int numberOfAvailableSeats = room.available_seats().size();
            int numberOfPurchasedTickets = bookedSeats.size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("current_income", currentIncome);
            stats.put("number_of_available_seats", numberOfAvailableSeats);
            stats.put("number_of_purchased_tickets", numberOfPurchasedTickets);

            return ResponseEntity.ok(stats);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("The password is wrong!"));
        }
    }

    private int calculateCurrentIncome() {
        int currentIncome = 0;
        for (Seat seat : bookedSeats.values()) {
            currentIncome += seat.price();
        }
        return currentIncome;
    }
}
