import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.simple.JSONArray;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import javax.swing.event.HyperlinkEvent;
import javax.swing.*;
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
    private JTextField ta; //typing area
    private JTextField Link; //typing area
    private int WIDTH = 800;
    private int HEIGHT = 700;


    public ReadJson() {
        try {
            pull();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        ReadJson ReadJson = new ReadJson(); //creates instance
        ReadJson.showEventDemo(); //displays GUI
    }

    public void pull() throws ParseException {

        mainFrame = new JFrame("Pokedex");//main window
        mainFrame.setSize(WIDTH, HEIGHT);//window size
        mainFrame.setLayout(new BorderLayout());//defines layout


        Link = new JTextField();//text field for user to input link
        Link.setBounds(500, 1, WIDTH - 100, 1);//position+size(not that relevant - will get sized automatically by layout)

        ta = new JTextField();//text field for user to input search term
        ta.setBounds(50, 1, WIDTH - 100, 1);
        ta.setSize(1, 1);

        //spacing labels for layout alignment
        FillerLabel1 = new JLabel("               ", JLabel.CENTER);
        FillerLabel1.setSize(5, 100);

        //label prompting user to insert link
        InsertLinkLabel = new JLabel("       Insert Link: ", JLabel.RIGHT);
        InsertLinkLabel.setSize(350, 100);

        //filler label for spacing
        FillerLabel2 = new JLabel("               ", JLabel.CENTER);
        FillerLabel2.setSize(350, 100);

        //label prompting user to insert search word
        SearchWordLabel = new JLabel("       Search Word:", JLabel.RIGHT);
        SearchWordLabel.setSize(350, 100);

        //filler label for spacing
        FillerLabel3 = new JLabel("           ", JLabel.CENTER);
        FillerLabel3.setSize(350, 100);

        //output area to display all links
        outputArea = new JEditorPane();//jEditorPane in order to allow an html reader so the links are blue
        outputArea.setText("Insert a link and search term to get the links you need!");
        outputArea.setEditable(false);//prevents user editing (acts like label kinda)
        outputArea.setContentType("text/html"); //enables HTML rendering
        outputArea.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try { Desktop.getDesktop().browse(e.getURL().toURI()); } catch (Exception ex) { ex.printStackTrace(); }
            }//handles hyperlink clicks inside output area
        });


        JScrollPane scroll = new JScrollPane(outputArea);//creates a scroll pane - MUST BE ADDED WITHIN A FRAME. Doesnt function alone
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); //vertical scroll

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });//program exits when window closes

        //panel at the bottom
        BottomPanel = new JPanel();
        BottomPanel.setLayout(new GridLayout(2, 1));
        BottomPanel.setVisible(true);

        //panel at the top
        TopPanel = new JPanel();
        TopPanel.setLayout(new GridLayout(1, 1));
        TopPanel.setVisible(true);

        //panel within bottom panel 1/4(from the top) that contains the link input area
        LongPanel = new JPanel();
        LongPanel.setLayout(new BorderLayout());
        LongPanel.setVisible(true);

        //panel within bottom panel 2/4(from the top) just a filler
        MidPanel = new JPanel();
        MidPanel.setLayout(new BorderLayout());
        MidPanel.setVisible(true);

        //panel within bottom panel 3/4(from the top) that contains search term and go panel
        SearchPanel = new JPanel();
        SearchPanel.setLayout(new GridLayout(1, 4));
        SearchPanel.setVisible(true);

        GoPanel = new JPanel();
        GoPanel.setLayout(new BorderLayout());
        GoPanel.setVisible(true);

        String output = "abc";
        String totlaJson = "";
        try {

            URL url = new URL("https://pokeapi.co/api/v2/pokemon");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            //if you get the error 403, uncomment line of code below
            //conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // Add User-Agent


            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            //System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                //System.out.println(output);
                totlaJson += output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        //System.out.println(str);
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totlaJson);
        //System.out.println(jsonObject);

        try {

            JSONArray abilities = (JSONArray) jsonObject.get("abilities");

            for (Object obj : abilities) {
                JSONObject abilityEntry = (JSONObject) obj;
                JSONObject ability = (JSONObject) abilityEntry.get("ability");
                String abilityName = (String) ability.get("name");

                System.out.println(abilityName);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showEventDemo() {

        //creating go button, size doesnt matter much
        JButton goButton = new JButton("Go");
        goButton.setSize(20, 1);
        goButton.setActionCommand("go");
        goButton.addActionListener(new ButtonClickListener());//for any mouseclicks

        mainFrame.add(TopPanel, BorderLayout.CENTER);//Output area
        mainFrame.add(BottomPanel, BorderLayout.SOUTH);//input area

        BottomPanel.add(LongPanel);//Name Area
        BottomPanel.add(SearchPanel);//search term+Go

        TopPanel.add(new JScrollPane(outputArea));//adding the scroll frame to top panel

        //adding a linkinput and two filler labels to sandwich (left has the insert link: text tho)
        LongPanel.add(InsertLinkLabel, BorderLayout.WEST);
        LongPanel.add(Link, BorderLayout.CENTER);
        LongPanel.add(FillerLabel1, BorderLayout.EAST);

        //Search Panel buttons and inputs
        SearchPanel.add(SearchWordLabel);
        SearchPanel.add(ta);
        SearchPanel.add(GoPanel);//dummy panel for go
        SearchPanel.add(FillerLabel3);

        //go panel
        GoPanel.add(FillerLabel2, BorderLayout.WEST); //dummy filler
        GoPanel.add(goButton, BorderLayout.CENTER); //go button

        mainFrame.setVisible(true);
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
                //Getting url and searchword texts

                if (urlText.isEmpty() || searchword.isEmpty()) {
                    outputArea.setText("Please enter both a link and a search word");
                    return;
                }//Text returns this error if both a search term and a link is not inserted

                String allLinks = ""; //stores matching links found
                try {
                    URL url = new URL(urlText); //creates url object from input

                    URLConnection urlc = url.openConnection();
                    urlc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; " + "Windows NT 5.1; en-US; rv:1.8.0.11) "); //opens connection to url

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(urlc.getInputStream())
                    ); //reading
                    String line;
                    while ((line = reader.readLine()) != null) {//reading line by line
                        int pos = 0; //start position for searching within the line
                        while ((pos = line.indexOf("href=", pos)) != -1) { //find EACh occurance of href
                            int start = pos + 5; //moving past href= (5 chars so +5)
                            char quote = line.charAt(start); //Getting the quote char type
                            if (quote == '"' || quote == '\'') {
                                int end = line.indexOf(quote, start + 1); //Finding closing quote
                                if (end != -1) { //when closing quote found
                                    String link = line.substring(start + 1, end); //extract quote
                                    if (link.contains(searchword)) { //checking if it has search term
                                        allLinks += link + "\n"; //adding to results
                                    }
                                    pos = end + 1; // continue searching rest of line
                                } else break; //stop if no closing quote
                            } else break; //stop if no other href
                        }
                    }
                    reader.close();
                } catch (Exception ex) {//for when it fails
                    allLinks = "Error: " + ex.getMessage();
                }

                if (allLinks.isEmpty()) {
                    allLinks = "No links found containing \"" + searchword + "\""; //if no matches are found
                }   else {
                    //build HTML with clickable links
                    StringBuilder html = new StringBuilder("<html><body>");
                    String[] lines = allLinks.split("\\R");   // split on newlines
                    //iterates through each found link
                    for (String link : lines) {
                        link = link.trim();//removing extra spaces
                        if (link.isEmpty()) continue; //skips empty lines

                        //adds eahc link as a clickable html anchor
                        html.append("<a href=\"")
                                .append(link)
                                .append("\">")
                                .append(link)
                                .append("</a><br>");
                    }

                    html.append("</body></html>"); //closes html structure

                    outputArea.setText(html.toString()); //displays clickable links in outputarea
                }

            }
        }
    }

}

