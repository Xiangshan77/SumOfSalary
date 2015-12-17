import java.io.*;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import org.json.JSONException;  
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

/*
 * Class: SingleCreateImportEmployees
 * Brief: To generate employees' info and insert it to MongoDB and also write it to a JSON file as required
 * Members: NUM: The total number of employees as required(at least one million)
 *          OUTPUT_PATH: The path of the Employee.json file in my local computer.
 *          
 * Methods: 
 * */
public class SingleCreateImportEmployees{
	//Define the total number of employees.
	private static int NUM = 1000000;
	//Define the path of the Json file.
    private static String OUTPUT_PATH = "/Users/xiangshan/Desktop/Employees.json";    
    
	public static void main(String[] args) throws IOException {
		//Record the start time 
		long start = System.currentTimeMillis();	
       
		BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(OUTPUT_PATH));
		
		/* Using DecimalFormat to ensure that there is only one digit 
		 * in the fraction part of "IncrementalRate"
		 */
		DecimalFormat df = new DecimalFormat("#.0");
		
		try {
            Mongo mg = new Mongo("localhost",27017);
            DB db = mg.getDB("sap");
            DBCollection employees = db.getCollection("Data");  
          
		/*
		 * To create required data structures and amount of employees
		 */
		for(int i = 0; i < NUM; i++){
			  JSONObject employee = new JSONObject();
			  String str1 = "FirstName" + String.valueOf(i);
			  String str2 = "LastName" + String.valueOf(i);
			  int monthySalary =(int) Math.round(Math.random()*9999);
			  double originalRate = Math.random();
			  double incrementaRate= Double.parseDouble(df.format(originalRate));
			
			  try{
			/*
			 * Using put() method to add pairs of key/value for each employee.
			 */
				  employee.put("FirstName", str1);
				  employee.put("LastName", str2);
				  employee.put("ID", String.valueOf(i));
				  employee.put("MonthySalary", monthySalary);
				  employee.put("IncrementalRate", incrementaRate);
				  
		   /*
		    * Convert JSONObject to DBObject class, and then insert this object to mongoDB.
		    */
				  DBObject dBoj = (DBObject) JSON.parse(employee.toString());
				  employees.insert(dBoj);
			  } catch(JSONException e){
				  e.printStackTrace();  
			  }
		/*
		 * Write the generated Json Data to the specified file.
		 */
			  bufferWriter.write(employee.toString()+"\n");
			   
		  }}catch (UnknownHostException | MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		bufferWriter.flush();
		bufferWriter.close();
		
		/*
		 * Record the finish time and then to calculate the running time.
		 * */
		long end = System.currentTimeMillis();		
		long runningTime = end-start;
		System.out.println("Created "+ NUM + " employees\n"+"RunningTime: " + runningTime+"ms");				
    }	
	
	
}
