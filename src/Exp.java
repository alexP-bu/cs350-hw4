 
    

/***************************************************/
/* CS-350 Spring 2022 - Homework 3 - Code Solution   */
/* Author: Renato Mancuso (BU)                     */
/*                                                 */
/* Description: This class implements the logic of */
/*   an exponentially distributed random number    */
/*   generator with parameter lambda. To test the  */
/*   code through the main, provide the lambda     */
/*   parameter followed by the number of samples   */
/*   to generate.                                  */
/*                                                 */
/***************************************************/

public class Exp {

    /* Generation of a single exponentially distributed sample is
     * performed by computing the inverse of the CDF function. */
    static double getExp(double lambda) {
	double x = Math.random();
	return Math.log(1-x)/-(lambda);
    }

    public static void main(String[] args) {
	/* Parse the input parameters */
	double lambda = Double.valueOf(args[0]);
	int N = Integer.valueOf(args[1]);

	/* Generate and print N samples from the exponential
	 * distribution */
	for (int i = 0; i < N; i++) {
	    double res = getExp(lambda);
	    System.out.println(res);
	}
    }
}

/* END -- Q1BSR1QgUmVuYXRvIE1hbmN1c28= */
