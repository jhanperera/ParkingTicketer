import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Exception;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.String;

/*
Done:
	- Layout of the main window
	- Layout of the popup-window for inputing numbers only
	- MouseListeners for pop-window only for Student ID
	- Back action to main window + input text in correct feilds.
	- Implemented ActionListeners for the Pop up number pad.
	- Implemented  13/13 buttons on Pop up number pad.
	- Layout of the keyboard for email
	- Implemeted ALL buttons onthe Keybaord
	- Implemented the maximum characters allowed for each field
	- Added "GhostText" for each field
	- Permit Layout and Design
To Do:
	- 

*/

/**
 *The main class that executes and first frame in the system.
 *
 *{@link a1Frame}
 */
public class a1
{
	public static void main(String[] args)
	{
		BufferedReader stdFile = null;
		BufferedReader cmpFile = null;
		
		boolean stdFormat = true;
		boolean cmpFormat = true;
		
		int i = 0;
		int j = 0;
		
		String stdRegex = "(.+),(.+),(.+),(.+),(.+)";
		String cmpRegex = "(.+),(.+)";
		String tempLine = null;
		
		try {
			stdFile = new BufferedReader(new FileReader("students.txt"));
			cmpFile = new BufferedReader(new FileReader("companies.txt"));
			
			String lines = "";
			while ((tempLine = stdFile.readLine()) != null)
			{
				if (!(tempLine.matches(stdRegex)))
				{
					lines = lines + (i + 1) + " ";
					stdFormat = false;
				}

				i++;
			}
			
			if (!(stdFormat))
			{
				JOptionPane.showMessageDialog(null, "<html>Student List File Format Error, Check line(s): "+lines+"<br>Format must be:<br>"+
					"<b>&lt studnet id &gt,&lt pin &gt,&lt last name &gt,&lt first name &gt,&lt ok|arrears &gt</b></html>", 
					"Format Error", JOptionPane.ERROR_MESSAGE);
			}
			
			lines = new String("");
			while ((tempLine = cmpFile.readLine()) != null)
			{
				if (!(tempLine.matches(cmpRegex)))
				{
					lines = lines + (j + 1) + " ";
					cmpFormat = false;
				}
					
				j++;
			}
			
			if (!(cmpFormat))
			{
				JOptionPane.showMessageDialog(null, "<html>Company List File Format Error, Check line(s): "+lines+"<br>Format must be:<br>"+
					"<b>&lt company title &gt,&lt policy number &gt</b></html>", "Format Error", JOptionPane.ERROR_MESSAGE);
			}
		
		} catch (Exception e){
			System.out.println("Exception: " + e);
		}
		
		if (stdFormat && cmpFormat)
		{
			IntroFrame frame = new IntroFrame();
			frame.setResizable(false);
			frame.pack();
		
			//Set the location at which it opens at (CENTERED)
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

			// Allow the fram to show up
			frame.setVisible(true);
		}
	}
}

/**
 *The main frame that collects the user information and prints the ticket.
 */
class a1Frame extends JFrame implements MouseListener
{
	private static final long serialVersionUID = 42L;
	
	private static final int currentYear = Calendar.getInstance().get(Calendar.YEAR); // current year
	private static final int currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1; // current month
	private static final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH); // current day
	
	private int numOfCmp = getNumberOfCompanies(); // total number of insurance companies present in the companies.txt
	
	private IntroFrame introF;

	private JTextField sid; // student ID to be entered
	private JTextField pin; // PIN to be entered and validated with the student ID
	private JTextField eml; // Email optional for daily mail or updates on permit balance
	
	private JComboBox<String> cmp; // list of companies in a dropdown menu
	private JComboBox<String> expDay; // drop down menu for the day of the month of the year when the permit expires
	private JComboBox<String> expMonth; // drop down menu for the month of the year when the permit expires
	private JComboBox<String> expYear; // drop down menu for the year the permit expires
	
	private int[] thirtyMonth = {4, 6, 9, 11}; // months that have 30 days
	private String[] months = {"Jan","Feb","March","April","May","June","July","Aug","Sept","Oct","Nov","Dec"};
	private String[] companyList = new String[numOfCmp]; // list of companies
	private String[] dayList = new String[31]; // list of maximum days in a month
	private String[] monthList = new String[12]; // list of months in numbers
	private String[] yearList = new String[10]; // list of years
	
	private JButton cancel;
	private JButton submit;

	private JLabel lInt1 = new JLabel("Fill the following information:");
	private JLabel lInt2 = new JLabel("* denotes required");
	private JLabel lSid = new JLabel("Student ID:");
	private JLabel lPin = new JLabel("PIN:");
	private JLabel lEml = new JLabel("Email:");
	private JLabel lCmp = new JLabel("Vehicle and Insurance Info:");
	private JLabel lExp = new JLabel("Expiry Date (MM/YY):");
	
	private String nF = "Not Found";

	//Variables to be accessed by other classes
	public int totalNumberOfDays;
	public String dateExpires;

	/**
	 *Creates a frame that collects the required information from the user.
	 *The passed frame cannot be <code>null</code>.
	 *
	 *@param introF The parent frame that called this frame.
	 */
	public a1Frame(IntroFrame introF)
	{
		// ----------------------------------
		// Construct and configure components
		// ----------------------------------
		
		this.introF = introF;
		
		//Sets an Icon for the main window/frame
		Image icon = Toolkit.getDefaultToolkit().getImage("images/YULOGO.png");
    	this.setIconImage(icon);
		
		//Set the default close action
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//Set the title of the window
		this.setTitle("Parking Ticketer");
		
		//Set the location at which it opens at (CENTERED)
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		
		//Fixes the Issue that allows users to resize the main window
		this.setResizable(false);
		
		//Set Look and feel of the frame to the system default
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			System.out.println("Exception: " + e);
		}
		
		//Set the Font for the Labels.
		lInt1.setFont(new Font("SansSerif", Font.BOLD, 20));
		lInt2.setFont(new Font("SansSerif", Font.PLAIN, 18));
		lSid.setFont(new Font("SansSerif", Font.PLAIN, 18));
		lPin.setFont(new Font("SansSerif", Font.PLAIN, 18));
		lEml.setFont(new Font("SansSerif", Font.PLAIN, 18));
		lCmp.setFont(new Font("SansSerif", Font.PLAIN, 18));
		lExp.setFont(new Font("SansSerif", Font.PLAIN, 18));
		
		sid = new JTextField(12);
		//Set the GhostText
		sid.setText("Student Number");
		sid.setFont(new Font("SansSerif", Font.PLAIN, 15));
		sid.setForeground(Color.GRAY);	
		sid.setHorizontalAlignment(JTextField.CENTER);	

		pin = new JTextField(5);
		//Set the GhostText
		pin.setText("Pin");
		pin.setFont(new Font("SansSerif", Font.PLAIN, 15));
		pin.setForeground(Color.GRAY);	
		pin.setHorizontalAlignment(JTextField.CENTER);	
		
		eml = new JTextField(12);
		//Set the GhostText
		eml.setText("username");
		eml.setFont(new Font("SansSerif", Font.PLAIN, 15));
		eml.setForeground(Color.GRAY);
		eml.setHorizontalAlignment(JTextField.CENTER);
		
		//Creating the Cancel and Submit button and setting the size
		cancel = new JButton("Cancel");
		cancel.setFont(new Font("SansSerif", Font.PLAIN, 15));
		submit = new JButton("Submit");
		submit.setFont(new Font("SansSerif", Font.PLAIN, 15));
		
		// ------------------------------
		// set initial and default values
		// ------------------------------
		
		setCompanyList();
		setDayList();
		setMonthList();
		setYearList();

		//Creating the ComboBoxes and setting the font/perfered size
		cmp = new JComboBox<>(companyList);
		cmp.setPreferredSize(new Dimension(300,25));
		cmp.setFont(new Font("SansSerif", Font.PLAIN, 15));
		expDay = new JComboBox<>(dayList);
		expDay.setFont(new Font("SansSerif", Font.PLAIN, 15));
		expMonth = new JComboBox<>(monthList);
		expMonth.setFont(new Font("SansSerif", Font.PLAIN, 15));
		expYear = new JComboBox<>(yearList);
		expYear.setFont(new Font("SansSerif", Font.PLAIN, 15));
		expMonth.setSelectedIndex(currentMonth-1);
		expDay.setSelectedIndex(currentDay-1);

		// -------------
		// add listeners
		// -------------

		sid.addMouseListener(this);
		pin.addMouseListener(this);
		eml.addMouseListener(this);
		cancel.addMouseListener(this);
		submit.addMouseListener(this);
		
		// -------------
		// add sub-panel
		// -------------
		
		JPanel sidP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		sidP.add(sid);
		sidP.add(new JLabel("*"));
		
		JPanel pinP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pinP.add(pin);
		pinP.add(new JLabel("*"));
		
		JPanel emlP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		emlP.add(eml);
		emlP.add(new JLabel("<html><font size=4>@yorku.ca</font></html>"));

		JPanel cmpP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cmpP.add(cmp);
		cmpP.add(new JLabel("*"));

		JPanel expP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		expP.add(expDay);
		expP.add(expMonth);
		expP.add(expYear);
		expP.add(new JLabel("*"));

		JPanel butP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		butP.add(cancel);
		butP.add(submit);

		// -----------------
		// Main Panel Design
		// -----------------
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	
		//Horizontal GroupLayout
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(lInt1)
				.addComponent(lSid)
				.addComponent(lPin)
				.addComponent(lEml)
				.addComponent(lCmp)
				.addComponent(lExp)
				.addComponent(cancel)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(lInt2)
				.addComponent(sidP)
				.addComponent(pinP)
				.addComponent(emlP)
				.addComponent(cmpP)
				.addComponent(expP)
				.addComponent(submit)
			)
		);
		
		//Vertical GroupLayout
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lInt1)
				.addComponent(lInt2)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lSid)
				.addComponent(sidP)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lPin)
				.addComponent(pinP)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lEml)
				.addComponent(emlP)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lCmp)
				.addComponent(cmpP)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lExp)
				.addComponent(expP)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(cancel)
				.addComponent(submit)
			)
		);
	}

	// -----------------------------------
	// implement MouseListener methods (5)
	// -----------------------------------
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mouseClicked(MouseEvent me)
	{
		Object source = me.getSource();
		TicketFrame tickF;
		String sidS = sid.getText();
		String pinS = pin.getText();
		int monthSelected = Integer.parseInt((String)expMonth.getSelectedItem());//getMonth();
		int daySelected = Integer.parseInt((String)expDay.getSelectedItem());
		int yearSelected = Integer.parseInt((String)expYear.getSelectedItem());
	
		// Check which button or field is the source of the action
		
		//Student Number Textfield has been clicked
		if (source == sid)
		{
			//Create the NumberPad window
			String title = "<html><font size=+2>Enter Student ID:</font></html>";
			NumberpadInput input = new NumberpadInput(title, this, 0);
			input.setVisible(true);
			input.pack();
			//Make the main window invisible.
			this.setVisible(false);
		}
		//The PIN TextField has been clicked.
		else if (source == pin)
		{
			String title = "<html><font size=+2>Enter PIN:</font></html>";
			NumberpadInput input = new NumberpadInput(title, this, 1);
			input.setVisible(true);
			input.pack();
			//Make the main window invisible.
			this.setVisible(false);
		}
		//The Email TextField has been clicked
		else if(source == eml)
		{
			String title = "<html><font size=+2>Enter your YorkU email:</font></html>";
			KeyboardInput input = new KeyboardInput(title, this);
			input.setVisible(true);
			input.pack();
			this.setVisible(false);
		}
		
		if (source == cancel)
		{
			introF.setVisible(true);
			this.dispose();
		}
		else if (source == submit)
		{
			if (((daySelected <= currentDay) && (monthSelected == currentMonth) && (yearSelected == currentYear)) || ((monthSelected < currentMonth)&& (yearSelected == currentYear)))
			JOptionPane.showMessageDialog(null, "Permit Expired, renew permit to continue."+
				"\nThanks You", "Permit Expired", JOptionPane.ERROR_MESSAGE);
			else
			{
				if ((monthSelected == 2) && (daySelected > 28))
				JOptionPane.showMessageDialog(null, "The month "+months[monthSelected-1]+" does not have "+
					"more than 28 days\nPlease try again", "Error Date limit", JOptionPane.ERROR_MESSAGE);
			
				else if (isThirtyDays(monthSelected) &&  daySelected == 31)
				JOptionPane.showMessageDialog(null, "The month "+months[monthSelected-1]+" does not have "+
					"31 days\nPlease try again", "Error Date limit", JOptionPane.ERROR_MESSAGE);
				else
				{
					if (sidS.equals("") || pinS.equals(""))
						JOptionPane.showMessageDialog(null, "* Fields cannot be empty");
					else
					{ // Expiry day is not this day or month is not this month
						if (!getStudentInfo(sidS).equals(nF))
						{
							if (checkPin(sidS, pinS))
							{
								if (getStatus(sidS))
								{
									totalNumberOfDays = numberOfDays(daySelected, monthSelected, yearSelected);
									dateExpires = getExpDate(daySelected, monthSelected, yearSelected);

									tickF = new TicketFrame(this, introF);
									tickF.pack();
									
									//Set the location at which it opens at (CENTERED)
									Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
									tickF.setLocation(dim.width / 2 - tickF.getSize().width / 2, dim.height / 2 - tickF.getSize().height / 2);

									tickF.setVisible(true);
									tickF.setResizable(false);
									this.setVisible(false);
								} // checkstatus is true
								else
								{
									JOptionPane.showMessageDialog(null, "Cannot print, pay "+
										"previous fine then try again.\nThanks You",
										"Outstanding Fine", JOptionPane.ERROR_MESSAGE);
								} // checkstatus is false
							}
							else
							JOptionPane.showMessageDialog(null, "Your Student ID and PIN"+
								" does not match","PIN Error", JOptionPane.ERROR_MESSAGE);
						}
						else
						JOptionPane.showMessageDialog(null, "Student not Found",
							"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mousePressed(MouseEvent me){	
	// do nothing
	}
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mouseReleased(MouseEvent me){
		// do nothing	
	}
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mouseEntered(MouseEvent me){
		// do nothing
	}
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mouseExited(MouseEvent me){
		//do nothing
	}
	
	// -------------
	// Other methods
	// -------------
	
	/**
	 *Returns the value of the number of companies in the file <code>companies.txt</code>.
	 *
	 *@return The number of companies in the file.
	 */
	public int getNumberOfCompanies()
	{
		//Read in all the companies from a file
		int i = 0;
		try (BufferedReader br = new BufferedReader(new FileReader("companies.txt")))
		{
    			for(String line; (line = br.readLine()) != null; i++);
    			//numOfCmp = i;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	
	/**
	 *Fills the array <code>companyList[]</code> with the company titles from the 
	 file <code>company.txt</code>.
	 */
	public void setCompanyList()
	{
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new FileReader("companies.txt"));
			String line = null;
			int i = 0;
			int index;
			
			while ((line = br.readLine()) != null)
	    		{
	    			index = line.indexOf(',');
	    			companyList[i] = ""+ line.substring(0,index);
	    			i++;
	    		}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 *Fills the array dayList[] with number of years from 1 to 31 denoting the
	  maximum number of days possible in a month
	 */
	public void setDayList()
	{
		for (int i=0; i < dayList.length; i++)
			dayList[i] = "" + (i+1);
	}

	/**
	 *Fills the array monthList[] with numbers from 1 to 12 denoting 
	 each month of a year.
	 */
	public void setMonthList()
	{
		for (int i=0; i < monthList.length; i++)
			monthList[i] = "" + (i+1);
	}
	
	/**
	 *Fills the array yearList[] with number of years from the current year 
	 to the next ten years.
	 */
	public void setYearList()
	{
		for (int i=0; i < yearList.length; i++)
			yearList[i] = "" + (i + currentYear);
	}
	
	/**
	 *Sets the text field for the Student ID to the passed String
	 *
	 *@param s The string to be inserted in the Student ID text field
	 */
	public void setSID(String s)
	{
		sid.setText(s);
		sid.setForeground(Color.BLACK);
	}

	/**
	 *Returns the Student ID inserted in the textfield
	 *
	 *@return String representation of Student ID
	 */
	public String getSID()
	{
		return new String(sid.getText());
	}
	
	/**
	 *Sets the text field for the PIN to the passed String
	 *
	 *@param s The string to be inserted in the PIN text field
	 */
	public void setPIN(String s)
	{
		pin.setText(s);
		pin.setForeground(Color.BLACK);
	}
	
	/**
	 *Sets the text field for the Email to the passed String
	 *
	 *@param s The string to be inserted in the Email text field
	 */
	public void setEmail(String s)
	{
		eml.setText(s);
		eml.setForeground(Color.BLACK);
	}
	
	/**
	 *Checks the file <code>students.txt</code> for whether the passed Student ID has the same
	  passed PIN.
	 *
	 *@param sid The Student ID to search for in the database.
	 *@param pin The PIN that should match the PIN in the database.
	 *@pre. sid is not null
	 *
	 *@return <code>true</code> if the PINs match, otherwise <code>false</code>
	 */
	public boolean checkPin(String sid, String pin)
	{
		String sidInfo = new String (getStudentInfo(sid));
		int lastInd = sidInfo.indexOf(',', 10);
		
		if (sidInfo.substring(10,lastInd).trim().equals(pin))
			return true;
			
		return false;
	}
	
	/**
	 *Checks the file passed month should have 30 days
	 *
	 *@param month The month selected by the user.
	 *
	 *@return <code>true</code> if the month has 30 days, otherwise <code>false</code>
	 */
	public boolean isThirtyDays(int month)
	{
		int i;
		for (i = 0; i < thirtyMonth.length; i++)
		{
			if (thirtyMonth[i] == month)
				return true;
		}
		return false;
	}
	
	/**
	 *Checks whether the passed Student ID exists in the file <code>students.txt</code>
	 *
	 *@param sid The Student ID to search for in the database
	 *
	 *@return The line the passed Student ID is found. Otherwise returns <i>Not Found</i>
	 */
	public String getStudentInfo(String sid)
	{
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader("students.txt")))
		{
    			while((line = br.readLine()) != null)
    			{
    				if (line.substring(0,9).equals(sid))
    					return line;
    			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return (new String (nF));
	}
	
	/**
	 *Checks the fine status for passed Student ID.
	 *
	 *@param sid The Student ID for which to search the status for in the database
	 *
	 *@return <code>true</code> if the passed Student ID does not have outstanding fines due. Otherwise <code>false</code>
	 */
	public boolean getStatus(String sid)
	{
		String sidInfo = new String (getStudentInfo(sid));
		int lastInd = sidInfo.lastIndexOf(',')+1;
		int len = sidInfo.length();
		
		if (sidInfo.substring(lastInd, len).trim().equals("ok"))
			return true;
		return false;
	}
	
	/**
	 *Returns the <last name>,<first name> from the student.txt file for the passed student ID
	 *
	 *@param sid The student ID of the names to extract
	 *
	 *@return String of the last name followed by the first name of the student ID
	 */
	 public String getLFName(String sid)
	 {
		String sidInfo = new String (getStudentInfo(sid));
		int thrdInd = sidInfo.indexOf(',', 10)+1; //Index of the second ',' in the line
		int lastInd = sidInfo.lastIndexOf(','); //Index of the last ',' in the line
		
		return new String(sidInfo.substring(thrdInd, lastInd).trim());
	}

	/**
	 *Returns the number of days between the expiry date of the permit and the current date
	 *
	 *@param day The day of the month of the year the permit is set to expire
	 *@param month The month of the year the permit is set to expire
	 *@param year the year the permit is set to expire
	 *
	 *@pre. The passed day, month or year is not less than the current day, month or year
	 *
	 *@return an integer value of the number of days between the passed date and the current date
	 */
	public int numberOfDays(int day, int month, int year)
	{
		int totalDays = 0;

		if ((month == currentMonth) && (year == currentYear))
		{ //the expiry month and year is the current month and year
			return (day - currentDay);
		}

		else if (year == currentYear)
		{//the expiry year is the current year 
			if (currentMonth == 2)
				totalDays = 28 - currentDay;
			else if (isThirtyDays(currentMonth))
				totalDays = 30 - currentDay;
			else
				totalDays = 31 - currentDay;

			//counting the months except the current month till the expiry month
			for (int i = currentMonth + 1; i < month; i++)
			{
				if (i == 2)
					totalDays += 28;
				else if (isThirtyDays(i))
					totalDays += 30;
				else
					totalDays += 31;
			}
			totalDays += day;

			return totalDays;
		}//the expiry year is the current year 

		else
		{//neither the expiry year or the month is the current year of month respectively
			if (currentMonth == 2)
				totalDays = 28 - currentDay;
			else if (isThirtyDays(currentMonth))
				totalDays = 30 - currentDay;
			else
				totalDays = 31 - currentDay;

			if ((currentMonth + 1) < month)
			{//the expiry month is after the current month
				for (int i = currentMonth + 1; i < month; i++)
				{
					if (i == 2)
						totalDays += 28;
					else if (isThirtyDays(i))
						totalDays += 30;
					else
						totalDays += 31;
				}
				totalDays += day;
			}
			else
			{//expiry month is before the current month, next year
				for (int i = currentMonth + 1; i < 12; i++)
				{
					if (i == 2)
						totalDays += 28;
					else if (isThirtyDays(i))
						totalDays += 30;
					else
						totalDays += 31;
				}

				for (int i = 0; i < month; i++)
				{
					if (i == 2)
						totalDays += 28;
					else if (isThirtyDays(i))
						totalDays += 30;
					else
						totalDays += 31;
				}
				totalDays += day;
				/*Cannot increment current year as one year has passed to calculate
				  month difference. Thus decrementing passed year.*/
				year--;
			}

			//counting the number of years in days
			for (int i = currentYear; i < year; i++)
				totalDays += 365;
			
		}
		return totalDays;
	}

	/**
	 *Returns a String representation of the expiry date in the Format: DD-MMMMM-YYYY
	 *
	 *@return String String representation of the expiry date
	 */
	public String getExpDate(int day, int month, int year)
	{
		return new String(""+day+"-"+months[month-1]+"-"+year);
	}
}

/*The subwindow that will popup for all number pad inputs
* Use to be MouseListener! Changed all buttons to ActionListener events
*/
class NumberpadInput extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 42L; // need to know the significance of this..
	
	//The textfield that holds the numbers
	private JTextField number;

	//Special Case buttons
	private JButton zer;
	private JButton clr;
	private JButton bks;
	private JButton dne;
	
	//Main window variable to hold a reference to the main window
	private a1Frame mainWindow;
	
	//The String for number text reference
	private String numbericaltext;

	/**The state at which the number pad is used for
	 *0 - StudentID
	 *1 - PIN
	 */
	private int state = 0;

	/**
	 *The constructor of the class
	 *This constructor takes in a title, a Jframe and a state
	 *The title is what we set the window title too
	 *The JFrame is for when we need to access the main window to transfer information
	 *The state is for when we need to know what textfield is being filled. 
	 */
	public NumberpadInput (String title, a1Frame mainWindow, int state)
	{
		//Saving variables to be used for other methods.
		this.mainWindow = mainWindow;
		this.state = state;

		numbericaltext = "123456789";
		
		//Set Icon of NumberPad window
		Image icon = Toolkit.getDefaultToolkit().getImage("images/YULOGO.png");
		this.setIconImage(icon);
	
		//This will do nothing when the 'x' is clicked 
		//Users must click OKAY to continue!
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	
		//Set the location at which it opens at (CENTERED)
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2 - 150, dim.height / 2 - this.getSize().height / 2 - 150);
		
		//Set the title
		this.setTitle(" ");
		
		number = new JTextField(7);
		number.setFont(new Font("SansSerif", Font.BOLD, 20));
		number.setHorizontalAlignment(JTextField.CENTER);

		bks = new JButton( );
		bks.addActionListener(this); 
		try {
			//Add an image into the button.
    		Image img = ImageIO.read(getClass().getResource("/images/back.png"));
    		bks.setIcon(new ImageIcon(img));
  		} catch (IOException ex) { }
		
		//Create Special Buttons and set the size for them
		zer = new JButton("0");
		zer.setPreferredSize(new Dimension(60, 45));
		zer.setFont(new Font("SansSerif", Font.BOLD, 20));
		clr = new JButton("C");
		clr.setPreferredSize(new Dimension(60, 45));
		clr.setFont(new Font("SansSerif", Font.BOLD, 20));
		dne = new JButton("OK");
		dne.setPreferredSize(new Dimension(60, 45));
		dne.setFont(new Font("SansSerif", Font.BOLD, 17));
		
		// arrange components
		JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER));
		banner.add(new JLabel(title));

		JPanel textField = new JPanel(new FlowLayout(FlowLayout.CENTER));
		textField.add(number);
		textField.add(bks);

		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//Create the buttons for the numberpad in row 1
	     for(int i = 0; i < 3; i++) {
	        String label = "" + numbericaltext.charAt(i);
			row1.add(new MyButton(label));
	    }

	    JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//Create the buttons for the numberpad in row 1
	     for(int i = 3; i < 6; i++) {
	        String label = "" + numbericaltext.charAt(i);
			row2.add(new MyButton(label));
	    }

	    JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//Create the buttons for the numberpad in row 1
	     for(int i = 6; i < 9; i++) {
	        String label = "" + numbericaltext.charAt(i);
			row3.add(new MyButton(label));
	    }

	    JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
	    clr.addActionListener(this);
	    zer.addActionListener(this);
	    dne.addActionListener(this);
	    row4.add(clr);
	    row4.add(zer);
	    row4.add(dne);

		// put panels in a content pane panel
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(6, 1));
		contentPane.add(banner);
		contentPane.add(textField);
		contentPane.add(row1);
		contentPane.add(row2);
		contentPane.add(row3);
		contentPane.add(row4);

		//Set the size of the back button to match the others
		bks.setPreferredSize(zer.getPreferredSize());
			
		// make panel this JFrame's content pane
		this.setContentPane(contentPane);	
	}

	// ---------------------------------
	// Implement ActionListener Methods(1)
	// ---------------------------------
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//Which button was clicked
		Object numberPadSource = e.getSource();
		
		//Save the text that in the textfeild
		String s = number.getText();
		
		//If the "OK" button is clicked we close and put the input
		//into the main screen texpanel.
		if(numberPadSource == dne)
		{
			/*Get the string from the JTextBox
			* Also this checkes if Student number/PIN are the correct
			* length
			* Student number = 9 digits
			* Pin = 4 digits */
			if(state == 0 && s.length() == 9)
			{
				mainWindow.setSID(s);

				//Re-open main windows
				mainWindow.setVisible(true);
			
				//Kill the popup window.
				this.dispose();

			}else if(state == 1 && s.length() == 4)
			{
				mainWindow.setPIN(s);
				//Re-open main windows
				mainWindow.setVisible(true);
			
				//Kill the popup window.
				this.dispose();

			}else if(state == 0 )
			{	
				//If the length is not valid we output an error message		
				JOptionPane.showMessageDialog(this, 
					"Student Number is not valid.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
			}else if(state == 1)
			{
				//If the length is not valid we output an error message	
				JOptionPane.showMessageDialog(this, 
					"Pin is not valid", "Invalid Input", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		//if "C" button is clicked we clear the textfield
		if(numberPadSource == clr)
		{
			number.setText("");
		}
		
		//If the back space button is clicked we delete the last characterr
		if(numberPadSource == bks)
		{
			if(s.length() > 0){
				s = s.substring(0, s.length()-1);
				number.setText(s);
			}
		}

		//Implement the "0" key
		if(numberPadSource == zer)
		{
			s += "0";
			number.setText(s);
		}
	}
	/*Create a personal Jbutton class to easily implement the Number keys
	*/
	class MyButton extends JButton implements ActionListener 
	{
		// constructor
	    MyButton(String name)
		{
			//Create a JButton with the specific Name
			super(name);
			//Addes the action Listener
	        addActionListener(this);
			//Set the Size of each button
			setPreferredSize(new Dimension(60, 45));
			setFont(new Font("SansSerif", Font.BOLD, 20));
		}
		// button was hit
		@Override
	    public void actionPerformed(ActionEvent e)
		{
			//Getts the text from the button.
			String s = this.getText();
			//Gets the main text fields text
			String i = number.getText();
			//Append the text to the end
			i = i + s;
			//Update the text.
	        number.setText(i);
		}
	}
}

/*Create an on screen keyboard (QWERTY Style keyboard)*/
class KeyboardInput extends JFrame implements ActionListener 
{
	private static final long serialVersionUID = 42L;
	
	//The textfield that will hold the input
	private JTextField textInput;
	
	//Mainwindow variable to help a reference to the main Window
	private a1Frame mainWindow;
	
	//The special button cases
	private JButton bks;
	private JButton spc;
	private JButton dne;
	private JButton clr;
	
	//String reference for the keyboard.
	private String qwerty;
	private String numbericaltext;
	
	/*Constructor for the keyboard */
	public KeyboardInput(String title, a1Frame mainWindow)
	{
		this.mainWindow = mainWindow;
		qwerty = "QWERTYUIOPASDFGHJKLZXCVBNM ";
		numbericaltext = "1234567890";
		
		//Set Icon of keyboard window
		Image icon = Toolkit.getDefaultToolkit().getImage("images/YULOGO.png");
		this.setIconImage(icon);
		
		//Users must click OKAY to continue!
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//Have the Popup show up in the middle of the screen.	
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((dim.width / 2 - this.getSize().width / 2) - 400, (dim.height / 2 - this.getSize().height / 2) - 250);
		
		//Set the title
		this.setTitle(" ");
		
		textInput = new JTextField(35);
		textInput.setFont(new Font("SansSerif", Font.BOLD, 20));
		textInput.setHorizontalAlignment(JTextField.CENTER);
		
		//Create the backspace button and give it a image
		bks = new JButton( ); 
		bks.setPreferredSize(new Dimension(60, 45));
		bks.addActionListener(this);
		try {
			//Add an image into the button.
    		Image img = ImageIO.read(getClass().getResource("/images/back.png"));
    		bks.setIcon(new ImageIcon(img));
  		} catch (IOException ex) { }		
		
		JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER));
		banner.add(new JLabel(title));

		JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		textFieldPanel.add(textInput);
		textFieldPanel.add(bks);
		
		JPanel numberRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//Create the buttons for the keyboard in row 3 
	   	 for(int i = 0; i < numbericaltext.length(); i++) {
	       		 String label = "" + numbericaltext.charAt(i);
			numberRow.add(new MyButton(label));
	  	}
		
		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//Create the buttons for the keyboard in row 1
	     for(int i = 0; i < 10; i++) {
	        String label = "" + qwerty.charAt(i);
			row1.add(new MyButton(label));
	    }
		
		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//Create the buttons for the keyboard in row 2
	    for(int i = 10; i < 19; i++) {
	        String label = "" + qwerty.charAt(i);
			row2.add(new MyButton(label));
	    }
		
		JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//Create the buttons for the keyboard in row 3 
	    for(int i = 19; i < qwerty.length() - 1; i++) {
	        String label = "" + qwerty.charAt(i);
			row3.add(new MyButton(label));
	    }
		
		//Center the layout
		JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		//Create the button for the keyboard in row 4
		clr = new JButton("C");
		clr.setPreferredSize(new Dimension(60, 45));
		clr.setFont(new Font("SansSerif", Font.BOLD, 20));
		dne = new JButton("OK");
		dne.setPreferredSize(new Dimension(60, 45));
		dne.setFont(new Font("SansSerif", Font.BOLD, 17));
		spc = new JButton(" ");
		spc.setPreferredSize(new Dimension(200, 45));
		spc.setFont(new Font("SansSerif", Font.BOLD, 20));
		
		
		clr.addActionListener(this);
		dne.addActionListener(this);
		spc.addActionListener(this);
		row4.add(clr);
		row4.add(spc);
		row4.add(dne);
		
		//Set the content panel and the layouy
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(7, 1));
		contentPane.add(banner);
		contentPane.add(textFieldPanel);
		contentPane.add(numberRow);
		contentPane.add(row1);
		contentPane.add(row2);
		contentPane.add(row3);
		contentPane.add(row4);

		//Set the size of the back button to match the others
		bks.setPreferredSize(dne.getPreferredSize());
		
		// make panel this JFrame's content pane
		this.setContentPane(contentPane);	
	}
	
	/**
	 *Used to implement BackSpace key, Space key, OK key and Clear key.
	 *{@inheritDoc}
	 */ 
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//Which button was clicked
		Object keyboardSource = e.getSource();
		String s = textInput.getText();
		
		//If "OK" is clicked, close the popup keyboard and transfer text to the
		//email feild in the main window.
		if(keyboardSource == dne)
		{
			//MAX SIZE OF YORKU USERNAME = 8 
			//username can be <= 8 charactes
			if(s.length() > 0 && s.length() <= 8){
				mainWindow.setEmail(textInput.getText());
				this.dispose();
				mainWindow.setVisible(true);
			}
			else{
				//If the length is not valid we output an error message	
				JOptionPane.showMessageDialog(this, 
						"Username is not valid", "Invalid Input", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		//If the back space button is clicked we delete the last characterr
		if(keyboardSource == bks)
		{
			if(s.length() > 0){
				s = s.substring(0, s.length()-1);
				textInput.setText(s);
			}
		}
	}
	
	/*Create a personal Jbutton class to easily implement all 26 keys
	*/
	class MyButton extends JButton implements ActionListener 
	{
		// constructor
	    MyButton(String name)
		{
			//Creating the button with the Text inside it.
			super(name);
			//Adding an acction listener to it.
	        addActionListener(this);
			//Setting the size of the button
			setPreferredSize(new Dimension(60, 45));
			//Set the font and size of each letter
			setFont(new Font("SansSerif", Font.BOLD, 20));
		}
		
		// button was hit
		@Override
	    public void actionPerformed(ActionEvent e)
		{
			//Getting the text from the button
			String s = this.getText();
			//Getting the text from the textinput
			String i = textInput.getText();
			//Appending to the end
			i = i + s;
			//Updating the current text
	        textInput.setText(i.toLowerCase());
		}
	}
}

/**
 *A simple frame with a background that appears first and reappears after all other frames close.
  A simple screensaver frame.
 */
class IntroFrame extends JFrame implements MouseListener
{
	private static final long serialVersionUID = 42L;
	
	private JLabel parkTick; 
	private JPanel panelImage;
	
	/**
	 *Creates a frame with a fixed backround.
	 */
  	public IntroFrame()
  	{
  		//Disabling default close button at the top
  		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Set Icon of keyboard window
		Image icon = Toolkit.getDefaultToolkit().getImage("images/YULOGO.png");
		this.setIconImage(icon);

  		parkTick = new JLabel();
  		try
  		{
	   		Image img = ImageIO.read(getClass().getResource("/images/Intro.jpg"));
	    		parkTick.setIcon(new ImageIcon(img));
    		} catch (IOException ex) {
  		}
  		
		parkTick.addMouseListener(this);
		panelImage = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelImage.add(parkTick);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(1, 1));
		contentPane.add(panelImage);

		this.setContentPane(contentPane);
  	}

	/**
	 *Implemented to go to the a1Frame for user input
	 *{@inheritDoc}
	 */
  	@Override
  	public void mouseClicked(MouseEvent me)
	{
		//Which button was clicked
		Object source = me.getSource();

		if (source == parkTick)
		{
			// Create the Window when the program is run.
			a1Frame frame = new a1Frame(this);
			frame.pack();

			//Have the Popup show up in the middle of the screen.	
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

			// Allow the fram to show up
			frame.setVisible(true);
			this.setVisible(false);
		}
	}
	
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mousePressed(MouseEvent me){
		// do nothing
	}
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mouseReleased(MouseEvent me){
		// do nothing	
	}
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mouseEntered(MouseEvent me){
		// do nothing
	}
	/**
	 *{@inheritDoc}
	 */
	@Override
	public void mouseExited(MouseEvent me){
		//do nothing
	}
}

class TicketFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 42L;
	
	private static final double pricePDay = 3.50; //The amount to be charged per day
	
	private JLabel background;
	private JLabel SIDLabel;
	private JLabel lastN;
	private JLabel firstN;
	private JLabel nameL;
	private JLabel nameF;
	private JLabel expiL;
	private JLabel dayLabel;
	private JLabel numDays;
	private JLabel expLabel;
	private JLabel expDate;

	
	private JButton cancel;
	private JButton print;
	
	private a1Frame parent;
	private IntroFrame introF;

	private String nameS;

	/**
	*/
	public TicketFrame(a1Frame parent, IntroFrame introF)
	{
		this.parent = parent;
		this.introF = introF;
		
		//Setting the Icon of the frame
		Image icon = Toolkit.getDefaultToolkit().getImage("images/YULOGO.png");
    	this.setIconImage(icon);
		
		//Set the background image to the permit image.
		background = new JLabel();
		//Place a grid layout on top of the background image.
		background.setLayout(new GridLayout(9,2, -70, -20));
		try
  		{
	   		Image img = ImageIO.read(getClass().getResource("/images/permitRED.gif"));
	    		background.setIcon(new ImageIcon(img));
    		} catch (IOException ex) {
  		}
		//Center the image.
		background.setHorizontalAlignment(JTextField.CENTER);	
		
		//Set the default close action
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		//Have the Popup show up in the middle of the screen.	
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		
		//Getting the time and setting a format for it.
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		//Get the Names from via the Student Number
		nameS = parent.getLFName(parent.getSID());
		
		//SID Lables
		SIDLabel = new JLabel("<html><center>Student Number:<br>"+ parent.getSID()+"</html>");
		SIDLabel.setHorizontalAlignment(JLabel.CENTER);
		SIDLabel.setFont(new Font("Serif", Font.BOLD, 20));
		SIDLabel.setForeground(Color.WHITE);
		
		
		//Name Labels
		lastN = new JLabel("Last Name");
		//Set the allighment in the Grid
		lastN.setHorizontalAlignment(JLabel.CENTER);
		//Set the font and size of the text
		lastN.setFont(new Font("Serif", Font.PLAIN, 22));
		lastN.setForeground(Color.RED);	
		firstN = new JLabel("First Name");
		//Set the allighment in the Grid
		firstN.setHorizontalAlignment(JLabel.CENTER);
		//Set the font and size of the text
		firstN.setFont(new Font("Serif", Font.PLAIN, 22));
		firstN.setForeground(Color.RED);
		
		//Get the infomration from the database (Student.txt file)
		nameL = new JLabel(nameS.substring(0, nameS.indexOf(',')).trim());
		nameL.setHorizontalAlignment(JLabel.CENTER);
		nameL.setFont(new Font("Serif", Font.BOLD, 19));
		nameF = new JLabel(nameS.substring(nameS.indexOf(',')+1, nameS.length()).trim());
		nameF.setHorizontalAlignment(JLabel.CENTER);
		nameF.setFont(new Font("Serif", Font.BOLD, 19));
		
		//Labels to gain the infomation from
		numDays = new JLabel("$ "+ String.format( "%.2f",((parent.totalNumberOfDays +1) * pricePDay))) ;
		//Set the allighment in the Grid
		numDays.setHorizontalAlignment(JLabel.CENTER);
		numDays.setFont(new Font("Serif", Font.BOLD, 19));
		expDate = new JLabel("<html><center>"+parent.dateExpires+"<br>"+"("+sdf.format(cal.getTime())+")"+"</html>");
		//Set the allighment in the Grid
		expDate.setHorizontalAlignment(JLabel.CENTER);
		expDate.setFont(new Font("Serif", Font.BOLD, 19));
		
		//Label for the Expire date and charge in $
		dayLabel = new JLabel("Sum:");
		dayLabel.setHorizontalAlignment(JLabel.CENTER);
		dayLabel.setFont(new Font("Serif", Font.PLAIN, 22));
		dayLabel.setForeground(Color.RED);
		expLabel = new JLabel("EXP. Date:");
		expLabel.setHorizontalAlignment(JLabel.CENTER);
		expLabel.setFont(new Font("Serif", Font.PLAIN, 22));
		expLabel.setForeground(Color.RED);
		
		
		//Create the buttons
		cancel = new JButton("Return");
		cancel.setPreferredSize(new Dimension(125, 45));
		print = new JButton("Print");
		print.setPreferredSize(new Dimension(125, 45));
	
		//Add actions listeners to the buttons
		cancel.addActionListener(this);
		print.addActionListener(this);
		
		//Set the content panel and the layout
		JPanel contentPane = new JPanel();
		//Allows the Panel color to be changed
		contentPane.setOpaque(true);
		//Change the color to WHITE
        contentPane.setBackground(Color.WHITE);
		//Creating a inner Content Pane
		JPanel innerCP = new JPanel();
		//Making the main Content Pane a BorderLayout
		contentPane.setLayout(new BorderLayout());
		//Making the inner Content Pane a BorderLayout 
		innerCP.setLayout(new BorderLayout());
		
		//Add all the components to the content panes.
		//Dummy holders
		background.add(SIDLabel);
		background.add(new JLabel());
		background.add(new JLabel());
		background.add(new JLabel());
		background.add(new JLabel());
		background.add(new JLabel());
		background.add(new JLabel());
		background.add(new JLabel());
		
		//Adding the actual Components. 
		background.add(lastN);
		background.add(firstN);
		background.add(nameL);
		background.add(nameF);
		background.add(dayLabel);
		background.add(numDays);
		background.add(expLabel);		
		background.add(expDate);
		
		//Adding the Buttons to the inner ContentPane
		innerCP.add(cancel, BorderLayout.WEST);
		innerCP.add(print, BorderLayout.EAST);
		
		//Adding the inner ContentPane to the main ContentPane
		contentPane.add(innerCP, BorderLayout.SOUTH);
		contentPane.add(background, BorderLayout.CENTER);	

		// make panel this JFrame's content pane
		this.setContentPane(contentPane);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//Which button was clicked
		Object source = e.getSource();

		if (source == cancel)
		{
			parent.setVisible(true);
			this.dispose();
		}
		else if (source == print)
		{
			//JOptionPane.showMessageDialog(null, "Printing... \n");
			introF.setVisible(true);
			parent.dispose();
			this.dispose();
		}
	}	
}