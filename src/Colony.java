import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JComponent;


class Colony extends JComponent implements MouseListener, MouseMotionListener 
{
	private boolean [][] grid;

	private int generations = 0;

	private boolean dragChange = false;

	private BufferedReader reader;

	private boolean popErad = false;
	private int dragStartX, dragStartY, dragEndX, dragEndY;
	private int minX, minY, maxX, maxY;

	final private int cellSize = 10;	// default cell size and spacing
	final private int cellSpacing = 2; 

	final private double popEradChance = 0.8;	// % chance of populating/eradicating cells

	public Colony (int gridSizeX, int gridSizeY, double density)	// constructor for a colony
	{
		grid = new boolean [gridSizeX][gridSizeY];

		for (int row = 0; row < grid.length; row++)			// random configuration w/ density
		{
			for (int col = 0; col < grid[row].length; col++)
				grid [row][col] = Math.random () < density; 
		}
		addMouseListener (this);		// adds mouse listeners
		addMouseMotionListener(this);
	}

	public void show ()	// method called to repaint GUI
	{
		repaint ();
	}

	public void paintComponent (Graphics g)	// paints the colony
	{
		for (int row = 0; row <grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				if (grid[row][col])				// yellow for alive, black for non-existent
					g.setColor (Color.YELLOW);
				else
					g.setColor(Color.BLACK);
				g.fillRect(row * (cellSize + cellSpacing), col * (cellSize + cellSpacing), cellSize, cellSize);	// draws life form
			}
		}

		if (popErad)	// for populating/eradicating selection box
		{	
			minX = Math.min(dragStartX, dragEndX);	// finds the upper left and lower right corners
			minY = Math.min(dragStartY, dragEndY);
			maxX = Math.max(dragStartX, dragEndX);
			maxY = Math.max(dragStartY, dragEndY);

			g.setColor(Color.GREEN);							// draws rectangle
			g.drawRect (minX, minY, maxX - minX, maxY - minY);
		}
	}

	public boolean live (int row, int column)	// checks live cells
	{
		int neighbours;
		boolean isAlive;

		neighbours = numOfNeighbours (row, column);

		if (grid[row][column])	// if live cell to begin with
		{
			if (neighbours == 2 || neighbours == 3)	// if exactly 2 or 3 neighbours, stays alive
				isAlive = true;
			else	// else overcrowding/underpopulation kills cell
				isAlive = false;
		}
		else	// if dead cell to begin with
		{
			if (neighbours == 3)	// if exactly 3 neighbours, becomes alive
				isAlive = true;
			else
				isAlive = false;
		}

		return isAlive;
	}

	private int numOfNeighbours (int row, int column)	// counts the number of neighbours for one cell
	{
		int counter = 0;
		boolean [][] check = new boolean [3][3];

		for (int i = 0; i < 3; i++)		// intialises all elements of check array as true
		{
			for (int j = 0; j < 3; j++)
				check[i][j] = true;
		}

		check[1][1] = false;	// intialises middle element of array as false (occupied by cell in question)

		// checks cell to see if it's on the first or last row	
		if (row == 0)	// if cell occupies first row, intialises top row of check array as false
		{
			for (int i = 0; i < 3; i++)
				check[0][i] = false;
		}
		else if (row == grid.length - 1)	// if cell occupies last row, intialises bottom row of check array as false
		{
			for (int i = 0; i < 3; i++)
				check[2][i] = false; 
		}

		// checks cell to see if it's on the first or last column		
		if (column == 0)	// if cell occupies first column, intialises left column of check array as false
		{
			for (int i = 0; i < 3; i++)
				check[i][0] = false;
		}
		else if (column == grid[row].length - 1)	// if cell occupies last column, intialises right column of check array as false
		{
			for (int i = 0; i < 3; i++)
				check[i][2] = false;
		}

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				if (check[i][j])	// if tile to be checked
				{
					if (grid [row + (i - 1)][column + (j - 1)])	// if checked neighbour cell is alive
						counter++;
				}
			}
		}

		return counter;
	}

	public void advance ()	// advances the colony by one generation 
	{
		boolean [][] tempGrid = new boolean [grid.length][grid[0].length];	// creates a temporary grid to record new cells

		generations++;	// increments the # of generations by 1

		for (int i = 0; i < grid.length; i++)		// loops to check grid for each cell
		{
			for (int j = 0; j < grid[i].length; j++)	// assigns live boolean to temporary grid
				tempGrid[i][j] = live (i, j);
		}
		grid = tempGrid;	// references temp grid back to original grid
		show();	// updates GUI
	}

	public void load (String path) // loads a text file with saved colony configuration
	{
		int row = 0, column = 0;

		FileReader file = null;
		try 										// loads file
		{
			file = new FileReader(path);
			reader = new BufferedReader (file);
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println(path + " not found.");
		}

		try													// reads the # of rows, columns and generations
		{
			row = Integer.parseInt(reader.readLine());
			column = Integer.parseInt(reader.readLine());
			generations = Integer.parseInt(reader.readLine());
		}
		catch (IOException e)
		{
			System.out.println(path + " is corrupted.");
		}

		grid = new boolean [row][column];	// assigns new boolean

		for (int i = 0; i < grid.length; i++)	// loops through new grid and assigns correct positions
		{
			String str = null;								// reads the text
			try
			{
				str = reader.readLine();
			}
			catch (IOException e)
			{
				System.out.println(path + " is corrupted.");
			}

			for (int j = 0; j < grid[i].length; j++)
			{
				int temp = 0;

				temp = str.charAt(j) - '0';

				if(temp == 1)			// changes to boolean
					grid[i][j] = true;
				else
					grid[i][j] = false;
			}
		}

	}

	public void save (String path)	// saves the current colony configuration
	{
		FileWriter output = null;				// creates a new file to store values
		BufferedWriter writer = null;

		try
		{
			output = new FileWriter("./savedfiles\\" + path);
			writer = new BufferedWriter(output);
		}
		catch(IOException e){}

		try												// stores # of rows,columns, and generations on first 3 lines
		{
			writer.write(grid.length + "");
			writer.newLine();
			writer.write(grid[0].length + "");
			writer.newLine();
			writer.write(generations + "");
			writer.newLine();
		}
		catch (IOException e){}

		for (int i = 0; i < grid.length; i++)			// writes values to store array of cells
		{
			for (int j = 0; j < grid[i].length; j++)
			{
				int temp;		// converts to integer

				if (grid[i][j])
					temp = 1;
				else
					temp = 0;

				try												// stores the value to the file
				{
					writer.write(temp + "");
				}
				catch (IOException e){}
			}

			try												// moves on to next line for new value
			{
				writer.newLine();
			}
			catch(IOException e){}
		}

		try						// closes the writer
		{
			writer.close();
		}
		catch (IOException e) {}
	}

	public void populate ()	// populates selected cells depending on % chance
	{
		int startX, endX, startY, endY;

		startX = minX / (cellSize + cellSpacing);	// finds start and end indices for rows and column
		endX = maxX / (cellSize + cellSpacing);
		startY = minY / (cellSize + cellSpacing);
		endY = maxY / (cellSize + cellSpacing);

		for (int i = startX; i <= endX; i++)	// goes through the selected area
		{
			for (int j = startY; j <= endY; j++)
			{
				if (i >= 0 && i < grid.length && j >= 0 && j < grid[i].length)	// checks if selection is within grid
				{
					if (Math.random() < popEradChance)	// factors in % chance
						grid[i][j] = true;	// populates
				}
			}
		}

		show();
	}

	public void eradicate ()	// eradicates selected cells depending on % chance
	{
		int startX, endX, startY, endY;

		startX = minX / (cellSize + cellSpacing);	// finds start and end indices for rows and column
		endX = maxX / (cellSize + cellSpacing);
		startY = minY / (cellSize + cellSpacing);
		endY = maxY / (cellSize + cellSpacing);

		for (int i = startX; i <= endX; i++)	// goes through the selected area
		{
			for (int j = startY; j <= endY; j++)
			{
				if (i >= 0 && i < grid.length && j >= 0 && j < grid[i].length)	// checks if selection is within grid
				{
					if (Math.random() < popEradChance)	// factors in % chance
						grid[i][j] = false;	// eradicates
				}
			}
		}

		show();
	}

	public int getGenerations ()	// getter method for # of generations
	{
		return generations;
	}

	public boolean getPopErad ()	// getter method for boolean popErad
	{
		return popErad;
	}

	public void setPopErad (boolean popErad)	// setter method for boolean popErad
	{
		this.popErad = popErad;
	}

	@Override
	public void mousePressed(MouseEvent e)	// mouse pressed mouse listener
	{
		int x = e.getX();	// obtains coordinates of mouse click
		int y = e.getY();

		if (popErad)	// if populating or eradicating
		{
			dragStartX = x;	// stores beginning point
			dragStartY = y;
		}
		else
		{
			x /= cellSize + cellSpacing;	// calculates corresponding cell row and column
			y /= cellSize + cellSpacing;

			if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length)	// if within constraints of grid
			{
				if (grid [x][y])		// if already alive, eradicates it
				{
					grid [x][y] = false;
					dragChange = false;
				}
				else					// if dead, populates it
				{
					grid [x][y] = true;
					dragChange = true;
				}

				show ();	// updates GUI
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)	// continuation of mouse pressed mouse listener
	{
		int x = e.getX();	// gets coordinates of mouse dragging
		int y = e.getY();

		if (popErad)	// if populating or eradicating
		{
			dragEndX = x;	// stores and updates final points
			dragEndY = y;
			show();
		}
		else
		{
			x /= cellSize + cellSpacing;	// calculates corresponding cell row and column
			y /= cellSize + cellSpacing;

			if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length)	// if within constraints of grid
			{
				grid[x][y] = dragChange;	// populates/eradicates according to intial mouse click

				show ();	// updates GUI
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
	}
}
