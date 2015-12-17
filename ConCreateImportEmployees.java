import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

/*
 * Class: ConCreateImportEmployees
 * brief: To generate employees' info and insert it to MongoDB 
 * and also write it to a JSON file as required in Multithreads.
 * */
public class ConCreateImportEmployees{
	public static void main(String[] args){
		String OUTPUT_PATH = "/Users/xiangshan/Desktop/Employees.json"; 
		
/*
 * Check if the output file is already created, if yes, clear it.
 * */
		 File w = new File(OUTPUT_PATH);
		 if(w.exists()){
			w.delete();
		 }
		 
        BlockingQueue<JSONObject> queue = new ArrayBlockingQueue<JSONObject>(5000);
        int NUM=1000000;
        DataCreate ct =new DataCreate(queue,NUM);
        FileWrite cr = new FileWrite(queue,NUM);
  /*
   * Thread(ct) generated data as required and insert it to MongoDB and save it in the queue
   * Thread(cr) retrieve value from queue and write it to the specified Json file.
   * */
        new Thread(ct).start();
        new Thread(cr).start();
      
	   if(!Thread.currentThread().isAlive()){  
	       System.out.println("线程已结束");  
	   }         
	}
}

/*
 * Class: FileWrite
 * brief: get the data from the queue and then write it to the file
 * Member: queue: buffer area. DataCreate thread would pass generated data to the queue, 
 *         and then FileWrite thread retrieve it.
 *         BufferWriter: Using bufferedWrite method for write to the file.
 *         countNum: Calculate how many results has been added to the file.
 *         employeeNum: The total number of employees.
 * 
 * */
class FileWrite implements Runnable{
 
private BlockingQueue<JSONObject> queue;
private static String OUTPUT_PATH = "/Users/xiangshan/Desktop/EmployeeHEHEdss.json"; 
private BufferedWriter bufferWriter;
private int countNum=0;
private int employeeNum;

    public FileWrite(BlockingQueue<JSONObject> q,int e){
        this.queue=q;
        this.employeeNum=e;
    }
    public void WriteToFile() throws InterruptedException {
    	 try { 
    	/*
    	 * Because multithreads here, using FileWriter(path,true) to ensure that
    	 * new lines would be added at the end of file rather than overwrite it.
    	 * */
    	bufferWriter = new BufferedWriter(new FileWriter(OUTPUT_PATH,true));
        synchronized (queue) {  
    	 while (!queue.isEmpty()) {  		 
    		 /*
    		  * Write the generated Json Data to the specified file.
    		  */
             	JSONObject jobj = new JSONObject();
             	jobj = queue.take();
             	 bufferWriter.write(jobj.toString()+"\n");
             	 countNum++;
    	 }
     }
    	 } catch (IOException e) {  
             e.printStackTrace();  
         } finally{  
               try {
				bufferWriter.flush();
				 bufferWriter.close();  
			} catch (IOException e) {
				e.printStackTrace();
			}        
       }  
    	
    }
  
    @Override
    public void run() {
		  try {
			  while(countNum != employeeNum){
			  WriteToFile();
		} 
		  }catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
       
    }
  
}

/*
 * Class: DataCreate
 * brief: Create data as required and insert it to mongoDB
 * Member: employeeNum: the total number of employees
 *         queue: buffer area. 
 * */
class DataCreate implements Runnable{
	private BlockingQueue<JSONObject> queue;
    private int employeeNum;
	
    public DataCreate(BlockingQueue<JSONObject> q, int e){
	        this.queue=q;
	        this.employeeNum = e;
	    }
    
	public void run(){
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
	 for(int i = 0; i < employeeNum; i++){
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
			  queue.offer(employee);
	   /*
	    * Convert JSONObject to DBObject class, and then insert this object to mongoDB.
	    */
			  DBObject dBoj = (DBObject) JSON.parse(employee.toString());
			  employees.insert(dBoj);
		  } catch(JSONException e){
			  e.printStackTrace();  
		  }
}
		}catch (MongoException e) {
                    e.printStackTrace();
        }
		
	}
}


