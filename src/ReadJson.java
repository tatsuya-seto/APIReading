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

// Program for print data in JSON format.
public class ReadJson implements ActionListener {
    private JFrame mainFrame;
    private JLabel FillerLabel1;
    private JLabel InsertLinkLabel;
    private JLabel FillerLabel2;
    private JLabel SearchWordLabel;
    private JLabel FillerLabel3;
    private JEditorPane outputArea;
    private JPanel BottomPanel;
    private JPanel TopPanel;
    private JPanel LongPanel;
    private JPanel MidPanel;
    private JPanel GoPanel;
    private JPanel SearchPanel;
    private JTextField ta;   // typing area
    private JTextField Link; // typing area
    private int WIDTH = 800;
    private int HEIGHT = 700;

    public ReadJson() {
        initGUI();

        try {
            loadPokemonList();
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
        FillerLabel1 = new JLabel("               ", JLabel.CENTER);
        FillerLabel1.setSize(5, 100);

        InsertLinkLabel = new JLabel("       Insert Link: ", JLabel.RIGHT);
        InsertLinkLabel.setSize(350, 100);

        FillerLabel2 = new JLabel("               ", JLabel.CENTER);
        FillerLabel2.setSize(350, 100);

        SearchWordLabel = new JLabel("       Search Word:", JLabel.RIGHT);
        SearchWordLabel.setSize(350, 100);

        FillerLabel3 = new JLabel("           ", JLabel.CENTER);
        FillerLabel3.setSize(350, 100);

        // output area (HTML so links are clickable)
        outputArea = new JEditorPane();
        outputArea.setText("Insert a link and search term to get the links you need!");
        outputArea.setEditable(false);
        outputArea.setContentType("text/html");
        outputArea.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(outputArea);
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
        TopPanel.setLayout(new GridLayout(1, 1));
        TopPanel.setVisible(true);

        LongPanel = new JPanel();
        LongPanel.setLayout(new BorderLayout());
        LongPanel.setVisible(true);

        MidPanel = new JPanel();
        MidPanel.setLayout(new BorderLayout());
        MidPanel.setVisible(true);

        SearchPanel = new JPanel();
        SearchPanel.setLayout(new GridLayout(1, 4));
        SearchPanel.setVisible(true);

        GoPanel = new JPanel();
        GoPanel.setLayout(new BorderLayout());
        GoPanel.setVisible(true);

        // Go button
        JButton goButton = new JButton("Go");
        goButton.setSize(20, 1);
        goButton.setActionCommand("go");
        goButton.addActionListener(new ButtonClickListener());

        // Add panels to frame
        mainFrame.add(TopPanel, BorderLayout.CENTER);
        mainFrame.add(BottomPanel, BorderLayout.SOUTH);

        BottomPanel.add(LongPanel);   // link area
        BottomPanel.add(SearchPanel); // search term + go

        TopPanel.add(scroll);

        // Link row
        LongPanel.add(InsertLinkLabel, BorderLayout.WEST);
        LongPanel.add(Link, BorderLayout.CENTER);
        LongPanel.add(FillerLabel1, BorderLayout.EAST);

        // Search row
        SearchPanel.add(SearchWordLabel);
        SearchPanel.add(ta);
        SearchPanel.add(GoPanel);
        SearchPanel.add(FillerLabel3);

        // Go panel
        GoPanel.add(FillerLabel2, BorderLayout.WEST);
        GoPanel.add(goButton, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    private void loadPokemonList() throws ParseException {
        String output;
        String totalJson = "";

        try {
            URL url = new URL("https://pokeapi.co/api/v2/pokemon");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            // conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            while ((output = br.readLine()) != null) {
                totalJson += output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(totalJson);

        try {
            // NOTE: for https://pokeapi.co/api/v2/pokemon,
            // the JSON has "results": [ { "name": ..., "url": ... }, ... ]
            JSONArray results = (JSONArray) jsonObject.get("results");

            for (Object obj : results) {
                JSONObject pokemonEntry = (JSONObject) obj;
                String name = (String) pokemonEntry.get("name");
                System.out.println(name); // for now: print to console
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    private class ButtonClickListener implements ActionListener {
        //handles click events
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand(); //which button was pressed?
            if (command.equals("go")) { //checks if command was go(go is the command for the go button)
                String urlText = Link.getText().trim(); //link input
                String searchword = ta.getText().trim(); //search term input

                if (urlText.isEmpty() || searchword.isEmpty()) {
                    outputArea.setText("Please enter both a link and a search word");
                    return;
                }

                String allLinks = ""; //stores matching links found
                try {
                    URL url = new URL(urlText); //creates url object from input

                    URLConnection urlc = url.openConnection();
                    urlc.setRequestProperty(
                            "User-Agent",
                            "Mozilla 5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.11)"
                    );

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(urlc.getInputStream())
                    );

                    String line;
                    while ((line = reader.readLine()) != null) {
                        int pos = 0; //start position for searching within the line
                        while ((pos = line.indexOf("href=", pos)) != -1) { //find EACH occurrence of href
                            int start = pos + 5; //moving past href=
                            char quote = line.charAt(start); //quote char
                            if (quote == '"' || quote == '\'') {
                                int end = line.indexOf(quote, start + 1); //closing quote
                                if (end != -1) {
                                    String link = line.substring(start + 1, end); //extract link
                                    if (link.contains(searchword)) { //contains search term
                                        allLinks += link + "\n";
                                    }
                                    pos = end + 1;
                                } else break;
                            } else break;
                        }
                    }
                    reader.close();
                } catch (Exception ex) {
                    allLinks = "Error: " + ex.getMessage();
                }

                if (allLinks.isEmpty()) {
                    allLinks = "No links found containing \"" + searchword + "\"";
                } else {
                    //build HTML with clickable links
                    StringBuilder html = new StringBuilder("<html><body>");
                    String[] lines = allLinks.split("\\R");
                    for (String link : lines) {
                        link = link.trim();
                        if (link.isEmpty()) continue;

                        html.append("<a href=\"")
                                .append(link)
                                .append("\">")
                                .append(link)
                                .append("</a><br>");
                    }
                    html.append("</body></html>");

                    outputArea.setText(html.toString());
                }
            }
        }
    }
}

