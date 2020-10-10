import java.awt.*;//applet
import javax.swing.*;


public class App extends JFrame {
  JTabbedPane tabPane;
  //basically title line m jo 4 tabs h vo inhi charo k h..
  HomePanel homePanel;
  DataPanel dataPanel;
  IndexPanel indexPanel; 
  QueryPanel queryPanel;
public App() {
              tabPane = new JTabbedPane();
              } 

}
tabPane.setFont(new Font("Comic Sans MS", 1, 30)); 
tabPane.setBackground(Color.DARK_GRAY);
 tabPane.setForeground(Color.WHITE);
homePanel = new HomePanel(tabPane); 
dataPanel = new DataPanel(tabPane);
 indexPanel = new IndexPanel(tabPane);
  queryPanel = new QueryPanel(tabPane);
super.add(tabPane); //super frame mein show krega vo jo super m added hoga .
pack();
super.setTitle("RDBMS Index Implementation");
 super.setExtendedState(MAXIMIZED_BOTH); 
 super.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
 super.setVisible(true);
}
}
