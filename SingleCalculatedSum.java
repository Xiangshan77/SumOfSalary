import java.math.BigDecimal;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;


/*Class: SingleCalculatedSum
 * brief: Calculated the sum of all salaries in single thread mode.
 * */
public class  SingleCalculatedSum{  
    static public BigDecimal sum() {  
    /*
     * Define the number of employees
     * */
        int count=1000000;
        BigDecimal s = BigDecimal.ZERO;  
        BigDecimal cSalary = BigDecimal.ZERO;
        BigDecimal parameter = new BigDecimal(1);
        int monthySalary=0;
        double incrementalRate=0;
        
          	      try {
          	    	/*
          	    	 * Connecting to the db
          	    	 * */
          	        Mongo mg = new Mongo("localhost",27017);
      			    DB db = mg.getDB("sap");
      		        DBCollection employees = db.getCollection("Data");  
      		        BasicDBObject query = new BasicDBObject();
      		        BasicDBObject field = new BasicDBObject();
      		        
      		        for (int i = 0; i < count; i++) {  
                 			  query.put("ID",String.valueOf(i));
                 		     field.put("MonthySalary", 1);
                 		     field.put("IncrementalRate", 1);
                 		     DBCursor cursor = employees.find(query,field);

                 		     while (cursor.hasNext()) {
                 		    	BasicDBObject obj = (BasicDBObject) cursor.next();  
                 				monthySalary =	 obj.getInt("MonthySalary");
                 				incrementalRate= obj.getDouble("IncrementalRate") ;
                                BigDecimal mSalary = new BigDecimal(monthySalary);
                                BigDecimal iRate = new BigDecimal(incrementalRate);
                                cSalary =  iRate.add(parameter);
                                cSalary = cSalary.multiply(mSalary);          
                 		     }	 
                 		    s = s.add(cSalary);
                    }  }  catch (MongoException e) {
             			e.printStackTrace();
                    }
            
        return s;  
}
    
  
    public static void main(String[] args) {  
        long start = System.currentTimeMillis();   
        double sumLast; 
        BigDecimal sum2 = sum();
        sumLast= sum2.doubleValue(); 
        long end = System.currentTimeMillis();  
        long runningTime = end - start;      
        System.out.println( "The total salary of all employees this month is: "+sumLast+" RunningTime:" + runningTime);  
    }  
  
}  
 
