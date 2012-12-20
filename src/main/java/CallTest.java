import java.util.concurrent.Callable;

/**
 *@version:2012-12-20-下午12:14:31
 *@author:jianjunwei
 *@date:下午12:14:31
 *
 */

/**
 * @author jianjunwei
 *
 */
public class CallTest implements Callable<Integer> {

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Integer call() throws Exception {
        // TODO Auto-generated method stub
        int i =0;
        while(true){
         
            try{
               
                i++;
                if (i %2000 == 0){
                    System.out.println("call-------------------------");
                    
                }
             
                Thread.sleep(1);
           } catch(InterruptedException e){
               System.out.println("InterruptedException");
               return 1;
           }
       }
       
    }

}
