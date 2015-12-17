import java.math.BigDecimal;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;

/*
 * Class: ThreadsSum
 * Brief : Calculated the total salary as required of all documents.
 * Methods: sum() 
 * 
 * Info: Using Class BigDecimal to calculate the sum of all employees
 *   to ensure that it would get a precise result after arithmetical calculations.
 * */
public class ConCalculatedSum {  
   static public BigDecimal sum() {  
    	//Define the default number of threads
        int threadNum=4;
        //Define the size of processing data for each thread 
        int threadScope=250000;
        // Get the relevant data when program is running
        final ProcessedData pData = new ProcessedData(); 
        
        BigDecimal parameter = new BigDecimal(1);
    
        /*
         * Multithreads: Use 2 threads to complete the task as required.
         * Each thread would responsible for calculating the current month salary for each doc first,
         * then adding each current month salary in a total with its process size. 
         * Lastly, write it to SUM. 
         * 
         * */
        
        try {
      	  /*
      	   * Connect to MongoDB
      	   * db info: addr: 127.0.0.1 PortNumber: 27017 Database: sap , Collection: Data
      	   * */
          Mongo mg = new Mongo("localhost",27017);
          DB db = mg.getDB("sap");
          DBCollection employees = db.getCollection("Data");  
         
        /*
         * 
         * */
    
        for (int i = 0; i < threadNum; i++) {  
            final int N = i;
            new Thread() {  
                @Override  
                public void run() {  
                    BigDecimal tempResults = BigDecimal.ZERO;  
                    BigDecimal cSalary = BigDecimal.ZERO;
                    int monthySalary=0;
                    double incrementalRate=0;
                    /*
                     *  Get the values of "MonthtSalary" and "IncrementalRate" fields of each document.
                     *  Then, to calculate the currentMonth Salary for each document(employee) using given formula
                     * */
                   DBCursor cursor = employees.find().skip(threadScope*N).limit(threadScope);
                	   
                      try{
                	   while (cursor.hasNext()) {
                 		    	BasicDBObject obj = (BasicDBObject) cursor.next();  
                               monthySalary =   obj.getInt("MonthySalary");
                               incrementalRate=  obj.getDouble("IncrementalRate");
                                BigDecimal mSalary = new BigDecimal(monthySalary);
                                BigDecimal iRate = new BigDecimal(incrementalRate);
                
                                cSalary =  iRate.add(parameter);
                                cSalary = cSalary.multiply(mSalary);
                                tempResults = tempResults.add(cSalary);
                             }
                      }finally {
                    	  cursor.close();
                      }
                         
             
                      
                  
                  /*
                   * Use the keyword SYNCHRONIZED to ensure that there is only one thread 
                   * would access/modify the value of SUM each time.
                   * */
                    synchronized (pData) {  
                        pData.sum =pData.sum.add(tempResults);  
                        pData.threadCompleted++;  
                         System.out.println("thread["+ getName() +"]  completed, CurrentSum:" + pData.sum);  
                    }    
                }; 
            }.start();  
        }  } catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
  
      
        while (pData.threadCompleted != threadNum) {  
            try {  
                Thread.sleep(1);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
                break;  
            }  
        }  
        return pData.sum;  
    }  
  
    public static void main(String[] args) {  
    	/*
    	 * Test the sum() method and record the running time in milliseconds.
    	 * */
        long start = System.currentTimeMillis(); 
        double sumLast; 
        BigDecimal sum2 = sum();
        sumLast= sum2.doubleValue();
        long end = System.currentTimeMillis();  
        long runningTime = end - start;  
       System.out.println( "The total salary of all employees this month is: "+sumLast+" \n RunningTime:" + runningTime+"ms");  
    }  
  
}  
  
/*
 * Class: ProcessData
 * Brief: Save the relevant data at run time.
 * Members: sum, threadCompleted.
 * */ 
class ProcessedData {  
	// The value of total salary.
    BigDecimal sum = BigDecimal.ZERO;  
  
    // The number of threads which are completed.
    int threadCompleted=0;  
  
}  
