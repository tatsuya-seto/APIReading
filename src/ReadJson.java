import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

// Program for print data in JSON format.
public class ReadJson implements ActionListener {
    private JFrame mainFrame;
    private JLabel NameLabel;
    private JLabel InsertLinkLabel;
    private JLabel FillerLabel2;
    private JLabel SearchWordLabel;
    private JLabel FillerLabel3;
    private JEditorPane AbilitytArea;
    private JPanel BottomPanel;
    private JPanel TopPanel;
    private JPanel LongPanel;
    private JPanel LeftPanel;
    private JPanel RightPanel;
    private JPanel SearchPanel;
    private JLabel pokemonImageLabel;
    private JTextField ta;   // typing area
    private JTextField Link;// typing area
    private JEditorPane InfoArea;
    private int WIDTH = 800;
    private int HEIGHT = 700;
    private int currentId = 1;      // start at Bulbasaur
    private final int MAX_ID = 1026; // upper bound

    public ReadJson() {
        initGUI();

        try {
            initGUI();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        new ReadJson(); // constructor builds UI and loads initial data
    }

    private void initGUI() {
        mainFrame = new JFrame("Pokedex"); // main window
        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setLayout(new BorderLayout());

        // input fields
        Link = new JTextField();
        Link.setBounds(500, 1, WIDTH - 100, 1);

        ta = new JTextField();
        ta.setBounds(50, 1, WIDTH - 100, 1);
        ta.setSize(1, 1);

        // spacing labels
        NameLabel = new JLabel("               ", JLabel.CENTER);
        NameLabel.setSize(5, 100);

        InsertLinkLabel = new JLabel("       Insert Link: ", JLabel.RIGHT);
        InsertLinkLabel.setSize(350, 100);

        FillerLabel2 = new JLabel("               ", JLabel.CENTER);
        FillerLabel2.setSize(350, 100);

        SearchWordLabel = new JLabel("       Search Word:", JLabel.RIGHT);
        SearchWordLabel.setSize(350, 100);

        FillerLabel3 = new JLabel("           ", JLabel.CENTER);
        FillerLabel3.setSize(350, 100);

        // output area (HTML so links are clickable)
        AbilitytArea = new JEditorPane();
        AbilitytArea.setText("Insert a link and search term to get the links you need!");
        AbilitytArea.setEditable(false);
        AbilitytArea.setContentType("text/html");
        AbilitytArea.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        InfoArea = new JEditorPane();
        InfoArea.setText("Insert a link and search term to get the links you need!");
        InfoArea.setEditable(false);
        InfoArea.setContentType("text/html");
        InfoArea.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(AbilitytArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        // Panels
        BottomPanel = new JPanel();
        BottomPanel.setLayout(new GridLayout(2, 1));
        BottomPanel.setVisible(true);

        TopPanel = new JPanel();
        TopPanel.setLayout(new GridLayout(1, 2));
        TopPanel.setVisible(true);

        LongPanel = new JPanel();
        LongPanel.setLayout(new BorderLayout());
        LongPanel.setVisible(true);

        LeftPanel = new JPanel();
        LeftPanel.setLayout(new BorderLayout());
        LeftPanel.setVisible(true);

        SearchPanel = new JPanel();
        SearchPanel.setLayout(new GridLayout(1, 3));
        SearchPanel.setVisible(true);

        RightPanel = new JPanel();
        RightPanel.setLayout(new GridLayout(2, 1));
        RightPanel.setVisible(true);

        // Go button
        JButton NextButton = new JButton("Next");
        NextButton.setSize(20, 1);
        NextButton.setActionCommand("next");
        NextButton.addActionListener(new ButtonClickListener());

        JButton BackButton = new JButton("Back");
        BackButton.setSize(20, 1);
        BackButton.setActionCommand("back");
        BackButton.addActionListener(new ButtonClickListener());

        // Add panels to frame
        mainFrame.add(TopPanel, BorderLayout.CENTER);
        mainFrame.add(BottomPanel, BorderLayout.SOUTH);

        BottomPanel.add(LongPanel);   // link area
        BottomPanel.add(SearchPanel); // search term + go

        TopPanel.add(LeftPanel);
        TopPanel.add(RightPanel);

        pokemonImageLabel = new JLabel("No image yet", JLabel.CENTER);
        LeftPanel.add(pokemonImageLabel, BorderLayout.CENTER);

        RightPanel.add(InfoArea);
        RightPanel.add(scroll);

        // Link row
        LongPanel.add(NameLabel, BorderLayout.CENTER);

        // Search row
        SearchPanel.add(BackButton);
        SearchPanel.add(ta);
        SearchPanel.add(NextButton);


        mainFrame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
    }

    private void loadPokemonById(int id) {
        String output;
        StringBuilder totalJson = new StringBuilder();

        try {
            URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((output = br.readLine()) != null) totalJson.append(output);
            conn.disconnect();

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(totalJson.toString());
        } catch (ParseException ex) {
            ex.printStackTrace();
            return;
        }

        JSONArray stats = (JSONArray) jsonObject.get("stats");
        StringBuilder statsHtml = new StringBuilder("<html><body>");
        statsHtml.append("<h2>#").append(id).append(" ").append((String) jsonObject.get("name")).append("</h2>");

        for (Object obj : stats) {
            JSONObject statEntry = (JSONObject) obj;
            long base = (long) statEntry.get("base_stat");
            JSONObject statObj = (JSONObject) statEntry.get("stat");
            String statName = (String) statObj.get("name");
            statsHtml.append(statName).append(": ").append(base).append("<br>");
        }
        statsHtml.append("</body></html>");
        InfoArea.setText(statsHtml.toString());

        ta.setText(""); // optional: clear search box
    }

    private class ButtonClickListener implements ActionListener {
        //handles click events
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand(); //which button was pressed?
            if (command.equals("next")) {
                currentId++;
                if (currentId > MAX_ID) currentId = 1; // wrap around
                loadPokemonById(currentId);
            }

            if (command.equals("back")) {
                currentId--;
                if (currentId < 1) currentId = MAX_ID; // wrap around
                loadPokemonById(currentId);
            }
        }

    }
}
