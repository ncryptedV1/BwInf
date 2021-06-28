package de.ncrypted.superstar;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ncrypted
 */
public class Superstar {

    private static List<User> users = new ArrayList();
    private static List<Connection> followCache = new ArrayList<>();
    private static int costs = 0;

    public static void main(String[] args) {
        String userDataFileName = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (userDataFileName == null) {
            System.out.println("Bitte geben Sie den Namen der Beispieldatei an. (Bsp.: superstar1.txt)");
            try {
                String line = reader.readLine();
                if (Superstar.class.getClassLoader().getResource("examples/" + line) != null) {
                    userDataFileName = line;
                }
            } catch (IOException e) {
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
        }

        System.out.println("---INFOS---");

        // Lese die Daten aus der Datei ein
        readUsersData(userDataFileName);
        System.out.println("---ALGORITHMUS---");
        // Fuehre den Algorithmus aus
        User superstar = getSuperstar();
        System.out.println("---ERGEBNIS---");
        System.out.println("User: " + users.size());
        System.out.println("Superstar: " + (superstar == null ? "/" : superstar.getName()));
        System.out.println("Maximale Kosten: " + (2 * ((users.size() - 1) * users.size() / 2)));
        System.out.println("Anfragen/Kosten: " + costs);
    }

    private static void readUsersData(String fileName) {
        try {
            InputStream fileStream = Superstar.class.getClassLoader().getResourceAsStream("examples/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            int current = 0;
            while (line != null) {
                // Pruefe, ob es sich um die 1. Zeile handlet -> User einlesen
                if (current == 0) {
                    Arrays.asList(line.split(" ")).forEach(userName -> users.add(new User(userName)));
                } else {
                    User first = getByName(line.split(" ")[0]);
                    User second = getByName(line.split(" ")[1]);
                    first.follow(second);
                }
                current++;
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static User getSuperstar() {
        first:
        for (User user : users) {
            // Pruefe ob der User jemandem folgt & ob alle dem User folgen
            for (User other : users) {
                if (other == user) {
                    continue;
                }
                // Falls er jemandem folgt, kann er kein Superstar sein -> Gehe zum nächsten User
                if (doesFollow(user, other)) {
                    continue first;
                }
                // Falls jemand ihm nicht folgt, kann er kein Superstar sein -> Gehe zum nächsten User
                if (!doesFollow(other, user)) {
                    continue first;
                }
            }
            return user;
        }
        return null;
    }

    public static class User {
        private String name;
        private List<User> following = new ArrayList<>();

        public User(String name) {
            System.out.println("neuer User: " + name);
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void follow(User toFollow) {
            System.out.println(name + " folgt " + toFollow.getName());
            following.add(toFollow);
        }
    }

    private static class Connection {
        private User user1;
        private User user2;
        private boolean follows;

        public Connection(User user1, User user2, boolean follows) {
            this.user1 = user1;
            this.user2 = user2;
            this.follows = follows;
        }

        public User getUser1() {
            return user1;
        }

        public User getUser2() {
            return user2;
        }

        public boolean isFollowing() {
            return follows;
        }

    }

    private static boolean doesFollow(User user1, User user2) {
        // Pruefe ob die Abfrage bereits gemacht wurde, um Kosten zu vermeiden
        for (Connection connection : followCache) {
            if (connection.getUser1() == user1 && connection.getUser2() == user2) {
                return connection.isFollowing();
            }
        }

        costs += 1;
        boolean follows = user1.following.contains(user2);
        System.out.println(user1.getName() + "->" + user2.getName() + "? " + follows);
        // Fuege die Anfrage dem Cache hinzu, um Kosten zu sparen (theoretisch reichen positiven Anfragen, aufgrund des Algorithmus)
        followCache.add(new Connection(user1, user2, follows));
        return follows;
    }

    private static User getByName(String name) {
        return users.stream().filter(user -> user.getName().equals(name)).findFirst().get();
    }

}
