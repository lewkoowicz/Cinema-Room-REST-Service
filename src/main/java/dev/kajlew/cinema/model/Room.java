package dev.kajlew.cinema.model;

import java.util.ArrayList;
import java.util.List;

public record Room(
        int total_rows,
        int total_columns,
        List<Seat> available_seats) {
    public Room(int total_rows, int total_columns) {
        this(total_rows, total_columns, new ArrayList<>());
        for (int row = 1; row <= total_rows; row++) {
            for (int column = 1; column <= total_columns; column++) {
                if (row <= 4) {
                    available_seats.add(new Seat(row, column, 10));
                } else {
                    available_seats.add(new Seat(row, column, 8));
                }
            }
        }
    }
}
