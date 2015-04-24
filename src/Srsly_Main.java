
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
 * Author: Andrey Kobzar
 * Date: 3/12/15
 *  This tool made for the company BREAD SRSLY formats two different kinds of their spreadsheet data into pdfs
 *  suitable for printing avery labels, the kind that come 6 on a standard printer sheet.
 * /**************************************************************************************************************
 */

public class Srsly_Main extends JFrame implements ActionListener
{
    private JPanel panel;
    private JButton chooseFile, writeLabelFile, makeLabelsFromGoogleFile, makeLabelsFromSquareSpaceFileCAonly,
            makeLabelsFromSquareSpaceFileIncludeAll;
    private JTextField fileNameField;
    private JLabel instructionLabel, fileNameLabel, buttonLabel;
    private final String instructions = "Avoid suffering :-)";
    private final String labelFileName = "Labels-Srsly";
    private final String error_no_file_chosen = "Why didn't you choose a source file?! I'm a sad program now :'( ";
    private final String something = "something";
    private String customer, item, box_size, state;
    private int num_of_labels, num_of_pages, which_CSV;
    private final int for_googleDocs = 0, for_squarespace_CA = 1, getFor_squarespace_all = 2;
    private int num_of_boxes, order_number, next_order_number, quantity, num_classic, num_seeded, num_kale, num_rolls, num_pullmans, num_dinner_rolls,
            label_header_spacing, label_entry_spacing, first_column, second_column, first_row, second_row, third_row;

    private File sourcefile, destinationFile;
    private LinkedList<Label> labels;
    private ArrayList<String> headings;
    private String[] wraparound_text;
    private ArrayList<Order_Entry> order_entries;
    private PDDocument pdDoc;
    private PDFont font_regular, font_bold;
    private PDPageContentStream writer;
    // Fields for entry of Label objects:
    private boolean is_empty_entry;

    public Srsly_Main()
    {
        setTitle("LBLS SRSLYS!");
        setLayout(new BorderLayout());
        setSize(300, 300);
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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(0);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        //instructionLabel = new JLabel(instructions);
        //panel.add(instructionLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        chooseFile = new JButton("Select the source .csv file");
        chooseFile.addActionListener(this);
        inputPanel.add(chooseFile);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        fileNameLabel = new JLabel("Name the output file:");
        inputPanel.add(fileNameLabel);
        // Get date to add to file name
        DateFormat dateFormat = new SimpleDateFormat("-MMM-dd-yyyy");
        Date date = new Date();
        fileNameField = new JTextField(labelFileName + dateFormat.format(date));
        fileNameField.setMaximumSize(new Dimension(500, 30));
        inputPanel.add(fileNameField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonLabel = new JLabel(" Select processing type:");
        inputPanel.add(buttonLabel);
        panel.add(inputPanel);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(500, 150));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
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
        label_header_spacing = 30;
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

    private void populate_ArrayList_googledocs()
    {
        labels = new LinkedList<Label>();
        if (sourcefile.exists())
        {
            try
            {
                Scanner scanner = new Scanner(sourcefile);
                scanner.useDelimiter(",");
                //Skip first two columns before loading product names, count product names.
                scanner.next();
                scanner.next();
                headings = new ArrayList<String>();
                String current = scanner.next();
                while (!current.matches("(.*)box(.*)") && !current.matches("(.*)Box(.*)" ))
                {
                    headings.add(current);
                    current = scanner.next();
                }
                //JOptionPane.showMessageDialog(null, "num of fields: " + headings.get(0) + headings.get(headings.size()-1));

                        // Move to data rows:
                scanner.nextLine();


                while (scanner.hasNext())
                {
                    customer = scanner.next();
                    // A '#' at the beginning of a row marks that row as one to skip for label
                    // printing. This while loop throws out '#' marked rows.
                    while (customer.charAt(0) == '#')
                    {
                        // handling for when the last row starts with '#'
                        scanner.nextLine();
                        if (!scanner.hasNextLine())
                        {
                            scanner.close();
                            return;
                        }
                        customer = scanner.next();
                    }
                    // Old band aid fix for wraparound problem
                    /*
                    if (customer.length() > 23)
                    {
                        customer = customer.substring(0, 23);
                    }
                    */
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
                    //Add first entry that was retrieved as part of the above comma-catching:
                    is_empty_entry = true;
                    order_entries = new ArrayList<Order_Entry>();
                    // Add first entry manually, since error handling already has first item's quantity qued up.
                    quantity = Integer.valueOf(next);
                    if(quantity > 0)
                    {
                        is_empty_entry = false;
                        order_entries.add(new Order_Entry(headings.get(0), quantity));
                    }
                    // End error handling
                    // Error handing for the notes column
                    for (int i = 1; i < headings.size() ; i++)
                    {
                        try
                        {
                            next = scanner.next();
                            next.trim();
                            if (next.isEmpty()) next = "0";
                            quantity = Integer.valueOf(next);
                            if (quantity > 0)
                            {
                                is_empty_entry = false;
                                order_entries.add(new Order_Entry(headings.get(i), quantity));
                            }
                        } catch (NumberFormatException nfe)
                        {
                            JOptionPane.showMessageDialog(null, "Error time, most likely a non-numerical entry in " +
                                    "a numerical field. \n Mark rows that should be ignored by placing a '#' as the " +
                                    "first character in the first column of the row. \n" + nfe.toString());
                        }
                    }
                    // Throw out this entry and start over if it's an empty entry
                    if(is_empty_entry)
                    {
                        scanner.nextLine();
                        continue;
                    }
                    box_size = scanner.next();
                    String[] boxes = box_size.split("&");
                    int number_of_boxes = boxes.length;
                    for (int i = 0; i < number_of_boxes; i++)
                    {
                        labels.add(new Label(customer, order_entries, boxes[i], (i + 1) + " of " + number_of_boxes));
                    }

                    scanner.nextLine();
                }
                scanner.close();
            } catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(null, "Error time : " + e.toString());
                e.printStackTrace();
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "Error time : " + e.toString());
                e.printStackTrace();
            }
        }
    }

    private void writeLabel(int positionx, int positiony)
    {
        if (labels.size() > 0)
        {
            try
            {
                {
                    Label current_label = labels.pop();
                    // Skip placeholder labels with zero orders.
                    writer.setFont(font_bold, 22);
                    writer.beginText();
                    writer.moveTextPositionByAmount(positionx, positiony);  // CURSOR POSITION
                    if(current_label.customer.length() > 23)
                    {
                        wraparound_text = new String[current_label.customer.length()/23 + 1];
                        for(int j = 0 ; j < wraparound_text.length ; j ++)
                        {
                            if(j == wraparound_text.length - 1)
                            {
                                wraparound_text[j] = current_label.customer;
                                continue;
                            }
                            for(int i = 23 ; i > 0 ; i --)
                            {
                                if(i > current_label.customer.length()) i = current_label.customer.length() - 1;
                                if (current_label.customer.charAt(i)==' ')
                                {
                                    wraparound_text[j] = current_label.customer.substring(0, i);
                                    current_label.customer = current_label.customer.substring(i, current_label.customer.length());
                                    break;
                                }
                            }
                        }

                        for(int i = 0 ; i < wraparound_text.length ; i ++)
                        {
                            writer.drawString("" + wraparound_text[i]);
                            if(i == wraparound_text.length - 1) continue;
                            writer.moveTextPositionByAmount(0, -label_entry_spacing);
                        }
                    }
                    else writer.drawString(current_label.customer);
                    // Set font size based on number of entries:
                    int order_item_font_size = 100 / current_label.order_entries.size();
                    if (order_item_font_size > 20) order_item_font_size = 20;
                    label_entry_spacing = order_item_font_size + 4;
                    writer.setFont(font_regular, order_item_font_size);
                    for( int i = 0 ; i < current_label.order_entries.size() ; i ++)
                    {
                        Order_Entry current_order_entry = current_label.order_entries.get(i);
                        //if(current_order_entry.quantity == 0)
                          //  continue;
                        if( i == 0 )writer.moveTextPositionByAmount(0, - label_header_spacing);
                        else writer.moveTextPositionByAmount(0, - label_entry_spacing);
                        if(current_order_entry.item.length() > 23)
                        {
                            wraparound_text = new String[current_order_entry.item.length()/23 + 1];
                            for(int j = 0 ; j < wraparound_text.length ; j ++)
                            {
                                if( j == wraparound_text.length - 1)                                 {
                                    wraparound_text[j] = current_order_entry.item;
                                    continue;
                                }
                                for(int k = 23; k > 0 ; k --)
                                {
                                    if(k > current_order_entry.item.length()) k = current_order_entry.item.length() - 1;
                                    if (current_order_entry.item.charAt(k)==' ')
                                    {
                                        wraparound_text[j] = current_order_entry.item.substring(0, k);
                                        current_order_entry.item = current_order_entry.item.substring(k, current_order_entry.item.length());
                                        break;
                                    }
                                }
                            }
                            writer.drawString("\t" + current_order_entry.quantity + " " + wraparound_text[0]);
                            for(int k = 1 ; k < wraparound_text.length ; k ++)
                            {
                                writer.moveTextPositionByAmount(0, - label_entry_spacing);
                                writer.drawString("   " + wraparound_text[k]);
                            }

                        }
                        else writer.drawString("\t" + current_order_entry.quantity + " " + current_order_entry.item);
                    }
                    //Squarespace labels won't have box info
                    if(!current_label.is_Squarespace)
                    {
                        writer.setFont(font_bold, 19);
                        //writer.moveTextPositionByAmount( 0, -25 );
                        //writer.drawString("Box: " + current_label.box_size);
                        writer.moveTextPositionByAmount(0, -label_header_spacing);
                        writer.drawString("Box " + current_label.box_which_of_how_many + " - " + current_label.box_size);
                    }
                    else
                    {
                        writer.setFont(font_bold, 19);
                        //writer.moveTextPositionByAmount( 0, -25 );
                        //writer.drawString("Box: " + current_label.box_size);
                        writer.moveTextPositionByAmount(0, -label_header_spacing);
                        writer.drawString("Box: ");
                    }
                    writer.endText();
                }
            } catch (IOException ioe)
            {
            }
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
                // get first order number, subsequent ones are retrieved at the end of each loop
                order_number = Integer.valueOf(scanner.next().replace("\"", ""));
                while (scanner.hasNext())
                {
                    order_entries = new ArrayList<Order_Entry>();
                    // order number is in first column - orders of multiple types of items appear as mostly empty rows
                    // following a customer's initial row, where only the additional items are specified and the same
                    // order number appears at the beginning of the row.
                    // Move across 15 columns until item quantity is reached
                    for(int i = 0 ; i < 15 ; i ++)
                    {
                        scanner.next();
                    }
                    quantity = Integer.valueOf(scanner.next().replace("\"", ""));
                    item = scanner.next();
                    if(item.matches("^\"Classic$"))
                    {
                        item = (item + scanner.next()).replace("\"","");

                    }
                    order_entries.add(new Order_Entry(item, quantity));
                    // Move across 6 columns until customer's name is reached
                    for(int i = 0 ; i < 6 ; i ++)
                    {
                        scanner.next();
                    }
                    customer = scanner.next();
                    //IF california only, skip all non-CA rows:
                    if(california_only)
                    {
                        for(int i = 0 ; i < 4 ; i ++)
                        {
                            scanner.next();
                        }
                        state = scanner.next();
                        if(!state.matches("^CA$"))
                        {
                            scanner.nextLine();
                            if(scanner.hasNext())
                            {
                                next_order_number = Integer.valueOf(scanner.next().replace("\"", ""));
                                while(order_number == next_order_number && scanner.hasNextLine())
                                {
                                    scanner.nextLine();
                                    next_order_number = Integer.valueOf(scanner.next().replace("\"", ""));
                                }
                                order_number = next_order_number;
                            }
                            //JOptionPane.showMessageDialog(null, customer + " " + "is from " + state);
                            continue;
                        }
                    }
                    //otherwise, check subsequent rows for identical order number, add their order items to this order.
                    next_order_number = order_number;
                    while(order_number == next_order_number && scanner.hasNextLine())
                    {
                        scanner.nextLine();
                        if(scanner.hasNext())
                        {
                            next_order_number = Integer.valueOf(scanner.next().replace("\"", ""));
                            if(order_number == next_order_number)
                            {
                                for(int i = 0 ; i < 15 ; i ++)
                                {
                                    scanner.next();
                                }
                                quantity = Integer.valueOf(scanner.next().replace("\"", ""));
                                order_entries.add(new Order_Entry(scanner.next(), quantity));
                            }
                        }
                    }
                    labels.add(new Label(customer, order_entries));
                    order_number = next_order_number;
                }

            } catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(null, "Error time :( ! " + e.toString());
                e.printStackTrace();
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "Error time :( ! " + e.toString());
                e.printStackTrace();
            }

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
        order_entries = new ArrayList<Order_Entry>();
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

    Label(String cust, ArrayList<Order_Entry> order_entries, String what_box, String which_box)
    {
        customer = cust;
        this.order_entries = order_entries;
        box_size = what_box;
        box_which_of_how_many = which_box;
        // this instantiation is used for googledoc
        is_Squarespace = false;

    }

    Label(String cust, ArrayList<Order_Entry> order_entries)
    {
        customer = cust;
        this.order_entries = order_entries;
        // this instantiation is used for squarespace
        is_Squarespace = true;
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


