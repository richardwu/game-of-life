import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GameOfLife 
{
	private JFrame window = new JFrame();

	private Colony colony = new Colony (100, 60, 0);	

	private JButton startStopBtn = new JButton ("Start");	// buttons for GUI
	private JButton nextBtn = new JButton ("Next");
	private JButton loadBtn = new JButton ("Load");
	private JButton saveBtn = new JButton ("Save");
	private JButton generateBtn = new JButton ("Generate");
	private JButton populateBtn = new JButton ("Populate");
	private JButton eradicateBtn = new JButton ("Eradicate");

	private JSlider speedSlider = new JSlider (JSlider.HORIZONTAL, 0, 100, 0);	// slider for generations per second

	private JTextArea generations = new JTextArea ("Generations: " + colony.getGenerations ());	// shows # of generations

	private JFileChooser fc = new JFileChooser ("./savedfiles");								// file chooser to load files
	FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files (*.txt)", "txt", "text");

	private JDialog savePrompt = new JDialog (window, "Save as...", true);	// dialog box for saving files

	private String [] templateStrings = {"Clear", "Glider", "Small-Exploder", "Exploder", "10-Cell-Row", "Lightweight-Spaceship", "Tumbler", "Gosper-Glider-Gun"};
	private JComboBox templateList = new JComboBox(templateStrings);	// combo box for choosing templates

	private boolean loop = false;

	public GameOfLife ()
	{
		JPanel content = new JPanel ();			// content pane
		content.setLayout(new BorderLayout ());

		JPanel buttonPane = new JPanel ();	// button pane (top row)

		buttonPane.setBackground(Color.WHITE);	// sets both panes to white colour
		content.setBackground(Color.WHITE);

		startStopBtn.addActionListener(new startStopBtnActionListener());	// adds action listeners for buttons
		nextBtn.addActionListener(new nextBtnActionListener());
		loadBtn.addActionListener(new loadBtnActionListener());
		saveBtn.addActionListener(new saveBtnActionListener());
		generateBtn.addActionListener(new generateBtnActionListener());
		populateBtn.addActionListener(new populateBtnActionListener());
		eradicateBtn.addActionListener(new eradicateBtnActionListener());

		speedSlider.setBackground(Color.WHITE);	// sets properties for the generations per second slider
		speedSlider.setMinorTickSpacing(5);
		speedSlider.setMajorTickSpacing(20);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);

		fc.setFileFilter(filter);				// makes it so that you can only select text files in file chooser
		fc.setAcceptAllFileFilterUsed(false);

		buttonPane.add(new JTextArea ("Generations Per Second"));	// adds components to button pane
		buttonPane.add(speedSlider);
		buttonPane.add(startStopBtn);
		buttonPane.add(nextBtn);
		buttonPane.add(generations);
		buttonPane.add(loadBtn);
		buttonPane.add(saveBtn);
		buttonPane.add(templateList);
		buttonPane.add(generateBtn);
		buttonPane.add(populateBtn);
		buttonPane.add(eradicateBtn);

		content.add(buttonPane, "North");	// adds the button pane and colony grid to content pane
		content.add(colony, "Center");

		window.add(content);										// sets properties of the frame
		window.setSize(1204, 800);
		window.setTitle("Conway's Game of Life - By Richard Wu");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		while (true)																// loops advance method when start button is pressed
		{
			if (loop)	// executes advance method when loop boolean is set to true
			{
				if (speedSlider.getValue() != 0)	// if gps is not 0, calls advance method
				{
					colony.advance();
					generations.setText("Generations: " + colony.getGenerations ());	// updates display of # of generations
					try 											// timer depending on slider value
					{
						Thread.sleep (1000/speedSlider.getValue());
					} 
					catch (InterruptedException e) {}
				}
			}

			try									// slight delay for program to register if statement
			{
				Thread.sleep (1);
			} 
			catch (InterruptedException e) {}
		}
	}

	public class startStopBtnActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			if (loop == false)					// if start button is pressed
			{
				loop = true;	// starts looping
				startStopBtn.setText ("Stop");	// changes text of button to stop
				nextBtn.setEnabled(false);		// disables other buttons
				loadBtn.setEnabled(false);
				saveBtn.setEnabled(false);
				generateBtn.setEnabled(false);
				populateBtn.setEnabled(false);
				eradicateBtn.setEnabled(false);
			}
			else								// if stop button is pressed
			{
				loop = false;	// stops looping
				startStopBtn.setText ("Start");	// changes text of button back to start
				nextBtn.setEnabled(true);		// renables disabled buttons
				loadBtn.setEnabled(true);
				saveBtn.setEnabled(true);
				generateBtn.setEnabled(true);
				populateBtn.setEnabled(true);
				eradicateBtn.setEnabled(true);
			}
		}	
	}

	public class nextBtnActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			colony.advance();	// advances generation by 1
			generations.setText("Generations: " + colony.getGenerations ());	// updates # of generations display
		}
	}

	public class loadBtnActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			int returnValue = fc.showOpenDialog(colony);	// obtains selected file

			if (returnValue == JFileChooser.APPROVE_OPTION)	// if valid selection, loads colony
				colony.load(fc.getSelectedFile().getPath());

			generations.setText("Generations: " + colony.getGenerations ());	// updates GUI
			colony.show ();
		}
	}

	public class saveBtnActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			JPanel content = new JPanel ();									// creates container panels for dialog box
			content.setLayout (new BoxLayout(content, BoxLayout.Y_AXIS));
			JPanel top = new JPanel ();				
			top.setLayout (new FlowLayout());
			JPanel middle = new JPanel ();
			middle.setLayout (new FlowLayout ());
			JPanel bottom = new JPanel ();
			bottom.setLayout (new FlowLayout ());

			final JTextField fileName = new JTextField (10);	// text field for dialog box

			JButton okBtn = new JButton ("Ok");				// ok button for dialog box
			okBtn.addActionListener(new ActionListener () 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					String path = fileName.getText();		// gets the user inputted text

					if (path.isEmpty())		// if blank text field, defaults to Untitled
						path = "Untitled";

					path += ".txt";	// adds the .txt extension

					colony.save(path);	// calls save method

					savePrompt.setVisible(false);	// hides the dialog box
				}
			});

			JButton cancelBtn = new JButton ("Cancel");			// cancel button for dialog box
			cancelBtn.addActionListener (new ActionListener () 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					savePrompt.setVisible(false);	// hides the dialog box
				}
			});

			fileName.setText ("Untitled");						// sets default name in text field as Untitled

			top.add(new JLabel ("Please enter file name:"));	

			middle.add(fileName);				// adds components to panels
			middle.add(new JLabel (".txt"));
			bottom.add(okBtn);
			bottom.add(cancelBtn);
			content.add(top);
			content.add(middle);
			content.add(bottom);

			savePrompt.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);	// sets properties for dialog box
			savePrompt.setLocationRelativeTo(null);
			savePrompt.setSize(200, 150);
			savePrompt.add (content);
			savePrompt.setVisible (true);
		}
	}

	public class generateBtnActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			String path = (String)templateList.getSelectedItem();	// takes selected option from combo box

			path += ".txt";

			colony.load("./templates\\" + path);	// loads template file from templates folder

			generations.setText("Generations: " + colony.getGenerations ());	// updates GUI
			colony.show();
		}
	}

	public class populateBtnActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			if (colony.getPopErad() == false)	// if button is just being clicked
			{
				colony.setPopErad(true);	// sets popErad boolean in colony class to true
				populateBtn.setForeground(Color.GREEN);	// changes text font to green
				populateBtn.setText("Confirm");	// sets new text to "Confirm"
				nextBtn.setEnabled(false);		// disables other buttons
				loadBtn.setEnabled(false);
				saveBtn.setEnabled(false);
				generateBtn.setEnabled(false);
				startStopBtn.setEnabled(false);
				eradicateBtn.setEnabled(false);
			}
			else
			{
				colony.setPopErad(false);	// sets popErad boolean in colony class to false
				populateBtn.setForeground(Color.BLACK);	// changes text font back to black
				populateBtn.setText("Populate");	// sets text back to "Populate"
				nextBtn.setEnabled(true);		// disables other buttons
				loadBtn.setEnabled(true);
				saveBtn.setEnabled(true);
				generateBtn.setEnabled(true);
				startStopBtn.setEnabled(true);
				eradicateBtn.setEnabled(true);

				colony.populate();
			}
		}
	}

	public class eradicateBtnActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			if (colony.getPopErad() == false)	// if button is just being clicked
			{
				colony.setPopErad(true);	// sets popErad boolean in colony class to true
				eradicateBtn.setForeground(Color.GREEN);	// changes text font to green
				eradicateBtn.setText("Confirm");	// sets new text to "Confirm"
				nextBtn.setEnabled(false);		// disables other buttons
				loadBtn.setEnabled(false);
				saveBtn.setEnabled(false);
				generateBtn.setEnabled(false);
				startStopBtn.setEnabled(false);
				populateBtn.setEnabled(false);
			}
			else	// if button is being clicked for "Confirm"
			{
				colony.setPopErad(false);	// sets popErad boolean in colony class to false
				eradicateBtn.setForeground(Color.BLACK);	// changes text font back to black
				eradicateBtn.setText("Eradicate");	// sets text back to "Eradicate"
				nextBtn.setEnabled(true);		// disables other buttons
				loadBtn.setEnabled(true);
				saveBtn.setEnabled(true);
				generateBtn.setEnabled(true);
				startStopBtn.setEnabled(true);
				populateBtn.setEnabled(true);

				colony.eradicate();
			}
		}
	}

	public static void main (String [] args)
	{
		GameOfLife game = new GameOfLife ();
	}
}
