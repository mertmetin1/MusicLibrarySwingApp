package deneme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

class Song {
    private String songName;
    private String artist;
    private int id;
    private String genre;
    private int year;

    public Song(String songName, String artist, int id, String genre, int year) {
        this.songName = songName;
        this.artist = artist;
        this.id = id;
        this.genre = genre;
        this.year = year;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtist() {
        return artist;
    }

    public int getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", artist='" + artist + '\'' +
                ", id=" + id +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                '}';
    }
}

class MusicRecordSystem {
    private Map<String, Integer> nameIndexMap;
    private Map<Integer, Integer> idIndexMap;
    private Map<String, Integer> artistIndexMap;
    private List<Song> songs;

    public MusicRecordSystem() {
        nameIndexMap = new HashMap<>();
        idIndexMap = new HashMap<>();
        artistIndexMap = new HashMap<>();
        songs = new ArrayList<>();
    }

    public void loadSongsFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    try {
                        Song song = new Song(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], Integer.parseInt(parts[4]));
                        songs.add(song);
                        nameIndexMap.put(song.getSongName(), songs.size() - 1);
                        idIndexMap.put(song.getId(), songs.size() - 1);
                        artistIndexMap.put(song.getArtist(), songs.size() - 1);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing integers in the file.");
                    }
                } else {
                    System.err.println("Invalid data format in the file.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String searchByName(String songName) {
        if (nameIndexMap.containsKey(songName)) {
            return songs.get(nameIndexMap.get(songName)).toString();
        } else {
            return "Song not found.";
        }
    }

    public String searchByArtist(String artist) {
        if (artistIndexMap.containsKey(artist)) {
            return songs.get(artistIndexMap.get(artist)).toString();
        } else {
            return "Artist not found.";
        }
    }

    public String searchById(int id) {
        if (idIndexMap.containsKey(id)) {
            return songs.get(idIndexMap.get(id)).toString();
        } else {
            return "Song with ID " + id + " not found.";
        }
    }

    public String displaySongsByGenre(String genre) {
        List<Song> genreSongs = new ArrayList<>();
        for (Song song : songs) {
            if (song.getGenre().equals(genre)) {
                genreSongs.add(song);
            }
        }
        if (!genreSongs.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (Song song : genreSongs) {
                result.append(song).append("\n");
            }
            return result.toString();
        } else {
            return "No songs found in the given genre.";
        }
    }

    public String displaySongsInIdRange(int lowerId, int upperId) {
        List<Song> rangeSongs = new ArrayList<>();
        for (Song song : songs) {
            if (song.getId() >= lowerId && song.getId() <= upperId) {
                rangeSongs.add(song);
            }
        }
        if (!rangeSongs.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (Song song : rangeSongs) {
                result.append(song).append("\n");
            }
            return result.toString();
        } else {
            return "No songs found in the given ID range.";
        }
    }

    public void insertSong(String songName, String artist, int id, String genre, int year) {
        Song song = new Song(songName, artist, id, genre, year);
        songs.add(song);
        nameIndexMap.put(song.getSongName(), songs.size() - 1);
        idIndexMap.put(song.getId(), songs.size() - 1);
        artistIndexMap.put(song.getArtist(), songs.size() - 1);
    }

    public boolean deleteSongById(int id) {
        if (idIndexMap.containsKey(id)) {
            int index = idIndexMap.get(id);
            Song removedSong = songs.remove(index);
            nameIndexMap.remove(removedSong.getSongName());
            artistIndexMap.remove(removedSong.getArtist());
            updateIndexesAfterDeletion(index);
            return true;
        } else {
            return false;
        }
    }

    private void updateIndexesAfterDeletion(int removedIndex) {
        for (Map.Entry<String, Integer> entry : nameIndexMap.entrySet()) {
            if (entry.getValue() > removedIndex) {
                entry.setValue(entry.getValue() - 1);
            }
        }
        for (Map.Entry<Integer, Integer> entry : idIndexMap.entrySet()) {
            if (entry.getValue() > removedIndex) {
                entry.setValue(entry.getValue() - 1);
            }
        }
        for (Map.Entry<String, Integer> entry : artistIndexMap.entrySet()) {
            if (entry.getValue() > removedIndex) {
                entry.setValue(entry.getValue() - 1);
            }
        }
    }
}

public class MusicLibrarySwingApp1 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MusicRecordSystem recordSystem = new MusicRecordSystem();
                recordSystem.loadSongsFromFile("C:\\Users\\erdem\\eclipse-workspace\\programms\\src\\müzikuyg\\songs.txt");

                JFrame frame = new JFrame("Music Library App");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JPanel panel = new MusicLibraryGUI(recordSystem);
                frame.getContentPane().add(panel);

                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}

class MusicLibraryGUI extends JPanel implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MusicRecordSystem recordSystem;
    private JTextField inputField;
    private JTextArea outputArea;

    public MusicLibraryGUI(MusicRecordSystem recordSystem) {
        this.recordSystem = recordSystem;

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Music Library App");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        JLabel inputLabel = new JLabel("Enter your choice: [1-9]");
        inputField = new JTextField(15);
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        centerPanel.add(inputPanel);

        JTextArea menuArea = new JTextArea(
                "-------------------------------\n" +
                "      Music Library App\n" +
                "-------------------------------\n" +
                "\n" +
                "1. Search by Song Name\n" +
                "2. Search by Artist\n" +
                "3. Search by Song ID\n" +
                "4. Display Songs by Genre\n" +
                "5. Display Songs by ID Range\n" +
                "6. Insert a New Song\n" +
                "7. Delete a Song by ID\n" +
                "8. Exit\n" +
                "\n" +
                "Enter your choice: [1-9]"
        );
        menuArea.setEditable(false);
        centerPanel.add(menuArea);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(outputPanel);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        bottomPanel.add(submitButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int choice = Integer.parseInt(inputField.getText());
            StringBuilder output = new StringBuilder();

            switch (choice) {
                case 1:
                    String songName = JOptionPane.showInputDialog(this, "Enter song name:");
                    output.append(recordSystem.searchByName(songName));
                    break;
                case 2:
                    String artist = JOptionPane.showInputDialog(this, "Enter artist:");
                    output.append(recordSystem.searchByArtist(artist));
                    break;
                case 3:
                    int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter ID:"));
                    output.append(recordSystem.searchById(id));
                    break;
                case 4:
                    String genre = JOptionPane.showInputDialog(this, "Enter genre:");
                    output.append(recordSystem.displaySongsByGenre(genre));
                    break;
                case 5:
                    int lowerId = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter lower ID:"));
                    int upperId = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter upper ID:"));
                    output.append(recordSystem.displaySongsInIdRange(lowerId, upperId));
                    break;
                case 6:
                    String newSongName = JOptionPane.showInputDialog(this, "Enter new song name:");
                    String newArtist = JOptionPane.showInputDialog(this, "Enter new artist:");
                    int newId = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new ID:"));
                    String newGenre = JOptionPane.showInputDialog(this, "Enter new genre:");
                    int newYear = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new year:"));
                    recordSystem.insertSong(newSongName, newArtist, newId, newGenre, newYear);
                    output.append("New song inserted successfully.\n");
                    break;
                case 7:
                    int deleteId = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter ID to delete:"));
                    if (recordSystem.deleteSongById(deleteId)) {
                        output.append("Song with ID " + deleteId + " deleted successfully.\n");
                    } else {
                        output.append("Song with ID " + deleteId + " not found.\n");
                    }
                    break;
                case 8:
                    output.append("Exiting...\n");
                    System.exit(0);
                    break;
                default:
                    output.append("Invalid choice! Please enter a valid option.\n");
            }

            outputArea.setText("");
            outputArea.append("Choice " + choice + " results:\n");
            outputArea.append("----------------------------------\n");
            outputArea.append(output.toString());
            outputArea.append("\n");

        } catch (NumberFormatException ex) {
            outputArea.append("Invalid choice! Please enter a valid option.\n");
        }
    }
}
