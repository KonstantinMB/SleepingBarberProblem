import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SleepingBarber {

    public static void main (String a[]) throws InterruptedException {

        Scanner sc = new Scanner(System.in);
        int numberOfBarbers; // the amount of barbers working
        int customerId = 1; // the ID of the customer
        int numberOfCustomers = 10; // the amount of customers
        int numberOfChairs;	// number of chairs in the waiting room

        System.out.println("Enter the number of barbers:");
        numberOfBarbers = sc.nextInt();

        System.out.println("Enter the number of waiting room chairs:");
        numberOfChairs=sc.nextInt();

        ExecutorService exec = Executors.newFixedThreadPool(12); // initializing with 12 threads
        Barbershop shop = new Barbershop(numberOfBarbers, numberOfChairs);	// initializing the barber shop with the number of barbers
        Random r = new Random(); // a random number to calculate delays for customer arrivals and haircuts

        System.out.println("\nBarber shop opened with " + numberOfBarbers +" barber(s)\n");

        long startTime  = System.currentTimeMillis(); // start time of program / started working day for barber


        // Each barber gets one thread:
        for(int i=1; i<=numberOfBarbers; i++) {

            Barber barber = new Barber(shop, i); // where i is the ID of a barber
            Thread barberThread = new Thread(barber);
            exec.execute(barberThread);
        }

        // Each customer gets one thread:
        for(int i=0;i<numberOfCustomers;i++) {

            Customer customer = new Customer(shop);
            customer.setInTime(new Date());
            Thread customerThread = new Thread(customer);
            customer.setCustomerId(customerId++);
            exec.execute(customerThread);

            try {
                //'r':object of Random class, nextGaussian() generates a number with mean 2000 and
                // standard deviation as 2000,
                // thus customers arrive at mean of 2000 milliseconds and standard deviation of 2000 milliseconds
                double val = r.nextGaussian() * 2000 + 2000;
                int millisDelay = Math.abs((int) Math.round(val));
                Thread.sleep(millisDelay);
            }
            catch(InterruptedException iex) {

                iex.printStackTrace();
            }

        }

        exec.shutdown(); // shuts down the executor service and frees all the resources
        exec.awaitTermination(12, SECONDS); //waits for 12 seconds until all the threads finish their execution

        long totalTimeInWaitingRoom = System.currentTimeMillis() - startTime; // to calculate the end time of program

        System.out.println("\nBarber shop closed");
        System.out.println("\nTotal time elapsed in seconds for serving " + numberOfCustomers + " customers by "
                + numberOfBarbers + " barbers with " + numberOfChairs + " chairs in the waiting room is: "
                + TimeUnit.MILLISECONDS.toSeconds(totalTimeInWaitingRoom));
        System.out.println("\nTotal customers: " + numberOfCustomers+
                "\nTotal customers served: " + shop.getTotalHairCuts()
                +"\nTotal customers lost: " + shop.getCustomerLost());

        sc.close();
    }
}