// create daatta
// createindex-- btree
// search indexx

package oui;
import java.io.*;
import java.util.*;
import javax.print.attribute.standard.Media; import javax.sound.midi.MetaEventListener;
public class DataManager {
public static String basedir = "";
public static class DataRow implements Serializable {
public int RollNum; public String Name; public String UserName; public String Password;
public String toString() {
return this.RollNum + "|" + this.Name + "|" + this.UserName + "|" +
this.Password;
}
}
public static class SearchResult { public DataRow DataRow;
public long TimeTaken;
public int PagesLoaded;
public boolean IndexesUsed; // false means scan, true means seek
}
public static void CreateData(int numRows, DataPanel dp) throws Exception { 
    createDirectory(basedir + "\\data");
+ pageNumber;
int extentNumber = 1, pageNumber = 1; for (int i = 1; i <= numRows; i++) {
String extentPath = basedir + "\\data\\extent_" + extentNumber;
String pagePath = basedir + "\\data\\extent_" + extentNumber + "\\page_"
// checks if the directory and file doesnot exist and creates if not createDirectory(extentPath);
createFile(pagePath);
// by the time code is here extent directory and file are guaranteed // created.
DataRow row = new DataRow();
row.RollNum = i;
row.Name = getRandomWord(); row.UserName = getRandomWord(); row.Password = getRandomWord();
if (canThePageAccomodateARow(pagePath, row)) { 
    writeTheRowInTheFile(pagePath, row.toString());
} else {
i--;
pageNumber++;

}
}
} }
if (pageNumber == 9) { extentNumber++;
pageNumber = 1;
dp.updateStatus((i * 100) / numRows);
dp.updateStatus(100);
String metadataFilePath = basedir + "\\data\\metadata"; createFile(metadataFilePath);
String metadata = numRows + ""; writeTheRowInTheFile(metadataFilePath, metadata);
public static void CreateIndex(String columnName, IndexPanel ip) { String indexDirPath = basedir + "\\indices";
String specificIndexDirPath = indexDirPath + "\\" + columnName;
createDirectory(indexDirPath); createDirectory(specificIndexDirPath);
String dataDirPath = basedir + "\\data";
String metadataFilePath = dataDirPath + "\\metadata";
int rowsTotal = 0, rowsCounter = 1, pagesCounter = 1, extentsCounter = 1; int columnIndex = columnName.equals("Name") ? 1 :
(columnName.equals("UserName") ? 2 : 3);
String metadataContent = readRowFromTheFile(metadataFilePath, 0); rowsTotal = Integer.parseInt(metadataContent);
while (rowsCounter <= rowsTotal) {
String extentPath = basedir + "\\data\\extent_" + extentsCounter; String pagePath = extentPath + "\\page_" + pagesCounter;
String line = "";
try {
File file = new File(pagePath);
BufferedReader reader = new BufferedReader(new FileReader(file)); int offset = 0;
do {
line = reader.readLine();
if (line != null && line.trim().length() > 0) { System.out.println(rowsCounter + ". " + line); String value = line.split("[|]")[columnIndex];

pagesCounter + "|" + offset; specificIndexDirPath);
String address = extentsCounter + "|" + addLineToIndex(value, address,
}
}
}
rowsCounter++;
offset++;
} while (line != null);
rowsCounter--; pagesCounter++;
if (pagesCounter == 9) {
extentsCounter++;
pagesCounter = 1;
ip.updateStatus((rowsCounter * 100) / rowsTotal);
reader.close(); } catch (Exception ex) {
System.out.println(ex);
} }
ip.updateStatus(100);
public static void Search(String columnName, String columnValue, QueryPanel qp) { SearchResult result = new SearchResult();
String indexDirPath = basedir + "\\indices";
String specificIndexDirPath = indexDirPath + "\\" + columnName; long start = System.currentTimeMillis();
if(new File(specificIndexDirPath).exists()){ // index seek
result.IndexesUsed = true;
String metadataFilePath = specificIndexDirPath + "\\metadata";
HashMap<String, Boolean> nodeLeafMap = getMetadataInfo(metadataFilePath);
searchValueInIndex(specificIndexDirPath, "root", nodeLeafMap, columnValue, start, result, qp);
} else {
// table scan
result.IndexesUsed = false;
String dataDirPath = basedir + "\\data";
String metadataFilePath = dataDirPath + "\\metadata";
int rowsTotal = 0, rowsCounter = 1, pagesCounter = 1, extentsCounter = 1;

int columnIndex = columnName.equals("Name") ? 1 : (columnName.equals("UserName") ? 2 : 3);
FileReader(file));
String metadataContent = readRowFromTheFile(metadataFilePath, 0); rowsTotal = Integer.parseInt(metadataContent);
while (rowsCounter <= rowsTotal) {
String extentPath = basedir + "\\data\\extent_" + extentsCounter; String pagePath = extentPath + "\\page_" + pagesCounter;
String line = "";
try {
File file = new File(pagePath);
BufferedReader reader = new BufferedReader(new
do {
line = reader.readLine();
if (line != null && line.trim().length() > 0) {
String value = line.split("[|]")[columnIndex]; if(value.equals(columnValue)){
(extentsCounter - 1) * 8 + pagesCounter - 1; System.currentTimeMillis() - start;
result.PagesLoaded = result.TimeTaken =
result.DataRow = new DataRow(); result.DataRow.RollNum =
result.DataRow.Name = result.DataRow.UserName = result.DataRow.Password =
qp.updateResults(result);
return; }
Integer.parseInt(line.split("[|]")[0]); line.split("[|]")[1]; line.split("[|]")[2]; line.split("[|]")[3];
}
}
rowsCounter++; } while (line != null);
rowsCounter--; pagesCounter++;
if (pagesCounter == 9) {
extentsCounter++; pagesCounter = 1;

pagesCounter - 1;
} }
result.TimeTaken = System.currentTimeMillis() - start; qp.updateResults(result);
reader.close();
} catch (Exception ex) { System.out.println(ex);
} }
result.PagesLoaded = (extentsCounter - 1) * 8 +
private static String readRowFromTheFile(String fileName, int rowNumber) { String line = "";
try {
File file = new File(fileName);
BufferedReader reader = new BufferedReader(new FileReader(file));
int rowCounter = 1; do {
line = reader.readLine();
rowCounter++;
} while (line != null && rowCounter <= rowNumber);
reader.close(); } catch (Exception ex) {
System.out.println(ex);
}
return line; }
private static void writeTheRowInTheFile(String fileName, String row) { try {
File file = new File(fileName);
BufferedWriter writer = new BufferedWriter(new FileWriter(file, true)); PrintWriter pwriter = new PrintWriter(writer);
pwriter.println(row);
pwriter.close();
} catch (Exception ex) { System.out.println(ex);
} }
private static boolean canThePageAccomodateARow(String fileName, DataRow row) { File file = new File(fileName);
long byteSize = file.length() + row.toString().length(); return byteSize < 8 * 1024;

}
private static boolean doesFileExist(String fileName) { File file = new File(fileName);
return file.exists();
}
private static void createFile(String fileName) { if (!doesFileExist(fileName)) {
try {
File file = new File(fileName);
file.createNewFile(); } catch (IOException e) {
// TODO Auto-generated catch block e.printStackTrace();
} }
private static boolean doesDirectoryExist(String dirName) { File file = new File(dirName);
return file.exists();
}
private static void createDirectory(String dirName) { if (!doesDirectoryExist(dirName)) {
}
}
File dir = new File(dirName);
dir.mkdir(); }
private static String getRandomWord() {
int length = (int) (Math.random() * 10) + 5; StringBuilder sb = new StringBuilder();
}
for (int i = 0; i < length; i++) {
char ch = (char) ((int) (Math.random() * 26) + 'a'); sb.append(ch);
}
return sb.toString();
private static void addLineToIndex(String value, String address, String indexDirPath) { String metadataFilePath = indexDirPath + "\\metadata"; createFile(metadataFilePath);
String rootFilePath = indexDirPath + "\\root"; createRootFile(rootFilePath, metadataFilePath);

}
// guarantee that the root and metadata file exists file exists
HashMap<String, Boolean> nodeLeafMap = getMetadataInfo(metadataFilePath); addLineToNode(indexDirPath, "root", nodeLeafMap, value, address); manageChildNodeSize(indexDirPath, "root", null, nodeLeafMap); updateMetadataInfo(metadataFilePath, nodeLeafMap);
private static void addLineToNode(String indexDirPath, String node, HashMap<String, Boolean> nodeLeafMap,
String value, String address) { Boolean isLeaf = nodeLeafMap.get(node);
if (!isLeaf) {
// search place and go down
String childNode = "";
try {
File file = new File(indexDirPath + "\\" + node);
BufferedReader reader = new BufferedReader(new FileReader(file)); String prevLine = "", line = "", lv = "";
boolean flag = false;
do {
prevLine = line;
line = reader.readLine();
if (line != null && line.trim().length() > 0) { lv = line.split("=")[0];
if (lv.compareTo(value) > 0) { flag = true;
break; }
}
} while (line != null);
if (flag) {
// smaller
childNode = line.split("=")[1].split("[|]")[0]; } else {
// larger
childNode = prevLine.split("=")[1].split("[|]")[1];
}
reader.close(); } catch (Exception ex) {
System.out.println(ex);
}
addLineToNode(indexDirPath, childNode, nodeLeafMap, value, address); manageChildNodeSize(indexDirPath, childNode, node, nodeLeafMap);

} else {
// add
try {
File file = new File(indexDirPath + "\\" + node);
BufferedReader reader = new BufferedReader(new FileReader(file)); ArrayList<String> lines = new ArrayList<>();
String line = "", lv = "";
boolean flag = false;
do {
line = reader.readLine();
if (line != null && line.trim().length() > 0) { lv = line.split("=")[0];
if (lv.compareTo(value) > 0) { if (flag == false) {
lines.add(value + "=" + address); lines.add(line);
flag = true;
} else {
lines.add(line);
} } else {
} }
} while (line != null); reader.close();
if (flag == false) {
}
lines.add(value + "=" + address);
BufferedWriter writer = new BufferedWriter(new FileWriter(file)); for (String lineContent : lines) {
writer.write(lineContent); writer.write("\n");
}
writer.close(); } catch (Exception ex) {
System.out.println(ex);
} }
private static void manageChildNodeSize(String indexDirPath, String childNode, String node, HashMap<String, Boolean> nodeLeafMap) {
File file = new File(indexDirPath + "\\" + childNode); long fileSize = file.length();
}
lines.add(line);

if (fileSize > 8 * 1024) { // split
String siblingNode = UUID.randomUUID().toString(); createFile(indexDirPath + "\\" + siblingNode); ArrayList<String> lines = new ArrayList<>();
try {
// reading data from child
BufferedReader reader = new BufferedReader(new FileReader(file)); String line = "";
do {
line = reader.readLine();
if (line != null && line.trim().length() > 0) { lines.add(line);
}
} while (line != null);
reader.close();
// half the data in original child
BufferedWriter writer = new BufferedWriter(new FileWriter(file)); for (int i = 0; i < lines.size() / 2; i++) {
writer.write(lines.get(i)); writer.write("\n");
} writer.close();
if (childNode == "root") {
childNode = UUID.randomUUID().toString(); file.renameTo(new File(indexDirPath + "\\" + childNode));
nodeLeafMap.put(childNode, nodeLeafMap.get("root")); nodeLeafMap.put(siblingNode, nodeLeafMap.get("root")); nodeLeafMap.put("root", false);
} else {
nodeLeafMap.put(siblingNode,
nodeLeafMap.get(childNode));
}
"\\" + siblingNode)));
// half the data in new child
writer = new BufferedWriter(new FileWriter(new File(indexDirPath +
for (int i = lines.size() / 2; i < lines.size(); i++) { writer.write(lines.get(i));
writer.write("\n"); writer.close();
// reading and preparing content from node String lineTI = lines.get(lines.size() / 2);
}

File(indexDirPath + "\\" + node)));
+ "=" + childNode + "|" + siblingNode);
reader = new BufferedReader(new FileReader(new
do {
line = reader.readLine();
if (line != null && line.trim().length() > 0) { if (line.compareTo(lineTI) < 0) {
lines.add(line); } else {
if (flag == false) { flag = true;
lines.add(lineTI.split("=")[0]
lines.add(
+ siblingNode);
File(indexDirPath + "\\" + node)));
File(indexDirPath + "\\root"))); siblingNode);
lines.clear();
if (node != null) {
boolean flag = false;
line.split("=")[0] + "=" + siblingNode + "|" + line.split("=")[1].split("[|]")[1]); } else {
} catch (Exception ex) { System.out.println(ex);
} }
}
} while (line != null);
if (flag == false) {
lines.add(lineTI.split("=")[0] + "=" + childNode + "|"
} reader.close();
// insert content in node
writer = new BufferedWriter(new FileWriter(new
for (int i = 0; i < lines.size(); i++) { writer.write(lines.get(i));
writer.write("\n");
}
writer.close(); } else {
createFile(indexDirPath + "\\root");
writer = new BufferedWriter(new FileWriter(new
writer.write(lineTI.split("=")[0] + "=" + childNode + "|" +
writer.write("\n");
writer.close(); }
lines.add(line);

} }
private static void searchValueInIndex(String indexDirPath, String node, HashMap<String, Boolean> nodeLeafMap, String columnValue,
SearchResult result, QueryPanel qp) {
Boolean isLeaf = nodeLeafMap.get(node);
if (!isLeaf) {
String childNode = "";
}
try {
File file = new File(indexDirPath + "\\" + node);
BufferedReader reader = new BufferedReader(new FileReader(file)); String prevLine = "", line = "", lv = "";
boolean flag = false;
do {
prevLine = line;
line = reader.readLine();
if (line != null && line.trim().length() > 0) { lv = line.split("=")[0];
if (lv.compareTo(columnValue) > 0) { flag = true;
break; }
}
} while (line != null);
if (flag) {
// smaller
childNode = line.split("=")[1].split("[|]")[0]; } else {
// larger
childNode = prevLine.split("=")[1].split("[|]")[1];
}
reader.close(); } catch (Exception ex) {
System.out.println(ex);
} result.PagesLoaded++;
long start,

start, result, qp);
} else {
result.TimeTaken = System.currentTimeMillis() - start; qp.updateResults(result);
searchValueInIndex(indexDirPath, childNode, nodeLeafMap, columnValue,
String childNode = "";
try {
File file = new File(indexDirPath + "\\" + node);
BufferedReader reader = new BufferedReader(new FileReader(file)); String prevLine = "", line = "", lv = "";
do {
prevLine = line;
line = reader.readLine();
if (line != null && line.trim().length() > 0) { lv = line.split("=")[0];
if (lv.compareTo(columnValue) == 0) { break;
} }
} while (line != null); reader.close();
String extentNumber = line.split("=")[1].split("[|]")[0];
String pageNumber = line.split("=")[1].split("[|]")[1];
int offsetNumber = Integer.parseInt(line.split("=")[1].split("[|]")[2]);
File dataFilePath = new File(basedir + "\\data\\extent_" + extentNumber + "\\page_" + pageNumber);
reader = new BufferedReader(new FileReader(dataFilePath));
for(int i = 0; i <= offsetNumber; i++){ line = reader.readLine();
}
result.PagesLoaded++;
result.TimeTaken = System.currentTimeMillis() - start; result.DataRow = new DataRow();
result.DataRow.RollNum = Integer.parseInt(line.split("[|]")[0]); result.DataRow.Name = line.split("[|]")[1]; result.DataRow.UserName = line.split("[|]")[2]; result.DataRow.Password = line.split("[|]")[3]; qp.updateResults(result);
reader.close();
} catch (Exception ex) { System.out.println(ex);
}

} }
private static HashMap<String, Boolean> getMetadataInfo(String metadataFilePath) { HashMap<String, Boolean> map = new HashMap<>();
try {
File file = new File(metadataFilePath);
BufferedReader reader = new BufferedReader(new FileReader(file));
String line = ""; do {
line = reader.readLine(); if (line != null) {
map.put(line.split("=")[0], Boolean.parseBoolean(line.split("=")[1]));
}
} while (line != null);
reader.close(); } catch (Exception ex) {
}
return map; }
private static void updateMetadataInfo(String metadataFilePath, HashMap<String, Boolean> nodeLeafMap) {
try {
BufferedWriter writer = new BufferedWriter(new FileWriter(new
File(metadataFilePath)));
Set<String> keys = nodeLeafMap.keySet();
for (String key : keys) {
writer.write(key + "=" + nodeLeafMap.get(key)); writer.write("\n");
}
writer.close(); } catch (Exception ex) {
} }
private static void createRootFile(String rootFilePath, String metadataFilePath) { if (!doesFileExist(rootFilePath)) {
true));
try {
File file = new File(metadataFilePath);
BufferedWriter writer = new BufferedWriter(new FileWriter(file,
PrintWriter pwriter = new PrintWriter(writer); pwriter.println("root=true"); // root is leaf to begin with pwriter.close();

file = new File(rootFilePath);
file.createNewFile(); } catch (IOException e) {
// TODO Auto-generated catch block e.printStackTrace();
} }
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;
public class DataPanel extends JPanel implements ActionListener { private JLabel lblRows;
private JTextField txtRows; private JButton btnCreate; private JProgressBar progressBar;
public DataPanel(JTabbedPane tabPane) { tabPane.addTab("Data", this);
initComponents();
}
private void initComponents() {
this.setBorder(BorderFactory.createMatteBorder(25, 25, 25, 25, Color.darkGray)); this.setBackground(new Color(0, 100, 0));
lblRows = new JLabel();
lblRows.setFont(new Font("Monospaced", 0, 50)); // NOI18N lblRows.setForeground(new Color(255, 255, 255)); lblRows.setText("Rows:");
txtRows = new JTextField("");
txtRows.setColumns(10);
txtRows.setFont(new Font("Monospaced", 0, 50)); // NOI18N
btnCreate = new JButton();
btnCreate.setFont(new Font("Monospaced", 0, 50)); // NOI18N btnCreate.setText("Create Test Data"); btnCreate.addActionListener(this);
progressBar = new JProgressBar(); progressBar.setStringPainted(true); progressBar.setMinimum(0); progressBar.setMaximum(100); progressBar.setFont(new Font("Monospaced", 0, 50));
} }
package oui;

GroupLayout layout = new GroupLayout(this); this.setLayout(layout);
layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
.addGroup(layout.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addComponent(lblRows, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
GroupLayout.PREFERRED_SIZE)
.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED) .addComponent(txtRows,
GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, .addContainerGap(GroupLayout.DEFAULT_SIZE,
Short.MAX_VALUE))
.addGroup(layout.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addComponent(btnCreate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
GroupLayout.PREFERRED_SIZE) .addContainerGap(GroupLayout.DEFAULT_SIZE,
Short.MAX_VALUE))
.addGroup(layout.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
GroupLayout.PREFERRED_SIZE) .addContainerGap(GroupLayout.DEFAULT_SIZE,
Short.MAX_VALUE))); layout.setVerticalGroup(
layout.createSequentialGroup().addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE) .addComponent(txtRows,
GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
GroupLayout.PREFERRED_SIZE)) .addGap(50, 50, 50)
.addComponent(lblRows,
.addComponent(btnCreate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
GroupLayout.PREFERRED_SIZE)

.addGap(50, 50, 50)
.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
.addContainerGap(GroupLayout.DEFAULT_SIZE,
Short.MAX_VALUE)); }
@Override
public void actionPerformed(ActionEvent e) {
DataPanel obj = this;
Thread t = new Thread(new Runnable() {
@Override
public void run() {
try {
int rows = Integer.parseInt(txtRows.getText()); DataManager.CreateData(rows, obj); JOptionPane.showMessageDialog(obj, "Data created
progressBar.setValue(0); } catch (Exception ex) {
} }
});
t.start(); }
public void updateStatus(int percent){ progressBar.setValue(percent);
} }
package oui;
import java.awt.*; import javax.swing.*;
public class HomePanel extends javax.swing.JPanel { public HomePanel(JTabbedPane tabPane) {
tabPane.addTab("Home", this); initComponents();
}
private JTextArea jTextArea; private void initComponents() {
this.setBorder(BorderFactory.createMatteBorder(25, 25, 25, 25, Color.darkGray)); this.setBackground(new Color(0, 100, 0));
jTextArea = new JTextArea();
jTextArea.setFont(new Font("Monospaced", 0, 35)); // NOI18N jTextArea.setForeground(new Color(102, 0, 102)); jTextArea.setColumns(60);
successfully");

jTextArea.setRows(5);
jTextArea.setText("In this project,I am making a software which will make searching in Relational Database Management system optimized. As well as we will also show better performance of Index seek method over Table scan method.");
jTextArea.setLineWrap(true); jTextArea.setEditable(false);
javax.swing.GroupLayout layout = new GroupLayout(this); this.setLayout(layout);
layout.setHorizontalGroup(
layout.createParallelGroup(GroupLayout.Alignment.LEADING) .addGroup(
layout.createSequentialGroup()
.addContainerGap(0, Short.MAX_VALUE)
.addComponent(jTextArea, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
GroupLayout.PREFERRED_SIZE) .addContainerGap(0, Short.MAX_VALUE))
); layout.setVerticalGroup(
layout.createParallelGroup(GroupLayout.Alignment.LEADING) .addGroup(
layout.createSequentialGroup()
.addContainerGap(0, Short.MAX_VALUE)
.addComponent(jTextArea, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
GroupLayout.PREFERRED_SIZE) .addContainerGap(0, Short.MAX_VALUE))
); }
}