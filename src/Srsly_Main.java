
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
 * <p/>               move first row left by two letter spaces.
 * move all columns up one line.
 * <p/>
 * /**************************************************************************************************************
 */

public class Srsly_Main extends JFrame implements ActionListener
{
    private JPanel panel;
    private JButton chooseFile, writeLabelFile, makeLabelsFromGoogleFile, makeLabelsFromSquareSpaceFileCAonly,
            makeLabelsFromSquareSpaceFileIncludeAll;
    private JTextField fileNameField;
    private JLabel instructionLabel, fileNameLabel, buttonLabel;
    private final String instructions = "Avoid suffering!!";
    private final String labelFileName = "Labels-Srsly";
    private final String error_no_file_chosen = "Why didn't you choose a source file?! I'm a sad program now :'( ";
    private File sourcefile, destinationFile;

    private LinkedList<Label> labels;
    private ArrayList<String> headings;

    private int num_of_labels, num_of_pages, which_CSV;
    private final int for_googleDocs = 0, for_squarespace_CA = 1, getFor_squarespace_all = 2;

    private PDDocument pdDoc;
    private PDFont font_regular, font_bold;
    private PDPageContentStream writer;
    // Fields for entry of Label objects:
    private String customer, box_size, box_which_of_how_many;
    private int num_of_boxes, num_classic, num_seeded, num_kale, num_rolls, num_pullmans, num_dinner_rolls,
            label_header_spacing, label_entry_spacing, first_column, second_column, first_row, second_row, third_row;
    private boolean is_one_of_several;

    public Srsly_Main()
    {
        setTitle("LBLS SRSLYS!");
        setLayout(new BorderLayout());
        setSize(600, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, 0);
        buildPanel();
        //add input panel and display:
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }


    private void buildPanel()
    {
        // build input panel:
        panel = new JPanel();
        panel.setAlignmentX(0);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        instructionLabel = new JLabel(instructions);
        panel.add(instructionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        chooseFile = new JButton("Select the source .csv file");
        chooseFile.addActionListener(this);
        panel.add(chooseFile);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        fileNameLabel = new JLabel("Name the output file:");
        panel.add(fileNameLabel);
        // Get date to add to file name
        DateFormat dateFormat = new SimpleDateFormat("-MMM-dd-yyyy");
        Date date = new Date();
        fileNameField = new JTextField(labelFileName + dateFormat.format(date));
        fileNameField.setMaximumSize(new Dimension(500, 30));
        panel.add(fileNameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonLabel = new JLabel(" Select processing type:");
        panel.add(buttonLabel);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(500, 50));
        buttonPanel.setLayout(new FlowLayout());
        makeLabelsFromGoogleFile = new JButton(" GoogleDoc ");
        makeLabelsFromGoogleFile.addActionListener(this);
        makeLabelsFromSquareSpaceFileCAonly = new JButton(" SqaureSpace CA only");
        makeLabelsFromSquareSpaceFileCAonly.addActionListener(this);
        makeLabelsFromSquareSpaceFileIncludeAll = new JButton(" SquareSpace All  ");
        makeLabelsFromSquareSpaceFileIncludeAll.addActionListener(this);
        buttonPanel.add(makeLabelsFromGoogleFile);
        buttonPanel.add(makeLabelsFromSquareSpaceFileCAonly);
        buttonPanel.add(makeLabelsFromSquareSpaceFileIncludeAll);
        //writeLabelFile = new JButton("       Suck it!       ");
        //writeLabelFile.setHorizontalAlignment(SwingConstants.CENTER);
        //writeLabelFile.addActionListener(this);
        panel.add(buttonPanel);
    }

    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == chooseFile) sourcefile = launchFileChooser();
        if (ae.getSource() == makeLabelsFromGoogleFile)
        {
            if (sourcefile != null)
            {
                which_CSV = for_googleDocs;
                String directory = sourcefile.getPath();
                String dir = directory.substring(0, directory.lastIndexOf(File.separator) + 1);
                make_PDF(dir);
            } else
                JOptionPane.showMessageDialog(null, error_no_file_chosen);
        }
        if (ae.getSource() == makeLabelsFromSquareSpaceFileCAonly)
        {
            if (sourcefile != null)
            {
                which_CSV = for_squarespace_CA;
                String directory = sourcefile.getPath();
                String dir = directory.substring(0, directory.lastIndexOf(File.separator) + 1);
                make_PDF(dir);
            } else
                JOptionPane.showMessageDialog(null, error_no_file_chosen);
        }
        if (ae.getSource() == makeLabelsFromSquareSpaceFileIncludeAll)
        {
            if (sourcefile != null)
            {
                which_CSV = getFor_squarespace_all;
                String directory = sourcefile.getPath();
                String dir = directory.substring(0, directory.lastIndexOf(File.separator) + 1);
                make_PDF(dir);
            } else
                JOptionPane.showMessageDialog(null, error_no_file_chosen);
        }

        // handle other buttons.
    }

    private File launchFileChooser()
    {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new CSV_Filter());
        int returnVal = fc.showOpenDialog(Srsly_Main.this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            return fc.getSelectedFile();
            //log.append("Opening: " + file.getName() + "." + newline);
        } else
        {
            //log.append("Open command cancelled by user." + newline);
            return null;
        }
    }

    private void writeTXTFIleFromGoogleSpreadsheet(String dir)
    {
        // old method for testing accuracy of parsing. writes a simple text file with relevant information.
        destinationFile = new File(dir + fileNameField.getText() + ".txt");
        //fileNameField.setText(destinationFile.getPath());
        if (sourcefile.exists())
        {
            try
            {
                Scanner scanner = new Scanner(sourcefile);
                scanner.nextLine();
                scanner.useDelimiter(",");
                PrintStream ps = new PrintStream(destinationFile);
                System.setOut(ps);
                while (scanner.hasNext())
                {
                    println(scanner.next());
                    scanner.next();
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

            } catch (FileNotFoundException e)
            {
            }
            ;
        }
    }

    private void make_PDF(String dir)
    {
        File destination = new File(dir + fileNameField.getText() + ".pdf");
        if (which_CSV == for_googleDocs)
            populate_ArrayList_googledocs();
        else if (which_CSV == for_squarespace_CA)
            populate_ArrayList_squarespace(true);
        else if (which_CSV == getFor_squarespace_all)
            populate_ArrayList_squarespace(false);
        num_of_labels = labels.size();
        num_of_pages = num_of_labels / 6 + 1;
        font_regular = PDType1Font.HELVETICA;
        font_bold = PDType1Font.HELVETICA_BOLD;
        label_header_spacing = 27;
        label_entry_spacing = 25;
        first_column = 35;  // 50 was too far left
        second_column = 330;
        first_row = 720;   // 700 was too high
        second_row = 475;  // 460 was too high
        third_row = 230;   // 220 was too high
        try
        {
            pdDoc = new PDDocument();
            for (int i = 0; i < num_of_pages; i++)
            {
                PDPage pdPage = new PDPage();
                pdDoc.addPage(pdPage);
                writer = new PDPageContentStream(pdDoc, pdPage);
                writer.setFont(font_bold, 17);

                //Move cursor to initial position and write first label
                if (labels.size() > 0)
                {
                    writeLabel(first_column, first_row);
                }
                //Move cursor to second row, first column
                if (labels.size() > 0)
                {
                    writeLabel(first_column, second_row);
                }
                //Move cursor to third row, first column
                if (labels.size() > 0)
                {
                    writeLabel(first_column, third_row);
                }
                //Move cursor to first row, second column
                if (labels.size() > 0)
                {
                    writeLabel(second_column, first_row);
                }
                //Move cursor to second row, second column
                if (labels.size() > 0)
                {
                    writeLabel(second_column, second_row);
                }
                //Move cursor to third row, second column
                if (labels.size() > 0)
                {
                    writeLabel(second_column, third_row);
                }
                //writer.endText();
                writer.close();
            }
            pdDoc.save(destination);
            pdDoc.close();
        } catch (IOException io)
        {
            JOptionPane.showMessageDialog(null, "Error time :( ! " + io.toString());
            io.printStackTrace();

        } catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Error time :( ! " + e.toString());
            e.printStackTrace();
        }

    }

    private void writeLabel(int positionx, int positiony)
    {
        if (labels.size() > 0)
        {
            try
            {
                if (which_CSV == for_googleDocs)
                {
                    Label current_label = labels.pop();
                    // Skip placeholder labels with zero orders.
                    while (current_label.num_classic == 0 && current_label.num_seeded == 0 &&
                            current_label.num_kale == 0 && current_label.num_rolls == 0 &&
                            current_label.num_dinner_rolls == 0 && current_label.num_pullmans == 0)
                    {
                        if (labels.size() > 0)
                        {
                            current_label = labels.pop();
                        } else return;
                    }
                    writer.setFont(font_bold, 20);
                    writer.beginText();
                    writer.moveTextPositionByAmount(positionx, positiony);  // CURSOR POSITION
                    writer.drawString(current_label.customer);
                    writer.setFont(font_regular, 19);
                    if (current_label.num_classic != 0)
                    {
                        writer.moveTextPositionByAmount(0, -label_header_spacing);
                        writer.drawString("\t" + current_label.num_classic + " Classic");
                    }
                    if (current_label.num_seeded != 0)
                    {
                        writer.moveTextPositionByAmount(0, -label_entry_spacing);
                        writer.drawString("\t" + current_label.num_seeded + " Seeded");
                    }
                    if (current_label.num_kale != 0)
                    {
                        writer.moveTextPositionByAmount(0, -label_entry_spacing);
                        writer.drawString("\t" + current_label.num_kale + " Kale");
                    }
                    if (current_label.num_rolls != 0)
                    {
                        writer.moveTextPositionByAmount(0, -label_entry_spacing);
                        writer.drawString("\t" + current_label.num_rolls + " Rolls");
                    }
                    if (current_label.num_pullmans != 0)
                    {
                        writer.moveTextPositionByAmount(0, -label_entry_spacing);
                        writer.drawString("\t" + current_label.num_pullmans + " Pullmans");
                    }
                    if (current_label.num_dinner_rolls != 0)
                    {
                        writer.moveTextPositionByAmount(0, -label_entry_spacing);
                        writer.drawString("\t" + current_label.num_dinner_rolls + " 12pk Dinner Rolls");
                    }
                    writer.setFont(font_bold, 19);
                    //writer.moveTextPositionByAmount( 0, -25 );
                    //writer.drawString("Box: " + current_label.box_size);
                    writer.moveTextPositionByAmount(0, -label_header_spacing);
                    writer.drawString("Box " + current_label.box_which_of_how_many + " - " + current_label.box_size);
                    writer.endText();
                } else if (which_CSV == for_squarespace_CA)
                {

                }
            } catch (IOException ioe)
            {
            }
        }
    }

    private void populate_ArrayList_googledocs()
    {
        labels = new LinkedList<Label>();
        if (sourcefile.exists())
        {
            try
            {
                Scanner scanner = new Scanner(sourcefile);
                scanner.nextLine(); // To get rid of header
                scanner.useDelimiter(",");
                while (scanner.hasNext())
                {
                    customer = scanner.next();
                    // A '#' at the beginning of a row marks that row as one to skip for label
                    // printing. This while loop throws out '#' marked rows.
                    while (customer.charAt(0) == '#')
                    {
                        scanner.nextLine();
                        // handling for when the last row starts with '#'
                        if (!scanner.hasNextLine())
                        {
                            scanner.close();
                            return;
                        }
                        customer = scanner.next();
                    }
                    // Skip this label creation in favor of forthcoming multiple label creation.
                    Label label = new Label(customer);
                    // Skip the next column, which is notes. Might have to refine error handling for commas in notes.
                    scanner.next();
                    // Error handing for the notes column
                    String next = scanner.next();
                    next.trim();
                    if (next.isEmpty())
                        next = "0";
                    while (!Character.isDigit(next.charAt(0)))
                    {
                        next = scanner.next();
                        next.trim();
                        if (next.isEmpty())
                        {
                            next = "0";
                            break;
                        }
                    }
                    // End error handling
                    try
                    {
                        //label.num_classic = Integer.valueOf(next);
                        num_classic = Integer.valueOf(next);
                        label.num_classic = num_classic;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_seeded = Integer.valueOf(next);
                        label.num_seeded = num_seeded;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_kale = Integer.valueOf(next);
                        label.num_kale = num_kale;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_rolls = Integer.valueOf(next);
                        label.num_rolls = num_rolls;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_pullmans = Integer.valueOf(next);
                        label.num_pullmans = num_pullmans;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_dinner_rolls = Integer.valueOf(next);
                        label.num_dinner_rolls = num_dinner_rolls;
                    } catch (NumberFormatException nfe)
                    {
                        JOptionPane.showMessageDialog(null, "Error time, most likely a non-numerical entry in " +
                                "a numerical field. Mark rows that should be ignored by placing a '#' as the " +
                                "first character in the first column of the row. " + nfe.toString());

                    }
                    box_size = scanner.next();
                    label.box_size = box_size;
                    //The following for testing:
                    //JOptionPane.showMessageDialog(null, customer + " has box size " + box_size);
                    // Handle MULTIPLE BOX SIZES/NUMBERS HERE!!!
                    //JOptionPane.showMessageDialog(null, box_size);
                    String[] boxes = box_size.split("&");
                    int number_of_boxes = boxes.length;
                    for (int i = 0; i < number_of_boxes; i++)
                    {
                        labels.add(new Label(customer, num_classic, num_seeded, num_kale, num_rolls, num_pullmans,
                                num_dinner_rolls, boxes[i], (i + 1) + " of " + number_of_boxes));
                    }
                    scanner.nextLine();
                }
                scanner.close();
            } catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(null, "Error time :( ! " + e.toString());
                e.printStackTrace();

            }
            ;
        }


    }

    private void populate_ArrayList_googledocs_2()
    {
        labels = new LinkedList<Label>();
        if (sourcefile.exists())
        {
            try
            {
                Scanner scanner = new Scanner(sourcefile);
                scanner.useDelimiter(",");
                String current = scanner.next();
                while(!current.matches("^box"))
                {

                }
                while (scanner.hasNext())
                {
                    customer = scanner.next();
                    // A '#' at the beginning of a row marks that row as one to skip for label
                    // printing. This while loop throws out '#' marked rows.
                    while (customer.charAt(0) == '#')
                    {
                        scanner.nextLine();
                        // handling for when the last row starts with '#'
                        if (!scanner.hasNextLine())
                        {
                            scanner.close();
                            return;
                        }
                        customer = scanner.next();
                    }
                    // Skip this label creation in favor of forthcoming multiple label creation.
                    Label label = new Label(customer);
                    // Skip the next column, which is notes. Might have to refine error handling for commas in notes.
                    scanner.next();
                    // Error handing for the notes column
                    String next = scanner.next();
                    next.trim();
                    if (next.isEmpty())
                        next = "0";
                    while (!Character.isDigit(next.charAt(0)))
                    {
                        next = scanner.next();
                        next.trim();
                        if (next.isEmpty())
                        {
                            next = "0";
                            break;
                        }
                    }
                    // End error handling
                    try
                    {
                        //label.num_classic = Integer.valueOf(next);
                        num_classic = Integer.valueOf(next);
                        label.num_classic = num_classic;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_seeded = Integer.valueOf(next);
                        label.num_seeded = num_seeded;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_kale = Integer.valueOf(next);
                        label.num_kale = num_kale;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_rolls = Integer.valueOf(next);
                        label.num_rolls = num_rolls;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_pullmans = Integer.valueOf(next);
                        label.num_pullmans = num_pullmans;
                        next = scanner.next();
                        if (next.isEmpty()) next = "0";
                        num_dinner_rolls = Integer.valueOf(next);
                        label.num_dinner_rolls = num_dinner_rolls;
                    } catch (NumberFormatException nfe)
                    {
                        JOptionPane.showMessageDialog(null, "Error time, most likely a non-numerical entry in " +
                                "a numerical field. Mark rows that should be ignored by placing a '#' as the " +
                                "first character in the first column of the row. " + nfe.toString());

                    }
                    box_size = scanner.next();
                    label.box_size = box_size;
                    //The following for testing:
                    //JOptionPane.showMessageDialog(null, customer + " has box size " + box_size);
                    // Handle MULTIPLE BOX SIZES/NUMBERS HERE!!!
                    //JOptionPane.showMessageDialog(null, box_size);
                    String[] boxes = box_size.split("&");
                    int number_of_boxes = boxes.length;
                    for (int i = 0; i < number_of_boxes; i++)
                    {
                        labels.add(new Label(customer, num_classic, num_seeded, num_kale, num_rolls, num_pullmans,
                                num_dinner_rolls, boxes[i], (i + 1) + " of " + number_of_boxes));
                    }
                    scanner.nextLine();
                }
                scanner.close();
            } catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(null, "Error time :( ! " + e.toString());
                e.printStackTrace();
            }
            ;
        }
    }

    private void populate_ArrayList_squarespace(boolean california_only)
    {
        labels = new LinkedList<Label>();
        if (sourcefile.exists())
        {
            try
            {
                Scanner scanner = new Scanner(sourcefile);
                scanner.nextLine(); // To get rid of header
                scanner.useDelimiter(",");
                while (scanner.hasNext())
                {
                    //= scanner.next();
                }

            } catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(null, "Error time :( ! " + e.toString());
                e.printStackTrace();
            }
            ;
        }
    }

    private void print(String s)
    {
        System.out.print(s);
    }

    private void println(String s)
    {
        System.out.println(s);
    }

    public static void main(String Args[])
    {
        new Srsly_Main();
    }
}

class Label
{
    public String customer, box_size, box_which_of_how_many;
    public int order_number, num_of_boxes, num_classic, num_seeded, num_kale, num_rolls, num_pullmans, num_dinner_rolls;
    public ArrayList<Order_Entry> order_entries;
    boolean is_Squarespace;


    Label(String cust)
    {
        customer = cust;
    }

    Label(String cust, int classic, int seeded, int kale, int rolls, int pullmans, int dinner_rolls, String what_box, String which_box)
    {
        customer = cust;
        num_classic = classic;
        num_seeded = seeded;
        num_kale = kale;
        num_rolls = rolls;
        num_pullmans = pullmans;
        num_dinner_rolls = dinner_rolls;
        box_size = what_box;
        box_which_of_how_many = which_box;
    }

    Label(String customer, int order_number, String order, int quantity)
    {
        this.customer = customer;
        this.order_number = order_number;
        order_entries = new ArrayList<Order_Entry>();
        order_entries.add(new Order_Entry(order, quantity));
    }

    public void add_order_entry(Order_Entry new_order)
    {
        if (order_entries.)
            order_entries.add(new_order);
    }

}

class Order_Entry
{
    public String item;
    public int quantity;

    Order_Entry(String item, int quantity)
    {
        this.item = item;
        this.quantity = quantity;
    }

}

class CSV_Filter extends FileFilter
// This class is a filefilter for the filechooser, and filters out only .csv files.
{

    //Accept all directories and only csv files
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }
        String extension = getExtension(f);
        if (extension != null)
        {
            if (extension.equals("csv"))
                return true;
            else
                return false;
        }

        return false;
    }

    private static String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1)
        {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    //The description of this filter
    public String getDescription()
    {
        return ".csv";
    }
}


