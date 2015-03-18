
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


/**
 * *************************************************************************************************************
 * <p/>
 * Author: andrey
 * Date: 3/12/15
 * File name:
 * <p/>
 * <p/>
 * /**************************************************************************************************************
 */

public class Srsly_Main extends JFrame implements ActionListener
{
    private JPanel panel;
    private JButton chooseFile, writeLabelFile;
    private JTextField fileNameField;
    private JLabel instructionLabel, fileNameLabel;
    private String instructions = "Superfluous commas will cause suffering!!";
    private String labelFileName = "Labels_Srsly";
    private File sourcefile, destinationFile;

    private LinkedList<Label> labels;
    private int num_of_labels, num_of_pages;

    private PDDocument pdDoc;
    private PDFont font;
    private PDPageContentStream writer;


    public Srsly_Main() {
        setTitle("LBLS SRSLYS!");
        setLayout(new BorderLayout());
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildPanel();
        //add input panel and display:
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }


    private void buildPanel() {
        // build input panel:
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        instructionLabel = new JLabel(instructions);
        panel.add(instructionLabel);
        chooseFile = new JButton("Select the source .csv file");
        chooseFile.addActionListener(this);
        panel.add(chooseFile);
        fileNameLabel = new JLabel("Name the output file:");
        panel.add(fileNameLabel);
        fileNameField = new JTextField(labelFileName);
        fileNameField.setMaximumSize(new Dimension(500, 30));
        panel.add(fileNameField);
        writeLabelFile = new JButton("Suck it!");
        writeLabelFile.addActionListener(this);
        panel.add(writeLabelFile);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == chooseFile) sourcefile = launchFileChooser();
        if (ae.getSource() == writeLabelFile)
        {
            if(sourcefile != null)
            {
                String directory = sourcefile.getPath();
                String dir = directory.substring(0, directory.lastIndexOf(File.separator) + 1);
                writeTheFuckingMotherfuckFile(dir);
                lets_PDF_this_bitch(dir);
            }
            else
                JOptionPane.showMessageDialog(null, "Why didn't you choose a source file?! I'm a sad program now :'( " );
        }
    }

    private File launchFileChooser() {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        //fc.addChoosableFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new CSV_Filter());

        int returnVal = fc.showOpenDialog(Srsly_Main.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
            //This is where a real application would open the file.
            //log.append("Opening: " + file.getName() + "." + newline);
        } else {
            //log.append("Open command cancelled by user." + newline);
            return null;
        }
    }

    private void writeTheFuckingMotherfuckFile(String dir) {

        destinationFile = new File(dir + fileNameField.getText() + ".txt");
        //fileNameField.setText(destinationFile.getPath());
        if (sourcefile.exists()) {
            try {
                Scanner scanner = new Scanner(sourcefile);
                scanner.nextLine();
                scanner.useDelimiter(",");
                PrintStream ps = new PrintStream(destinationFile);
                System.setOut(ps);
                while (scanner.hasNext()) {
                    println(scanner.next());
                    scanner.next();
                    //String s = scanner.next();
                    //while(!Character.isDigit(scanner.next().charAt(0))) scanner.next();
                    println("Classic: " + scanner.next());

                    println("Seeded: " + scanner.next());
                    println("Kale: " + scanner.next());
                    println("Rolls: " + scanner.next());
                    println("Pullmans: " + scanner.next());
                    println("Dinner Rolls: " + scanner.next());
                    println("Box Size: " + scanner.next());
                    println("");
                    scanner.nextLine();
                }
                scanner.close();
                ps.flush();
                ps.close();

            } catch (FileNotFoundException e) {
            }
            ;
        }
    }

    private void lets_PDF_this_bitch(String dir)
    {
        File destination = new File(dir + fileNameField.getText() + ".pdf");
        populate_ArrayList();
        num_of_labels = labels.size();
        num_of_pages = num_of_labels/6 + 1;
        font = PDType1Font.HELVETICA_BOLD;
        try{
            pdDoc = new PDDocument();
            for(int i = 0; i< num_of_pages ; i++)
            {
                PDPage pdPage = new PDPage();
                pdDoc.addPage(pdPage);
                writer = new PDPageContentStream(pdDoc, pdPage);
                writer.beginText();
                writer.setFont( font, 17 );

                //Move cursor to initial position and write first label
                if(labels.size() > 0)
                {
                    Label current_label = labels.pop();
                    writer.moveTextPositionByAmount(80, 680);
                    writer.drawString(current_label.customer);
                    if(!current_label.num_classic.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Classic: " + current_label.num_classic);
                    }
                    if(!current_label.num_seeded.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Seeded: " + current_label.num_seeded);
                    }
                    if(!current_label.num_kale.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Kale: " + current_label.num_kale);
                    }
                    if(!current_label.num_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Rolls: " + current_label.num_rolls);
                    }
                    if(!current_label.num_pullmans.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Pullmans: " + current_label.num_pullmans);
                    }
                    if(!current_label.num_dinner_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Dinner rolls 12pk: " + current_label.num_dinner_rolls);
                    }
                    writer.moveTextPositionByAmount( 0, -20 );
                    writer.drawString("Box Size: " + current_label.box_size);
                    writer.endText();
                }
                //Move cursor to second row, first column
                if(labels.size() > 0)
                {
                    Label current_label = labels.pop();
                    writer.beginText();
                    writer.moveTextPositionByAmount(80, 440);  // CURSOR POSITION
                    writer.drawString(current_label.customer);
                    if(!current_label.num_classic.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Classic: " + current_label.num_classic);
                    }
                    if(!current_label.num_seeded.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Seeded: " + current_label.num_seeded);
                    }
                    if(!current_label.num_kale.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Kale: " + current_label.num_kale);
                    }
                    if(!current_label.num_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Rolls: " + current_label.num_rolls);
                    }
                    if(!current_label.num_pullmans.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Pullmans: " + current_label.num_pullmans);
                    }
                    if(!current_label.num_dinner_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Dinner rolls 12pk: " + current_label.num_dinner_rolls);
                    }
                    writer.moveTextPositionByAmount( 0, -20 );
                    writer.drawString("Box Size: " + current_label.box_size);
                    writer.endText();

                }
                //Move cursor to third row, first column
                if(labels.size() > 0)
                {
                    Label current_label = labels.pop();
                    writer.beginText();
                    writer.moveTextPositionByAmount(80, 200);  // CURSOR POSITION
                    writer.drawString(current_label.customer);
                    if(!current_label.num_classic.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Classic: " + current_label.num_classic);
                    }
                    if(!current_label.num_seeded.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Seeded: " + current_label.num_seeded);
                    }
                    if(!current_label.num_kale.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Kale: " + current_label.num_kale);
                    }
                    if(!current_label.num_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Rolls: " + current_label.num_rolls);
                    }
                    if(!current_label.num_pullmans.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Pullmans: " + current_label.num_pullmans);
                    }
                    if(!current_label.num_dinner_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Dinner rolls 12pk: " + current_label.num_dinner_rolls);
                    }
                    writer.moveTextPositionByAmount( 0, -20 );
                    writer.drawString("Box Size: " + current_label.box_size);
                    writer.endText();

                }
                //Move cursor to first row, second column
                if(labels.size() > 0)
                {
                    Label current_label = labels.pop();
                    writer.beginText();
                    writer.moveTextPositionByAmount(360, 680);  // CURSOR POSITION
                    writer.drawString(current_label.customer);
                    if(!current_label.num_classic.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Classic: " + current_label.num_classic);
                    }
                    if(!current_label.num_seeded.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Seeded: " + current_label.num_seeded);
                    }
                    if(!current_label.num_kale.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Kale: " + current_label.num_kale);
                    }
                    if(!current_label.num_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Rolls: " + current_label.num_rolls);
                    }
                    if(!current_label.num_pullmans.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Pullmans: " + current_label.num_pullmans);
                    }
                    if(!current_label.num_dinner_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Dinner rolls 12pk: " + current_label.num_dinner_rolls);
                    }
                    writer.moveTextPositionByAmount( 0, -20 );
                    writer.drawString("Box Size: " + current_label.box_size);
                    writer.endText();

                }
                //Move cursor to second row, second column
                if(labels.size() > 0)
                {
                    Label current_label = labels.pop();
                    writer.beginText();
                    writer.moveTextPositionByAmount(360, 440);  // CURSOR POSITION
                    writer.drawString(current_label.customer);
                    if(!current_label.num_classic.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Classic: " + current_label.num_classic);
                    }
                    if(!current_label.num_seeded.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Seeded: " + current_label.num_seeded);
                    }
                    if(!current_label.num_kale.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Kale: " + current_label.num_kale);
                    }
                    if(!current_label.num_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Rolls: " + current_label.num_rolls);
                    }
                    if(!current_label.num_pullmans.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Pullmans: " + current_label.num_pullmans);
                    }
                    if(!current_label.num_dinner_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Dinner rolls 12pk: " + current_label.num_dinner_rolls);
                    }
                    writer.moveTextPositionByAmount( 0, -20 );
                    writer.drawString("Box Size: " + current_label.box_size);
                    writer.endText();

                }
                //Move cursor to third row, second column
                if(labels.size() > 0)
                {
                    Label current_label = labels.pop();
                    writer.beginText();
                    writer.moveTextPositionByAmount(360, 200);  // CURSOR POSITION
                    writer.drawString(current_label.customer);
                    if(!current_label.num_classic.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Classic: " + current_label.num_classic);
                    }
                    if(!current_label.num_seeded.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Seeded: " + current_label.num_seeded);
                    }
                    if(!current_label.num_kale.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Kale: " + current_label.num_kale);
                    }
                    if(!current_label.num_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Rolls: " + current_label.num_rolls);
                    }
                    if(!current_label.num_pullmans.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Pullmans: " + current_label.num_pullmans);
                    }
                    if(!current_label.num_dinner_rolls.equals("0"))
                    {
                        writer.moveTextPositionByAmount( 0, -20 );
                        writer.drawString("Dinner rolls 12pk: " + current_label.num_dinner_rolls);
                    }
                    writer.moveTextPositionByAmount( 0, -20 );
                    writer.drawString("Box Size: " + current_label.box_size);
                    writer.endText();

                }
                //writer.endText();
                writer.close();
            }
            pdDoc.save(destination);
            pdDoc.close();
        }
        catch (IOException io)
        {
            JOptionPane.showMessageDialog(null, "Error time :( ! " + io.toString() );
            io.printStackTrace();

        } catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Error time :( ! " + e.toString() );
            e.printStackTrace();
        }

    }

    private void populate_ArrayList()
    {
        labels = new LinkedList<Label>();
        if (sourcefile.exists()) {
            try {
                Scanner scanner = new Scanner(sourcefile);
                scanner.nextLine(); // To get rid of header
                scanner.useDelimiter(",");
                while (scanner.hasNext())
                {
                    Label label = new Label(scanner.next());
                    // Skip the next column, which is notes. Might have to add error handling for commas in notes.
                    scanner.next();
                    // Error handing for the notes column
                    String next = scanner.next();
                    next.trim();
                    if(next.isEmpty())
                        next = "0";
                    while(!Character.isDigit(next.charAt(0)))
                    {
                        next = scanner.next();
                        next.trim();
                        if (next.isEmpty())
                        {
                            next = "0" ;
                            break;
                        }
                    }
                    label.num_classic = next;
                    // End error handling
                    next = scanner.next();
                    if(next.isEmpty()) next = "0";
                    label.num_seeded = next;
                    next = scanner.next();
                    if(next.isEmpty()) next = "0";
                    label.num_kale = next;
                    next = scanner.next();
                    if(next.isEmpty()) next = "0";
                    label.num_rolls = next;
                    next = scanner.next();
                    if(next.isEmpty()) next = "0";
                    label.num_pullmans = next;
                    next = scanner.next();
                    if(next.isEmpty()) next = "0";
                    label.num_dinner_rolls= next;

                    label.box_size = scanner.next();
                    labels.add(label);
                    scanner.nextLine();
                }
                scanner.close();
            } catch (FileNotFoundException e) {
            }
            ;
        }


    }

    private void print(String s) {
        System.out.print(s);
    }

    private void println(String s) {
        System.out.println(s);
    }

    public static void main(String Args[]) {
        new Srsly_Main();
    }
}

class Label
{
    public String customer, box_size;
    public String num_of_boxes, num_classic, num_seeded, num_kale, num_rolls, num_pullmans, num_dinner_rolls;

    Label(String cust)
    {
        customer = cust;

    }
    Label(String cust, String classic, String seeded, String kale, String rolls, String pullmans)
    {
        customer = cust;
        num_classic = classic;
        num_kale = kale;
        num_seeded = seeded;
        num_rolls = rolls;
        num_pullmans = pullmans;
    }

}

class CSV_Filter extends FileFilter
// This class is a filefilter for the filechooser, and filters out only .csv files.
{

    //Accept all directories and only csv files
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals("csv"))
                return true;
            else
                return false;
        }

        return false;
    }

    private static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    //The description of this filter
    public String getDescription() {
        return ".csv";
    }
}


