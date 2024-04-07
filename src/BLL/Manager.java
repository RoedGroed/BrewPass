package BLL;

import BE.Event;
import BE.User;
import DAL.EventDAO;
import DAL.UserDAO;
import GUI.Model.Model;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Manager {
    private UserDAO userDAO = new UserDAO();
    private EventDAO eventDAO = new EventDAO();

    public List<User> getAllUsers() throws SQLException, IOException {
        return userDAO.readAllUsers();
    }

    public void updateUser(User selectedUser) throws SQLException, IOException {
        userDAO.updateUser(selectedUser);
    }

    /*public User validateUser(String username, String password) throws SQLException, IOException {
        return userDAO.getUserByNameAndPassword(username, password);
    }*/

    public User validateUser(String email, String password) throws SQLException, IOException {
        User user = userDAO.getUserByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())){
            return user;
        }
        return null;
    }

        public Manager() {
            this.userDAO = new UserDAO();
        }

    public void createNewUser(String username, String password, String role, String email) throws SQLException, IOException {
        User user = new User(username, password, email);
        user.setRole(role);
        userDAO.createNewUser(user);
    }

    ///EVENT MANAGEMENT///

    public Event createEvent (Event selectedEvent) throws IOException, SQLException {
        return eventDAO.createEvent(selectedEvent);
    }

    public void deleteEvent(Event selectedEvent) throws SQLException, IOException {
        eventDAO.deleteEvent(selectedEvent);
    }

    public void updateEvents(Event selectedEvent) throws SQLException, IOException {
        eventDAO.updateEvents(selectedEvent);
    }

    public Manager(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    public List<Event> getAllEvents() throws SQLException, IOException {
        return eventDAO.readAllEvents();
    }

    public List<Event> getEventsForEventCo(int userId) throws SQLException, IOException {
        return eventDAO.readEventsForCoordinator(userId);
    }
}

