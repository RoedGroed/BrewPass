package DAL;

import BE.Ticket;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketDAO {

    /**
     * Adds a new ticket to the database
     * @param ticketName name of the ticket
     * @param ticketType ticket type (Event or Special)
     */
    public void addTicket(String ticketName, String ticketType) throws IOException, SQLException {
        DBConnector dbConnector = new DBConnector();
        try (Connection conn = dbConnector.getConnection()) {
            String query = "INSERT INTO Tickets (Name, Type) VALUES (?,?)";
            try (PreparedStatement prep = conn.prepareStatement(query)) {
                prep.setString(1, ticketName);
                prep.setString(2, ticketType);
                prep.executeUpdate();
            }
        }

    }

    /**
     * Retrieving all the tickets from the database
     */
    public List<Ticket> readAllTickets() throws SQLException, IOException {
        DBConnector dbConnector = new DBConnector();
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection()) {
            String query = "SELECT * FROM dbo.Tickets";
            try (PreparedStatement prep = conn.prepareStatement(query)) {
                ResultSet resultSet = prep.executeQuery();

                while(resultSet.next()) {
                    Ticket ticket = new Ticket();
                    ticket.setTicketID(resultSet.getInt("TicketID"));
                    ticket.setTicketName(resultSet.getString("Name"));
                    ticket.setTicketType(resultSet.getString("Type"));
                    tickets.add(ticket);
                }
            }
        }
        return tickets;
    }

    /**
     * A method which links a ticket to an event
     * @param eventID the event
     * @param ticketID the ticket
     */
    public void linkTicketToEvent(int eventID, int ticketID) throws SQLException, IOException {
        DBConnector dbConnector = new DBConnector();
        try (Connection conn = dbConnector.getConnection()) {
            String query = "INSERT INTO EventTickets (EventID, TicketID) VALUES (?,?)";
            try (PreparedStatement prep = conn.prepareStatement(query)) {
                prep.setInt(1, eventID);
                prep.setInt(2, ticketID);
                prep.executeUpdate();
            }
        }
    }

    /**
     * Retrieving the tickets linked to an event
     * @param eventID the event
     */
    public List<Ticket> getLinkedTickets(int eventID) throws SQLException, IOException {
        List<Ticket> linkedTickets = new ArrayList<>();
        String query = "SELECT * FROM EventTickets WHERE EventID =?";
        DBConnector dbConnector = new DBConnector();
        try (Connection conn = dbConnector.getConnection()) {
            PreparedStatement prep = conn.prepareStatement(query);
            prep.setInt(1, eventID);

            try (ResultSet resultSet = prep.executeQuery()) {
                while (resultSet.next()) {
                    int ticketID = resultSet.getInt("TicketID");
                    Ticket ticket = getTicketByID(ticketID);
                    if (ticket != null) {
                        linkedTickets.add(ticket);
                    }
                }
            }
        }
        return linkedTickets;
    }

    /**
     * Retrieving a ticket based on its ID
     * @param ticketID the ticket
     */
    private Ticket getTicketByID(int ticketID) throws SQLException, IOException {
        String query = "SELECT * FROM Tickets WHERE TicketID = ?";
        DBConnector dbConnector = new DBConnector();
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, ticketID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve ticket details from the result set
                    int id = resultSet.getInt("TicketID");
                    String name = resultSet.getString("Name");
                    String type = resultSet.getString("Type");
                    // Create and return the Ticket object
                    return new Ticket(id, name, type);
                }
            }
        }
        // Return null if ticket with the given ID is not found
        return null;
    }

    /**
     * Finding the ticket ID by using the name of the ticket
     * @param ticketName name of ticket
     */
    public int getTicketIDByName(String ticketName) throws SQLException, IOException {
        String query = "SELECT TicketID FROM Tickets WHERE Name = ?";
        DBConnector dbConnector = new DBConnector();
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, ticketName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the ticket ID from the result set
                    return resultSet.getInt("TicketID");
                }
            }
        }
        // Return -1 if ticket with the given name is not found
        return -1;
    }

    /**
     * Removing a ticket from an event
     * @param eventID the event
     * @param ticketID the ticket
     */
    public void removeTicketFromEvent (int eventID, int ticketID) throws IOException, SQLException {
        DBConnector dbConnector = new DBConnector();
        String query = "DELETE FROM EventTickets WHERE EventID = ? AND TicketID = ?";

        try (Connection conn = dbConnector.getConnection()) {
            PreparedStatement prep = conn.prepareStatement(query);

            prep.setInt(1, eventID);
            prep.setInt(2, ticketID);
            prep.executeUpdate();
        }
    }

    /**
     * Delete a ticket from the database
     * @param ticketID the ticket
     */
    public void deleteTicket(int ticketID) throws IOException, SQLException {
        DBConnector dbConnector = new DBConnector();
        String query = "DELETE FROM Tickets WHERE TicketID = ?";

        try (Connection conn = dbConnector.getConnection()) {
            PreparedStatement prep = conn.prepareStatement(query);

            prep.setInt(1, ticketID);
            prep.executeUpdate();
        }
    }

    /**
     * Linking a ticket to the user
     * @param ticketID the ticket
     * @param userID the user
     * @param eventID the event
     * @param uuid the UUID
     */
    public void linkUserToTicket(int ticketID, int userID, int eventID, UUID uuid) throws SQLException, IOException {
        DBConnector dbConnector = new DBConnector();
        try (Connection conn = dbConnector.getConnection()) {
            String query = "INSERT INTO TicketUser (TicketID, UserID, EventID, UniqueID) VALUES (?,?,?,?)";
            try (PreparedStatement prep = conn.prepareStatement(query)) {
                prep.setInt(1, ticketID);
                prep.setInt(2, userID);
                prep.setInt(3, eventID);
                prep.setString(4, String.valueOf(uuid));
                prep.executeUpdate();
            }
        }
    }

    /**
     * Link the special ticket
     * @param ticketID the ticket
     * @param uuid the uuid
     */
    public void linkSpecialTicket(int ticketID, UUID uuid) throws IOException, SQLException {
        DBConnector dbConnector = new DBConnector();
        try (Connection conn = dbConnector.getConnection()) {
            String query = "INSERT INTO TicketUser (TicketID, UniqueID) VALUES (?,?)";
            try (PreparedStatement prep = conn.prepareStatement(query)) {
                prep.setInt(1, ticketID);
                prep.setString(2, String.valueOf(uuid));
                prep.executeUpdate();
            }
        }
    }
}
